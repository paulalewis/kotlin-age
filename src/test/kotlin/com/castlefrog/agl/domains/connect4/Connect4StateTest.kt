package com.castlefrog.agl.domains.connect4

import com.google.common.truth.Truth
import org.junit.Test

class Connect4StateTest {

    @Test
    fun testEmptyStateAgentTurn0() {
        Truth.assertThat(Connect4State().agentTurn).isEqualTo(0)
    }

    @Test
    fun testStateOneMoveAgentTurn1() {
        val state = Connect4State(longArrayOf(1L, 0))
        Truth.assertThat(state.agentTurn).isEqualTo(1)
    }

    @Test
    fun testStateTwoMoveAgentTurn0() {
        val state = Connect4State(longArrayOf(1L, 1L shl 14))
        Truth.assertThat(state.agentTurn).isEqualTo(0)
    }

    @Test
    fun testCopy() {
        val state = Connect4State(longArrayOf(1L, 1L shl 14))
        Truth.assertThat(state).isEqualTo(state.copy())
        Truth.assertThat(state).isNotSameAs(state.copy())
    }

    @Test
    fun testCopyChange() {
        val state = Connect4State()
        val copyState = state.copy()
        state.bitBoards[0] = 1L
        Truth.assertThat(copyState).isEqualTo(Connect4State())
    }

    @Test
    fun testNotEqualsNull() {
        Truth.assertThat(Connect4State()).isNotEqualTo(null)
    }

    @Test
    fun testNotEqualsAny() {
        Truth.assertThat(Connect4State()).isNotEqualTo(Any())
    }

    @Test
    fun testNotEquals() {
        Truth.assertThat(Connect4State()).isNotEqualTo(Connect4State(longArrayOf(1L, 0)))
    }

    @Test
    fun testHashCode() {
        Truth.assertThat(Connect4State().hashCode()).isEqualTo(961)
    }

    @Test
    fun testToString() {
        Truth.assertThat(Connect4State(longArrayOf(1L, 1L shl 14)).toString()).isEqualTo("""
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