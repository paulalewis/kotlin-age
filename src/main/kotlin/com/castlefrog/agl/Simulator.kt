package com.castlefrog.agl

/**
 * A simulator controls the state transitions of a given domain.
 * A simulator is associated with a domain specific state and action
 * type.
 */
interface Simulator<S : State<S>, A : Action<A>> {

    /**
     * @return the number of players in the domain
     */
    val nPlayers: Int

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
    fun calculateLegalActions(state: S): List<List<A>>

    /**
     * Transition from the current state to the next state
     * given a set of player actions.
     * @param actions map of actions to be performed by each player
     */
    fun stateTransition(state: S, actions: Map<Int, A>): S

    /**
     * A state is terminal if no player has any
     * legal actions from the current state.
     * @param state check if this state is terminal
     * @return true if no player has any legal actions
     *         from the state
     */
    fun isTerminalState(state: S): Boolean {
        val legalActions = calculateLegalActions(state)
        return (0..legalActions.size - 1).all { legalActions[it].isEmpty() }
    }

}
