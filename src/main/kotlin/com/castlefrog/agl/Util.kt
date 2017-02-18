package com.castlefrog.agl

val ADVERSARIAL_REWARDS_NEUTRAL = intArrayOf(0, 0)
val ADVERSARIAL_REWARDS_BLACK_WINS = intArrayOf(1, -1)
val ADVERSARIAL_REWARDS_WHITE_WINS = intArrayOf(-1, 1)

fun nextPlayerTurnSequential(playerTurn: Int, nPlayers: Int): Int {
    return (playerTurn + 1) % nPlayers
}

fun nextPlayerTurnRandom(nPlayers: Int): Int {
    return (Math.random() * nPlayers).toInt()
}
