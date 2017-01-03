package com.castlefrog.agl

abstract class AdversarialSimulator<S : State<S>, A : Action<A>> : Simulator<S, A> {

    override val nPlayers: Int = N_PLAYERS

    companion object {
        val N_PLAYERS = 2
        val REWARDS_NEUTRAL = intArrayOf(0, 0)
        val REWARDS_BLACK_WINS = intArrayOf(1, -1)
        val REWARDS_WHITE_WINS = intArrayOf(-1, 1)
    }
}
