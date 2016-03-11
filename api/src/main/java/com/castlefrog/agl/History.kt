package com.castlefrog.agl

import java.io.Serializable
import java.util.ArrayList

/**
 * Keeps track of state transition history.
 */
class History<S : State<S>, A : Action>(initialState: S) : Serializable {

    private val nodes: MutableList<Node<S, A>>

    data class Node<S, A>(val state: S, val actions: List<A>) : Serializable

    init {
        nodes = ArrayList<Node<S, A>>()
        add(initialState, ArrayList<A>())
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
        nodes.add(Node(state, actions))
    }

    fun add(state: S, actions: List<A>, index: Int) {
        while (index < nodes.size - 1) {
            removeLast()
        }
        nodes.add(Node(state, actions))
    }

    fun removeLast() {
        if (nodes.size > 0) {
            nodes.removeAt(nodes.size - 1)
        }
    }

    fun getState(index: Int): S {
        return nodes[index].state
    }

    fun getActions(index: Int): List<A> {
        return nodes[index].actions
    }

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
