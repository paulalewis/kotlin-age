package com.castlefrog.agl

class TestSimulator(
        override val nPlayers: Int = 2,
        override val initialState: TestState,
        private val legalActions: List<List<TestAction>>,
        private val rewards: IntArray,
        private val testStateTransition: (TestState, Map<Int, TestAction>) -> TestState = { _, _ -> initialState }) : Simulator<TestState, TestAction> {

    override fun calculateRewards(state: TestState): IntArray {
        return rewards
    }

    override fun calculateLegalActions(state: TestState): List<List<TestAction>> {
        return legalActions
    }

    override fun stateTransition(state: TestState, actions: Map<Int, TestAction>): TestState {
        return testStateTransition(state, actions)
    }

}
