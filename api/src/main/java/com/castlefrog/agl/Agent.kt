package com.castlefrog.agl

/**
 * An agent interacts in a domain by selecting
 * and action from a list of legal actions from
 * the current state. An agent should override
 * toString(). The toString() method should return
 * the agent name and any parameters passed when
 * creating that agent.
 */
interface Agent {
    /**
     * An agent selects an action given a state and simulator.
     * @param agentId the id of the agent that this is selecting the action
     * *
     * @param state current domain state.
     * *
     * @param simulator simulator that determines action outcomes in domain.
     * *
     * @return selected action from the current state.
     */
    fun <S : State<S>, A : Action> selectAction(agentId: Int, state: S, simulator: Simulator<S, A>): A
}
