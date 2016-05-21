package com.castlefrog.agl

/**
 * An agent interacts in a domain by selecting
 * an action from a list of legal actions from
 * the current state.
 */
interface Agent {
    /**
     * An agent selects an action given a state and simulator.
     * @param agentId the id of the agent that this is selecting the action
     * @param state current domain state.
     * @param simulator simulator that determines action outcomes in domain.
     * @return selected action from the current state.
     */
    fun <S : State<S>, A : Action<A>> selectAction(agentId: Int, state: S, simulator: Simulator<S, A>): A
}
