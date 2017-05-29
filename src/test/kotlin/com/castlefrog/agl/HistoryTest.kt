package com.castlefrog.agl

import com.castlefrog.agl.domains.hex.HexAction
import com.castlefrog.agl.domains.hex.HexSimulator
import com.castlefrog.agl.domains.hex.HexState
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class HistoryTest {

    @Test
    fun initialHexState() {
        val simulator = HexSimulator(boardSize = 5)
        val history = History.create<HexState, HexAction>(simulator.initialState)
        Assertions.assertEquals(history.toString(), """
        |History(nodes=[Node(state=- - - - -
        | - - - - -
        |  - - - - -
        |   - - - - -
        |    - - - - -, actions={})])""".trimMargin())
    }

    @Test
    fun addHexStateAndActions() {
        val simulator = HexSimulator(boardSize = 5)
        val history = History.create<HexState, HexAction>(simulator.initialState)
        val nextActions = mapOf(Pair(0, HexAction.valueOf(2, 2)))
        val nextState = simulator.stateTransition(simulator.initialState, nextActions)
        history.add(nextState, nextActions)
        Assertions.assertEquals(history.toString(), """
        |History(nodes=[Node(state=- - - - -
        | - - - - -
        |  - - - - -
        |   - - - - -
        |    - - - - -, actions={}), Node(state=- - - - -
        | - - - - -
        |  - - X - -
        |   - - - - -
        |    - - - - -, actions={0=C2})])
        """.trimMargin())
    }

}