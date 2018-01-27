package com.castlefrog.agl.domains.hex

import com.castlefrog.agl.Simulator
import com.castlefrog.agl.domains.AdversarialRewards
import com.castlefrog.agl.domains.nextPlayerTurnSequential
import java.util.ArrayList
import java.util.Arrays
import java.util.Stack

class HexSimulator(
        val boardSize: Int = 11,
        val pieRule: Boolean = true) : Simulator<HexState, HexAction> {

    init {
        if (boardSize !in MIN_BOARD_SIZE until MAX_BOARD_SIZE) {
            throw IllegalArgumentException("Invalid board size: " + boardSize)
        }
    }

    override val nPlayers: Int = 2

    override val initialState: HexState
        get() = HexState(boardSize = boardSize)

    override fun calculateRewards(state: HexState): IntArray {
        val locations = state.locations
        val visited = Array(state.boardSize) { BooleanArray(state.boardSize) }
        for (i in 0 until state.boardSize) {
            if (locations[0][i].toInt() == HexState.LOCATION_BLACK && !visited[0][i]) {
                if (dfsSides(0, i, locations, visited, state.boardSize) and 3 == 3) {
                    return AdversarialRewards.BLACK_WINS
                }
            }
            if (locations[i][0].toInt() == HexState.LOCATION_WHITE && !visited[i][0]) {
                if (dfsSides(i, 0, locations, visited, state.boardSize) and 12 == 12) {
                    return AdversarialRewards.WHITE_WINS
                }
            }
        }
        return AdversarialRewards.NEUTRAL
    }

    override fun calculateLegalActions(state: HexState): List<MutableList<HexAction>> {
        val legalActions = ArrayList<MutableList<HexAction>>()
        legalActions.add(ArrayList())
        legalActions.add(ArrayList())
        val rewards = calculateRewards(state)
        if (Arrays.equals(rewards, AdversarialRewards.NEUTRAL)) {
            (0 until state.boardSize).forEach { i ->
                (0 until state.boardSize)
                        .filter { state.isLocationEmpty(i, it) || (pieRule && isSecondMove(state)) }
                        .forEach { legalActions[state.agentTurn.toInt()].add(HexAction.valueOf(i, it)) }
            }
        }
        return legalActions
    }

    override fun stateTransition(state: HexState, actions: Map<Int, HexAction>): HexState {
        val action = actions[state.agentTurn.toInt()]
        val legalActions = calculateLegalActions(state)
        if (action === null || !legalActions[state.agentTurn.toInt()].contains(action)) {
            throw IllegalArgumentException("Illegal action, $action, from state, $state")
        }
        val x = action.x.toInt()
        val y = action.y.toInt()
        if (state.isLocationEmpty(x, y)) {
            state.setLocation(x, y, state.agentTurn + 1)
            state.agentTurn = nextPlayerTurnSequential(state.agentTurn.toInt(), nPlayers).toByte()
        } else {
            state.setLocation(x, y, 0)
            state.setLocation(y, x, state.agentTurn + 1)
            state.agentTurn = nextPlayerTurnSequential(state.agentTurn.toInt(), nPlayers).toByte()
        }
        return state
    }

    companion object {
        private const val MIN_BOARD_SIZE = 3
        private const val MAX_BOARD_SIZE = 255

        private fun dfsSides(x0: Int,
                             y0: Int,
                             locations: Array<ByteArray>,
                             visited: Array<BooleanArray>,
                             boardSize: Int): Int {
            var value = 0
            val stack = Stack<HexAction>()
            stack.push(HexAction.valueOf(x0, y0))
            visited[x0][y0] = true
            while (!stack.empty()) {
                val v = stack.pop()
                val x = v.x.toInt()
                val y = v.y.toInt()
                value = value or getLocationMask(x, y, boardSize)
                var i = -1
                while (i <= 1) {
                    var j = -1
                    while (j <= 1) {
                        val xi = x + i
                        val yi = y + j
                        if (i + j != 0 && xi >= 0 && yi >= 0 &&
                                xi < boardSize && yi < boardSize) {
                            if (!visited[xi][yi] && locations[xi][yi] == locations[x][y]) {
                                stack.push(HexAction.valueOf(xi, yi))
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
            var side = 0
            if (x == 0) {
                side = side or 1
            } else if (x == boardSize - 1) {
                side = side or 2
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
