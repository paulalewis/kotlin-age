package com.castlefrog.agl.util

import arrow.core.Either
import com.castlefrog.agl.Action
import com.castlefrog.agl.ResultError

typealias LegalActions<A> = List<Set<A>>

/**
 * Helper method for checking if a player has at least 1 legal action.
 */
fun <A : Action<A>> LegalActions<A>.playerHasLegalActions(playerId: Int) =
    playerId in 0 until size && this[playerId].isNotEmpty()

/**
 * Helper method to get all legal actions of given player.
 */
fun <A : Action<A>> LegalActions<A>.getPlayerActions(playerId: Int): Either<ResultError, Set<A>> =
    if (playerHasLegalActions(playerId)) Either.Right(this[playerId]) else Either.Left(ResultError("Player $playerId has no legal actions."))
