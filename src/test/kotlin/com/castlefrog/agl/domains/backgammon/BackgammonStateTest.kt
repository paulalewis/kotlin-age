package com.castlefrog.agl.domains.backgammon

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BackgammonStateTest {

    @Test
    fun copy() {
        val state = BackgammonState()
        assertThat(state).isEqualTo(state.copy())
        assertThat(state).isNotSameInstanceAs(state.copy())
    }

    @Test
    fun copyModification() {
        val state = BackgammonState()
        val stateCopy = state.copy()
        state.locations[0] = -1
        assertThat(stateCopy.locations[0]).isNotEqualTo(state.locations[0])
    }

    @Test
    fun equalsDiceOrderDifferent() {
        assertThat(BackgammonState(dice = byteArrayOf(0, 4)))
            .isEqualTo(BackgammonState(dice = byteArrayOf(4, 0)))
    }

    @Test
    fun hashCodeDiceOrderDifferent() {
        assertThat(BackgammonState(dice = byteArrayOf(0, 4)).hashCode())
            .isEqualTo(BackgammonState(dice = byteArrayOf(4, 0)).hashCode())
    }

    @Test
    fun `toString value`() {
        val state = BackgammonState()
        state.dice[0] = 1
        state.dice[1] = 2
        assertThat(state.toString()).isEqualTo("""
        | 0 - [3][2]
        | 5 0 0 0-3 0|-5 0 0 0 0 2 [0]
        |------------|------------
        |-5 0 0 0 3 0| 5 0 0 0 0-2 [0]
        """.trimMargin()
        )
    }
}