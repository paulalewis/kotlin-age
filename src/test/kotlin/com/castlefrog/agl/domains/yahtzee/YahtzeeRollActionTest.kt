package com.castlefrog.agl.domains.yahtzee

import com.google.common.truth.Truth
import org.junit.Test

class YahtzeeRollActionTest {

    @Test
    fun testCopy() {
        val action = YahtzeeRollAction()
        Truth.assertThat(action).isEqualTo(action.copy())
        Truth.assertThat(action).isNotSameAs(action.copy())
    }

    @Test
    fun testToString() {
        Truth.assertThat(YahtzeeRollAction().toString()).isEqualTo("[ 0 0 0 0 0 0 ]")
    }
}
