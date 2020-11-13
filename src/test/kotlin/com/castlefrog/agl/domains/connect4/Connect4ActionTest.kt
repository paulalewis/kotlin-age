package com.castlefrog.agl.domains.connect4

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class Connect4ActionTest {

    @Test
    fun copy() {
        val action = Connect4Action.valueOf(2)
        assertThat(action).isSameInstanceAs(action.copy())
    }

    @Test
    fun valueOf() {
        assertThat(Connect4Action.valueOf(3)).isSameInstanceAs(Connect4Action.valueOf(3))
    }

    @Test
    fun equals() {
        assertThat(Connect4Action.valueOf(4)).isNotEqualTo(Connect4Action.valueOf(3))
    }

    @Test
    fun valueOfLowerBound() {
        Connect4Action.valueOf(0)
    }

    @Test
    fun valueOfUpperBound() {
        Connect4Action.valueOf(6)
    }

    @Test
    fun valueOfIndexOutOfBoundsLowerBound() {
        assertThrows(IndexOutOfBoundsException::class.java) { Connect4Action.valueOf(-1) }
    }

    @Test
    fun valueOfIndexOutOfBoundsUpperBound() {
        assertThrows(IndexOutOfBoundsException::class.java) { Connect4Action.valueOf(8) }
    }

    @Test
    fun `toString value`() {
        assertThat(Connect4Action.valueOf(2).toString()).isEqualTo("3")
    }
}