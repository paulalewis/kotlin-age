package com.castlefrog.agl.domains.hex

import com.castlefrog.agl.ADVERSARIAL_N_PLAYERS
import com.castlefrog.agl.ADVERSARIAL_REWARDS_BLACK_WINS
import com.castlefrog.agl.ADVERSARIAL_REWARDS_NEUTRAL
import com.castlefrog.agl.ADVERSARIAL_REWARDS_WHITE_WINS
import com.castlefrog.agl.Simulator
import com.castlefrog.agl.TurnType
import com.castlefrog.agl.nextPlayerTurnSequential
import java.util.ArrayList
import java.util.Arrays
import java.util.Stack

class HexSimulator(state: HexState,
                   legalActions: List<MutableList<HexAction>>? = null,
                   rewards: IntArray? = null,
                   val pieRule: Boolean = true) : Simulator<HexState, HexAction> {

    override var state: HexState = state
        set(value) {
            field = value
            _legalActions = null
            _rewards = null
        }

    private var _legalActions: List<MutableList<HexAction>>? = null
    override val legalActions: List<MutableList<HexAction>>
        get() {
            if (_legalActions == null) {
                _legalActions = computeLegalActions(state, rewards, pieRule)
            }
            return _legalActions ?: computeLegalActions(state, rewards, pieRule)
        }

    private var _rewards: IntArray? = null
    override val rewards: IntArray
        get() {
            if (_rewards == null) {
                _rewards = computeRewards(state)
            }
            return _rewards ?: computeRewards(state)
        }

    init {
        _legalActions = legalActions
        _rewards = rewards
    }

    override fun copy(): HexSimulator {
        return HexSimulator(state.copy(), _legalActions?.copy(), _rewards?.copyOf(), pieRule)
    }

    override fun stateTransition(actions: Map<Int, HexAction>) {
        val action = actions[state.agentTurn.toInt()]
        if (action === null || !legalActions[state.agentTurn.toInt()].contains(action)) {
            throw IllegalArgumentException("Illegal action, $action, from state, $state")
        }
        val x = action.x.toInt()
        val y = action.y.toInt()
        if (state.isLocationEmpty(x, y)) {
            state.setLocation(x, y, state.agentTurn + 1)
            state.agentTurn = nextPlayerTurnSequential(state.agentTurn.toInt(), nPlayers).toByte()
            _rewards = computeRewards(action, state)
            _legalActions = null
        } else {
            state.setLocation(x, y, 0)
            state.setLocation(y, x, state.agentTurn + 1)
            state.agentTurn = nextPlayerTurnSequential(state.agentTurn.toInt(), nPlayers).toByte()
            _rewards = computeRewards(action, state)
            _legalActions = null
        }
    }

    companion object {
        private val MIN_BOARD_SIZE = 1

        /**
         * @return hex simulator from initial board state
         */
        fun create(boardSize: Int, pieRule: Boolean): HexSimulator {
            return HexSimulator(state = getInitialState(boardSize), pieRule = pieRule)
        }

        /**
         * @param boardSize must be an int > 0
         * @return initial board state
         */
        fun getInitialState(boardSize: Int): HexState {
            if (boardSize < MIN_BOARD_SIZE) {
                throw IllegalArgumentException("Invalid board size: " + boardSize)
            }
            return HexState(boardSize,Array(ADVERSARIAL_N_PLAYERS) {
                ByteArray((boardSize * boardSize + java.lang.Byte.SIZE - 1) / java.lang.Byte.SIZE) },
                    HexState.TURN_BLACK)
        }

        private fun computeLegalActions(state: HexState, rewards: IntArray, pieRule: Boolean):
                List<MutableList<HexAction>> {
            val legalActions = ArrayList<MutableList<HexAction>>()
            legalActions.add(ArrayList<HexAction>())
            legalActions.add(ArrayList<HexAction>())
            if (Arrays.equals(rewards, ADVERSARIAL_REWARDS_NEUTRAL)) {
                for (i in 0..state.boardSize - 1) {
                    for (j in 0..state.boardSize - 1) {
                        if (state.isLocationEmpty(i, j) || (pieRule && isSecondMove(state))) {
                            legalActions[state.agentTurn.toInt()].add(HexAction.valueOf(i, j))
                        }
                    }
                }
            }
            return legalActions
        }

        private fun computeRewards(state: HexState): IntArray {
            val locations = state.locations
            val visited = Array(state.boardSize) { BooleanArray(state.boardSize) }
            for (i in 0..state.boardSize - 1) {
                if (locations[0][i].toInt() == HexState.LOCATION_BLACK && !visited[0][i]) {
                    if (dfsSides(0, i, locations, visited, state.boardSize) and 3 == 3) {
                        return ADVERSARIAL_REWARDS_BLACK_WINS
                    }
                }
                if (locations[i][0].toInt() == HexState.LOCATION_WHITE && !visited[i][0]) {
                    if (dfsSides(i, 0, locations, visited, state.boardSize) and 12 == 12) {
                        return ADVERSARIAL_REWARDS_WHITE_WINS
                    }
                }
            }
            return ADVERSARIAL_REWARDS_NEUTRAL
        }

        private fun computeRewards(action: HexAction, state: HexState): IntArray {
            val locations = state.locations
            val visited = Array(state.boardSize) { BooleanArray(state.boardSize) }
            val x = action.x.toInt()
            val y = action.y.toInt()
            val value = dfsSides(x, y, locations, visited, state.boardSize)
            if (locations[x][y].toInt() == HexState.LOCATION_BLACK && value and 3 == 3) {
                return ADVERSARIAL_REWARDS_BLACK_WINS
            } else if (locations[x][y].toInt() == HexState.LOCATION_WHITE && value and 12 == 12) {
                return ADVERSARIAL_REWARDS_WHITE_WINS
            } else {
                return ADVERSARIAL_REWARDS_NEUTRAL
            }
        }

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
