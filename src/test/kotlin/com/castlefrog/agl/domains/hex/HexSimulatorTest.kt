package com.castlefrog.agl.domains.hex

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class HexSimulatorTest {

    @Test
    fun stateTransitionMove1() {
        val simulator = HexSimulator(boardSize = 5)
        val state2 = simulator.stateTransition(simulator.initialState, listOf(HexAction(0, 0), null))
        val expectedState = simulator.initialState
        expectedState.setLocation(0, 0, HexState.LOCATION_BLACK)
        expectedState.agentTurn = HexState.TURN_WHITE
        assertEquals(expectedState, state2)
    }

    @Test
    fun stateTransitionIllegalMove() {
        val simulator = HexSimulator(boardSize = 5)
        val state2 = simulator.stateTransition(simulator.initialState, listOf(HexAction(0, 0), null))
        assertThrows(
            IllegalArgumentException::class.java
        ) { simulator.stateTransition(state2, listOf(HexAction(0, 0), null)) }
    }

    @Test
    fun stateTransitionMove2SameLocation() {
        val simulator = HexSimulator(boardSize = 5)
        val state2 = simulator.stateTransition(simulator.initialState, listOf(HexAction(0, 0), null))
        val state3 = simulator.stateTransition(state2, listOf(null, HexAction(0, 0)))
        val expectedState = simulator.initialState
        expectedState.setLocation(0, 0, HexState.LOCATION_WHITE)
        expectedState.agentTurn = HexState.TURN_BLACK
        assertEquals(expectedState, state3)
    }

    @Test
    fun stateTransitionMove2DifferentLocation() {
        val simulator = HexSimulator(boardSize = 5)
        val state2 = simulator.stateTransition(simulator.initialState, listOf(HexAction(0, 0), null))
        val state3 = simulator.stateTransition(state2, listOf(null, HexAction(0, 1)))
        val expectedState = simulator.initialState
        expectedState.setLocation(0, 0, HexState.LOCATION_BLACK)
        expectedState.setLocation(0, 1, HexState.LOCATION_WHITE)
        expectedState.agentTurn = HexState.TURN_BLACK
        assertEquals(expectedState, state3)
    }
}