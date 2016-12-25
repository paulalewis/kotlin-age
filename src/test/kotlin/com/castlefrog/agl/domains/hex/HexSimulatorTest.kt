package com.castlefrog.agl.domains.hex

import com.google.common.truth.Truth
import org.junit.Test

class HexSimulatorTest {

    @Test
    fun stateTransitionMove1() {
        val simulator = HexSimulator.create(5, true)
        simulator.stateTransition(mapOf(Pair(0, HexAction.valueOf(0, 0))))
        val state = HexSimulator.getInitialState(5)
        state.setLocation(0, 0, HexState.LOCATION_BLACK)
        state.agentTurn = HexState.TURN_WHITE
        Truth.assertThat(simulator.state).isEqualTo(state)
    }

    @Test(expected = IllegalArgumentException::class)
    fun stateTransitionIllegalMove() {
        val simulator = HexSimulator.create(5, true)
        simulator.stateTransition(mapOf(Pair(0, HexAction.valueOf(0, 0))))
        simulator.stateTransition(mapOf(Pair(0, HexAction.valueOf(0, 0))))
    }

    @Test
    fun stateTransitionMove2SameLocation() {
        val simulator = HexSimulator.create(5, true)
        simulator.stateTransition(mapOf(Pair(0, HexAction.valueOf(0, 0))))
        simulator.stateTransition(mapOf(Pair(1, HexAction.valueOf(0, 0))))
        val state = HexSimulator.getInitialState(5)
        state.setLocation(0, 0, HexState.LOCATION_WHITE)
        state.agentTurn = HexState.TURN_BLACK
        Truth.assertThat(simulator.state).isEqualTo(state)
    }

    @Test
    fun stateTransitionMove2DifferentLocation() {
        val simulator = HexSimulator.create(5, true)
        simulator.stateTransition(mapOf(Pair(0, HexAction.valueOf(0, 0))))
        simulator.stateTransition(mapOf(Pair(1, HexAction.valueOf(0, 1))))
        val state = HexSimulator.getInitialState(5)
        state.setLocation(0, 0, HexState.LOCATION_BLACK)
        state.setLocation(0, 1, HexState.LOCATION_WHITE)
        state.agentTurn = HexState.TURN_BLACK
        Truth.assertThat(simulator.state).isEqualTo(state)
    }

}