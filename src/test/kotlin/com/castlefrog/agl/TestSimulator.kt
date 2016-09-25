package com.castlefrog.agl

import com.castlefrog.agl.Simulator

class TestSimulator(override var state: TestState,
                    override val legalActions: List<MutableList<TestAction>>,
                    override val rewards: IntArray) : Simulator<TestState, TestAction> {

    override fun copy(): Simulator<TestState, TestAction> {
        return TestSimulator(state.copy(), legalActions.copy(), rewards.copyOf())
    }

    override fun stateTransition(actions: List<TestAction?>) {
        throw UnsupportedOperationException("not implemented")
    }

}
