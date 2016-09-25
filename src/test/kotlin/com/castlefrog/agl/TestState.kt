package com.castlefrog.agl

import com.castlefrog.agl.State

data class TestState(val value: Int = 0): State<TestState> {
    override fun copy(): TestState {
        return TestState(value)
    }
}

