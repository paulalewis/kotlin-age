package com.castlefrog.agl.domains.backgammon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class BackgammonMoveTest {

    @Test
    fun testCompare() {
        assertThat(BackgammonMove.valueOf(1, 1)).isEqualByComparingTo(BackgammonMove.valueOf(1, 1))
    }

    @Test
    fun testCompareLessThan() {
        assertThat(BackgammonMove.valueOf(1, 1)).isLessThan(BackgammonMove.valueOf(2, 1))
    }

    @Test
    fun testCompareLessThan2() {
        assertThat(BackgammonMove.valueOf(1, 1)).isLessThan(BackgammonMove.valueOf(1, 2))
    }

    @Test
    fun testCompareGreaterThan() {
        assertThat(BackgammonMove.valueOf(2, 1)).isGreaterThan(BackgammonMove.valueOf(1, 2))
    }

    @Test
    fun testCompareGreaterThan2() {
        assertThat(BackgammonMove.valueOf(1, 4)).isGreaterThan(BackgammonMove.valueOf(1, 2))
    }

    @Test
    fun testValueOf() {
        assertThat(BackgammonMove.valueOf(3, 3)).isSameAs(BackgammonMove.valueOf(3, 3))
    }

    @Test
    fun testToString() {
        assertThat(BackgammonMove.valueOf(1, 3).toString()).isEqualTo("1/3")
    }
}