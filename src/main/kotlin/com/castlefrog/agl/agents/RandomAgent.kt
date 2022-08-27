package com.castlefrog.agl.agents

import com.castlefrog.agl.Action
import com.castlefrog.agl.Agent
import com.castlefrog.agl.Simulator
import com.castlefrog.agl.State
import com.castlefrog.agl.util.getPlayerActions
import kotlin.random.Random

/**
 * This agent selects an action at random (normal distribution)
 * from the list of possible actions from a given state.
 */
class RandomAgent(private val random: Random = Random) : Agent {

    override fun <S : State<S>, A : Action<A>> selectAction(playerId: Int, state: S, simulator: Simulator<S, A>): A {
        val actions = simulator.calculateLegalActions(state).getPlayerActions(playerId)
        return actions.random(random)
    }

    override fun toString(): String = RandomAgent::class.java.simpleName
}
