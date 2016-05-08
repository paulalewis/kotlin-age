package com.castlefrog.agl

import org.junit.Test

import com.google.common.truth.Truth.assertThat

class NoOpActionTest {
    @Test
    fun singleton() {
        assertThat(NoOpAction.instance).isSameAs(NoOpAction.instance)
    }

    @Test
    fun copy() {
        val action = NoOpAction.instance
        assertThat(action).isSameAs(action.copy())
    }
}