package com.castlefrog.agl

import arrow.core.None
import arrow.core.Some
import com.castlefrog.agl.domains.hex.HexAction
import com.castlefrog.agl.domains.hex.HexSimulator
import com.castlefrog.agl.domains.hex.HexState
import com.google.common.truth.Truth.assertThat
import org.junit.Test

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
        val nextActions = mapOf(0 to HexAction(2, 2))
        val nextState = simulator.stateTransition(
            simulator.initialState,
            listOf(Some(HexAction(2, 2)), None)
        )
        history.add(nextState, nextActions)
        assertThat(history.nodes)
            .isEqualTo(
                listOf(
                    History.Node(state = HexState(boardSize = 5), actions = emptyMap()),
                    History.Node(state = nextState, actions = nextActions)
                )
            )
    }

}