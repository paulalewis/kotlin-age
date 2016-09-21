package com.castlefrog.agl.domains.yahtzee

import com.google.common.truth.Truth
import org.junit.Test

class YahtzeeSelectActionTest {

    @Test
    fun testValueOfIdentical() {
        Truth.assertThat(YahtzeeSelectAction.valueOf(1)).isSameAs(YahtzeeSelectAction.valueOf(1))
    }

    @Test
    fun testValueOfIdentical2() {
        Truth.assertThat(YahtzeeSelectAction.valueOf(YahtzeeScoreCategory.CHANCE))
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
        Truth.assertThat(YahtzeeSelectAction.valueOf(YahtzeeScoreCategory.FIVES).toString()).isEqualTo("FIVES")
    }
}