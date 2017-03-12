package com.castlefrog.agl.domains.backgammon

import com.google.common.truth.Truth
import org.junit.Test

class BackgammonStateTest {

    @Test
    fun testCopy() {
        val state = BackgammonState()
        Truth.assertThat(state).isEqualTo(state.copy())
        Truth.assertThat(state).isNotSameAs(state.copy())
    }

    @Test
    fun testCopyModification() {
        val state = BackgammonState()
        val stateCopy = state.copy()
        state.locations[0] = -1
        Truth.assertThat(stateCopy.locations[0]).isNotEqualTo(state.locations[0])
    }

    @Test
    fun testEqualsDiceOrderDifferent() {
        Truth.assertThat(BackgammonState(dice = byteArrayOf(0, 4)))
                .isEqualTo(BackgammonState(dice = byteArrayOf(4, 0)))
    }

    @Test
    fun testHashCodeDiceOrderDifferent() {
        Truth.assertThat(BackgammonState(dice = byteArrayOf(0, 4)).hashCode())
                .isEqualTo(BackgammonState(dice = byteArrayOf(4, 0)).hashCode())
    }

    @Test
    fun testToString() {
        val state = BackgammonState()
        state.dice[0] = 1
        state.dice[1] = 2
        Truth.assertThat(state.toString()).isEqualTo("""
        | 0 - [3][2]
        | 5 0 0 0-3 0|-5 0 0 0 0 2 [0]
        |------------|------------
        |-5 0 0 0 3 0| 5 0 0 0 0-2 [0]
        """.trimMargin())
    }

}