package com.castlefrog.agl.util

import com.castlefrog.agl.Action

typealias LegalActions<A> = List<Set<A>>

/**
 * Helper method for checking if a player has at least 1 legal action.
 */
fun <A : Action> LegalActions<A>.playerHasLegalActions(playerId: Int) =
    playerId in indices && this[playerId].isNotEmpty()

/**
 * Helper method to get all legal actions of a given player.
 * @return the player's legal actions, or null if the player has none
 *         (including out-of-bounds player ids)
 */
fun <A : Action> LegalActions<A>.getPlayerActions(playerId: Int): Set<A>? =
    if (playerHasLegalActions(playerId)) this[playerId] else null
