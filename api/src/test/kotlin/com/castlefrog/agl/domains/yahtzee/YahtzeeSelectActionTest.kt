package com.castlefrog.agl.domains.yahtzee

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class YahtzeeSelectActionTest {

    @Test
    fun testValueOfIdentical() {
        assertThat(YahtzeeSelectAction.valueOf(1)).isSameAs(YahtzeeSelectAction.valueOf(1))
    }

    @Test
    fun testValueOfIdentical2() {
        assertThat(YahtzeeSelectAction.valueOf(YahtzeeScoreCategory.CHANCE))
                .isSameAs(YahtzeeSelectAction.valueOf(YahtzeeScoreCategory.CHANCE))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testValueOfUpperBound() {
        YahtzeeSelectAction.valueOf(20)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun testValueOfLowerBound() {
        YahtzeeSelectAction.valueOf(-1)
    }

    @Test
    fun testToString() {
        assertThat(YahtzeeSelectAction.valueOf(YahtzeeScoreCategory.FIVES).toString())
                .isEqualTo("FIVES")
    }
}