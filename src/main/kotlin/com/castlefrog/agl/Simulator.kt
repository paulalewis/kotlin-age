package com.castlefrog.agl

import java.util.ArrayList

/**
 * A simulator controls the state transitions of a given domain.
 * A simulator is associated with a domain specific state and action
 * type.
 */
interface Simulator<S : State<S>, A : Action<A>> : Copyable<Simulator<S, A>> {

    /**
     * Current state of the simulator.
     */
    var state: S

    /**
     * List of legal actions for each agent.
     */
    val legalActions: List<MutableList<A>>

    /**
     * Rewards for each agent that may be indexed by that agent's id.
     */
    val rewards: IntArray

    /**
     * The number of agents in the given domain.
     */
    val nAgents: Int

    /**
     * A simulator takes a list of actions to transition from
     * one state to another.
     * @param actions list of actions to be performed by each agent.
     */
    fun stateTransition(actions: List<A?>)

    /**
     * A state is terminal if no agent has any
     * legal actions from the current state.
     */
    val isTerminalState: Boolean
        get() {
            for (i in 0..legalActions.size - 1) {
                if (legalActions[i].size != 0) {
                    return false
                }
            }
            return true
        }

    fun <A : Action<A>> List<MutableList<A>>.copy(): List<MutableList<A>> {
        val legalActions = ArrayList<MutableList<A>>()
        for (agentActions in this) {
            val tempActions = ArrayList<A>()
            for (action in agentActions) {
                tempActions.add(action.copy())
            }
            legalActions.add(tempActions)
        }
        return legalActions
    }

}
