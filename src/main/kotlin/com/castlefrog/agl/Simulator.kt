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
     * List of legal actions for each player.
     */
    val legalActions: List<MutableList<A>>

    /**
     * Rewards for each player that may be indexed by that player's id.
     */
    val rewards: IntArray

    /**
     * Transition from the current state to the next state
     * given a set of player actions.
     * @param actions map of actions to be performed by each player
     */
    fun stateTransition(actions: Map<Int, A>)

    /**
     * The number of players in the given domain.
     */
    val nPlayers: Int
        get() = rewards.size

    /**
     * A state is terminal if no player has any
     * legal actions from the current state.
     */
    val terminalState: Boolean
        get() = (0..legalActions.size - 1).all { legalActions[it].isEmpty() }

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
