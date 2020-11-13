package com.castlefrog.agl

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class SimulatorTest {

    @Test
    fun isTerminalStateFalse() {
        val simulator = TestSimulator(
            initialState = TestState(),
            legalActions = arrayListOf(arrayListOf(TestAction()), arrayListOf()),
            rewards = intArrayOf(0, 0)
        )
        assertThat(simulator.isTerminalState(TestState())).isFalse()
    }

    @Test
    fun isTerminalStateTrue() {
        val simulator = TestSimulator(
            initialState = TestState(),
            legalActions = arrayListOf(arrayListOf(), arrayListOf()),
            rewards = intArrayOf(0, 0)
        )
        assertThat(simulator.isTerminalState(TestState())).isTrue()
    }
}