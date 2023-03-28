package com.castlefrog.agl.agents

import arrow.core.*
import com.castlefrog.agl.ResultError
import com.castlefrog.agl.TestAction
import com.castlefrog.agl.TestSimulator
import com.castlefrog.agl.TestState
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.random.Random

class RandomAgentTest {

    @Test
    fun selectActionInvalidLegalActionsArray() {
        val agent = RandomAgent(Random(5555))
        val state = TestState()
        val simulator = TestSimulator(initialState = state, legalActions = ArrayList(), rewards = intArrayOf(0))

        val result = agent.selectAction(0, state, simulator)

        assertThat(result.swap().orNull()).isEqualTo(ResultError("Player 0 has no legal actions."))
    }

    @Test
    fun selectActionNoActions() {
        val agent = RandomAgent(Random(5555))
        val state = TestState()
        val simulator =
            TestSimulator(initialState = state, legalActions = arrayListOf(setOf()), rewards = intArrayOf(0))

        val result = agent.selectAction(0, state, simulator)

        assertThat(result.swap().orNull()).isEqualTo(ResultError("Player 0 has no legal actions."))
    }

    @Test
    fun selectAction() {
        val expectedActions = arrayListOf(
            Either.Right(TestAction(2)),
            Either.Right(TestAction(1)),
            Either.Right(TestAction(3)),
            Either.Right(TestAction(1)),
            Either.Right(TestAction(2)),
        )
        val actualActions = ArrayList<Either<ResultError, TestAction>>()
        val agent = RandomAgent(Random(6345))
        val simulator = TestSimulator(
            initialState = TestState(0),
            legalActions = arrayListOf(setOf(TestAction(1), TestAction(2), TestAction(3))),
            rewards = intArrayOf(0)
        )

        for (i in 0 until expectedActions.size) {
            actualActions.add(agent.selectAction(0, simulator.initialState, simulator))
        }

        assertThat(actualActions).isEqualTo(expectedActions)
    }
}