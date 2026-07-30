package com.castlefrog.agl.domains.backgammon

import com.castlefrog.agl.IllegalActionException
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.random.Random

class BackgammonSimulatorTest {

    @Test
    fun getInitialStatePlayer1First() {
        val simulator = BackgammonSimulator(Random(381582))
        assertEquals(BackgammonState(dice = byteArrayOf(3, 0), agentTurn = 0), simulator.initialState)
    }

    @Test
    fun getInitialStatePlayer2First() {
        val simulator = BackgammonSimulator(Random(5331224))
        assertEquals(BackgammonState(dice = byteArrayOf(1, 4), agentTurn = 1), simulator.initialState)
    }

    @Test
    fun calculateRewardsInitialState() {
        val simulator = BackgammonSimulator(Random(111))
        assertArrayEquals(intArrayOf(0, 0), simulator.calculateRewards(simulator.initialState))
    }

    @Test
    fun calculateRewardsPlayer1Wins() {
        val simulator = BackgammonSimulator(Random(111))
        val state = BackgammonState(
            locations = byteArrayOf(
                0,
                0,
                0,
                0,
                0,
                0,
                -5,
                0,
                -3,
                0,
                0,
                0,
                0,
                -5,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                -2,
                0
            )
        )
        assertArrayEquals(intArrayOf(1, -1), simulator.calculateRewards(state))
    }

    @Test
    fun calculateRewardsPlayer2Wins() {
        val simulator = BackgammonSimulator(Random(111))
        val state = BackgammonState(
            locations = byteArrayOf(
                0,
                0,
                0,
                0,
                0,
                0,
                5,
                0,
                3,
                0,
                0,
                0,
                0,
                5,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                2,
                0
            )
        )
        assertArrayEquals(intArrayOf(-1, 1), simulator.calculateRewards(state))
    }

    @Test
    fun calculateLegalActionsInitialState() {
        val simulator = BackgammonSimulator(Random(111))
        val expectedLegalActions = arrayListOf(
            setOf(
                BackgammonAction(setOf(BackgammonMove.valueOf(1, 6), BackgammonMove.valueOf(7, 4))),
                BackgammonAction(setOf(BackgammonMove.valueOf(12, 4), BackgammonMove.valueOf(1, 6))),
                BackgammonAction(setOf(BackgammonMove.valueOf(1, 6), BackgammonMove.valueOf(17, 4))),
                BackgammonAction(setOf(BackgammonMove.valueOf(1, 6), BackgammonMove.valueOf(19, 4))),
                BackgammonAction(setOf(BackgammonMove.valueOf(1, 6), BackgammonMove.valueOf(1, 4))),
                BackgammonAction(setOf(BackgammonMove.valueOf(5, 6), BackgammonMove.valueOf(1, 4))),
                BackgammonAction(setOf(BackgammonMove.valueOf(12, 6), BackgammonMove.valueOf(1, 4))),
                BackgammonAction(setOf(BackgammonMove.valueOf(17, 6), BackgammonMove.valueOf(1, 4))),
                BackgammonAction(setOf(BackgammonMove.valueOf(12, 6), BackgammonMove.valueOf(17, 4))),
                BackgammonAction(setOf(BackgammonMove.valueOf(12, 6), BackgammonMove.valueOf(18, 4))),
                BackgammonAction(setOf(BackgammonMove.valueOf(19, 4), BackgammonMove.valueOf(12, 6))),
                BackgammonAction(setOf(BackgammonMove.valueOf(12, 4), BackgammonMove.valueOf(12, 6))),
                BackgammonAction(setOf(BackgammonMove.valueOf(12, 4), BackgammonMove.valueOf(16, 6))),
                BackgammonAction(setOf(BackgammonMove.valueOf(12, 4), BackgammonMove.valueOf(17, 6))),
                BackgammonAction(setOf(BackgammonMove.valueOf(17, 6), BackgammonMove.valueOf(19, 4))),
                BackgammonAction(setOf(BackgammonMove.valueOf(17, 6), BackgammonMove.valueOf(17, 4))),
            ),
            emptySet(),
        )
        assertEquals(expectedLegalActions, simulator.calculateLegalActions(simulator.initialState))
    }

    @Test
    fun stateTransitionEmptyActionsList() {
        val simulator = BackgammonSimulator(Random(111))
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(simulator.initialState, emptyList())
        }
    }

    @Test
    fun stateTransitionWrongArityWhenSecondPlayerToMove() {
        val simulator = BackgammonSimulator(Random(5331224)) // agentTurn = 1
        val state = simulator.initialState
        assertEquals(1, state.agentTurn)
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(state, listOf(null))
        }
    }

    @Test
    fun stateTransitionIllegalMove() {
        val simulator = BackgammonSimulator(Random(111))
        val state = simulator.initialState
        val illegal = BackgammonAction(emptySet())
        val actions = if (state.agentTurn == 0) listOf(illegal, null) else listOf(null, illegal)
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(state, actions)
        }
    }

    @Test
    fun stateTransitionNullActionForPlayerToMove() {
        val simulator = BackgammonSimulator(Random(111))
        val state = simulator.initialState
        val actions = if (state.agentTurn == 0) listOf(null, null) else listOf(null, null)
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(state, actions)
        }
    }
}