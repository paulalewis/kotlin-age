package com.castlefrog.agl.domains.connect4

import com.google.common.truth.Truth
import org.junit.Test


class Connect4SimulatorTest {

    @Test(expected = IllegalArgumentException::class)
    fun testStateTransitionInvalidNumberOfActions() {
        val simulator = Connect4Simulator()
        simulator.stateTransition(simulator.getInitialState(), emptyMap())
    }

    @Test
    fun testStateTransitionMove1() {
        val simulator = Connect4Simulator()
        val state = simulator.stateTransition(simulator.getInitialState(), mapOf(Pair(0, Connect4Action.valueOf(3))))
        val expectedState = Connect4State(1L shl 21, 0, 1)
        Truth.assertThat(state).isEqualTo(expectedState)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testStateTransitionNullAction() {
        val simulator = Connect4Simulator()
        val state = simulator.stateTransition(simulator.getInitialState(), mapOf(Pair(0, Connect4Action.valueOf(2))))
        simulator.stateTransition(state, mapOf(Pair(0, Connect4Action.valueOf(2))))
    }

    @Test
    fun testStateTransitionMove2() {
        val simulator = Connect4Simulator()
        val state2 = simulator.stateTransition(simulator.getInitialState(), mapOf(Pair(0, Connect4Action.valueOf(2))))
        val state3 = simulator.stateTransition(state2, mapOf(Pair(1, Connect4Action.valueOf(2))))
        val expectedState = Connect4State(1L shl 14, 1L shl 15, 0)
        Truth.assertThat(state3).isEqualTo(expectedState)
    }

}