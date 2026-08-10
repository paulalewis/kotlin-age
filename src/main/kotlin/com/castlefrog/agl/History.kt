package com.castlefrog.agl

/**
 * Keeps track of state transition history.
 *
 * ## Actions
 * Each node stores actions in the same shape as [Simulator.stateTransition]:
 * a [List] of length equal to the number of players (or empty for the root),
 * with `null` meaning that player took no action.
 */
class History<A : Action> {
    private val nodeList: MutableList<Node<A>> = mutableListOf()

    /** Read-only view of history nodes in chronological order (root first). */
    val nodes: List<Node<A>>
        get() = nodeList

    /**
     * One step in the trajectory: the state after applying [actions], or the
     * root state when [actions] is empty.
     *
     * @param actions actions taken by each player to reach this state from the
     *        previous node; empty for the root
     */
    data class Node<out A>(val actions: List<A?>)

    /**
     * Appends a defensive copy of [actions].
     *
     * @param actions actions taken by each player to reach this state from the
     *        previous node
     */
    fun add(actions: List<A?>) {
        nodeList.add(Node(actions.toList()))
    }
}
