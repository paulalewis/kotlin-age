package com.castlefrog.agl.domains.connect4

import com.castlefrog.agl.Simulator
import com.castlefrog.agl.domains.AdversarialRewards
import com.castlefrog.agl.requireLegalAction

class Connect4Simulator : Simulator<Connect4State, Connect4Action> {
    private val columnHeights: IntArray = IntArray(Connect4State.WIDTH)

    override val initialState: Connect4State
        get() = Connect4State()

    override fun calculateRewards(state: Connect4State): IntArray {
        for (i in 0 until NUMBER_OF_PLAYERS) {
            if (hasConnectFour(state.bitBoards[i])) {
                return if (i == 0) AdversarialRewards.firstPlayerWins() else AdversarialRewards.secondPlayerWins()
            }
        }
        return AdversarialRewards.neutral()
    }

    override fun calculateLegalActions(state: Connect4State): List<Set<Connect4Action>> {
        val legalActions: List<MutableSet<Connect4Action>> = listOf(mutableSetOf(), mutableSetOf())
        val rewards = calculateRewards(state)
        val columnHeights = calculateColumnHeights(state)
        if (rewards.contentEquals(AdversarialRewards.neutral())) {
            (0 until Connect4State.WIDTH)
                .filter { isPlayableHeight(columnHeights[it]) }
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
        val nextState = state.copy()
        nextState.bitBoards[agentTurn] = nextState.bitBoards[agentTurn] xor (1L shl height)
        return nextState
    }

    override fun numberOfPlayers(): Int = NUMBER_OF_PLAYERS

    private fun calculateColumnHeights(state: Connect4State): IntArray {
        val bitBoard = state.bitBoards[0] or state.bitBoards[1]
        for (i in 0 until Connect4State.WIDTH) {
            columnHeights[i] = Connect4State.COLUMN_STRIDE * i
            while ((bitBoard and (1L shl columnHeights[i])) != 0L) {
                columnHeights[i] += 1
            }
        }
        return columnHeights
    }

    companion object {
        private const val NUMBER_OF_PLAYERS = 2

        private const val ALL_LOCATIONS = (1L shl (Connect4State.COLUMN_STRIDE * Connect4State.WIDTH)) - 1
        private const val FIRST_COLUMN = (1L shl Connect4State.COLUMN_STRIDE) - 1
        private const val BOTTOM_ROW = ALL_LOCATIONS / FIRST_COLUMN
        private const val ABOVE_TOP_ROW = BOTTOM_ROW shl Connect4State.HEIGHT

        private const val VERTICAL_SHIFT = 1
        private const val HORIZONTAL_SHIFT = Connect4State.COLUMN_STRIDE
        private const val DIAGONAL_SHIFT = Connect4State.HEIGHT
        private const val ANTIDIAGONAL_SHIFT = Connect4State.HEIGHT + 2

        private fun hasConnectFour(bitBoard: Long): Boolean {
            return connects(bitBoard, VERTICAL_SHIFT) ||
                connects(bitBoard, HORIZONTAL_SHIFT) ||
                connects(bitBoard, DIAGONAL_SHIFT) ||
                connects(bitBoard, ANTIDIAGONAL_SHIFT)
        }

        private fun connects(bitBoard: Long, shift: Int): Boolean {
            val pairs = bitBoard and (bitBoard shr shift)
            return (pairs and (pairs shr (2 * shift))) != 0L
        }

        private fun isPlayableHeight(heightBit: Int): Boolean {
            return ((1L shl heightBit) and ABOVE_TOP_ROW) == 0L
        }
    }
}
