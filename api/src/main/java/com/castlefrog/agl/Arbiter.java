package com.castlefrog.agl;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Arbiter is used to regulate agents with the simulator.
 * It allows each agent to run in its own thread for
 * simultaneous domains and records history and other game data.
 */
public final class Arbiter<S extends State<S>, A extends Action> {
    /** The actual domain being used */
    private Simulator<S, A> world_;
    /** Simulator to use for each agent */
    private List<Simulator<S, A>> simulators_ = new ArrayList<>();
    private History<S, A> history_;
    private int historyIndex_;
    private final List<Agent> agents_ = new ArrayList<>();
    private long[] decisionTimes_;

    private ExecutorService executor_;
    private CountDownLatch actionsReady_;
    private OnEventListener listener_ = new DummyOnEventListener();

    public interface OnEventListener {
        void onStep();
    }

    public class DummyOnEventListener implements OnEventListener {
        @Override public void onStep() {}
    }

    private class AgentAction implements Runnable {
        private final int agentId_;
        private final Vector<A> actions_;
        private final long[] decisionTimes_;

        public AgentAction(int agentId,
                           Vector<A> actions,
                           long[] decisionTimes) {
            agentId_ = agentId;
            actions_ = actions;
            decisionTimes_ = decisionTimes;
        }

        public void run() {
            if (world_.hasLegalActions(agentId_)) {
                long startTime = System.currentTimeMillis();
                S state = world_.getState();
                A action = agents_.get(agentId_).selectAction(agentId_, state, simulators_.get(agentId_));
                actions_.set(agentId_, action);
                decisionTimes_[agentId_] = System.currentTimeMillis() - startTime;
            }
            actionsReady_.countDown();
        }
    }

    public Arbiter(S initialState,
                   Simulator<S, A> world,
                   List<Agent> agents) {
        this(initialState, world, null, agents);
    }

    public Arbiter(S initialState,
                   Simulator<S, A> world,
                   List<Simulator<S, A>> simulators,
                   List<Agent> agents) {
        this(new History<S, A>(initialState), world, simulators, agents);
    }

    public Arbiter(History<S, A> history,
                   Simulator<S, A> world,
                   List<Simulator<S, A>> simulators,
                   List<Agent> agents) {
        if (world.getNAgents() != agents.size()) {
            throw new IllegalArgumentException("Expects " + world.getNAgents() +
                                               " agents but " + agents.size() + " provided.");
        }
        if (simulators == null) {
            simulators_ = new ArrayList<>();
            for (int i = 0; i < agents.size(); i += 1) {
                simulators_.add(world.copy());
            }
        } else if (agents.size() == simulators.size()) {
            for (Simulator<S, A> simulator: simulators) {
                simulators_.add(simulator);
            }
        } else {
            throw new IllegalArgumentException("Required one simulator per agent.");
        }
        history_ = history;
        historyIndex_ = history.getSize() - 1;
        world_ = world.copy();
        world_.setState(history.getState(historyIndex_));
        for (Agent agent: agents) {
            agents_.add(agent);
        }
        decisionTimes_ = new long[world.getNAgents()];
        executor_ = Executors.newFixedThreadPool(world.getNAgents());
    }

    /**
     * Reset the arbiter the initial
     * state so that another game may be played.
     */
    public void reset() {
        reset(history_.getState(0));
    }

    /**
     * Reset the arbiter to a new initial
     * state so that another game may be played.
     */
    public synchronized void reset(S state) {
        world_.setState(state);
        history_ = new History<>(world_.getState());
        historyIndex_ = 0;
    }

    /**
     * Take a single state transition in domain.
     * If the agent has no legal actions to take
     * from a given state then that agent is only
     * allowed to return a null action. This method
     * skips an agents select action method if the
     * only action available is null.
     */
    public synchronized void step() throws InterruptedException {
        if (!world_.isTerminalState()) {
            Vector<A> actions = new Vector<>();
            actions.setSize(world_.getNAgents());
            actionsReady_ = new CountDownLatch(world_.getNAgents());
            try {
                for (int i = 0; i < world_.getNAgents(); i += 1) {
                    executor_.execute(new AgentAction(i, actions, decisionTimes_));
                }
                actionsReady_.await();
            } catch (InterruptedException e) {
                executor_.shutdown();
                throw new InterruptedException();
            }
            world_.stateTransition(actions);
            history_.add(world_.getState(), actions, historyIndex_);
            historyIndex_ += 1;
        }
        listener_.onStep();
    }

    public void done() {
        executor_.shutdown();
    }

    /**
     * Move back one state in history.
     */
    public synchronized void prevHistory() {
        if (historyIndex_ > 0) {
            historyIndex_ -= 1;
            world_.setState(history_.getState(historyIndex_));
        }
    }

    /**
     * Move forward one state in history.
     */
    public synchronized void nextHistory() {
        if (historyIndex_ < history_.getSize() - 1) {
            historyIndex_ += 1;
            world_.setState(history_.getState(historyIndex_));
        }
    }

    public Simulator<S, A> getWorld() {
        return world_;
    }

    public List<Agent> getAgents() {
        return agents_;
    }

    public History<S, A> getHistory() {
        return history_;
    }

    public long getDecisionTime(int agentId) {
        return decisionTimes_[agentId];
    }

    public long getReward(int agentId) {
        return world_.getReward(agentId);
    }

    public void setOnStateChangeListener(OnEventListener listener) {
        listener_ = (listener != null) ? listener : new DummyOnEventListener();
    }
}
