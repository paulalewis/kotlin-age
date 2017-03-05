package com.castlefrog.agl.domains.backgammon

import com.google.common.truth.Truth
import org.junit.Test
import java.util.Random

class BackgammonSimulatorTest {

    private var simulator = BackgammonSimulator(Random(111))

    @Test
    fun testGetNPlayers() {
        Truth.assertThat(simulator.nPlayers).isEqualTo(2)
    }

    @Test
    fun testGetInitialState() {
        Truth.assertThat(simulator.initialState).isEqualTo(BackgammonState(dice = byteArrayOf(0, 6)))
    }

    @Test
    fun testCalculateRewardsInitialState() {
        Truth.assertThat(simulator.calculateRewards(simulator.initialState)).isEqualTo(intArrayOf(0, 0))
    }

    @Test
    fun testCalculateRewardsPlayer1Wins() {
        val state = BackgammonState(locations = byteArrayOf(0, 0, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, 0))
        Truth.assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(1, -1))
    }

    @Test
    fun testCalculateRewardsPlayer2Wins() {
        val state = BackgammonState(locations = byteArrayOf(0, 0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0))
        Truth.assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(-1, 1))
    }

    @Test
    fun testCalculateLegalActionsInitialState() {
        Truth.assertThat(simulator.calculateLegalActions(simulator.initialState)).isNotEmpty()
    }
}