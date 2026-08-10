package com.castlefrog.agl

import com.castlefrog.agl.domains.hex.HexAction
import com.castlefrog.agl.domains.hex.HexSimulator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test

internal class HistoryTest {

    @Test
    fun newHistoryIsEmpty() {
        val history = History<HexAction>()

        assertTrue(history.nodes.isEmpty())
        assertEquals(emptyList<History.Node<HexAction>>(), history.nodes)
    }

    @Test
    fun addAppendsActionNodes() {
        val history = History<HexAction>()
        val actions1 = listOf(HexAction(0, 0), null)
        val actions2 = listOf(null, HexAction(1, 1))

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
        val history = History<HexAction>()
        val mutableActions = mutableListOf<HexAction?>(HexAction(1, 1), null)
        history.add(mutableActions)

        mutableActions[0] = HexAction(0, 0)

        assertEquals(listOf(HexAction(1, 1), null), history.nodes[0].actions)
        assertNotSame(mutableActions, history.nodes[0].actions)
    }

    @Test
    fun recordedActionsReplayOnDeterministicSimulator() {
        val simulator = HexSimulator(boardSize = 5)
        val history = History<HexAction>()

        val actions1 = listOf(HexAction(0, 0), null)
        val s1 = simulator.stateTransition(simulator.initialState, actions1)
        history.add(actions1)

        val actions2 = listOf(null, HexAction(1, 1))
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
