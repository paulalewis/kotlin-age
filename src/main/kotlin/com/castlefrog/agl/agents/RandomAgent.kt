package com.castlefrog.agl.agents

import com.castlefrog.agl.Action
import com.castlefrog.agl.Agent
import com.castlefrog.agl.Simulator
import com.castlefrog.agl.State
import java.util.Optional
import java.util.Random

/**
 * This agent selects an action at random (normal distribution)
 * from the list of possible actions from a given state.
 */
class RandomAgent(private val random: Random = Random()) : Agent {

    override fun <S : State<S>, A : Action<A>> selectAction(playerId: Int, state: S, simulator: Simulator<S, A>):
            Optional<A> {
        val legalActions = simulator.calculateLegalActions(state)
        return if (Agent.playerHasLegalActions(playerId, legalActions)) {
            val actions = legalActions[playerId]
            Optional.of(actions[random.nextInt(actions.size)])
        } else {
            Optional.empty()
        }
    }

    override fun toString(): String {
        return "RandomAgent"
    }

}
