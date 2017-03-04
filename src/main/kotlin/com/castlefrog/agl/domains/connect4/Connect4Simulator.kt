package com.castlefrog.agl.domains.connect4

import com.castlefrog.agl.domains.ADVERSARIAL_REWARDS_BLACK_WINS
import com.castlefrog.agl.domains.ADVERSARIAL_REWARDS_NEUTRAL
import com.castlefrog.agl.domains.ADVERSARIAL_REWARDS_WHITE_WINS
import com.castlefrog.agl.Simulator
import com.castlefrog.agl.util.LruCache

import java.util.ArrayList
import java.util.Arrays

class Connect4Simulator : Simulator<Connect4State, Connect4Action> {

    private val columnHeightsCache = LruCache<Connect4State, IntArray>(1)

    override val nPlayers: Int = N_PLAYERS

    override fun getInitialState(): Connect4State {
        return Connect4State()
    }

    override fun calculateRewards(state: Connect4State): IntArray {
        val height = Connect4State.HEIGHT
        for (i in 0..N_PLAYERS - 1) {
            val bitBoard = state.bitBoards[i]
            val diagonal1 = bitBoard and (bitBoard shr height)
            val horizontal = bitBoard and (bitBoard shr height + 1)
            val diagonal2 = bitBoard and (bitBoard shr height + 2)
            val vertical = bitBoard and (bitBoard shr 1)
            if (diagonal1 and (diagonal1 shr 2 * height) or
                    (horizontal and (horizontal shr 2 * (height + 1))) or
                    (diagonal2 and (diagonal2 shr 2 * (height + 2))) or
                    (vertical and (vertical shr 2)) != 0L) {
                if (i == 0) {
                    return ADVERSARIAL_REWARDS_BLACK_WINS
                } else {
                    return ADVERSARIAL_REWARDS_WHITE_WINS
                }
            }
        }
        return ADVERSARIAL_REWARDS_NEUTRAL
    }

    override fun calculateLegalActions(state: Connect4State): List<List<Connect4Action>> {
        val legalActions = ArrayList<MutableList<Connect4Action>>()
        legalActions.add(ArrayList<Connect4Action>())
        legalActions.add(ArrayList<Connect4Action>())
        val columnHeights = getColumnHeights(state)
        val rewards = calculateRewards(state)
        if (Arrays.equals(rewards, ADVERSARIAL_REWARDS_NEUTRAL)) {
            for (i in 0..Connect4State.WIDTH - 1) {
                if (1L shl columnHeights[i] and ABOVE_TOP_ROW == 0L) {
                    legalActions[state.agentTurn].add(Connect4Action.valueOf(i))
                }
            }
        }
        return legalActions
    }

    override fun stateTransition(state: Connect4State, actions: Map<Int, Connect4Action>): Connect4State {
        val agentTurn = state.agentTurn
        val action = actions[agentTurn]
        val legalActions = calculateLegalActions(state)
        val columnHeights = getColumnHeights(state)
        if (action === null || !legalActions[agentTurn].contains(action)) {
            throw IllegalArgumentException("Illegal action, $action, from state, $state")
        }
        state.bitBoards[agentTurn] = state.bitBoards[agentTurn] xor (1L shl columnHeights[action.location]++)
        return state
    }

    private fun getColumnHeights(state: Connect4State): IntArray {
        val columnHeights = columnHeightsCache[state] ?: calculateColumnHeights(state)
        columnHeightsCache[state] = columnHeights
        return columnHeights
    }

    companion object {
        private val N_PLAYERS = 2
        private val ALL_LOCATIONS = (1L shl (Connect4State.HEIGHT + 1) * Connect4State.WIDTH) - 1
        private val FIRST_COLUMN = (1L shl Connect4State.HEIGHT + 1) - 1
        private val BOTTOM_ROW = ALL_LOCATIONS / FIRST_COLUMN
        private val ABOVE_TOP_ROW = BOTTOM_ROW shl Connect4State.HEIGHT

        private fun calculateLegalActions(action: Connect4Action,
                                          state: Connect4State,
                                          rewards: IntArray,
                                          legalActions: List<MutableList<Connect4Action>>,
                                          columnHeights: IntArray) {
            legalActions[state.agentTurn].addAll(legalActions[(state.agentTurn + 1) % 2])
            legalActions[(state.agentTurn + 1) % 2].clear()
            if (Arrays.equals(rewards, ADVERSARIAL_REWARDS_NEUTRAL)) {
                if (1L shl columnHeights[action.location] and ABOVE_TOP_ROW != 0L) {
                    legalActions[state.agentTurn].remove(action)
                }
            } else {
                legalActions[0].clear()
                legalActions[1].clear()
            }
        }

        private fun calculateColumnHeights(state: Connect4State): IntArray {
            val columnHeights = IntArray(Connect4State.WIDTH)
            val bitBoard = state.bitBoards[0] or state.bitBoards[1]
            for (i in 0..Connect4State.WIDTH - 1) {
                columnHeights[i] = (Connect4State.HEIGHT + 1) * i
                while (bitBoard and (1L shl columnHeights[i]) != 0L) {
                    columnHeights[i] += 1
                }
            }
            return columnHeights
        }

    }
}
