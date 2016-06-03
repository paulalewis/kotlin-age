package com.castlefrog.agl

abstract class AdversarialSimulator<S : State<S>, A : Action<A>> : Simulator<S, A> {

    override val nAgents: Int = N_AGENTS

    companion object {
        val N_AGENTS = 2
        val REWARDS_NEUTRAL = intArrayOf(0, 0)
        val REWARDS_BLACK_WINS = intArrayOf(1, -1)
        val REWARDS_WHITE_WINS = intArrayOf(-1, 1)
    }
}
