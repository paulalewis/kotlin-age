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
class RandomAgent(val random: Random = Random()) : Agent {

    override fun <S : State<S>, A : Action<A>> selectAction(agentId: Int, state: S, simulator: Simulator<S, A>):
            Optional<A> {
        simulator.state = state
        val legalActions = simulator.legalActions
        if (agentId >= legalActions.size) {
            return Optional.empty()
        }
        val actions = simulator.legalActions[agentId]
        if (actions.isEmpty()) {
            return Optional.empty()
        }
        return Optional.of(actions[random.nextInt(31) % actions.size])
    }

    override fun toString(): String {
        return javaClass.simpleName
    }

}
