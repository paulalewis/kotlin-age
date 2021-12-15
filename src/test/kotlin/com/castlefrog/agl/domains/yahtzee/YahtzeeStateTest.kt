package com.castlefrog.agl.domains.yahtzee

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class YahtzeeStateTest {

    @Test
    fun testCopy() {
        val yahtzeeState = YahtzeeState(diceValues = ByteArray(YahtzeeState.N_DICE, Int::toByte))
        assertThat(yahtzeeState).isEqualTo(yahtzeeState.copy())
        assertThat(yahtzeeState).isNotSameInstanceAs(yahtzeeState.copy())
    }

    @Test
    fun testEqualityNotEqual() {
        val yahtzeeState = YahtzeeState(diceValues = ByteArray(YahtzeeState.N_DICE, Int::toByte))
        val otherYahtzeeState = YahtzeeState(diceValues = ByteArray(YahtzeeState.N_DICE, Int::toByte), nRolls = 2)
        assertThat(yahtzeeState).isNotEqualTo(otherYahtzeeState)
    }

    @Test
    fun testToString() {
        val yahtzeeState = YahtzeeState(
            diceValues = ByteArray(YahtzeeState.N_DICE, Int::toByte),
            nRolls = 1, scores = IntArray(YahtzeeState.N_SCORES)
        )
        assertThat(yahtzeeState.toString())
            .isEqualTo(
                """
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
                    """.trimMargin()
            )
    }

    @Test
    fun testToString2() {
        val yahtzeeState = YahtzeeState(diceValues = ByteArray(YahtzeeState.N_DICE, Int::toByte), nRolls = 1)
        assertThat(yahtzeeState.toString())
            .isEqualTo(
                """
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
                    """.trimMargin()
            )
    }
}
