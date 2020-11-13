package com.castlefrog.agl

data class TestState(val value: Int = 0) : State<TestState> {
    override fun copy(): TestState = TestState(value)
}

