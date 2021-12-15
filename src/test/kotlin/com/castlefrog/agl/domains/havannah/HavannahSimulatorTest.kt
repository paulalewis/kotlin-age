package com.castlefrog.agl.domains.havannah

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class HavannahSimulatorTest {

    @Test
    fun stateTransitionMove1() {
        val simulator = HavannahSimulator(5)
        val state2 = simulator.stateTransition(simulator.initialState, mapOf(Pair(0, HavannahAction(0, 0))))
        val expectedState = simulator.initialState
        expectedState.locations[0][0] = HavannahState.LOCATION_BLACK
        expectedState.agentTurn = HavannahState.TURN_WHITE
        assertThat(state2).isEqualTo(expectedState)
    }

    @Test
    fun stateTransitionIllegalMove() {
        val simulator = HavannahSimulator(5)
        val state2 = simulator.stateTransition(simulator.initialState, mapOf(Pair(0, HavannahAction(0, 0))))
        assertThrows(
            IllegalArgumentException::class.java
        ) { simulator.stateTransition(state2, mapOf(Pair(0, HavannahAction(0, 0)))) }
    }

    @Test
    fun stateTransitionMove2SameLocationPieRuleTrue() {
        val simulator = HavannahSimulator(base = 5, pieRule = true)
        val state2 = simulator.stateTransition(simulator.initialState, mapOf(Pair(0, HavannahAction(0, 0))))
        val state3 = simulator.stateTransition(state2, mapOf(Pair(1, HavannahAction(0, 0))))
        val expectedState = simulator.initialState
        expectedState.locations[0][0] = HavannahState.LOCATION_WHITE
        expectedState.agentTurn = HavannahState.TURN_BLACK
        assertThat(state3).isEqualTo(expectedState)
    }

    @Test
    fun stateTransitionMove2SameLocationPieRuleFalse() {
        val simulator = HavannahSimulator(base = 5, pieRule = false)
        val state2 = simulator.stateTransition(simulator.initialState, mapOf(Pair(0, HavannahAction(0, 0))))
        assertThrows(
            IllegalArgumentException::class.java
        ) { simulator.stateTransition(state2, mapOf(Pair(1, HavannahAction(0, 0)))) }
    }

    @Test
    fun stateTransitionMove2DifferentLocation() {
        val simulator = HavannahSimulator(5)
        val state2 = simulator.stateTransition(simulator.initialState, mapOf(Pair(0, HavannahAction(0, 0))))
        val state3 = simulator.stateTransition(state2, mapOf(Pair(1, HavannahAction(0, 1))))
        val expectedState = simulator.initialState
        expectedState.locations[0][0] = HavannahState.LOCATION_BLACK
        expectedState.locations[0][1] = HavannahState.LOCATION_WHITE
        expectedState.agentTurn = HavannahState.TURN_BLACK
        assertThat(state3).isEqualTo(expectedState)
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
        val simulator = HavannahSimulator(base = 5, pieRule = false)
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(1, -1))
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
        val simulator = HavannahSimulator(base = 5, pieRule = false)
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(-1, 1))
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
        val simulator = HavannahSimulator(base = 5, pieRule = false)
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(1, -1))
    }
}
