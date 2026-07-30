package com.castlefrog.agl.agents

import com.castlefrog.agl.Action
import com.castlefrog.agl.Agent
import com.castlefrog.agl.Simulator
import com.castlefrog.agl.State
import com.castlefrog.agl.util.getPlayerActions
import java.util.Scanner

class IoAgent(
    private val scanner: Scanner = Scanner(System.`in`),
) : Agent {
    override fun <S : State<S>, A : Action> selectAction(
        playerId: Int,
        state: S,
        simulator: Simulator<S, A>
    ): A? {
        val actions = simulator.calculateLegalActions(state).getPlayerActions(playerId) ?: return null
        return getAction(actions)
    }

    private fun <A> getAction(actions: Set<A>): A? {
        return try {
            while (true) {
                println("Legal Actions: $actions")
                val input = scanner.next()
                val match = actions.find { it.toString() == input }
                if (match != null) {
                    return match
                }
            }
            @Suppress("UNREACHABLE_CODE")
            null
        } catch (_: Exception) {
            null
        }
    }
}
