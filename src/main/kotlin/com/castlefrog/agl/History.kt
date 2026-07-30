package com.castlefrog.agl

/**
 * Keeps track of state transition history.
 *
 * ## Ownership
 * History owns **deep-copied** state snapshots. [create] and [add] copy each
 * state via [State.copy], so later mutation of the caller's state (or of states
 * returned from [Simulator.stateTransition]) does not alter history entries.
 * Callers must not rely on sharing mutable references with history nodes.
 *
 * ## Actions
 * Each node stores actions in the same shape as [Simulator.stateTransition]:
 * a [List] of length equal to the number of players (or empty for the root),
 * with `null` meaning that player took no action.
 */
class History<S : State<S>, A : Action> private constructor(
    private val nodeList: MutableList<Node<S, A>>
) {

    /** Read-only view of history nodes in chronological order (root first). */
    val nodes: List<Node<S, A>>
        get() = nodeList

    companion object {
        /**
         * Creates a history whose root is a deep copy of [initialState] with
         * no actions (empty list).
         */
        fun <S : State<S>, A : Action> create(initialState: S): History<S, A> {
            return History(mutableListOf(Node(initialState.copy(), emptyList())))
        }
    }

    /**
     * One step in the trajectory: the state after applying [actions], or the
     * root state when [actions] is empty.
     *
     * @param state snapshot of the domain state at this step (owned by history)
     * @param actions actions taken by each player to reach this state from the
     *        previous node; empty for the root
     */
    data class Node<out S, out A>(val state: S, val actions: List<A?>)

    /**
     * Appends a deep copy of [state] and a defensive copy of [actions].
     *
     * @param state the state reached after applying [actions]
     * @param actions actions taken by each player to reach [state], same shape
     *        as [Simulator.stateTransition] (`null` = no action for that player)
     */
    fun add(state: S, actions: List<A?>) {
        nodeList.add(Node(state.copy(), actions.toList()))
    }
}
