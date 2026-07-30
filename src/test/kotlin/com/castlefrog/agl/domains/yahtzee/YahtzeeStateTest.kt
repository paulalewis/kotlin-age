package com.castlefrog.agl.domains.yahtzee

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

class YahtzeeStateTest {

    @Test
    fun testCopy() {
        val yahtzeeState = YahtzeeState(diceValues = ByteArray(YahtzeeState.N_DICE, Int::toByte))
        assertEquals(yahtzeeState.copy(), yahtzeeState)
        assertNotSame(yahtzeeState.copy(), yahtzeeState)
    }

    @Test
    fun testCopyIsolation() {
        val original = YahtzeeState(
            diceValues = ByteArray(YahtzeeState.N_DICE, Int::toByte),
            nRolls = 1,
            scores = IntArray(YahtzeeState.N_SCORES) { -1 }
        )
        val copy = original.copy()

        original.diceValues[0] = 6
        original.scores[0] = 5
        original.nRolls = 3

        assertEquals(0.toByte(), copy.diceValues[0])
        assertEquals(-1, copy.scores[0])
        assertEquals(1.toByte(), copy.nRolls)

        copy.diceValues[1] = 5
        copy.scores[1] = 10
        assertEquals(1.toByte(), original.diceValues[1])
        assertEquals(-1, original.scores[1])
    }

    @Test
    fun testEqualityNotEqual() {
        val yahtzeeState = YahtzeeState(diceValues = ByteArray(YahtzeeState.N_DICE, Int::toByte))
        val otherYahtzeeState = YahtzeeState(diceValues = ByteArray(YahtzeeState.N_DICE, Int::toByte), nRolls = 2)
        assertNotEquals(otherYahtzeeState, yahtzeeState)
    }

    @Test
    fun testToString() {
        val yahtzeeState = YahtzeeState(
            diceValues = ByteArray(YahtzeeState.N_DICE, Int::toByte),
            nRolls = 1, scores = IntArray(YahtzeeState.N_SCORES)
        )
        assertEquals("""
                    |1 - [ 0 1 2 3 4 ]
                    |ONES: 0
                    |TWOS: 0
                    |THREES: 0
                    |FOURS: 0
                    |FIVES: 0
                    |SIXES: 0
                    |THREE_OF_KIND: 0
                    |FOUR_OF_KIND: 0
                    |FULL_HOUSE: 0
                    |SMALL_STRAIGHT: 0
                    |LARGE_STRAIGHT: 0
                    |YAHTZEE: 0
                    |CHANCE: 0
                    """.trimMargin(), yahtzeeState.toString())
    }

    @Test
    fun testToString2() {
        val yahtzeeState = YahtzeeState(diceValues = ByteArray(YahtzeeState.N_DICE, Int::toByte), nRolls = 1)
        assertEquals("""
                    |1 - [ 0 1 2 3 4 ]
                    |ONES: -
                    |TWOS: -
                    |THREES: -
                    |FOURS: -
                    |FIVES: -
                    |SIXES: -
                    |THREE_OF_KIND: -
                    |FOUR_OF_KIND: -
                    |FULL_HOUSE: -
                    |SMALL_STRAIGHT: -
                    |LARGE_STRAIGHT: -
                    |YAHTZEE: -
                    |CHANCE: -
                    """.trimMargin(), yahtzeeState.toString())
    }
}
