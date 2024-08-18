package com.castlefrog.agl.domains.hex

import arrow.core.Option
import com.castlefrog.agl.Simulator
import com.castlefrog.agl.domains.AdversarialRewards
import com.castlefrog.agl.domains.nextPlayerTurnSequential

class HexSimulator(
    private val boardSize: Int = 11,
    private val pieRule: Boolean = true
) : Simulator<HexState, HexAction> {

    private val actions = HexAction.generateActions(boardSize)

    init {
        if (boardSize !in MIN_BOARD_SIZE until MAX_BOARD_SIZE) {
            throw IllegalArgumentException("Invalid board size: $boardSize")
        }
    }

    override val initialState: HexState
        get() = HexState(boardSize = boardSize)

    override fun calculateRewards(state: HexState): IntArray {
        val locations = state.locations
        val visited = Array(state.boardSize) { BooleanArray(state.boardSize) }
        for (i in 0 until state.boardSize) {
            if (locations[0][i].toInt() == HexState.LOCATION_BLACK && !visited[0][i]) {
                if (dfsSides(0, i, locations, visited, actions) and 3 == 3) {
                    return AdversarialRewards.BLACK_WINS
                }
            }
            if (locations[i][0].toInt() == HexState.LOCATION_WHITE && !visited[i][0]) {
                if (dfsSides(i, 0, locations, visited, actions) and 12 == 12) {
                    return AdversarialRewards.WHITE_WINS
                }
            }
        }
        return AdversarialRewards.NEUTRAL
    }

    override fun calculateLegalActions(state: HexState): List<Set<HexAction>> {
        val legalActions = ArrayList<MutableSet<HexAction>>()
        legalActions.add(mutableSetOf())
        legalActions.add(mutableSetOf())
        val rewards = calculateRewards(state)
        if (rewards.contentEquals(AdversarialRewards.NEUTRAL)) {
            (0 until state.boardSize).forEach { i ->
                (0 until state.boardSize)
                    .filter { state.isLocationEmpty(i, it) || (pieRule && isSecondMove(state)) }
                    .forEach { legalActions[state.agentTurn.toInt()].add(actions[i][it]) }
            }
        }
        return legalActions
    }

    override fun stateTransition(state: HexState, actions: List<Option<HexAction>>): HexState {
        val action = actions[state.agentTurn.toInt()].orNull()
        val legalActions = calculateLegalActions(state)
        if (action === null || !legalActions[state.agentTurn.toInt()].contains(action)) {
            throw IllegalArgumentException("Illegal action, $action, from state, $state")
        }
        val x = action.x.toInt()
        val y = action.y.toInt()
        if (state.isLocationEmpty(x, y)) {
            state.setLocation(x, y, state.agentTurn + 1)
            state.agentTurn = nextPlayerTurnSequential(state.agentTurn.toInt(), NUMBER_OF_PLAYERS).toByte()
        } else {
            state.setLocation(x, y, 0)
            state.setLocation(y, x, state.agentTurn + 1)
            state.agentTurn = nextPlayerTurnSequential(state.agentTurn.toInt(), NUMBER_OF_PLAYERS).toByte()
        }
        return state
    }

    override fun numberOfPlayers(): Int = NUMBER_OF_PLAYERS

    companion object {
        private const val NUMBER_OF_PLAYERS = 2
        private const val MIN_BOARD_SIZE = 3
        private const val MAX_BOARD_SIZE = 255

        private fun dfsSides(
            x0: Int,
            y0: Int,
            locations: Array<ByteArray>,
            visited: Array<BooleanArray>,
            actions: Array<Array<HexAction>>
        ): Int {
            var value = 0
            val stack = ArrayDeque<HexAction>()
            stack.addFirst(actions[x0][y0])
            visited[x0][y0] = true
            while (!stack.isEmpty()) {
                val v = stack.removeFirst()
                val x = v.x.toInt()
                val y = v.y.toInt()
                value = value or getLocationMask(x, y, actions.size)
                var i = -1
                while (i <= 1) {
                    var j = -1
                    while (j <= 1) {
                        val xi = x + i
                        val yi = y + j
                        if (i + j != 0 && xi >= 0 && yi >= 0 &&
                            xi < actions.size && yi < actions.size
                        ) {
                            if (!visited[xi][yi] && locations[xi][yi] == locations[x][y]) {
                                stack.addFirst(actions[xi][yi])
                                visited[xi][yi] = true
                            }
                        }
                        j += 1
                    }
                    i += 1
                }
            }
            return value
        }

        private fun getLocationMask(x: Int, y: Int, boardSize: Int): Int {
            var side = when (x) {
                0 -> { 1 }
                boardSize - 1 -> { 2 }
                else -> { 0 }
            }
            if (y == 0) {
                side = side or 4
            } else if (y == boardSize - 1) {
                side = side or 8
            }
            return side
        }

        private fun isSecondMove(state: HexState): Boolean {
            return state.nPieces == 1 && state.agentTurn == HexState.TURN_WHITE
        }
    }
}
