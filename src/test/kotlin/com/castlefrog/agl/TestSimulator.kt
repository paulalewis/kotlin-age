package com.castlefrog.agl

import arrow.core.Option

class TestSimulator(
    override val initialState: TestState,
    private val legalActions: List<Set<TestAction>>,
    private val rewards: IntArray,
    private val testStateTransition: (TestState, List<Option<TestAction>>) -> TestState = { _, _ -> initialState }
) : Simulator<TestState, TestAction> {

    override fun calculateRewards(state: TestState): IntArray {
        return rewards
    }

    override fun calculateLegalActions(state: TestState): List<Set<TestAction>> {
        return legalActions
    }

    override fun stateTransition(state: TestState, actions: List<Option<TestAction>>): TestState {
        return testStateTransition(state, actions)
    }

    override fun numberOfPlayers(): Int = legalActions.size.coerceAtLeast(1)
}
