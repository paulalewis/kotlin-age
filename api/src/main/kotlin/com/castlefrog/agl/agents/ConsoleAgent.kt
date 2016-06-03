package com.castlefrog.agl.agents

import java.io.BufferedReader
import java.io.InputStreamReader

import com.castlefrog.agl.Action
import com.castlefrog.agl.Agent
import com.castlefrog.agl.Simulator
import com.castlefrog.agl.State

class ConsoleAgent : Agent {
    override fun <S : State<S>, A : Action<A>> selectAction(agentId: Int, state: S, simulator: Simulator<S, A>): A {
        simulator.state = state
        val legalActions = simulator.legalActions[agentId]
        println(state)
        print("Input Move (" + legalActions.size + ") (")
        for (i in legalActions.indices) {
            if (i == legalActions.size - 1) {
                print(legalActions[i].toString() + ")\n")
            } else {
                print(legalActions[i].toString() + ",")
            }
        }
        var action: A?
        do {
            val input = input
            action = matchToAction(input, legalActions)
        } while (action == null)
        return action
    }

    private fun <A : Action<A>> matchToAction(input: String, actions: List<A>): A? {
        for (action in actions) {
            if (action.toString().equals(input, ignoreCase = true)) {
                return action
            }
        }
        return null
    }

    private val input: String
        get() {
            val `in` = BufferedReader(InputStreamReader(System.`in`))
            return `in`.readLine()
        }

    override fun toString(): String {
        return javaClass.simpleName
    }

}
