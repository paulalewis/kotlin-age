package com.castlefrog.agl.domains.connect4

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test

class Connect4ActionTest {

    @Test
    fun valueOf() {
        assertSame(Connect4Action.valueOf(3), Connect4Action.valueOf(3))
    }

    @Test
    fun equals() {
        assertNotEquals(Connect4Action.valueOf(3), Connect4Action.valueOf(4))
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
        assertEquals("3", Connect4Action.valueOf(2).toString())
    }
}