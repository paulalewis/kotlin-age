package com.castlefrog.agl.domains

import kotlin.random.Random

/**
 * Standard two-player zero-sum reward vectors.
 *
 * Index 0 is the first player, index 1 is the second — independent of domain
 * color names (e.g. white/black in chess).
 */
object AdversarialRewards {
    private val NEUTRAL = intArrayOf(0, 0)
    private val FIRST_PLAYER_WINS = intArrayOf(1, -1)
    private val SECOND_PLAYER_WINS = intArrayOf(-1, 1)

    fun neutral(): IntArray {
        return NEUTRAL.copyOf()
    }

    /** Rewards when player 0 wins: `[1, -1]`. */
    fun firstPlayerWins(): IntArray {
        return FIRST_PLAYER_WINS.copyOf()
    }

    /** Rewards when player 1 wins: `[-1, 1]`. */
    fun secondPlayerWins(): IntArray {
        return SECOND_PLAYER_WINS.copyOf()
    }
}

fun nextPlayerTurnSequential(playerTurn: Int, nPlayers: Int): Int {
    return (playerTurn + 1) % nPlayers
}

fun nextPlayerTurnRandom(random: Random, nPlayers: Int): Int {
    return random.nextInt(nPlayers)
}
