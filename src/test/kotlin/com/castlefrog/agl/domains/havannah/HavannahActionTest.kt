package com.castlefrog.agl.domains.havannah

import com.google.common.truth.Truth
import org.junit.Test

class HavannahActionTest {

    @Test
    fun testCopy() {
        val action = HavannahAction.valueOf(0, 1)
        Truth.assertThat(action).isSameAs(action.copy())
    }

    @Test
    fun testToString() {
        Truth.assertThat(HavannahAction.valueOf(1, 4).toString()).isEqualTo("B4")
    }

    @Test
    fun testValueOfIdentity() {
        val action = HavannahAction.valueOf(0, 1)
        val action2 = HavannahAction.valueOf(0, 1)
        Truth.assertThat(action).isSameAs(action2)
    }

    @Test
    fun testValueOfNotEqual() {
        val action = HavannahAction.valueOf(0, 1)
        val action2 = HavannahAction.valueOf(1, 1)
        Truth.assertThat(action).isNotEqualTo(action2)
    }
}
