package com.castlefrog.agl

import com.castlefrog.agl.domains.hex.HexAction
import com.castlefrog.agl.domains.hex.HexSimulator
import com.castlefrog.agl.domains.hex.HexState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

internal class HistoryTest {

    @Test
    fun createStoresDeepCopyOfInitialState() {
        val simulator = HexSimulator(boardSize = 5)
        val initial = simulator.initialState
        val history = History.create<HexState, HexAction>(initial)

        assertEquals(
            listOf(History.Node<HexState, HexAction>(state = HexState(boardSize = 5), actions = emptyList())),
            history.nodes
        )

        // Mutating the caller's state must not affect the root snapshot.
        initial.setLocation(0, 0, HexState.LOCATION_BLACK)
        assertEquals(HexState(boardSize = 5), history.nodes[0].state)
        assertNotEquals(initial, history.nodes[0].state)
    }

    @Test
    fun addStoresDeepCopyAndListActions() {
        val simulator = HexSimulator(boardSize = 5)
        val history = History.create<HexState, HexAction>(simulator.initialState)
        val nextActions = listOf(HexAction(2, 2), null)
        val nextState = simulator.stateTransition(
            simulator.initialState,
            nextActions
        )
        history.add(nextState, nextActions)

        assertEquals(
            listOf(
                History.Node<HexState, HexAction>(state = HexState(boardSize = 5), actions = emptyList()),
                History.Node(state = nextState, actions = nextActions)
            ),
            history.nodes
        )
        assertNotSame(nextState, history.nodes[1].state)
    }

    @Test
    fun historyIsolatedFromLaterStateTransitionAndMutation() {
        val simulator = HexSimulator(boardSize = 5)
        val s0 = simulator.initialState
        val history = History.create<HexState, HexAction>(s0)

        val actions1 = listOf(HexAction(0, 0), null)
        val s1 = simulator.stateTransition(s0, actions1)
        history.add(s1, actions1)

        val rootSnapshot = history.nodes[0].state.copy()
        val firstSnapshot = history.nodes[1].state.copy()

        // Further transition from s1 must not change history (simulators do not
        // mutate input; history also holds its own deep copies).
        val actions2 = listOf(null, HexAction(1, 1))
        val s2 = simulator.stateTransition(s1, actions2)
        history.add(s2, actions2)

        assertEquals(rootSnapshot, history.nodes[0].state)
        assertEquals(firstSnapshot, history.nodes[1].state)

        // In-place mutation of a state that was passed to add must not corrupt history.
        s1.setLocation(2, 2, HexState.LOCATION_BLACK)
        assertEquals(firstSnapshot, history.nodes[1].state)
        assertNotEquals(s1, history.nodes[1].state)
    }

    @Test
    fun addDefensivelyCopiesActionsList() {
        val simulator = HexSimulator(boardSize = 5)
        val history = History.create<HexState, HexAction>(simulator.initialState)
        val mutableActions = mutableListOf<HexAction?>(HexAction(1, 1), null)
        val nextState = simulator.stateTransition(simulator.initialState, mutableActions)
        history.add(nextState, mutableActions)

        mutableActions[0] = HexAction(0, 0)
        assertEquals(listOf(HexAction(1, 1), null), history.nodes[1].actions)
    }
}
