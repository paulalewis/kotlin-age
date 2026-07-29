package com.castlefrog.agl.domains.yahtzee

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test

class YahtzeeSelectActionTest {

    @Test
    fun valueOfIdentical() {
        assertSame(YahtzeeSelectAction.valueOf(1), YahtzeeSelectAction.valueOf(1))
    }

    @Test
    fun valueOfIdentical2() {
        assertSame(YahtzeeSelectAction.valueOf(YahtzeeScoreCategory.CHANCE), YahtzeeSelectAction.valueOf(YahtzeeScoreCategory.CHANCE))
    }

    @Test
    fun valueOfUpperBound() {
        assertThrows(IndexOutOfBoundsException::class.java) { YahtzeeSelectAction.valueOf(20) }
    }

    @Test
    fun valueOfLowerBound() {
        assertThrows(IndexOutOfBoundsException::class.java) { YahtzeeSelectAction.valueOf(-1) }
    }

    @Test
    fun `toString value`() {
        assertEquals("FIVES", YahtzeeSelectAction.valueOf(YahtzeeScoreCategory.FIVES).toString())
    }
}