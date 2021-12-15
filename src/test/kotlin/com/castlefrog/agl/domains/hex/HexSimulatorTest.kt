package com.castlefrog.agl.domains.hex

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class HexSimulatorTest {

    @Test
    fun stateTransitionMove1() {
        val simulator = HexSimulator(boardSize = 5)
        val state2 = simulator.stateTransition(simulator.initialState, mapOf(Pair(0, HexAction(0, 0))))
        val expectedState = simulator.initialState
        expectedState.setLocation(0, 0, HexState.LOCATION_BLACK)
        expectedState.agentTurn = HexState.TURN_WHITE
        assertThat(state2).isEqualTo(expectedState)
    }

    @Test
    fun stateTransitionIllegalMove() {
        val simulator = HexSimulator(boardSize = 5)
        val state2 = simulator.stateTransition(simulator.initialState, mapOf(Pair(0, HexAction(0, 0))))
        assertThrows(
            IllegalArgumentException::class.java
        ) { simulator.stateTransition(state2, mapOf(Pair(0, HexAction(0, 0)))) }
    }

    @Test
    fun stateTransitionMove2SameLocation() {
        val simulator = HexSimulator(boardSize = 5)
        val state2 = simulator.stateTransition(simulator.initialState, mapOf(Pair(0, HexAction(0, 0))))
        val state3 = simulator.stateTransition(state2, mapOf(Pair(1, HexAction(0, 0))))
        val expectedState = simulator.initialState
        expectedState.setLocation(0, 0, HexState.LOCATION_WHITE)
        expectedState.agentTurn = HexState.TURN_BLACK
        assertThat(state3).isEqualTo(expectedState)
    }

    @Test
    fun stateTransitionMove2DifferentLocation() {
        val simulator = HexSimulator(boardSize = 5)
        val state2 = simulator.stateTransition(simulator.initialState, mapOf(Pair(0, HexAction(0, 0))))
        val state3 = simulator.stateTransition(state2, mapOf(Pair(1, HexAction(0, 1))))
        val expectedState = simulator.initialState
        expectedState.setLocation(0, 0, HexState.LOCATION_BLACK)
        expectedState.setLocation(0, 1, HexState.LOCATION_WHITE)
        expectedState.agentTurn = HexState.TURN_BLACK
        assertThat(state3).isEqualTo(expectedState)
    }
}