package com.castlefrog.agl.domains

import kotlin.random.Random

object AdversarialRewards {
    val NEUTRAL = intArrayOf(0, 0)
    val BLACK_WINS = intArrayOf(1, -1)
    val WHITE_WINS = intArrayOf(-1, 1)
}

fun nextPlayerTurnSequential(playerTurn: Int, nPlayers: Int): Int {
    return (playerTurn + 1) % nPlayers
}

fun nextPlayerTurnRandom(random: Random, nPlayers: Int): Int {
    return random.nextInt(nPlayers)
}
