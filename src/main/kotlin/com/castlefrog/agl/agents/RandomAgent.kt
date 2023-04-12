package com.castlefrog.agl.agents

import arrow.core.Either
import com.castlefrog.agl.*
import com.castlefrog.agl.util.getPlayerActions
import kotlin.random.Random

/**
 * This agent selects an action at random (normal distribution)
 * from the list of possible actions from a given state.
 */
class RandomAgent(private val random: Random = Random) : Agent {

    override fun <S : State<S>, A : Action<A>> selectAction(
        playerId: Int,
        state: S,
        simulator: Simulator<S, A>
    ): Either<ResultError, A> {
        val result = simulator.calculateLegalActions(state).getPlayerActions(playerId)
        return result.fold({
            Either.Left(it)
        }, {
            Either.Right(it.random(random))
        })
    }

    override fun toString(): String = RandomAgent::class.java.simpleName
}
