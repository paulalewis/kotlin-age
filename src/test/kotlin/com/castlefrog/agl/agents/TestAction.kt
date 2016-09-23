package com.castlefrog.agl.agents

import com.castlefrog.agl.Action

data class TestAction(val value: Int = 0): Action<TestAction> {
    override fun copy(): TestAction {
        return TestAction(value)
    }
}

