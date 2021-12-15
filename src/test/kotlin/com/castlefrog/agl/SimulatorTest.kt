package com.castlefrog.agl

import com.castlefrog.agl.util.isTerminalState
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SimulatorTest {

    @Test
    fun isTerminalStateFalse() {
        val simulator = TestSimulator(
            initialState = TestState(),
            legalActions = arrayListOf(setOf(TestAction()), setOf()),
            rewards = intArrayOf(0, 0)
        )
        assertThat(simulator.isTerminalState(TestState())).isFalse()
    }

    @Test
    fun isTerminalStateTrue() {
        val simulator = TestSimulator(
            initialState = TestState(),
            legalActions = arrayListOf(setOf(), setOf()),
            rewards = intArrayOf(0, 0)
        )
        assertThat(simulator.isTerminalState(TestState())).isTrue()
    }
}