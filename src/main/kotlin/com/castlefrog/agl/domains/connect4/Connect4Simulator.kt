package com.castlefrog.agl.domains.connect4

import com.castlefrog.agl.Simulator
import com.castlefrog.agl.domains.AdversarialRewards
import com.castlefrog.agl.requireLegalAction

class Connect4Simulator : Simulator<Connect4State, Connect4Action> {
    private val columnHeights: IntArray = IntArray(Connect4State.WIDTH)

    override val initialState: Connect4State
        get() = Connect4State()

    override fun calculateRewards(state: Connect4State): IntArray {
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

    override fun calculateLegalActions(state: Connect4State): List<Set<Connect4Action>> {
        val legalActions: List<MutableSet<Connect4Action>> = listOf(mutableSetOf(), mutableSetOf())
        val rewards = calculateRewards(state)
        val columnHeights = calculateColumnHeights(state)
        if (rewards.contentEquals(AdversarialRewards.NEUTRAL)) {
            (0 until Connect4State.WIDTH)
                .filter { 1L shl columnHeights[it] and ABOVE_TOP_ROW == 0L }
                .forEach { legalActions[state.agentTurn].add(Connect4Action.valueOf(it)) }
        }
        return legalActions
    }

    override fun stateTransition(state: Connect4State, actions: List<Connect4Action?>): Connect4State {
        val agentTurn = state.agentTurn
        val legalActions = calculateLegalActions(state)
        val action = requireLegalAction(actions, agentTurn, legalActions[agentTurn], state)
        val columnHeights = calculateColumnHeights(state)
        val height = columnHeights[action.location]
        state.bitBoards[agentTurn] = state.bitBoards[agentTurn] xor (1L shl height)
        return state
    }

    override fun numberOfPlayers(): Int = NUMBER_OF_PLAYERS

    private fun calculateColumnHeights(state: Connect4State): IntArray {
        val bitBoard = state.bitBoards[0] or state.bitBoards[1]
        for (i in 0 until Connect4State.WIDTH) {
            columnHeights[i] = (Connect4State.HEIGHT + 1) * i
            while (bitBoard and (1L shl columnHeights[i]) != 0L) {
                columnHeights[i] += 1
            }
        }
        return columnHeights
    }

    companion object {
        private const val NUMBER_OF_PLAYERS = 2
        private const val ALL_LOCATIONS = (1L shl (Connect4State.HEIGHT + 1) * Connect4State.WIDTH) - 1
        private const val FIRST_COLUMN = (1L shl Connect4State.HEIGHT + 1) - 1
        private const val BOTTOM_ROW = ALL_LOCATIONS / FIRST_COLUMN
        private const val ABOVE_TOP_ROW = BOTTOM_ROW shl Connect4State.HEIGHT
    }
}
