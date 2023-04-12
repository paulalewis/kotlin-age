package com.castlefrog.agl.agents

import arrow.core.Either
import com.castlefrog.agl.*
import com.castlefrog.agl.util.getPlayerActions
import java.util.*

class IoAgent(
    private val scanner: Scanner = Scanner(System.`in`),
) : Agent {
    override fun <S : State<S>, A : Action<A>> selectAction(
        playerId: Int,
        state: S,
        simulator: Simulator<S, A>
    ): Either<ResultError, A> {
        val result = simulator.calculateLegalActions(state).getPlayerActions(playerId)
        return result.fold({
            Either.Left(it)
        }, {
            getAction(it)
        })
    }

    private fun <A> getAction(actions: Set<A>): Either<ResultError, A> {
        try {
            do {
                println("Legal Actions: $actions")
                val input = scanner.next()
                actions.find { it.toString() == input }?.let { return Either.Right(it) }
            } while (true)
        } catch (e: Exception) {
            return Either.Left(ResultError(e.message ?: ""))
        }
    }
}