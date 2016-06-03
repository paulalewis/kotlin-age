package com.castlefrog.agl.domains.backgammon

import com.google.common.truth.Truth
import org.junit.Test


class BackgammonMoveTest {

    @Test
    fun testCompare() {
        Truth.assertThat(BackgammonMove.valueOf(1, 1)).isEquivalentAccordingToCompareTo(BackgammonMove.valueOf(1, 1))
    }

    @Test
    fun testCompareLessThan() {
        Truth.assertThat(BackgammonMove.valueOf(1, 1)).isLessThan(BackgammonMove.valueOf(2, 1))
    }

    @Test
    fun testCompareLessThan2() {
        Truth.assertThat(BackgammonMove.valueOf(1, 1)).isLessThan(BackgammonMove.valueOf(1, 2))
    }

    @Test
    fun testCompareGreaterThan() {
        Truth.assertThat(BackgammonMove.valueOf(2, 1)).isGreaterThan(BackgammonMove.valueOf(1, 2))
    }

    @Test
    fun testCompareGreaterThan2() {
        Truth.assertThat(BackgammonMove.valueOf(1, 4)).isGreaterThan(BackgammonMove.valueOf(1, 2))
    }

    @Test
    fun testValueOf() {
        Truth.assertThat(BackgammonMove.valueOf(3, 3)).isSameAs(BackgammonMove.valueOf(3, 3))
    }

    @Test
    fun testToString() {
        Truth.assertThat(BackgammonMove.valueOf(1, 3).toString()).isEqualTo("1/3")
    }
}