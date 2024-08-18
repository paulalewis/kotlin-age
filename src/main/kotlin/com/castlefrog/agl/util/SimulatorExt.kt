package com.castlefrog.agl.util

import com.castlefrog.agl.Action
import com.castlefrog.agl.Simulator
import com.castlefrog.agl.State

/**
 * A state is terminal if no player has any
 * legal actions from the current state.
 * @param state check if this state is terminal
 * @return true if no player has any legal actions
 *         from the state
 */
fun <S: State<S>, A : Action<A>> Simulator<S, A>.isTerminalState(state: S): Boolean {
    val legalActions = calculateLegalActions(state)
    return (legalActions.indices).all { legalActions[it].isEmpty() }
}
