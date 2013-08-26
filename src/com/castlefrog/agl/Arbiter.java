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
public final class Arbiter<S extends State, A extends Action> {
    /** The actual domain being used */
    private Simulator<S, A> world;
    /** Simulator to use for each agent */
    private List<Simulator<S,A>> simulators = new ArrayList<Simulator<S,A>>();
    private History<S, A> history;
    private int historyIndex;
    private List<Agent> agents = new ArrayList<Agent>();
    private long[] decisionTimes;
    private ExecutorService executor;
    private CountDownLatch actionsReady;
    
    private class AgentAction implements Runnable {
        private int agentId;
        private Vector<A> actions;
        private long[] decisionTimes;

        public AgentAction(int agentId,
                           Vector<A> actions,
                           long[] decisionTimes) {
            this.agentId = agentId;
            this.actions = actions;
            this.decisionTimes = decisionTimes;
        }

        public void run() {
            if (world.hasLegalActions(agentId)) {
                long startTime = System.currentTimeMillis();
                S state = world.getState();
                A action = agents.get(agentId).selectAction(agentId, state, simulators.get(agentId));
                actions.set(agentId, action);
                decisionTimes[agentId] = System.currentTimeMillis() - startTime;
            }
            actionsReady.countDown();
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
        if (world.getNAgents() != agents.size())
            throw new IllegalArgumentException("Expects " + world.getNAgents() + 
                                               " agents but " + agents.size() + " provided.");
        if (simulators == null) {
            this.simulators = new ArrayList<Simulator<S, A>>();
            for (int i = 0; i < agents.size(); i += 1)
                this.simulators.add(world.copy());
        } else if (agents.size() == simulators.size()) {
            for (Simulator<S, A> simulator: simulators)
	            this.simulators.add(simulator);
        } else {
        	throw new IllegalArgumentException("Required one simulator per agent.");
        }
        this.history = history;
        historyIndex = history.getSize() - 1;
        this.world = world.copy();
        this.world.setState(history.getState(historyIndex));
        for (Agent agent: agents)
            this.agents.add(agent);
        decisionTimes = new long[world.getNAgents()];
        executor = Executors.newFixedThreadPool(world.getNAgents());
    }
    
    /**
     * Reset the arbiter to a new initial
     * state so that another game may be played.
     */
    public void reset() {
        reset(world.getInitialState());
    }

    /**
     * Reset the arbiter to a new initial
     * state so that another game may be played.
     */
    public synchronized void reset(S state) {
        world.setState(state);
        history = new History<S, A>(world.getState());
        historyIndex = 0;
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
        if (!world.isTerminalState()) {
            Vector<A> actions = new Vector<A>();
            actions.setSize(world.getNAgents());
            actionsReady = new CountDownLatch(world.getNAgents());
            try {
                for (int i = 0; i < world.getNAgents(); i += 1)
                    executor.execute(new AgentAction(i, actions, decisionTimes));
                actionsReady.await();
            } catch (InterruptedException e) {
                executor.shutdown();
                throw new InterruptedException();
            }
            world.stateTransition(actions);
            history.add(world.getState(), actions, historyIndex);
            historyIndex += 1;
        }
    }

    public void done() {
        executor.shutdown();
    }

    /**
     * Move back one state in history.
     */
    public synchronized void prevHistory() {
        if (historyIndex > 0) {
            historyIndex -= 1;
            world.setState(history.getState(historyIndex));
        }
    }

    /**
     * Move forward one state in history.
     */
    public synchronized void nextHistory() {
        if (historyIndex < history.getSize() - 1) {
            historyIndex += 1;
            world.setState(history.getState(historyIndex));
        }
    }

    public boolean isTerminalState() {
        return world.isTerminalState();
    }
    
    public Simulator<S, A> getWorld() {
    	return world;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public History<S, A> getHistory() {
        return history;
    }
    
    public long getDecisionTime(int agentId) {
        return decisionTimes[agentId];
    }

    public long getReward(int agentId) {
        return world.getReward(agentId);
    }
}
