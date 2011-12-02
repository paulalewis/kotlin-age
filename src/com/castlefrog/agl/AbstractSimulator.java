package com.castlefrog.agl;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * A simulator controls the state transitions of a given domain.
 * A simulator is associated with a domain specific state and action
 * type. It also generates initial states for the given domain.
 */
public abstract class AbstractSimulator<S, A> implements Simulator<S, A> {
    /** number of agents in domain */
    protected static int nAgents_;
    /** type of turn order in domain */
    protected static TurnType turnType_;

    protected S state_;
    protected List<HashSet<A>> legalActions_;
    protected int[] rewards_;

    /**
     * The simulator can create a deep copy of itself.
     * @return a copy of this simulator
     */
    public abstract Simulator<S, A> clone();

    /**
     * Sets simulator to an arbitrary state.
     * @param state any legal state.
     */
    //public void setState(S state) {
    //    state_ = state.clone();
    //}
    
    /**
     * Rewards for each agent may be indexed by that agent's id.
     * @return array of rewards for each agent.
     */
    public int[] getRewards() {
        int[] rewards = new int[rewards_.length];
        for (int i = 0; i < rewards_.length; i += 1)
            rewards[i] = rewards_[i];
        return rewards;
    }

    /**
     * Get reward for the specified agent.
     * @param agentId the agent to get reward value.
     * @return the reward in current state of single agent.
     */
    public int getReward(int agentId) {
        return rewards_[agentId];
    }

    /**
     * A state is terminal if no agent has any
     * legal actions from the current state.
     * @return true if current state is terminal.
     */
    public boolean isTerminalState() {
        for (int i = 0; i < nAgents_; i += 1)
            if (hasLegalActions(i))
                return false;
        return true;
    }

    /**
     * Current state of the simulator.
     * @return current state.
     */
    //public S getState() {
    //    return state_.clone();
    //}
    
    /**
     * This method returns a list of legal actions
     * for each agent.
     *
     * @return
     *      a list of legal actions for each agent
     *      from the current state.
     */
    public List<List<A>> getLegalActions() {
        List<List<A>> allLegalActions = new ArrayList<List<A>>();
        for (int i = 0; i < nAgents_; i += 1)
            allLegalActions.add(getLegalActions(i));
        return allLegalActions;
    }

    /**
     * This method returns a list of legal actions
     * for the given agent.
     *
     * @param agentId
     *      agent ID associated with list of legal actions.
     * @return
     *      a list of legal actions from current state.
     */
    public List<A> getLegalActions(int agentId) {
        List<A> legalActions = new ArrayList<A>();
        for (A action: legalActions_.get(agentId))
            legalActions.add(action);
        return legalActions;
    }
    
    /**
     * Returns true if the given agent has 1
     * or more legal actions.
     *
     * @param agentId
     *      agent ID associated with list of legal actions.
     * @return
     *      true if agent has 1 or more legal actions
     *      from current state, otherwise false.
     */
    public boolean hasLegalActions(int agentId) {
        return legalActions_.get(agentId).size() != 0;
    }

    /**
     * Gets the number of agents in the given domain.
     *
     * @return
     *      number of agents.
     */
    public int getNAgents() {
        assert nAgents_ > 0;
        return nAgents_;
    }

    /**
     * Get the turn type of this domain.
     *
     * @return
     *      turn type object.
     */
    public TurnType getTurnType() {
        return turnType_;
    }
}
