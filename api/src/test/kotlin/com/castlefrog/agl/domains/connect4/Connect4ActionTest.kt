package com.castlefrog.agl.domains.connect4

import com.google.common.truth.Truth
import org.junit.Test

class Connect4ActionTest {

    @Test
    fun testCopy() {
        val action = Connect4Action.valueOf(2)
        Truth.assertThat(action).isSameAs(action.copy)
    }

    @Test
    fun testValueOf() {
        Truth.assertThat(Connect4Action.valueOf(3)).isSameAs(Connect4Action.valueOf(3))
    }

    @Test
    fun testEquals() {
        Truth.assertThat(Connect4Action.valueOf(4)).isNotEqualTo(Connect4Action.valueOf(3))
    }

    @Test
    fun testValueOfLowerBound() {
        Connect4Action.valueOf(0)
    }

    @Test
    fun testValueOfUpperBound() {
        Connect4Action.valueOf(7)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testValueOfIndexOutOfBoundsLowerBound() {
        Connect4Action.valueOf(-1)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testValueOfIndexOutOfBoundsUpperBound() {
        Connect4Action.valueOf(8)
    }

    @Test
    fun testToString() {
        Truth.assertThat(Connect4Action.valueOf(2)).isEqualTo("3")
    }

}