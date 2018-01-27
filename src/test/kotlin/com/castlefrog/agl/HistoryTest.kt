package com.castlefrog.agl

import org.assertj.core.api.Assertions.assertThat
import com.castlefrog.agl.domains.hex.HexAction
import com.castlefrog.agl.domains.hex.HexSimulator
import com.castlefrog.agl.domains.hex.HexState
import org.junit.jupiter.api.Test

internal class HistoryTest {

    @Test
    fun initialHexState() {
        val simulator = HexSimulator(boardSize = 5)
        val history = History.create<HexState, HexAction>(simulator.initialState)
        assertThat(history.nodes)
                .isEqualTo(listOf(History.Node<HexState, HexAction>(state = HexState(boardSize = 5), actions = emptyMap())))
    }

    @Test
    fun addHexStateAndActions() {
        val simulator = HexSimulator(boardSize = 5)
        val history = History.create<HexState, HexAction>(simulator.initialState)
        val nextActions = mapOf(Pair(0, HexAction.valueOf(2, 2)))
        val nextState = simulator.stateTransition(simulator.initialState, nextActions)
        history.add(nextState, nextActions)
        assertThat(history.nodes)
                .isEqualTo(listOf(
                        History.Node(state = HexState(boardSize = 5), actions = emptyMap()),
                        History.Node(state = nextState, actions = nextActions)
                        ))
    }

}