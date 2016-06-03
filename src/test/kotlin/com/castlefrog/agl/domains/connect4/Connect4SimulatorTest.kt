package com.castlefrog.agl.domains.connect4

import com.castlefrog.agl.TurnType
import com.google.common.truth.Truth
import org.junit.Test
import java.util.ArrayList


class Connect4SimulatorTest {

    @Test(expected = AssertionError::class)
    fun testStateTransitionInvalidNumberOfActions() {
        val simulator = Connect4Simulator.create(TurnType.SEQUENTIAL)
        val actions = ArrayList<Connect4Action>()
        actions.add(Connect4Action.valueOf(3))
        simulator.stateTransition(actions)
    }

    @Test
    fun testStateTransitionMove1() {
        val simulator = Connect4Simulator.create(TurnType.SEQUENTIAL)
        val actions = ArrayList<Connect4Action?>()
        actions.add(Connect4Action.valueOf(3))
        actions.add(null)
        simulator.stateTransition(actions)
        val expectedState = Connect4State(1L shl 21, 0, 1)
        Truth.assertThat(simulator.state).isEqualTo(expectedState)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testStateTransitionNullAction() {
        val simulator = Connect4Simulator.create(TurnType.SEQUENTIAL)
        val actions = ArrayList<Connect4Action?>()
        actions.add(Connect4Action.valueOf(2))
        actions.add(null)
        simulator.stateTransition(actions)
        actions.clear()
        actions.add(Connect4Action.valueOf(2))
        actions.add(null)
        simulator.stateTransition(actions)
    }

    @Test
    fun testStateTransitionMove2() {
        val simulator = Connect4Simulator.create(TurnType.SEQUENTIAL)
        val actions = ArrayList<Connect4Action?>()
        actions.add(Connect4Action.valueOf(2))
        actions.add(null)
        simulator.stateTransition(actions)
        actions.clear()
        actions.add(null)
        actions.add(Connect4Action.valueOf(2))
        simulator.stateTransition(actions)
        val expectedState = Connect4State(1L shl 14, 1L shl 15, 0)
        Truth.assertThat(simulator.state).isEqualTo(expectedState)
    }

}