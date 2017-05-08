package com.castlefrog.agl.domains.backgammon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Random

class BackgammonSimulatorTest {

    @Test
    fun testGetNPlayers() {
        assertThat(BackgammonSimulator().nPlayers).isEqualTo(2)
    }

    @Test
    fun testGetInitialStatePlayer1First() {
        val simulator = BackgammonSimulator(Random(381582))
        assertThat(simulator.initialState).isEqualTo(BackgammonState(dice = byteArrayOf(4, 0), agentTurn = 0))
    }

    @Test
    fun testGetInitialStatePlayer2First() {
        val simulator = BackgammonSimulator(Random(5311224))
        assertThat(simulator.initialState).isEqualTo(BackgammonState(dice = byteArrayOf(4, 5), agentTurn = 1))
    }

    @Test
    fun testCalculateRewardsInitialState() {
        val simulator = BackgammonSimulator(Random(111))
        assertThat(simulator.calculateRewards(simulator.initialState)).isEqualTo(intArrayOf(0, 0))
    }

    @Test
    fun testCalculateRewardsPlayer1Wins() {
        val simulator = BackgammonSimulator(Random(111))
        val state = BackgammonState(locations = byteArrayOf(0, 0, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, 0))
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(1, -1))
    }

    @Test
    fun testCalculateRewardsPlayer2Wins() {
        val simulator = BackgammonSimulator(Random(111))
        val state = BackgammonState(locations = byteArrayOf(0, 0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0))
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(-1, 1))
    }

    @Test
    fun testCalculateLegalActionsInitialState() {
        val simulator = BackgammonSimulator(Random(111))
        val expectedLegalActions = arrayListOf<List<BackgammonAction>>(emptyList(),
                arrayListOf(
                        BackgammonAction(setOf(BackgammonMove.valueOf(6, 2), BackgammonMove.valueOf(6, 4))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(8, 4), BackgammonMove.valueOf(4, 2))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(8, 4), BackgammonMove.valueOf(6, 2))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(8, 4), BackgammonMove.valueOf(8, 2))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(8, 2), BackgammonMove.valueOf(6, 4))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(6, 2), BackgammonMove.valueOf(13, 4))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(8, 2), BackgammonMove.valueOf(13, 4))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(9, 2), BackgammonMove.valueOf(13, 4))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(13, 2), BackgammonMove.valueOf(13, 4))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(13, 2), BackgammonMove.valueOf(6, 4))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(8, 4), BackgammonMove.valueOf(13, 2))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(13, 2), BackgammonMove.valueOf(11, 4))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(24, 4), BackgammonMove.valueOf(6, 2))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(24, 4), BackgammonMove.valueOf(8, 2))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(24, 4), BackgammonMove.valueOf(13, 2))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(24, 4), BackgammonMove.valueOf(20, 2))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(24, 4), BackgammonMove.valueOf(24, 2))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(24, 2), BackgammonMove.valueOf(6, 4))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(8, 4), BackgammonMove.valueOf(24, 2))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(24, 2), BackgammonMove.valueOf(13, 4))),
                        BackgammonAction(setOf(BackgammonMove.valueOf(24, 2), BackgammonMove.valueOf(22, 4)))
                ))
        assertThat(simulator.calculateLegalActions(simulator.initialState)).isEqualTo(expectedLegalActions)
    }
}