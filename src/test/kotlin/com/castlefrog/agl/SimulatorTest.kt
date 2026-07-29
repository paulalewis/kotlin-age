package com.castlefrog.agl

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulatorTest {

    @Test
    fun isTerminalStateFalse() {
        val simulator = TestSimulator(
            initialState = TestState(),
            legalActions = arrayListOf(setOf(TestAction()), setOf()),
            rewards = intArrayOf(0, 0)
        )
        assertFalse(simulator.isTerminalState(TestState()))
    }

    @Test
    fun isTerminalStateTrue() {
        val simulator = TestSimulator(
            initialState = TestState(),
            legalActions = arrayListOf(setOf(), setOf()),
            rewards = intArrayOf(0, 0)
        )
        assertTrue(simulator.isTerminalState(TestState()))
    }
}