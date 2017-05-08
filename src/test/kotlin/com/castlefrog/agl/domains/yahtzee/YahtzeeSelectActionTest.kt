package com.castlefrog.agl.domains.yahtzee

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

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

    @Test
    fun testValueOfUpperBound() {
        assertThrows(IndexOutOfBoundsException::class.java, { YahtzeeSelectAction.valueOf(20) })
    }

    @Test
    fun testValueOfLowerBound() {
        assertThrows(IndexOutOfBoundsException::class.java, { YahtzeeSelectAction.valueOf(-1) })
    }

    @Test
    fun testToString() {
        assertThat(YahtzeeSelectAction.valueOf(YahtzeeScoreCategory.FIVES).toString()).isEqualTo("FIVES")
    }
}