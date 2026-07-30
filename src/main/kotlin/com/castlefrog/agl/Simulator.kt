package com.castlefrog.agl

/**
 * A simulator controls the state transitions of a given domain.
 * A simulator is associated with a domain specific state and action
 * type.
 */
interface Simulator<S : State<S>, A : Action> {
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
     * @param state the state from which to calculate legal actions
     * @return list of legal actions for each player
     */
    fun calculateLegalActions(state: S): List<Set<A>>

    /**
     * Transition from the current state to the next state
     * given a set of player actions.
     *
     * @param state the state to transition from (may be mutated in place)
     * @param actions list of actions to be performed by each player;
     *        null entries mean that player takes no action
     * @return the resulting state after applying the actions
     * @throws IllegalActionException if [actions] has the wrong length for the
     *         player to move, the required action is null, or the action is not
     *         legal from [state]
     */
    fun stateTransition(state: S, actions: List<A?>): S

    /**
     * @return the number of players in the domain.
     */
    fun numberOfPlayers(): Int

    /**
     * @param state check if this state is terminal
     * @return true if no player has any legal actions from the state
     */
    fun isTerminalState(state: S): Boolean {
        val legalActions = calculateLegalActions(state)
        return (legalActions.indices).all { legalActions[it].isEmpty() }
    }
}

/**
 * Resolves the action for [agentTurn] from [actions], ensuring the list is long
 * enough and the action is legal.
 *
 * @throws IllegalActionException if the actions list is too short, the action is
 *         null, or it is not in [legalForPlayer]
 */
fun <A : Action> requireLegalAction(
    actions: List<A?>,
    agentTurn: Int,
    legalForPlayer: Set<A>,
    state: Any
): A {
    if (agentTurn !in actions.indices) {
        throw IllegalActionException(
            "Illegal actions size, ${actions.size}, expected at least ${agentTurn + 1} for state, $state"
        )
    }
    val action = actions[agentTurn]
    if (action === null || action !in legalForPlayer) {
        throw IllegalActionException("Illegal action, $action, from state, $state")
    }
    return action
}
