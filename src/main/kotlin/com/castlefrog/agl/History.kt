package com.castlefrog.agl

import java.util.LinkedList

/**
 * Keeps track of state transition history.
 */
class History<S : State<S>, A : Action<A>>(initialState: S) {

    private val nodes: MutableList<Node<S, A>> = LinkedList()

    var index: Int = 0
        private set

    data class Node<out S, out A>(val state: S, val actions: List<A>)

    init {
        add(initialState, emptyList())
    }

    fun clear() {
        val initialState = nodes[0].state
        nodes.clear()
        add(initialState, emptyList())
        index = 0
    }

    /**
     * Add the next state and the actions taken by each agent
     * to arrive at that state.
     * @param state
     * *      the current state
     * *
     * @param actions
     * *      the actions taken by each agent to end up in the current state
     */
    fun add(state: S, actions: List<A>) {
        while (index < nodes.size - 1) {
            removeLast()
        }
        nodes.add(Node(state, actions))
    }

    private fun removeLast() {
        if (hasPrevState()) {
            nodes.removeAt(nodes.size - 1)
        }
    }

    fun hasPrevState(): Boolean {
        return index > 0
    }

    fun hasNextState(): Boolean {
        return index < nodes.size - 1
    }

    fun nextState() {
        if (hasNextState()) {
            index++
        }
    }

    fun prevState() {
        if (hasPrevState()) {
            index--
        }
    }

    val state: S
        get() = nodes[index].state

    val actions: List<A>
        get() = nodes[index].actions

    val size: Int
        get() = nodes.size

    override fun toString(): String {
        val output = StringBuilder()
        for (node in nodes) {
            output.append(node.actions).append("\n\n")
            output.append(node.state).append("\n\n")
        }
        return output.toString()
    }
}
