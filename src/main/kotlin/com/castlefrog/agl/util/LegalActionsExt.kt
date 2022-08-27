package com.castlefrog.agl.util

import com.castlefrog.agl.Action

typealias LegalActions<A> = List<Set<A>>

/**
 * Helper method for checking if a player has at least 1 legal action
 * @receiver legalActions list of legal actions from a given state in the simulator
 * @param playerId id of the player to select
 * @return true if the given player has at least 1 legal action
 */
fun <A : Action<A>> LegalActions<A>.playerHasLegalActions(playerId: Int): Boolean {
    if (playerId < 0 || playerId >= this.size) {
        return false
    }
    val actions = this[playerId]
    if (actions.isEmpty()) {
        return false
    }
    return true
}

fun <A : Action<A>> LegalActions<A>.getPlayerActions(playerId: Int): Set<A> {
    if (!playerHasLegalActions(playerId)) {
        throw IllegalStateException("Player $playerId has no legal actions")
    }
    return this[playerId]
}
