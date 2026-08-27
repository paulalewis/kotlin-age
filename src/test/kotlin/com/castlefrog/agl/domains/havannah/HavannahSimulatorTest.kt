package com.castlefrog.agl.domains.havannah

import com.castlefrog.agl.IllegalActionException
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class HavannahSimulatorTest {

    @Test
    fun stateTransitionEmptyActionsList() {
        val simulator = HavannahSimulator(5)
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(simulator.initialState, emptyList())
        }
    }

    @Test
    fun stateTransitionWrongArityWhenSecondPlayerToMove() {
        val simulator = HavannahSimulator(5)
        val state = simulator.stateTransition(simulator.initialState, listOf(HavannahAction(0, 0), null))
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(state, listOf(null))
        }
    }

    @Test
    fun stateTransitionMove1() {
        val simulator = HavannahSimulator(5)
        val state2 = simulator.stateTransition(simulator.initialState, listOf(HavannahAction(0, 0), null))
        val expectedState = simulator.initialState
        expectedState.locations[0][0] = HavannahState.LOCATION_BLACK
        expectedState.agentTurn = HavannahState.TURN_WHITE
        assertEquals(expectedState, state2)
    }

    @Test
    fun stateTransitionIllegalMove() {
        val simulator = HavannahSimulator(5)
        val state2 = simulator.stateTransition(simulator.initialState, listOf(HavannahAction(0, 0), null))
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(state2, listOf(HavannahAction(0, 0), null))
        }
    }

    @Test
    fun stateTransitionNullActionForPlayerToMove() {
        val simulator = HavannahSimulator(5)
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(simulator.initialState, listOf(null, null))
        }
    }

    @Test
    fun stateTransitionMove2SameLocationPieRuleTrue() {
        val simulator = HavannahSimulator(base = 5, pieRule = true)
        val state2 = simulator.stateTransition(simulator.initialState, listOf(HavannahAction(0, 0), null))
        val state3 = simulator.stateTransition(state2, listOf(null, HavannahAction(0, 0)))
        val expectedState = simulator.initialState
        expectedState.locations[0][0] = HavannahState.LOCATION_WHITE
        expectedState.agentTurn = HavannahState.TURN_BLACK
        assertEquals(expectedState, state3)
    }

    @Test
    fun stateTransitionMove2SameLocationPieRuleFalse() {
        val simulator = HavannahSimulator(base = 5, pieRule = false)
        val state2 = simulator.stateTransition(simulator.initialState, listOf(HavannahAction(0, 0), null))
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(state2, listOf(null, HavannahAction(0, 0)))
        }
    }

    @Test
    fun stateTransitionMove2DifferentLocation() {
        val simulator = HavannahSimulator(5)
        val state2 = simulator.stateTransition(simulator.initialState, listOf(HavannahAction(0, 0), null))
        val state3 = simulator.stateTransition(state2, listOf(null, HavannahAction(0, 1)))
        val expectedState = simulator.initialState
        expectedState.locations[0][0] = HavannahState.LOCATION_BLACK
        expectedState.locations[0][1] = HavannahState.LOCATION_WHITE
        expectedState.agentTurn = HavannahState.TURN_BLACK
        assertEquals(expectedState, state3)
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
        assertArrayEquals(intArrayOf(1, -1), simulator.calculateRewards(state))
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
        assertArrayEquals(intArrayOf(-1, 1), simulator.calculateRewards(state))
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
        assertArrayEquals(intArrayOf(1, -1), simulator.calculateRewards(state))
    }

    @Test
    fun sameSimulatorDetectsWinsOnUnrelatedStatesAfterTransition() {
        val simulator = HavannahSimulator(base = 5, pieRule = false)
        // Prime any instance-level last-move cache with a cell far from the winning groups.
        simulator.stateTransition(simulator.initialState, listOf(HavannahAction(8, 8), null))

        val ringLocations = Array(9) { ByteArray(9) }
        ringLocations[2][2] = HavannahState.LOCATION_BLACK
        ringLocations[2][3] = HavannahState.LOCATION_BLACK
        ringLocations[3][2] = HavannahState.LOCATION_BLACK
        ringLocations[4][4] = HavannahState.LOCATION_BLACK
        ringLocations[4][3] = HavannahState.LOCATION_BLACK
        ringLocations[3][4] = HavannahState.LOCATION_BLACK
        ringLocations[3][3] = HavannahState.LOCATION_WHITE
        ringLocations[0][0] = HavannahState.LOCATION_WHITE
        ringLocations[0][1] = HavannahState.LOCATION_WHITE
        ringLocations[1][0] = HavannahState.LOCATION_WHITE
        ringLocations[1][2] = HavannahState.LOCATION_WHITE
        val ringState = HavannahState(5, ringLocations, HavannahState.TURN_WHITE)
        assertArrayEquals(intArrayOf(1, -1), simulator.calculateRewards(ringState))
        assertTrue(simulator.isTerminalState(ringState))
        assertTrue(simulator.calculateLegalActions(ringState).all { it.isEmpty() })

        val cornerLocations = Array(9) { ByteArray(9) }
        cornerLocations[2][2] = HavannahState.LOCATION_BLACK
        cornerLocations[3][2] = HavannahState.LOCATION_BLACK
        cornerLocations[4][4] = HavannahState.LOCATION_BLACK
        cornerLocations[4][3] = HavannahState.LOCATION_BLACK
        cornerLocations[3][4] = HavannahState.LOCATION_BLACK
        cornerLocations[0][0] = HavannahState.LOCATION_WHITE
        cornerLocations[0][1] = HavannahState.LOCATION_WHITE
        cornerLocations[0][2] = HavannahState.LOCATION_WHITE
        cornerLocations[0][3] = HavannahState.LOCATION_WHITE
        cornerLocations[0][4] = HavannahState.LOCATION_WHITE
        val cornerState = HavannahState(5, cornerLocations, HavannahState.TURN_BLACK)
        assertArrayEquals(intArrayOf(-1, 1), simulator.calculateRewards(cornerState))
        assertTrue(simulator.isTerminalState(cornerState))
        assertTrue(simulator.calculateLegalActions(cornerState).all { it.isEmpty() })

        val sideLocations = Array(9) { ByteArray(9) }
        sideLocations[0][1] = HavannahState.LOCATION_BLACK
        sideLocations[1][0] = HavannahState.LOCATION_BLACK
        sideLocations[1][1] = HavannahState.LOCATION_BLACK
        sideLocations[1][2] = HavannahState.LOCATION_BLACK
        sideLocations[1][3] = HavannahState.LOCATION_BLACK
        sideLocations[1][4] = HavannahState.LOCATION_BLACK
        sideLocations[1][5] = HavannahState.LOCATION_BLACK
        sideLocations[4][4] = HavannahState.LOCATION_WHITE
        sideLocations[0][2] = HavannahState.LOCATION_WHITE
        sideLocations[0][3] = HavannahState.LOCATION_WHITE
        sideLocations[3][3] = HavannahState.LOCATION_WHITE
        val sideState = HavannahState(5, sideLocations, HavannahState.TURN_WHITE)
        assertArrayEquals(intArrayOf(1, -1), simulator.calculateRewards(sideState))
        assertTrue(simulator.isTerminalState(sideState))
        assertTrue(simulator.calculateLegalActions(sideState).all { it.isEmpty() })
    }
}
