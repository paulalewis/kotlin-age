package com.castlefrog.agl.domains.connect4

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class Connect4ActionTest {

    @Test
    fun testCopy() {
        val action = Connect4Action.valueOf(2)
        assertThat(action).isSameAs(action.copy())
    }

    @Test
    fun testValueOf() {
        assertThat(Connect4Action.valueOf(3)).isSameAs(Connect4Action.valueOf(3))
    }

    @Test
    fun testEquals() {
        assertThat(Connect4Action.valueOf(4)).isNotEqualTo(Connect4Action.valueOf(3))
    }

    @Test
    fun testValueOfLowerBound() {
        Connect4Action.valueOf(0)
    }

    @Test
    fun testValueOfUpperBound() {
        Connect4Action.valueOf(6)
    }

    @Test
    fun testValueOfIndexOutOfBoundsLowerBound() {
        assertThrows(IndexOutOfBoundsException::class.java, { Connect4Action.valueOf(-1) })
    }

    @Test
    fun testValueOfIndexOutOfBoundsUpperBound() {
        assertThrows(IndexOutOfBoundsException::class.java, { Connect4Action.valueOf(8) })
    }

    @Test
    fun testToString() {
        assertThat(Connect4Action.valueOf(2).toString()).isEqualTo("3")
    }

}