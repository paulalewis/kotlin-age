package com.castlefrog.agl.domains.biniax

import com.google.common.truth.Truth
import org.junit.Test
import java.util.Random

class BiniaxSimulatorTest {

    @Test
    fun testNPlayers() {
        val simulator = BiniaxSimulator()
        Truth.assertThat(simulator.nPlayers).isEqualTo(1)
    }

    @Test
    fun testInitialState() {
        val simulator = BiniaxSimulator(random = Random(1234))
        val state = BiniaxState(locations = byteArrayOf(
                24, 14, 23, 0, 13,
                14, 23, 0, 13, 14,
                14, 23, 0, 14, 13,
                14, 0, 13, 12, 13,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 1, 0, 0
        ), maxElements = 10, freeMoves = 2)
        Truth.assertThat(simulator.initialState).isEqualTo(state)
    }

    @Test
    fun testCalculateRewards() {
        val simulator = BiniaxSimulator()
        simulator.calculateRewards(BiniaxState())[0] = 2
        Truth.assertThat(simulator.calculateRewards(BiniaxState())).isEqualTo(intArrayOf(1))
    }

    @Test
    fun testCalculateLegalActions() {
        val simulator = BiniaxSimulator(random = Random(1234))
        Truth.assertThat(simulator.calculateLegalActions(simulator.initialState))
                .isEqualTo(arrayListOf(arrayListOf(BiniaxAction.NORTH, BiniaxAction.EAST, BiniaxAction.WEST)))
    }

}