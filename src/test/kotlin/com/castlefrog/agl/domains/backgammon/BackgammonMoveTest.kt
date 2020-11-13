package com.castlefrog.agl.domains.backgammon

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class BackgammonMoveTest {

    @Test
    fun `compareTo is equal`() {
        assertThat(BackgammonMove.valueOf(1, 1)).isEquivalentAccordingToCompareTo(BackgammonMove.valueOf(1, 1))
    }

    @Test
    fun `compareTo is less than`() {
        assertThat(BackgammonMove.valueOf(1, 1)).isLessThan(BackgammonMove.valueOf(2, 1))
    }

    @Test
    fun `compareTo is less than 2`() {
        assertThat(BackgammonMove.valueOf(1, 1)).isLessThan(BackgammonMove.valueOf(1, 2))
    }

    @Test
    fun `compareTo is greater than`() {
        assertThat(BackgammonMove.valueOf(2, 1)).isGreaterThan(BackgammonMove.valueOf(1, 2))
    }

    @Test
    fun `compareTo is greater than 2`() {
        assertThat(BackgammonMove.valueOf(1, 4)).isGreaterThan(BackgammonMove.valueOf(1, 2))
    }

    @Test
    fun valueOf() {
        assertThat(BackgammonMove.valueOf(3, 3)).isSameInstanceAs(BackgammonMove.valueOf(3, 3))
    }
}