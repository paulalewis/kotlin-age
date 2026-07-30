package com.castlefrog.agl.domains.go

import com.castlefrog.agl.IllegalActionException
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class GoSimulatorTest {

    @Test
    fun initialStateEmpty() {
        val simulator = GoSimulator(boardSize = 5)
        val state = simulator.initialState
        assertEquals(5, state.boardSize)
        assertEquals(GoState.TURN_BLACK, state.agentTurn)
        assertEquals(0, state.consecutivePasses)
    }

    @Test
    fun placeStoneAndSwitchTurn() {
        val simulator = GoSimulator(boardSize = 5)
        val next = simulator.stateTransition(
            simulator.initialState,
            listOf(GoAction.place(2, 2), null)
        )
        assertEquals(GoState.LOCATION_BLACK, next.get(2, 2))
        assertEquals(GoState.TURN_WHITE, next.agentTurn)
    }

    @Test
    fun captureSingleStone() {
        val simulator = GoSimulator(boardSize = 5)
        // Build: white stone at (1,0) surrounded after black plays (0,0) with black already on (1,1) and (2,0)
        var state = simulator.initialState
        state = simulator.stateTransition(state, listOf(GoAction.place(1, 1), null))
        state = simulator.stateTransition(state, listOf(null, GoAction.place(1, 0)))
        state = simulator.stateTransition(state, listOf(GoAction.place(2, 0), null))
        state = simulator.stateTransition(state, listOf(null, GoAction.place(3, 3))) // white elsewhere
        state = simulator.stateTransition(state, listOf(GoAction.place(0, 0), null))
        assertEquals(GoState.LOCATION_EMPTY, state.get(1, 0))
        assertEquals(1, state.capturedByBlack)
    }

    @Test
    fun suicideIsIllegal() {
        val simulator = GoSimulator(boardSize = 3)
        val state = GoState(boardSize = 3)
        // White surrounds (1,1) completely except we'll check black can't fill last liberty without capture
        state.set(0, 1, GoState.LOCATION_WHITE)
        state.set(2, 1, GoState.LOCATION_WHITE)
        state.set(1, 0, GoState.LOCATION_WHITE)
        state.set(1, 2, GoState.LOCATION_WHITE)
        state.agentTurn = GoState.TURN_BLACK
        val legal = simulator.calculateLegalActions(state)[0]
        assertFalse(legal.contains(GoAction.place(1, 1)))
    }

    @Test
    fun twoPassesEndsGameWithScoring() {
        val simulator = GoSimulator(boardSize = 5, komi = 0.0)
        var state = simulator.initialState
        state = simulator.stateTransition(state, listOf(GoAction.place(0, 0), null))
        state = simulator.stateTransition(state, listOf(null, GoAction.pass()))
        state = simulator.stateTransition(state, listOf(GoAction.pass(), null))
        // Two consecutive passes after white pass + black pass → consecutivePasses == 2
        assertEquals(2, state.consecutivePasses)
        val rewards = simulator.calculateRewards(state)
        // Black has a stone and some territory; with komi 0 black should win or at least not lose blindly
        assertTrue(rewards[0] >= rewards[1])
    }

    @Test
    fun emptyActionsThrows() {
        val simulator = GoSimulator(boardSize = 5)
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(simulator.initialState, emptyList())
        }
    }

    @Test
    fun illegalMoveThrows() {
        val simulator = GoSimulator(boardSize = 5)
        val state = simulator.stateTransition(simulator.initialState, listOf(GoAction.place(1, 1), null))
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(state, listOf(null, GoAction.place(1, 1)))
        }
    }

    @Test
    fun rewardsNeutralMidGame() {
        val simulator = GoSimulator(boardSize = 5)
        assertArrayEquals(
            intArrayOf(0, 0),
            simulator.calculateRewards(simulator.initialState)
        )
    }

    @Test
    fun passIsAlwaysLegalWhenGameOngoing() {
        val simulator = GoSimulator(boardSize = 5)
        val legal = simulator.calculateLegalActions(simulator.initialState)[0]
        assertTrue(legal.contains(GoAction.pass()))
    }
}
