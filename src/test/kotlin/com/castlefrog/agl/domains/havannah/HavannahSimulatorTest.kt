package com.castlefrog.agl.domains.havannah

import com.google.common.truth.Truth
import org.junit.Test

class HavannahSimulatorTest {

    @Test
    fun stateTransitionMove1() {
        val simulator = HavannahSimulator.create(5, true)
        simulator.stateTransition(arrayListOf(HavannahAction.valueOf(0, 0), null))
        val state = HavannahSimulator.getInitialState(5)
        state.locations[0][0] = HavannahState.LOCATION_BLACK
        state.agentTurn = HavannahState.TURN_WHITE
        Truth.assertThat(simulator.state).isEqualTo(state)
    }

    @Test(expected = IllegalArgumentException::class)
    fun stateTransitionIllegalMove() {
        val simulator = HavannahSimulator.create(5, true)
        simulator.stateTransition(arrayListOf(HavannahAction.valueOf(0, 0), null))
        simulator.stateTransition(arrayListOf(HavannahAction.valueOf(0, 0), null))
    }

    @Test
    fun stateTransitionMove2SameLocation() {
        val simulator = HavannahSimulator.create(5, true)
        simulator.stateTransition(arrayListOf(HavannahAction.valueOf(0, 0), null))
        simulator.stateTransition(arrayListOf(null, HavannahAction.valueOf(0, 0)))
        val state = HavannahSimulator.getInitialState(5)
        state.locations[0][0] = HavannahState.LOCATION_WHITE
        state.agentTurn = HavannahState.TURN_BLACK
        Truth.assertThat(simulator.state).isEqualTo(state)
    }

    @Test
    fun stateTransitionMove2DifferentLocation() {
        val simulator = HavannahSimulator.create(5, true)
        simulator.stateTransition(arrayListOf(HavannahAction.valueOf(0, 0), null))
        simulator.stateTransition(arrayListOf(null, HavannahAction.valueOf(0, 1)))
        val state = HavannahSimulator.getInitialState(5)
        state.locations[0][0] = HavannahState.LOCATION_BLACK
        state.locations[0][1] = HavannahState.LOCATION_WHITE
        state.agentTurn = HavannahState.TURN_BLACK
        Truth.assertThat(simulator.state).isEqualTo(state)
    }

    @Test
    fun stateBlackWinsRing() {
        val locations = Array(9) { ByteArray(9) }
        locations[2][2] = HavannahState.LOCATION_BLACK
        locations[2][3] = HavannahState.LOCATION_BLACK
        locations[3][2] = HavannahState.LOCATION_BLACK
        locations[4][4] = HavannahState.LOCATION_BLACK
        locations[4][3] = HavannahState.LOCATION_BLACK
        locations[3][4] = HavannahState.LOCATION_BLACK
        locations[3][3] = HavannahState.LOCATION_WHITE
        locations[0][0] = HavannahState.LOCATION_WHITE
        locations[0][1] = HavannahState.LOCATION_WHITE
        locations[1][0] = HavannahState.LOCATION_WHITE
        locations[1][2] = HavannahState.LOCATION_WHITE
        val state = HavannahState(5, locations, HavannahState.TURN_WHITE)
        val simulator = HavannahSimulator(state = state, pieRule = false)
        Truth.assertThat(simulator.rewards).isEqualTo(intArrayOf(1, -1))
    }

    @Test
    fun stateWhiteWinsCorners() {
        val locations = Array(9) { ByteArray(9) }
        locations[2][2] = HavannahState.LOCATION_BLACK
        locations[3][2] = HavannahState.LOCATION_BLACK
        locations[4][4] = HavannahState.LOCATION_BLACK
        locations[4][3] = HavannahState.LOCATION_BLACK
        locations[3][4] = HavannahState.LOCATION_BLACK
        locations[0][0] = HavannahState.LOCATION_WHITE
        locations[0][1] = HavannahState.LOCATION_WHITE
        locations[0][2] = HavannahState.LOCATION_WHITE
        locations[0][3] = HavannahState.LOCATION_WHITE
        locations[0][4] = HavannahState.LOCATION_WHITE
        val state = HavannahState(5, locations, HavannahState.TURN_BLACK)
        val simulator = HavannahSimulator(state = state, pieRule = false)
        Truth.assertThat(simulator.rewards).isEqualTo(intArrayOf(-1, 1))
    }

    @Test
    fun stateBlackWinsSides() {
        val locations = Array(9) { ByteArray(9) }
        locations[0][1] = HavannahState.LOCATION_BLACK
        locations[1][0] = HavannahState.LOCATION_BLACK
        locations[1][1] = HavannahState.LOCATION_BLACK
        locations[1][2] = HavannahState.LOCATION_BLACK
        locations[1][3] = HavannahState.LOCATION_BLACK
        locations[1][4] = HavannahState.LOCATION_BLACK
        locations[1][5] = HavannahState.LOCATION_BLACK
        locations[4][4] = HavannahState.LOCATION_WHITE
        locations[0][2] = HavannahState.LOCATION_WHITE
        locations[0][3] = HavannahState.LOCATION_WHITE
        locations[3][3] = HavannahState.LOCATION_WHITE
        val state = HavannahState(5, locations, HavannahState.TURN_WHITE)
        val simulator = HavannahSimulator(state = state, pieRule = false)
        Truth.assertThat(simulator.rewards).isEqualTo(intArrayOf(1, -1))
    }

}
