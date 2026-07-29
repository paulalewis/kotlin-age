package com.castlefrog.agl.util

import com.castlefrog.agl.TestAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LegalActionsExtKtTest {

    @Test
    fun `playerHasLegalActions empty actions`() {
        val legalActions = emptyList<Set<TestAction>>()
        assertFalse(legalActions.playerHasLegalActions(0))
    }

    @Test
    fun `playerHasLegalActions negative index`() {
        val legalActions = listOf(setOf(TestAction()), setOf(TestAction()))
        assertFalse(legalActions.playerHasLegalActions(-1))
    }

    @Test
    fun `playerHasLegalActions upper bound out of bounds index`() {
        val legalActions = listOf(setOf(TestAction()), setOf(TestAction()))
        assertFalse(legalActions.playerHasLegalActions(2))
    }

    @Test
    fun `playerHasLegalActions valid index`() {
        val legalActions = listOf(setOf(TestAction()), setOf(TestAction()))
        assertTrue(legalActions.playerHasLegalActions(1))
    }

    @Test
    fun `getPlayerActions empty actions`() {
        val legalActions = emptyList<Set<TestAction>>()
        assertNull(legalActions.getPlayerActions(0))
    }

    @Test
    fun `getPlayerActions out of bounds index`() {
        val legalActions = listOf(setOf(TestAction()), setOf(TestAction()))
        assertNull(legalActions.getPlayerActions(-1))
    }

    @Test
    fun `getPlayerActions valid index no actions`() {
        val legalActions = listOf(setOf<TestAction>(), setOf())
        assertNull(legalActions.getPlayerActions(1))
    }

    @Test
    fun `getPlayerActions valid index`() {
        val legalActions = listOf(setOf(TestAction()), setOf(TestAction()))
        assertEquals(setOf(TestAction()), legalActions.getPlayerActions(1))
    }
}
