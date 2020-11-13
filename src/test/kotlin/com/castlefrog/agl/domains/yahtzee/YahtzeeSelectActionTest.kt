package com.castlefrog.agl.domains.yahtzee

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class YahtzeeSelectActionTest {

    @Test
    fun valueOfIdentical() {
        assertThat(YahtzeeSelectAction.valueOf(1)).isSameInstanceAs(YahtzeeSelectAction.valueOf(1))
    }

    @Test
    fun valueOfIdentical2() {
        assertThat(YahtzeeSelectAction.valueOf(YahtzeeScoreCategory.CHANCE))
            .isSameInstanceAs(YahtzeeSelectAction.valueOf(YahtzeeScoreCategory.CHANCE))
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
        assertThat(YahtzeeSelectAction.valueOf(YahtzeeScoreCategory.FIVES).toString()).isEqualTo("FIVES")
    }
}