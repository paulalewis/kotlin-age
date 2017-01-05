package com.castlefrog.agl

import java.util.LinkedList

/**
 * Keeps track of state transition history.
 */
data class History<S : State<S>, A : Action<A>>(val nodes: MutableList<Node<S, A>>) {

    companion object {
        fun <S : State<S>, A : Action<A>> create(initialState: S): History<S, A> {
            val nodes = LinkedList<Node<S, A>>()
            nodes.add(Node(initialState, emptyMap()))
            return History(nodes)
        }
    }

    data class Node<out S, out A>(val state: S, val actions: Map<Int, A>)

    /**
     * Add the next state and the actions taken by each agent
     * to arrive at that state.
     * @param state
     * *      the current state
     * *
     * @param actions
     * *      the actions taken by each player to end up in the current state
     */
    fun add(state: S, actions: Map<Int, A>) {
        nodes.add(Node(state, actions))
    }

    override fun toString(): String {
        val output = StringBuilder()
        for (node in nodes) {
            output.append(node.actions).append("\n\n")
            output.append(node.state).append("\n\n")
        }
        return output.toString()
    }
}
