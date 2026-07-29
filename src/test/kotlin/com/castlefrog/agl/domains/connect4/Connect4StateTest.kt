package com.castlefrog.agl.domains.connect4

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

class Connect4StateTest {

    @Test
    fun emptyStateAgentTurn0() {
        assertEquals(0, Connect4State().agentTurn)
    }

    @Test
    fun stateOneMoveAgentTurn1() {
        val state = Connect4State(longArrayOf(1L, 0))
        assertEquals(1, state.agentTurn)
    }

    @Test
    fun stateTwoMoveAgentTurn0() {
        val state = Connect4State(longArrayOf(1L, 1L shl 14))
        assertEquals(0, state.agentTurn)
    }

    @Test
    fun copy() {
        val state = Connect4State(longArrayOf(1L, 1L shl 14))
        assertEquals(state.copy(), state)
        assertNotSame(state.copy(), state)
    }

    @Test
    fun copyChange() {
        val state = Connect4State()
        val copyState = state.copy()
        state.bitBoards[0] = 1L
        assertEquals(Connect4State(), copyState)
    }

    @Test
    fun notEqualsNull() {
        assertNotEquals(null, Connect4State())
    }

    @Test
    fun notEqualsAny() {
        assertNotEquals(Any(), Connect4State())
    }

    @Test
    fun notEquals() {
        assertNotEquals(Connect4State(longArrayOf(1L, 0)), Connect4State())
    }

    @Test
    fun `hashCode value`() {
        assertEquals(961, Connect4State().hashCode())
    }

    @Test
    fun `toString value`() {
        assertEquals("""
        |-----------------
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: X - O - - - - :
        |-----------------
        """.trimMargin(), Connect4State(longArrayOf(1L, 1L shl 14)).toString())
    }
}