package com.castlefrog.agl

import com.google.common.truth.Truth
import org.junit.Test

class SimulatorTest {

    @Test
    fun nAgents() {
        val simulator = TestSimulator(state = TestState(),
                legalActions = arrayListOf(arrayListOf(), arrayListOf()),
                rewards = intArrayOf(0, 0))
        Truth.assertThat(simulator.nPlayers).isEqualTo(2)
    }

    @Test
    fun isTerminalStateFalse() {
        val simulator = TestSimulator(state = TestState(),
                legalActions = arrayListOf(arrayListOf(TestAction()), arrayListOf()),
                rewards = intArrayOf(0, 0))
        Truth.assertThat(simulator.terminalState).isFalse()
    }

    @Test
    fun isTerminalStateTrue() {
        val simulator = TestSimulator(state = TestState(),
                legalActions = arrayListOf(arrayListOf(), arrayListOf()),
                rewards = intArrayOf(0, 0))
        Truth.assertThat(simulator.terminalState).isTrue()
    }

}