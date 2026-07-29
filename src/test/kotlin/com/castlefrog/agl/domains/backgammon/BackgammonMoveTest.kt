package com.castlefrog.agl.domains.backgammon

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class BackgammonMoveTest {

    @Test
    fun `compareTo is equal`() {
        assertEquals(0, BackgammonMove.valueOf(1, 1).compareTo(BackgammonMove.valueOf(1, 1)))
    }

    @Test
    fun `compareTo is less than`() {
        assertTrue(BackgammonMove.valueOf(1, 1).compareTo(BackgammonMove.valueOf(2, 1)) < 0)
    }

    @Test
    fun `compareTo is less than 2`() {
        assertTrue(BackgammonMove.valueOf(1, 1).compareTo(BackgammonMove.valueOf(1, 2)) < 0)
    }

    @Test
    fun `compareTo is greater than`() {
        assertTrue(BackgammonMove.valueOf(2, 1).compareTo(BackgammonMove.valueOf(1, 2)) > 0)
    }

    @Test
    fun `compareTo is greater than 2`() {
        assertTrue(BackgammonMove.valueOf(1, 4).compareTo(BackgammonMove.valueOf(1, 2)) > 0)
    }

    @Test
    fun valueOf() {
        assertSame(BackgammonMove.valueOf(3, 3), BackgammonMove.valueOf(3, 3))
    }
}