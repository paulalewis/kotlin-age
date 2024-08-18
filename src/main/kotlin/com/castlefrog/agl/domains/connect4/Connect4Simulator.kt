package com.castlefrog.agl.domains.connect4

import arrow.core.Option
import com.castlefrog.agl.Simulator
import com.castlefrog.agl.domains.AdversarialRewards
import com.castlefrog.agl.util.LruCache

class Connect4Simulator : Simulator<Connect4State, Connect4Action> {

    private val columnHeightsCache = LruCache<Connect4State, IntArray>(1)
    private val rewardsCache = LruCache<Connect4State, IntArray>(1)
    private val legalActionsCache = LruCache<Connect4State, List<Set<Connect4Action>>>(1)

    override val initialState: Connect4State
        get() = Connect4State()

    override fun calculateRewards(state: Connect4State): IntArray {
        val rewards = rewardsCache[state] ?: Companion.calculateRewards(state)
        rewardsCache[state] = rewards
        return rewards
    }

    override fun calculateLegalActions(state: Connect4State): List<Set<Connect4Action>> {
        val legalActions = legalActionsCache[state] ?: calculateLegalActions(
            state,
            calculateRewards(state),
            calculateColumnHeights(state)
        )
        legalActionsCache[state] = legalActions
        return legalActions
    }

    override fun stateTransition(state: Connect4State, actions: List<Option<Connect4Action>>): Connect4State {
        val agentTurn = state.agentTurn
        val action = actions[agentTurn].orNull()
        val legalActions = calculateLegalActions(state)
        val columnHeights = calculateColumnHeights(state)
        if (action === null || !legalActions[agentTurn].contains(action)) {
            throw IllegalArgumentException("Illegal action, $action, from state, $state")
        }
        state.bitBoards[agentTurn] = state.bitBoards[agentTurn] xor (1L shl columnHeights[action.location]++)
        return state
    }

    override fun numberOfPlayers(): Int = NUMBER_OF_PLAYERS

    private fun calculateColumnHeights(state: Connect4State): IntArray {
        val columnHeights = columnHeightsCache[state] ?: Companion.calculateColumnHeights(state)
        columnHeightsCache[state] = columnHeights
        return columnHeights
    }

    companion object {
        private const val NUMBER_OF_PLAYERS = 2
        private const val ALL_LOCATIONS = (1L shl (Connect4State.HEIGHT + 1) * Connect4State.WIDTH) - 1
        private const val FIRST_COLUMN = (1L shl Connect4State.HEIGHT + 1) - 1
        private const val BOTTOM_ROW = ALL_LOCATIONS / FIRST_COLUMN
        private const val ABOVE_TOP_ROW = BOTTOM_ROW shl Connect4State.HEIGHT

        private fun calculateLegalActions(
            state: Connect4State,
            rewards: IntArray,
            columnHeights: IntArray
        ): List<Set<Connect4Action>> {
            val legalActions: List<MutableSet<Connect4Action>> = listOf(mutableSetOf(), mutableSetOf())
            if (rewards.contentEquals(AdversarialRewards.NEUTRAL)) {
                (0 until Connect4State.WIDTH)
                    .filter { 1L shl columnHeights[it] and ABOVE_TOP_ROW == 0L }
                    .forEach { legalActions[state.agentTurn].add(Connect4Action.valueOf(it)) }
            }
            return legalActions
        }

        private fun calculateRewards(state: Connect4State): IntArray {
            val height = Connect4State.HEIGHT
            for (i in 0 until NUMBER_OF_PLAYERS) {
                val bitBoard = state.bitBoards[i]
                val diagonal1 = bitBoard and (bitBoard shr height)
                val horizontal = bitBoard and (bitBoard shr height + 1)
                val diagonal2 = bitBoard and (bitBoard shr height + 2)
                val vertical = bitBoard and (bitBoard shr 1)
                if (diagonal1 and (diagonal1 shr 2 * height) or
                    (horizontal and (horizontal shr 2 * (height + 1))) or
                    (diagonal2 and (diagonal2 shr 2 * (height + 2))) or
                    (vertical and (vertical shr 2)) != 0L
                ) {
                    return if (i == 0) AdversarialRewards.BLACK_WINS else AdversarialRewards.WHITE_WINS
                }
            }
            return AdversarialRewards.NEUTRAL
        }

        private fun calculateColumnHeights(state: Connect4State): IntArray {
            val columnHeights = IntArray(Connect4State.WIDTH)
            val bitBoard = state.bitBoards[0] or state.bitBoards[1]
            for (i in 0 until Connect4State.WIDTH) {
                columnHeights[i] = (Connect4State.HEIGHT + 1) * i
                while (bitBoard and (1L shl columnHeights[i]) != 0L) {
                    columnHeights[i] += 1
                }
            }
            return columnHeights
        }

    }
}
