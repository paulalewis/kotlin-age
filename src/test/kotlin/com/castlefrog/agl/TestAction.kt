package com.castlefrog.agl

data class TestAction(val value: Int = 0): Action<TestAction> {
    override fun copy(): TestAction {
        return TestAction(value)
    }
}

