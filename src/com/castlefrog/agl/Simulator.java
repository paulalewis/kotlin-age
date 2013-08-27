package com.castlefrog.agl;

import java.util.List;

/**
 * A simulator controls the state transitions of a given domain.
 * A simulator is associated with a domain specific state and action
 * type. It also generates initial states for the given domain.
 */
public interface Simulator<S extends State, A extends Action> extends Copyable<Simulator<S, A>> {
    /**
     * The simulator can create a deep copy of itself.
     * @return a copy of this simulator
     */
    Simulator<S, A> copy();

    /**
     * Sets simulator to an arbitrary state.
     * @param state any legal state.
     */
    void setState(S state);

    /**
     * A simulator takes a list of actions to transition from
     * one state to another. If an agent cannot perform an action
     * then it passes a null action.
     * @param actions list of actions to be performed by all agents.
     */
    void stateTransition(List<A> actions);

    /**
     * Generates and returns a legal initial state.
     * @return an initial state for the domain.
     */
    S getInitialState();

    /**
     * Rewards for each agent may be indexed by that agent's id.
     * @return array of rewards for each agent.
     */
    int[] getRewards();

    /**
     * Get reward for the specified agent.
     * @param agentId the agent to get reward value.
     * @return the reward in current state of single agent.
     */
    int getReward(int agentId);

    /**
     * A state is terminal if no agent has any
     * legal actions from the current state.
     * @return true if current state is terminal.
     */
    boolean isTerminalState();

    /**
     * Current state of the simulator.
     * @return current state.
     */
    S getState();

    /**
     * This method returns a list of legal actions for each agent.
     * If an agent has no legal actions then an empty list is added
     * to the main list.
     * @return a list of legal actions for each agent from the current state.
     */
    List<List<A>> getLegalActions();

    /**
     * This method returns a list of legal actions for the given agent.
     * @param agentId agent ID associated with list of legal actions.
     * @return a list of legal actions from current state.
     */
    List<A> getLegalActions(int agentId);

    /**
     * Returns true if the given agent has 1
     * or more legal actions.
     * @param agentId agent ID associated with list of legal actions.
     * @return true if there are 1 or more legal actions from current state.
     */
    boolean hasLegalActions(int agentId);

    /**
     * Gets the number of agents in the given domain.
     * @return number of agents.
     */
    int getNAgents();

    /**
     * Get the turn type of this domain.
     */
    TurnType getTurnType();

    //This should be a separate interface
    //public abstract double[] getFeatureVector(A action);
}
