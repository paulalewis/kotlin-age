package com.castlefrog.agl.domains.connect4

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class Connect4SimulatorTest {

    @Test
    fun getInitialState() {
        val simulator = Connect4Simulator()
        val initialState = simulator.initialState
        assertThat(initialState.toString()).isEqualTo(
            """
        |-----------------
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |-----------------
        """.trimMargin()
        )
    }

    @Test
    fun calculateRewardsInitialState() {
        val simulator = Connect4Simulator()
        val state = simulator.initialState
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(0, 0))
    }

    @Test
    fun calculateRewardsAfterSomeMovesNoWinner() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(1, 16384))
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(0, 0))
    }

    @Test
    fun calculateRewardsHorizontalWinnerPlayer1() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(2113665, 33026))
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(1, -1))
    }

    @Test
    fun calculateRewardsHorizontalWinnerPlayer2() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(57209232818176, 8865355661312))
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(-1, 1))
    }

    @Test
    fun calculateRewardsVerticalWinnerPlayer1() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(31457280, 268451969))
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(1, -1))
    }

    @Test
    fun calculateRewardsVerticalWinnerPlayer2() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(17280, 15))
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(-1, 1))
    }

    @Test
    fun calculateLegalActionsInitialState() {
        val simulator = Connect4Simulator()
        val state = simulator.initialState
        assertThat(simulator.calculateLegalActions(state)).isEqualTo(
            arrayListOf(
                arrayListOf(
                    Connect4Action.valueOf(0),
                    Connect4Action.valueOf(1),
                    Connect4Action.valueOf(2),
                    Connect4Action.valueOf(3),
                    Connect4Action.valueOf(4),
                    Connect4Action.valueOf(5),
                    Connect4Action.valueOf(6)
                ),
                arrayListOf()
            )
        )
    }

    @Test
    fun calculateLegalActionsOneMove() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(1, 0))
        assertThat(simulator.calculateLegalActions(state)).isEqualTo(
            arrayListOf(
                arrayListOf(),
                arrayListOf(
                    Connect4Action.valueOf(0),
                    Connect4Action.valueOf(1),
                    Connect4Action.valueOf(2),
                    Connect4Action.valueOf(3),
                    Connect4Action.valueOf(4),
                    Connect4Action.valueOf(5),
                    Connect4Action.valueOf(6)
                )
            )
        )
    }

    @Test
    fun calculateLegalActionsFullColumn() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(2688, 5376))
        assertThat(simulator.calculateLegalActions(state)).isEqualTo(
            arrayListOf(
                arrayListOf(
                    Connect4Action.valueOf(0),
                    Connect4Action.valueOf(2),
                    Connect4Action.valueOf(3),
                    Connect4Action.valueOf(4),
                    Connect4Action.valueOf(5),
                    Connect4Action.valueOf(6)
                ),
                arrayListOf()
            )
        )
    }

    @Test
    fun stateTransitionInvalidNumberOfActions() {
        val simulator = Connect4Simulator()
        assertThrows(
            IllegalArgumentException::class.java
        ) { simulator.stateTransition(simulator.initialState, emptyMap()) }
    }

    @Test
    fun stateTransitionMove1() {
        val simulator = Connect4Simulator()
        val state = simulator.stateTransition(simulator.initialState, mapOf(Pair(0, Connect4Action.valueOf(3))))
        val expectedState = Connect4State(longArrayOf(2097152, 0))
        assertThat(state).isEqualTo(expectedState)
    }

    @Test
    fun stateTransitionNullAction() {
        val simulator = Connect4Simulator()
        val state = simulator.stateTransition(simulator.initialState, mapOf(Pair(0, Connect4Action.valueOf(2))))
        assertThrows(
            IllegalArgumentException::class.java
        ) { simulator.stateTransition(state, mapOf(Pair(0, Connect4Action.valueOf(2)))) }
    }

    @Test
    fun stateTransitionMove2() {
        val simulator = Connect4Simulator()
        val state2 = simulator.stateTransition(simulator.initialState, mapOf(Pair(0, Connect4Action.valueOf(2))))
        val state3 = simulator.stateTransition(state2, mapOf(Pair(1, Connect4Action.valueOf(2))))
        val expectedState = Connect4State(longArrayOf(16384, 32768))
        assertThat(state3).isEqualTo(expectedState)
    }
}