package com.castlefrog.agl.domains.connect4

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class Connect4SimulatorTest {

    @Test
    fun getInitialState() {
        val simulator = Connect4Simulator()
        val initialState = simulator.initialState
        assertEquals("""
        |-----------------
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |-----------------
        """.trimMargin(), initialState.toString())
    }

    @Test
    fun calculateRewardsInitialState() {
        val simulator = Connect4Simulator()
        val state = simulator.initialState
        assertArrayEquals(intArrayOf(0, 0), simulator.calculateRewards(state))
    }

    @Test
    fun calculateRewardsAfterSomeMovesNoWinner() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(1, 16384))
        assertArrayEquals(intArrayOf(0, 0), simulator.calculateRewards(state))
    }

    @Test
    fun calculateRewardsHorizontalWinnerPlayer1() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(2113665, 33026))
        assertArrayEquals(intArrayOf(1, -1), simulator.calculateRewards(state))
    }

    @Test
    fun calculateRewardsHorizontalWinnerPlayer2() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(57209232818176, 8865355661312))
        assertArrayEquals(intArrayOf(-1, 1), simulator.calculateRewards(state))
    }

    @Test
    fun calculateRewardsVerticalWinnerPlayer1() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(31457280, 268451969))
        assertArrayEquals(intArrayOf(1, -1), simulator.calculateRewards(state))
    }

    @Test
    fun calculateRewardsVerticalWinnerPlayer2() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(17280, 15))
        assertArrayEquals(intArrayOf(-1, 1), simulator.calculateRewards(state))
    }

    @Test
    fun calculateLegalActionsInitialState() {
        val simulator = Connect4Simulator()
        val state = simulator.initialState
        assertEquals(arrayListOf(
                setOf(
                    Connect4Action.valueOf(0),
                    Connect4Action.valueOf(1),
                    Connect4Action.valueOf(2),
                    Connect4Action.valueOf(3),
                    Connect4Action.valueOf(4),
                    Connect4Action.valueOf(5),
                    Connect4Action.valueOf(6)
                ),
                setOf()
            ), simulator.calculateLegalActions(state))
    }

    @Test
    fun calculateLegalActionsOneMove() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(1, 0))
        assertEquals(arrayListOf(
                setOf(),
                setOf(
                    Connect4Action.valueOf(0),
                    Connect4Action.valueOf(1),
                    Connect4Action.valueOf(2),
                    Connect4Action.valueOf(3),
                    Connect4Action.valueOf(4),
                    Connect4Action.valueOf(5),
                    Connect4Action.valueOf(6)
                )
            ), simulator.calculateLegalActions(state))
    }

    @Test
    fun calculateLegalActionsFullColumn() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(2688, 5376))
        assertEquals(arrayListOf(
                setOf(
                    Connect4Action.valueOf(0),
                    Connect4Action.valueOf(2),
                    Connect4Action.valueOf(3),
                    Connect4Action.valueOf(4),
                    Connect4Action.valueOf(5),
                    Connect4Action.valueOf(6)
                ),
                setOf()
            ), simulator.calculateLegalActions(state))
    }

    @Test
    fun stateTransitionInvalidNumberOfActions() {
        val simulator = Connect4Simulator()
        assertThrows(
            IllegalArgumentException::class.java
        ) { simulator.stateTransition(simulator.initialState, emptyList()) }
    }

    @Test
    fun stateTransitionMove1() {
        val simulator = Connect4Simulator()
        val state = simulator.stateTransition(simulator.initialState, listOf(Connect4Action.valueOf(3), null))
        val expectedState = Connect4State(longArrayOf(2097152, 0))
        assertEquals(expectedState, state)
    }

    @Test
    fun stateTransitionNullAction() {
        val simulator = Connect4Simulator()
        val state = simulator.stateTransition(simulator.initialState, listOf(Connect4Action.valueOf(2), null))
        assertThrows(
            IllegalArgumentException::class.java
        ) { simulator.stateTransition(state, listOf(Connect4Action.valueOf(2), null)) }
    }

    @Test
    fun stateTransitionMove2() {
        val simulator = Connect4Simulator()
        val state2 = simulator.stateTransition(simulator.initialState, listOf(Connect4Action.valueOf(2), null))
        val state3 = simulator.stateTransition(state2, listOf(null, Connect4Action.valueOf(2)))
        val expectedState = Connect4State(longArrayOf(16384, 32768))
        assertEquals(expectedState, state3)
    }
}