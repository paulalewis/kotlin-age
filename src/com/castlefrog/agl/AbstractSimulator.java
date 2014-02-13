package com.castlefrog.agl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractSimulator<S extends State<S>, A extends Action> implements Simulator<S, A> {
    protected S state_;
    protected List<List<A>> legalActions_;
    protected int[] rewards_;

    protected AbstractSimulator() {}

    protected AbstractSimulator(AbstractSimulator<S, A> simulator) {
        state_ = simulator.getState();
        legalActions_ = simulator.getLegalActions();
        rewards_ = simulator.getRewards();
    }

    /**
     * Rewards for each agent may be indexed by that agent's id.
     * @return array of rewards for each agent.
     */
    public final int[] getRewards() {
        return Arrays.copyOf(rewards_, rewards_.length);
    }

    /**
     * Get reward for the specified agent.
     * @param agentId the agent to get reward value.
     * @return the reward in current state of single agent.
     */
    public final int getReward(int agentId) {
        return rewards_[agentId];
    }

    /**
     * A state is terminal if no agent has any
     * legal actions from the current state.
     * @return true if current state is terminal.
     */
    public final boolean isTerminalState() {
        for (int i = 0; i < getNAgents(); i += 1) {
            if (hasLegalActions(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Current state of the simulator.
     * @return current state.
     */
    public final S getState() {
        return state_.copy();
    }

    /**
     * This method returns a list of legal actions
     * for each agent.
     *
     * @return
     *      a list of legal actions for each agent
     *      from the current state.
     */
    public final List<List<A>> getLegalActions() {
        List<List<A>> allLegalActions = new ArrayList<>();
        for (int i = 0; i < getNAgents(); i += 1) {
            allLegalActions.add(getLegalActions(i));
        }
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
    public final List<A> getLegalActions(int agentId) {
        List<A> legalActions = new ArrayList<>();
        for (A action: legalActions_.get(agentId)) {
            legalActions.add(action);
        }
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
    public final boolean hasLegalActions(int agentId) {
        return legalActions_.get(agentId).size() != 0;
    }

    protected final void clearLegalActions() {
        for (List<A> legalActions: legalActions_) {
            legalActions.clear();
        }
    }
}
