package com.castlefrog.agl.domains.connect4

import com.google.common.truth.Truth
import org.junit.Test

class Connect4SimulatorTest {

    @Test
    fun testNPlayersIs2() {
        val simulator = Connect4Simulator()
        Truth.assertThat(simulator.nPlayers).isEqualTo(2)
    }

    @Test
    fun testGetInitialState() {
        val simulator = Connect4Simulator()
        val initialState = simulator.initialState
        Truth.assertThat(initialState.toString()).isEqualTo("""
        |-----------------
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |-----------------
        """.trimMargin())
    }

    @Test
    fun testCalculateRewardsInitialState() {
        val simulator = Connect4Simulator()
        val state = simulator.initialState
        Truth.assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(0, 0))
    }

    @Test
    fun testCalculateRewardsAfterSomeMovesNoWinner() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(1, 16384))
        Truth.assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(0, 0))
    }

    @Test
    fun testCalculateRewardsHorizontalWinnerPlayer1() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(2113665, 33026))
        Truth.assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(1, -1))
    }

    @Test
    fun testCalculateRewardsHorizontalWinnerPlayer2() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(57209232818176, 8865355661312))
        Truth.assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(-1, 1))
    }

    @Test
    fun testCalculateRewardsVerticalWinnerPlayer1() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(31457280, 268451969))
        Truth.assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(1, -1))
    }

    @Test
    fun testCalculateRewardsVerticalWinnerPlayer2() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(17280, 15))
        Truth.assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(-1, 1))
    }

    @Test
    fun testCalculateLegalActionsInitialState() {
        val simulator = Connect4Simulator()
        val state = simulator.initialState
        Truth.assertThat(simulator.calculateLegalActions(state)).isEqualTo(arrayListOf(
                arrayListOf(Connect4Action.valueOf(0),
                        Connect4Action.valueOf(1),
                        Connect4Action.valueOf(2),
                        Connect4Action.valueOf(3),
                        Connect4Action.valueOf(4),
                        Connect4Action.valueOf(5),
                        Connect4Action.valueOf(6)),
                arrayListOf()))
    }

    @Test
    fun testCalculateLegalActionsOneMove() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(1, 0))
        Truth.assertThat(simulator.calculateLegalActions(state)).isEqualTo(arrayListOf(
                arrayListOf(),
                arrayListOf(Connect4Action.valueOf(0),
                        Connect4Action.valueOf(1),
                        Connect4Action.valueOf(2),
                        Connect4Action.valueOf(3),
                        Connect4Action.valueOf(4),
                        Connect4Action.valueOf(5),
                        Connect4Action.valueOf(6))
        ))
    }

    @Test
    fun testCalculateLegalActionsFullColumn() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(2688, 5376))
        Truth.assertThat(simulator.calculateLegalActions(state)).isEqualTo(arrayListOf(
                arrayListOf(Connect4Action.valueOf(0),
                        Connect4Action.valueOf(2),
                        Connect4Action.valueOf(3),
                        Connect4Action.valueOf(4),
                        Connect4Action.valueOf(5),
                        Connect4Action.valueOf(6)),
                arrayListOf()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testStateTransitionInvalidNumberOfActions() {
        val simulator = Connect4Simulator()
        simulator.stateTransition(simulator.initialState, emptyMap())
    }

    @Test
    fun testStateTransitionMove1() {
        val simulator = Connect4Simulator()
        val state = simulator.stateTransition(simulator.initialState, mapOf(Pair(0, Connect4Action.valueOf(3))))
        val expectedState = Connect4State(longArrayOf(2097152, 0))
        Truth.assertThat(state).isEqualTo(expectedState)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testStateTransitionNullAction() {
        val simulator = Connect4Simulator()
        val state = simulator.stateTransition(simulator.initialState, mapOf(Pair(0, Connect4Action.valueOf(2))))
        simulator.stateTransition(state, mapOf(Pair(0, Connect4Action.valueOf(2))))
    }

    @Test
    fun testStateTransitionMove2() {
        val simulator = Connect4Simulator()
        val state2 = simulator.stateTransition(simulator.initialState, mapOf(Pair(0, Connect4Action.valueOf(2))))
        val state3 = simulator.stateTransition(state2, mapOf(Pair(1, Connect4Action.valueOf(2))))
        val expectedState = Connect4State(longArrayOf(16384, 32768))
        Truth.assertThat(state3).isEqualTo(expectedState)
    }

}