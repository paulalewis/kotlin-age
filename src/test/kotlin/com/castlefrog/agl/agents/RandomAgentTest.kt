package com.castlefrog.agl.agents

import com.castlefrog.agl.TestAction
import com.castlefrog.agl.TestSimulator
import com.castlefrog.agl.TestState
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import java.util.ArrayList
import java.util.Random

class RandomAgentTest {

    @Test
    fun testSelectActionInvalidLegalActionsArray() {
        val agent = RandomAgent(Random(5555))
        val state = TestState()
        val simulator = TestSimulator(initialState = state, legalActions = ArrayList(), rewards = intArrayOf(0))

        val exception = catchThrowable {
            agent.selectAction(0, state, simulator)
        }

        assertThat(exception)
                .isInstanceOf(IllegalStateException::class.java)
                .hasMessage("Player 0 has no legal actions")
    }

    @Test
    fun testSelectActionNoActions() {
        val agent = RandomAgent(Random(5555))
        val state = TestState()
        val simulator = TestSimulator(initialState = state, legalActions = arrayListOf(ArrayList()), rewards = intArrayOf(0))

        val exception = catchThrowable {
            agent.selectAction(0, state, simulator)
        }

        assertThat(exception)
                .isInstanceOf(IllegalStateException::class.java)
                .hasMessage("Player 0 has no legal actions")
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
            actualActions.add(agent.selectAction(0, simulator.initialState, simulator))
        }

        assertThat(actualActions).isEqualTo(expectedActions)
    }

    @Test
    fun testToString() {
        val agent = RandomAgent()
        assertThat(agent.toString()).isEqualTo(agent.javaClass.simpleName)
    }
}