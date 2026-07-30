package com.castlefrog.agl.domains

import kotlin.random.Random

object AdversarialRewards {
    private val NEUTRAL = intArrayOf(0, 0)
    private val BLACK_WINS = intArrayOf(1, -1)
    private val WHITE_WINS = intArrayOf(-1, 1)

    fun neutral(): IntArray {
        return NEUTRAL.copyOf()
    }

    fun blackWins(): IntArray {
        return BLACK_WINS.copyOf()
    }

    fun whiteWins(): IntArray {
        return WHITE_WINS.copyOf()
    }
}

fun nextPlayerTurnSequential(playerTurn: Int, nPlayers: Int): Int {
    return (playerTurn + 1) % nPlayers
}

fun nextPlayerTurnRandom(random: Random, nPlayers: Int): Int {
    return random.nextInt(nPlayers)
}
