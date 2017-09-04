package com.castlefrog.agl.agents

import com.castlefrog.agl.TestAction
import com.castlefrog.agl.TestSimulator
import com.castlefrog.agl.TestState
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.ArrayList
import java.util.Random

class RandomAgentTest {

    @Test
    fun testSelectActionInvalidLegalActionsArray() {
        val agent = RandomAgent(Random(5555))
        var state = TestState()
        val simulator = TestSimulator(initialState = state, legalActions = ArrayList(), rewards = intArrayOf(0))
        while (!simulator.isTerminalState(state)) {
            val action = agent.selectAction(0, state, simulator)
            action.ifPresent {
                state = simulator.stateTransition(state, mapOf(Pair(0, action.get())))
            }
        }
        assertThat(agent.selectAction(0, state, simulator).isPresent).isFalse()
    }

    @Test
    fun testSelectActionNoActions() {
        val agent = RandomAgent(Random(5555))
        var state = TestState()
        val simulator = TestSimulator(initialState = state, legalActions = arrayListOf(ArrayList()), rewards = intArrayOf(0))
        while (!simulator.isTerminalState(state)) {
            val action = agent.selectAction(0, state, simulator)
            action.ifPresent {
                state = simulator.stateTransition(state, mapOf(Pair(0, action.get())))
            }
        }
        assertThat(agent.selectAction(0, state, simulator).isPresent).isFalse()
    }

    @Test
    fun testSelectAction() {
        val expectedActions = arrayListOf(TestAction(3),
                TestAction(1),
                TestAction(2),
                TestAction(1),
                TestAction(2))
        val actualActions = ArrayList<TestAction>()
        val agent = RandomAgent(Random(6345))
        val simulator = TestSimulator(initialState = TestState(0),
                legalActions = arrayListOf(arrayListOf(TestAction(1), TestAction(2), TestAction(3))),
                rewards = intArrayOf(0))
        for (i in 0 until expectedActions.size) {
            actualActions.add(agent.selectAction(0, simulator.initialState, simulator).orElse(null))
        }
        assertThat(actualActions).isEqualTo(expectedActions)
    }

    @Test
    fun testToString() {
        val agent = RandomAgent()
        assertThat(agent.toString()).isEqualTo(agent.javaClass.simpleName)
    }
}