package com.castlefrog.agl

abstract class AdversarialSimulator<S : State<S>, A : Action> : AbstractSimulator<S, A> {

    protected constructor() {
    }

    protected constructor(simulator: AdversarialSimulator<S, A>) : super(simulator) {
    }

    override fun getNAgents(): Int {
        return N_AGENTS
    }

    companion object {
        protected val N_AGENTS = 2
        protected val REWARDS_NEUTRAL = intArrayOf(0, 0)
        protected val REWARDS_BLACK_WINS = intArrayOf(1, -1)
        protected val REWARDS_WHITE_WINS = intArrayOf(-1, 1)
    }
}
