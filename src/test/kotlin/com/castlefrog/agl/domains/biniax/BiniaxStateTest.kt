package com.castlefrog.agl.domains.biniax

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class BiniaxStateTest {

    @Test
    fun illegalBiniaxStateSize() {
        assertThrows(IllegalArgumentException::class.java) { BiniaxState(locations = byteArrayOf()) }
    }

    @Test
    fun copy() {
        val state = BiniaxSimulator().initialState
        assertThat(state).isEqualTo(state.copy())
    }

    @Test
    fun copyModification() {
        val state = BiniaxSimulator().initialState
        val copyState = state.copy()
        state.locations[0] = -8
        assertThat(copyState.locations[0]).isNotEqualTo(-8)
    }

    @Test
    fun `hashCode value`() {
        assertThat(BiniaxState().hashCode()).isEqualTo(BiniaxState().hashCode())
    }

    @Test
    fun `toString value`() {
        val locations = byteArrayOf(
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            28, 28, 28, 0, 28,
            18, 18, 18, 18, 18,
            0, 0, 0, 0, 0,
            0, 0, 4, 0, 0
        )
        val state = BiniaxState(locations = locations)
        assertThat(state.toString()).isEqualTo(
            """
        |Turns: 0
        |Free Moves: 2
        |---------------------
        |:                   :
        |:                   :
        |:                   :
        |:B-H B-H B-H     B-H:
        |:A-H A-H A-H A-H A-H:
        |:                   :
        |:        [D]        :
        |---------------------
        """.trimMargin()
        )
    }
}