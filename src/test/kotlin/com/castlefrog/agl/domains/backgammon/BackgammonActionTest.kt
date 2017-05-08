package com.castlefrog.agl.domains.backgammon

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BackgammonActionTest {

    @Test
    fun testCopy() {
        val action = BackgammonAction(hashSetOf(BackgammonMove.valueOf(1, 2), BackgammonMove.valueOf(2, 1)))
        assertThat(action).isEqualTo(action.copy())
        assertThat(action).isNotSameAs(action.copy())
    }

    @Test
    fun testToString() {
        val action = BackgammonAction(hashSetOf(BackgammonMove.valueOf(1, 2), BackgammonMove.valueOf(2, 1)))
        assertThat(action.toString()).isEqualTo("[ 1/2 2/1 ]")
    }

}