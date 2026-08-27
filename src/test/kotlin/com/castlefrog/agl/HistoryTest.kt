package com.castlefrog.agl

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test

internal class HistoryTest {

    @Test
    fun newHistoryIsEmpty() {
        val history = History<TestAction>()

        assertTrue(history.nodes.isEmpty())
        assertEquals(emptyList<History.Node<TestAction>>(), history.nodes)
    }

    @Test
    fun addAppendsActionNodes() {
        val history = History<TestAction>()
        val actions1 = listOf(TestAction(1), null)
        val actions2 = listOf(null, TestAction(2))

        history.add(actions1)
        history.add(actions2)

        assertEquals(
            listOf(
                History.Node(actions = actions1),
                History.Node(actions = actions2)
            ),
            history.nodes
        )
    }

    @Test
    fun addDefensivelyCopiesActionsList() {
        val history = History<TestAction>()
        val mutableActions = mutableListOf<TestAction?>(TestAction(2), null)
        history.add(mutableActions)

        mutableActions[0] = TestAction(1)

        assertEquals(listOf(TestAction(2), null), history.nodes[0].actions)
        assertNotSame(mutableActions, history.nodes[0].actions)
    }

    @Test
    fun recordedActionsReplayOnDeterministicSimulator() {
        val simulator = TestSimulator(
            initialState = TestState(0),
            legalActions = listOf(setOf(TestAction(1)), setOf(TestAction(2))),
            rewards = intArrayOf(0, 0),
            testStateTransition = { state, actions ->
                val applied = actions.filterNotNull().single()
                TestState(state.value * 10 + applied.value)
            }
        )
        val history = History<TestAction>()

        val actions1 = listOf(TestAction(1), null)
        val s1 = simulator.stateTransition(simulator.initialState, actions1)
        history.add(actions1)

        val actions2 = listOf(null, TestAction(2))
        val s2 = simulator.stateTransition(s1, actions2)
        history.add(actions2)

        // Derive states from the action log alone (deterministic domains).
        var state = simulator.initialState
        for (node in history.nodes) {
            state = simulator.stateTransition(state, node.actions)
        }
        assertEquals(s2, state)

        var afterFirst = simulator.initialState
        afterFirst = simulator.stateTransition(afterFirst, history.nodes[0].actions)
        assertEquals(s1, afterFirst)
    }
}
