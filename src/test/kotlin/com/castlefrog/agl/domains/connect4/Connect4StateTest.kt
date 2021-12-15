package com.castlefrog.agl.domains.connect4

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class Connect4StateTest {

    @Test
    fun emptyStateAgentTurn0() {
        assertThat(Connect4State().agentTurn).isEqualTo(0)
    }

    @Test
    fun stateOneMoveAgentTurn1() {
        val state = Connect4State(longArrayOf(1L, 0))
        assertThat(state.agentTurn).isEqualTo(1)
    }

    @Test
    fun stateTwoMoveAgentTurn0() {
        val state = Connect4State(longArrayOf(1L, 1L shl 14))
        assertThat(state.agentTurn).isEqualTo(0)
    }

    @Test
    fun copy() {
        val state = Connect4State(longArrayOf(1L, 1L shl 14))
        assertThat(state).isEqualTo(state.copy())
        assertThat(state).isNotSameInstanceAs(state.copy())
    }

    @Test
    fun copyChange() {
        val state = Connect4State()
        val copyState = state.copy()
        state.bitBoards[0] = 1L
        assertThat(copyState).isEqualTo(Connect4State())
    }

    @Test
    fun notEqualsNull() {
        assertThat(Connect4State()).isNotEqualTo(null)
    }

    @Test
    fun notEqualsAny() {
        assertThat(Connect4State()).isNotEqualTo(Any())
    }

    @Test
    fun notEquals() {
        assertThat(Connect4State()).isNotEqualTo(Connect4State(longArrayOf(1L, 0)))
    }

    @Test
    fun `hashCode value`() {
        assertThat(Connect4State().hashCode()).isEqualTo(961)
    }

    @Test
    fun `toString value`() {
        assertThat(Connect4State(longArrayOf(1L, 1L shl 14)).toString()).isEqualTo(
            """
        |-----------------
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: X - O - - - - :
        |-----------------
        """.trimMargin()
        )
    }
}