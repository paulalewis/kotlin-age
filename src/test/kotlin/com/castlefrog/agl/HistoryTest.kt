package com.castlefrog.agl

import com.castlefrog.agl.domains.hex.HexAction
import com.castlefrog.agl.domains.hex.HexSimulator
import com.castlefrog.agl.domains.hex.HexState
import org.junit.Assert.assertEquals
import org.junit.Test

internal class HistoryTest {

    @Test
    fun initialHexState() {
        val simulator = HexSimulator(boardSize = 5)
        val history = History.create<HexState, HexAction>(simulator.initialState)
        assertEquals(listOf(History.Node<HexState, HexAction>(state = HexState(boardSize = 5), actions = emptyMap())), history.nodes)
    }

    @Test
    fun addHexStateAndActions() {
        val simulator = HexSimulator(boardSize = 5)
        val history = History.create<HexState, HexAction>(simulator.initialState)
        val nextActions = mapOf(0 to HexAction(2, 2))
        val nextState = simulator.stateTransition(
            simulator.initialState,
            listOf(HexAction(2, 2), null)
        )
        history.add(nextState, nextActions)
        assertEquals(listOf(
                    History.Node(state = HexState(boardSize = 5), actions = emptyMap()),
                    History.Node(state = nextState, actions = nextActions)
                ), history.nodes)
    }

}