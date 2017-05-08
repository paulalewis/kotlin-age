package com.castlefrog.agl.domains.hex

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HexActionTest {

    @Test
    fun testCopy() {
        val action = HexAction.valueOf(0, 1)
        assertThat(action).isSameAs(action.copy())
    }

    @Test
    fun testToString() {
        assertThat(HexAction.valueOf(1, 4).toString()).isEqualTo("B4")
    }

    @Test
    fun testValueOfIdentity() {
        val action = HexAction.valueOf(0, 1)
        val action2 = HexAction.valueOf(0, 1)
        assertThat(action).isSameAs(action2)
    }

    @Test
    fun testValueOfNotEqual() {
        val action = HexAction.valueOf(0, 1)
        val action2 = HexAction.valueOf(1, 1)
        assertThat(action).isNotEqualTo(action2)
    }
}