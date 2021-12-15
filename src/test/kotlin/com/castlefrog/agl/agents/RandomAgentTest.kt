package com.castlefrog.agl.agents

import com.castlefrog.agl.TestAction
import com.castlefrog.agl.TestSimulator
import com.castlefrog.agl.TestState
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.random.Random

class RandomAgentTest {

    @Test
    fun selectActionInvalidLegalActionsArray() {
        val agent = RandomAgent(Random(5555))
        val state = TestState()
        val simulator = TestSimulator(initialState = state, legalActions = ArrayList(), rewards = intArrayOf(0))

        val exception = assertThrows(IllegalStateException::class.java) { agent.selectAction(0, state, simulator) }
        assertThat(exception)
            .hasMessageThat().contains("Player 0 has no legal actions")
    }

    @Test
    fun selectActionNoActions() {
        val agent = RandomAgent(Random(5555))
        val state = TestState()
        val simulator =
            TestSimulator(initialState = state, legalActions = arrayListOf(ArrayList()), rewards = intArrayOf(0))

        val exception = assertThrows(IllegalStateException::class.java) {
            agent.selectAction(0, state, simulator)
        }

        assertThat(exception)
            .hasMessageThat().contains("Player 0 has no legal actions")
    }

    @Test
    fun selectAction() {
        val expectedActions = arrayListOf(
            TestAction(2),
            TestAction(1),
            TestAction(3),
            TestAction(1),
            TestAction(2)
        )
        val actualActions = ArrayList<TestAction>()
        val agent = RandomAgent(Random(6345))
        val simulator = TestSimulator(
            initialState = TestState(0),
            legalActions = arrayListOf(arrayListOf(TestAction(1), TestAction(2), TestAction(3))),
            rewards = intArrayOf(0)
        )

        for (i in 0 until expectedActions.size) {
            actualActions.add(agent.selectAction(0, simulator.initialState, simulator))
        }

        assertThat(actualActions).isEqualTo(expectedActions)
    }
}