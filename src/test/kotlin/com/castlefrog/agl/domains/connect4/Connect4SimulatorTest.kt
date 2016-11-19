package com.castlefrog.agl.domains.connect4

import com.castlefrog.agl.TurnType
import com.google.common.truth.Truth
import org.junit.Test


class Connect4SimulatorTest {

    @Test(expected = AssertionError::class)
    fun testStateTransitionInvalidNumberOfActions() {
        val simulator = Connect4Simulator.create(TurnType.SEQUENTIAL)
        simulator.stateTransition(arrayListOf(Connect4Action.valueOf(3)))
    }

    @Test
    fun testStateTransitionMove1() {
        val simulator = Connect4Simulator.create(TurnType.SEQUENTIAL)
        simulator.stateTransition(arrayListOf(Connect4Action.valueOf(3), null))
        val expectedState = Connect4State(1L shl 21, 0, 1)
        Truth.assertThat(simulator.state).isEqualTo(expectedState)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testStateTransitionNullAction() {
        val simulator = Connect4Simulator.create(TurnType.SEQUENTIAL)
        simulator.stateTransition(arrayListOf(Connect4Action.valueOf(2), null))
        simulator.stateTransition(arrayListOf(Connect4Action.valueOf(2), null))
    }

    @Test
    fun testStateTransitionMove2() {
        val simulator = Connect4Simulator.create(TurnType.SEQUENTIAL)
        simulator.stateTransition(arrayListOf(Connect4Action.valueOf(2), null))
        simulator.stateTransition(arrayListOf(null, Connect4Action.valueOf(2)))
        val expectedState = Connect4State(1L shl 14, 1L shl 15, 0)
        Truth.assertThat(simulator.state).isEqualTo(expectedState)
    }

}