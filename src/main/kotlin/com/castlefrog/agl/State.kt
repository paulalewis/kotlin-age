package com.castlefrog.agl

/**
 * Domain-specific game state.
 *
 * Implementations should provide a deep [copy] so that nested mutable structure
 * (boards, dice, scores) is not shared between the original and the copy.
 * [Simulator.stateTransition] relies on this to leave its input state unchanged.
 */
interface State<out T : State<T>> {
    /**
     * Returns a deep copy of this state. Mutating the result must not affect
     * this instance.
     */
    fun copy(): T
}
