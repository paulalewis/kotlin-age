package com.castlefrog.agl.domains.connect4

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Connect4StateTest {

    @Test
    fun testEmptyStateAgentTurn0() {
        assertThat(Connect4State().agentTurn).isEqualTo(0)
    }

    @Test
    fun testStateOneMoveAgentTurn1() {
        val state = Connect4State(longArrayOf(1L, 0))
        assertThat(state.agentTurn).isEqualTo(1)
    }

    @Test
    fun testStateTwoMoveAgentTurn0() {
        val state = Connect4State(longArrayOf(1L, 1L shl 14))
        assertThat(state.agentTurn).isEqualTo(0)
    }

    @Test
    fun testCopy() {
        val state = Connect4State(longArrayOf(1L, 1L shl 14))
        assertThat(state).isEqualTo(state.copy())
        assertThat(state).isNotSameAs(state.copy())
    }

    @Test
    fun testCopyChange() {
        val state = Connect4State()
        val copyState = state.copy()
        state.bitBoards[0] = 1L
        assertThat(copyState).isEqualTo(Connect4State())
    }

    @Test
    fun testNotEqualsNull() {
        assertThat(Connect4State()).isNotEqualTo(null)
    }

    @Test
    fun testNotEqualsAny() {
        assertThat(Connect4State()).isNotEqualTo(Any())
    }

    @Test
    fun testNotEquals() {
        assertThat(Connect4State()).isNotEqualTo(Connect4State(longArrayOf(1L, 0)))
    }

    @Test
    fun testHashCode() {
        assertThat(Connect4State().hashCode()).isEqualTo(961)
    }

    @Test
    fun testToString() {
        assertThat(Connect4State(longArrayOf(1L, 1L shl 14)).toString()).isEqualTo("""
        |-----------------
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: X - O - - - - :
        |-----------------
        """.trimMargin())
    }

}