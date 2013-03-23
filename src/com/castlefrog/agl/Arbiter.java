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
 * simultaneous domains. Otherwise agents don't think
 * during the opponent's turn.
 * It records history and other game data.
 */
public final class Arbiter<S, A> {
    /** The actual domain being used */
    private Simulator<S, A> world_;
    /** List of simulators to use for each agent */
    //private List<Simulator<S,A>> simulators_;
    /** Information stored about game */
    private History<S, A> history_;
    private List<Agent> agents_;
    private long[] decisionTimes_;
    
    private ExecutorService executor_;
    private CountDownLatch actionsReady_;
    
    private class AgentAction implements Runnable {
        public int agentId_;
        public Vector<A> actions_;
        public long[] decisionTimes_;

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
                A action = agents_.get(agentId_).selectAction(agentId_, state, world_.clone());
                actions_.set(agentId_, action);
                decisionTimes_[agentId_] = System.currentTimeMillis() - startTime;
            }
            actionsReady_.countDown();
        }
    }

    public Arbiter(S initialState,
                   Simulator<S, A> world,
                   List<Agent> agents) {
        if (world.getNAgents() != agents.size())
            throw new IllegalArgumentException("Expects " + world.getNAgents() + 
                                               " agents but " + agents.size() + " provided.");
        world_ = world.clone();
        world_.setState(initialState);
        history_ = new History<S, A>(world_.getState());
        agents_ = new ArrayList<Agent>();
        for (Agent agent: agents)
            agents_.add(agent);
        decisionTimes_ = new long[world.getNAgents()];
    }
    
    public Arbiter(History<S, A> history,
                   Simulator<S, A> world,
                   List<Agent> agents) {
        if (world.getNAgents() != agents.size())
            throw new IllegalArgumentException("Expects " + world.getNAgents() + 
                                               " agents but " + agents.size() + " provided.");
        history_ = history;
        world_ = world.clone();
        world_.setState(history.getState(history.size() - 1));
        agents_ = new ArrayList<Agent>();
        for (Agent agent: agents)
            agents_.add(agent);
        decisionTimes_ = new long[world.getNAgents()];
    }
    
    /**
     * Reset the arbiter to a new initial
     * state so that another game may be played.
     */
    public synchronized void reset() {
        world_.setState(world_.getInitialState());
        history_ = new History<S, A>(world_.getState());
    }

    /**
     * Reset the arbiter to a new initial
     * state so that another game may be played.
     */
    public synchronized void reset(S state) {
        world_.setState(state);
        history_ = new History<S, A>(world_.getState());
    }

    /**
     * Take a single state transition in domain.
     * If the agent has no legal actions to take
     * from a given state then that agent is only
     * allowed to ruturn a null action. This method
     * skips an agents select action method if the
     * only action available is null.
     */
    public synchronized void step() throws InterruptedException {
        if (!world_.isTerminalState()) {
            Vector<A> actions = new Vector<A>();
            actions.setSize(world_.getNAgents());
            actionsReady_ = new CountDownLatch(world_.getNAgents());
            executor_ = Executors.newFixedThreadPool(world_.getNAgents());
            try {
                for (int i = 0; i < world_.getNAgents(); i += 1)
                    executor_.execute(new AgentAction(i, actions, decisionTimes_));
                actionsReady_.await();
            } catch (InterruptedException e) {
                throw new InterruptedException();
            } finally {
                executor_.shutdown();
            }
            world_.stateTransition(actions);
            history_.add(world_.getState(), actions);
        }
    }

    /**
     * undo a move.
     */
    public synchronized void stepBack() {
        if (history_.size() > 1) {
            history_.removeLast();
            world_.setState(history_.getState(history_.size() - 1));
        }
    }

    public synchronized Simulator<S, A> getWorld() {
        return world_.clone();
    }

    public List<Agent> getAgents() {
        List<Agent> agents = new ArrayList<Agent>();
        for (Agent agent: agents_)
            agents.add(agent);
        return agents;
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
}
