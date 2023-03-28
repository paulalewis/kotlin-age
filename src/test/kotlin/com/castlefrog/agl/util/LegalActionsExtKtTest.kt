package com.castlefrog.agl.util

import arrow.core.Either
import com.castlefrog.agl.ResultError
import com.castlefrog.agl.TestAction
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LegalActionsExtKtTest {

    @Test
    fun `playerHasLegalActions empty actions`() {
        val legalActions = emptyList<Set<TestAction>>()
        assertThat(legalActions.playerHasLegalActions(0)).isFalse()
    }

    @Test
    fun `playerHasLegalActions negative index`() {
        val legalActions = listOf(setOf(TestAction()), setOf(TestAction()))
        assertThat(legalActions.playerHasLegalActions(-1)).isFalse()
    }

    @Test
    fun `playerHasLegalActions upper bound out of bounds index`() {
        val legalActions = listOf(setOf(TestAction()), setOf(TestAction()))
        assertThat(legalActions.playerHasLegalActions(2)).isFalse()
    }

    @Test
    fun `playerHasLegalActions valid index`() {
        val legalActions = listOf(setOf(TestAction()), setOf(TestAction()))
        assertThat(legalActions.playerHasLegalActions(1)).isTrue()
    }

    @Test
    fun `getPlayerActions empty actions`() {
        val legalActions = emptyList<Set<TestAction>>()
        assertThat(legalActions.getPlayerActions(0))
            .isEqualTo(Either.Left(ResultError(message = "Player 0 has no legal actions.")))
    }

    @Test
    fun `getPlayerActions out of bounds index`() {
        val legalActions = listOf(setOf(TestAction()), setOf(TestAction()))
        assertThat(legalActions.getPlayerActions(-1))
            .isEqualTo(Either.Left(ResultError(message = "Player -1 has no legal actions.")))
    }

    @Test
    fun `getPlayerActions valid index no actions`() {
        val legalActions = listOf(setOf<TestAction>(), setOf())
        assertThat(legalActions.getPlayerActions(1))
            .isEqualTo(Either.Left(ResultError(message = "Player 1 has no legal actions.")))
    }

    @Test
    fun `getPlayerActions valid index`() {
        val legalActions = listOf(setOf(TestAction()), setOf(TestAction()))
        assertThat(legalActions.getPlayerActions(1))
            .isEqualTo(Either.Right(setOf(TestAction())))
    }
}
