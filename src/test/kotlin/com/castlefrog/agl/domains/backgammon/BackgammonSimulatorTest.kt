package com.castlefrog.agl.domains.backgammon

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import kotlin.random.Random

class BackgammonSimulatorTest {

    @Test
    fun getInitialStatePlayer1First() {
        val simulator = BackgammonSimulator(Random(381582))
        assertThat(simulator.initialState).isEqualTo(BackgammonState(dice = byteArrayOf(3, 0), agentTurn = 0))
    }

    @Test
    fun getInitialStatePlayer2First() {
        val simulator = BackgammonSimulator(Random(5331224))
        assertThat(simulator.initialState).isEqualTo(BackgammonState(dice = byteArrayOf(1, 4), agentTurn = 1))
    }

    @Test
    fun calculateRewardsInitialState() {
        val simulator = BackgammonSimulator(Random(111))
        assertThat(simulator.calculateRewards(simulator.initialState)).isEqualTo(intArrayOf(0, 0))
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
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(1, -1))
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
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(-1, 1))
    }

    @Test
    fun calculateLegalActionsInitialState() {
        val simulator = BackgammonSimulator(Random(111))
        val expectedLegalActions = arrayListOf(
            arrayListOf(
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
            emptyList(),
        )
        assertThat(simulator.calculateLegalActions(simulator.initialState)).isEqualTo(expectedLegalActions)
    }
}