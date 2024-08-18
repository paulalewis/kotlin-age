package com.castlefrog.agl

import arrow.core.Option

/**
 * A simulator controls the state transitions of a given domain.
 * A simulator is associated with a domain specific state and action
 * type.
 */
interface Simulator<S : State<S>, A : Action<A>> {
    /**
     * @return an initial state in the domain
     */
    val initialState: S

    /**
     * @param state the state from which to calculate rewards
     * @return list of rewards for each player
     */
    fun calculateRewards(state: S): IntArray

    /**
     * @param state the state from which to calculate rewards
     * @return list of legal actions for each player
     */
    fun calculateLegalActions(state: S): List<Set<A>>

    /**
     * Transition from the current state to the next state
     * given a set of player actions.
     * @param actions map of actions to be performed by each player
     */
    fun stateTransition(state: S, actions: List<Option<A>>): S

    /**
     * @return the number of players in the domain.
     */
    fun numberOfPlayers(): Int
}
