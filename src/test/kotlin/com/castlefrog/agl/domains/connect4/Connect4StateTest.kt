package com.castlefrog.agl.domains.connect4

import com.google.common.truth.Truth
import org.junit.Test

class Connect4StateTest {

    @Test
    fun testInitialState() {
        val initialState = Connect4State()
        Truth.assertThat(initialState.toString()).isEqualTo("""
        |-----------------
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |-----------------
        """.trimMargin())
    }

    @Test
    fun testCopy() {
        val state = Connect4State(1L, 1L shl 14, 1)
        Truth.assertThat(state).isEqualTo(state.copy())
        Truth.assertThat(state).isNotSameAs(state.copy())
    }

    @Test
    fun testCopyChange() {
        val state = Connect4State()
        val copyState = state.copy()
        state.bitBoardBlack = 1L
        Truth.assertThat(copyState).isEqualTo(Connect4State())
    }

    @Test
    fun testToString() {
        Truth.assertThat(Connect4State(1L, 1L shl 14, 0).toString()).isEqualTo("""
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