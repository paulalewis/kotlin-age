package com.castlefrog.agl.agents

import com.castlefrog.agl.TestAction
import com.castlefrog.agl.TestSimulator
import com.castlefrog.agl.TestState
import com.google.common.truth.Truth
import org.junit.Test
import java.util.ArrayList
import java.util.Random

class RandomAgentTest {

    @Test
    fun testSelectActionInvalidLegalActionsArray() {
        val agent = RandomAgent(Random(5555))
        val simulator = TestSimulator(state = TestState(), legalActions = ArrayList(), rewards = intArrayOf(0))
        while (!simulator.isTerminalState) {
            val action = agent.selectAction(0, simulator.state, simulator.copy())
            action.ifPresent {
                simulator.stateTransition(mapOf(Pair(0, action.get())))
            }
        }
        Truth.assertThat(agent.selectAction(0, simulator.state, simulator.copy()).isPresent).isFalse()
    }

    @Test
    fun testSelectActionNoActions() {
        val agent = RandomAgent(Random(5555))
        val simulator = TestSimulator(state = TestState(), legalActions = arrayListOf(ArrayList()), rewards = intArrayOf(0))
        while (!simulator.isTerminalState) {
            val action = agent.selectAction(0, simulator.state, simulator.copy())
            action.ifPresent {
                simulator.stateTransition(mapOf(Pair(0, action.get())))
            }
        }
        Truth.assertThat(agent.selectAction(0, simulator.state, simulator.copy()).isPresent).isFalse()
    }

    @Test
    fun testSelectAction() {
        val expectedActions = arrayListOf(TestAction(3),
                TestAction(1),
                TestAction(3),
                TestAction(1),
                TestAction(2))
        val actualActions = ArrayList<TestAction>()
        val agent = RandomAgent(Random(6345))
        val simulator = TestSimulator(state = TestState(0),
                legalActions = arrayListOf(arrayListOf(TestAction(1), TestAction(2), TestAction(3))),
                rewards = intArrayOf(0))
        for (i in 0..expectedActions.size - 1) {
            actualActions.add(agent.selectAction(0, simulator.state, simulator).orElse(null))
        }
        Truth.assertThat(actualActions).isEqualTo(expectedActions)
    }

    @Test
    fun testToString() {
        val agent = RandomAgent()
        Truth.assertThat(agent.toString()).isEqualTo(agent.javaClass.simpleName)
    }
}