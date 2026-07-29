package com.castlefrog.agl.domains.backgammon

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

class BackgammonStateTest {

    @Test
    fun copy() {
        val state = BackgammonState()
        assertEquals(state.copy(), state)
        assertNotSame(state.copy(), state)
    }

    @Test
    fun copyModification() {
        val state = BackgammonState()
        val stateCopy = state.copy()
        state.locations[0] = -1
        assertNotEquals(state.locations[0], stateCopy.locations[0])
    }

    @Test
    fun equalsDiceOrderDifferent() {
        assertEquals(BackgammonState(dice = byteArrayOf(4, 0)), BackgammonState(dice = byteArrayOf(0, 4)))
    }

    @Test
    fun hashCodeDiceOrderDifferent() {
        assertEquals(BackgammonState(dice = byteArrayOf(4, 0)).hashCode(), BackgammonState(dice = byteArrayOf(0, 4)).hashCode())
    }

    @Test
    fun `toString value`() {
        val state = BackgammonState()
        state.dice[0] = 1
        state.dice[1] = 2
        assertEquals("""
        | 0 - [3][2]
        | 5 0 0 0-3 0|-5 0 0 0 0 2 [0]
        |------------|------------
        |-5 0 0 0 3 0| 5 0 0 0 0-2 [0]
        """.trimMargin(), state.toString())
    }
}