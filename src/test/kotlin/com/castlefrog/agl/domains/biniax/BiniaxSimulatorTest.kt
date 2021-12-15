package com.castlefrog.agl.domains.biniax

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import kotlin.random.Random

class BiniaxSimulatorTest {

    @Test
    fun initialState() {
        val simulator = BiniaxSimulator(random = Random(1234))
        val state = BiniaxState(
            locations = byteArrayOf(
                34, 34, 23, 13, 0,
                12, 14, 12, 23, 0,
                0, 14, 12, 12, 13,
                0, 13, 13, 13, 14,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 1, 0, 0
            ), maxElements = 10, freeMoves = 2
        )
        assertThat(simulator.initialState).isEqualTo(state)
    }

    @Test
    fun calculateRewards() {
        val simulator = BiniaxSimulator()
        simulator.calculateRewards(BiniaxState())[0] = 2
        assertThat(simulator.calculateRewards(BiniaxState())).isEqualTo(intArrayOf(1))
    }

    @Test
    fun calculateLegalActions() {
        val simulator = BiniaxSimulator(random = Random(1234))
        assertThat(simulator.calculateLegalActions(simulator.initialState))
            .isEqualTo(arrayListOf(arrayListOf(BiniaxAction.NORTH, BiniaxAction.EAST, BiniaxAction.WEST)))
    }
}