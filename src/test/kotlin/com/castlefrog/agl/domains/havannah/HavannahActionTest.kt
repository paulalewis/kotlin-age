package com.castlefrog.agl.domains.havannah

import org.junit.Test

import com.google.common.truth.Truth.assertThat

class HavannahActionTest {

    @Test
    fun testCopy() {
        val action = HavannahAction.valueOf(0, 1)
        assertThat(action).isSameAs(action.copy())
    }

    @Test
    fun testToString() {
        assertThat(HavannahAction.valueOf(1, 4).toString()).isEqualTo("B4")
    }

    @Test
    fun testValueOfIdentity() {
        val action = HavannahAction.valueOf(0, 1)
        val action2 = HavannahAction.valueOf(0, 1)
        assertThat(action).isSameAs(action2)
    }

    @Test
    fun testValueOfNotEqual() {
        val action = HavannahAction.valueOf(0, 1)
        val action2 = HavannahAction.valueOf(1, 1)
        assertThat(action).isNotEqualTo(action2)
    }
}
