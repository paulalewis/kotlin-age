package com.castlefrog.agl.domains.yahtzee

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class YahtzeeRollActionTest {

    @Test
    fun testCopy() {
        val action = YahtzeeRollAction()
        assertThat(action).isEqualTo(action.copy())
        assertThat(action).isNotSameAs(action.copy())
    }

    @Test
    fun testToString() {
        assertThat(YahtzeeRollAction().toString())
                .isEqualTo("[ 0 0 0 0 0 0 ]")
    }
}
