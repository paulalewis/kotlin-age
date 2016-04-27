package com.castlefrog.agl.domains.hex

import com.castlefrog.agl.AdversarialSimulator
import com.castlefrog.agl.IllegalActionException
import java.util.ArrayList
import java.util.Stack

class HexSimulator : AdversarialSimulator<HexState, HexAction> {

    val pieRule: Boolean

    private constructor(state: HexState, pieRule: Boolean) {
        this.pieRule = pieRule
        legalActions_ = ArrayList<List<HexAction>>()
        legalActions_.add(ArrayList<HexAction>())
        legalActions_.add(ArrayList<HexAction>())
        setState(state)
    }

    private constructor(simulator: HexSimulator) : super(simulator) {
        pieRule = simulator.pieRule
    }

    override fun copy(): HexSimulator {
        return HexSimulator(this)
    }

    override fun setState(state: HexState) {
        state_ = state
        computeRewards()
        computeLegalActions(null)
    }

    override fun stateTransition(actions: List<HexAction?>) {
        val action = actions[state_.agentTurn.toInt()]
        if (action == null || !legalActions_[state_.agentTurn.toInt()].contains(action)) {
            throw IllegalActionException(action, state_)
        }
        val x = action.x.toInt()
        val y = action.y.toInt()
        if (state_.isLocationEmpty(x, y)) {
            state_.setLocation(x, y, state_.agentTurn + 1)
            state_.agentTurn = nextAgentTurn.toByte()
            computeRewards(action)
            computeLegalActions(action)
        } else {
            state_.setLocation(x, y, 0)
            state_.setLocation(y, x, state_.agentTurn + 1)
            state_.agentTurn = nextAgentTurn.toByte()
            computeRewards(action)
            computeLegalActions(null)
        }
    }

    private fun computeLegalActions(prevAction: HexAction?) {
        if (rewards_ == AdversarialSimulator.REWARDS_NEUTRAL) {
            val agentTurn = state_.agentTurn.toInt()
            val otherTurn = (agentTurn + 1) % AdversarialSimulator.N_AGENTS
            legalActions_[agentTurn] = legalActions_[otherTurn]
            legalActions_[otherTurn] = ArrayList<HexAction>()
            val legalActions = legalActions_[agentTurn]
            if (prevAction != null && isForthMoveOrLater()) {
                legalActions.remove(prevAction)
            } else {
                legalActions.clear()
                for (i in 0..state_.boardSize - 1) {
                    for (j in 0..state_.boardSize - 1) {
                        if (state_.isLocationEmpty(i, j) || (pieRule && isSecondMove())) {
                            legalActions.add(HexAction.valueOf(i, j))
                        }
                    }
                }
            }
        } else {
            clearLegalActions()
        }
    }

    private fun computeRewards() {
        val locations = state_.locations
        val visited = Array(state_.boardSize) { BooleanArray(state_.boardSize) }
        for (i in 0..state_.boardSize - 1) {
            if (locations[0][i].toInt() == HexState.LOCATION_BLACK && !visited[0][i]) {
                if (dfsSides(0, i, locations, visited) and 3 == 3) {
                    rewards_ = AdversarialSimulator.REWARDS_BLACK_WINS
                    return
                }
            }
            if (locations[i][0].toInt() == HexState.LOCATION_WHITE && !visited[i][0]) {
                if (dfsSides(i, 0, locations, visited) and 12 == 12) {
                    rewards_ = AdversarialSimulator.REWARDS_WHITE_WINS
                    return
                }
            }
        }
        rewards_ = AdversarialSimulator.REWARDS_NEUTRAL
    }

    private fun computeRewards(action: HexAction) {
        val locations = state_.locations
        val visited = Array(state_.boardSize) { BooleanArray(state_.boardSize) }
        val x = action.x.toInt()
        val y = action.y.toInt()
        val value = dfsSides(x, y, locations, visited)
        if (locations[x][y].toInt() == HexState.LOCATION_BLACK && value and 3 == 3) {
            rewards_ = AdversarialSimulator.REWARDS_BLACK_WINS
        } else if (locations[x][y].toInt() == HexState.LOCATION_WHITE && value and 12 == 12) {
            rewards_ = AdversarialSimulator.REWARDS_WHITE_WINS
        } else {
            rewards_ = AdversarialSimulator.REWARDS_NEUTRAL
        }
    }

    private fun dfsSides(x0: Int,
                         y0: Int,
                         locations: Array<ByteArray>,
                         visited: Array<BooleanArray>): Int {
        var value = 0
        val stack = Stack<HexAction>()
        stack.push(HexAction.valueOf(x0, y0))
        visited[x0][y0] = true
        while (!stack.empty()) {
            val v = stack.pop()
            val x = v.x.toInt()
            val y = v.y.toInt()
            value = value or getLocationMask(x, y)
            var i = -1
            while (i <= 1) {
                var j = -1
                while (j <= 1) {
                    val xi = x + i
                    val yi = y + j
                    if (i + j != 0 && xi >= 0 && yi >= 0 &&
                            xi < state_.boardSize && yi < state_.boardSize) {
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

    private fun getLocationMask(x: Int, y: Int): Int {
        var side = 0
        if (x == 0) {
            side = side or 1
        } else if (x == state_.boardSize - 1) {
            side = side or 2
        }
        if (y == 0) {
            side = side or 4
        } else if (y == state_.boardSize - 1) {
            side = side or 8
        }
        return side
    }

    private fun isSecondMove(): Boolean {
        return state_.nPieces == 1 && state_.agentTurn == HexState.TURN_WHITE
    }

    private fun isThirdMove(): Boolean {
        return (state_.nPieces == 1 || state_.nPieces == 2) && state_.agentTurn == HexState.TURN_BLACK
    }

    private fun isForthMoveOrLater(): Boolean {
        return state_.nPieces > 2 || (state_.nPieces == 2 && state_.agentTurn == HexState.TURN_WHITE)
    }

    private val nextAgentTurn: Int
        get() = (state_.agentTurn + 1) % AdversarialSimulator.N_AGENTS

    companion object {
        private val MIN_BOARD_SIZE = 1

        /**
         * @return hex simulator from initial board state
         */
        fun create(boardSize: Int, pieRule: Boolean): HexSimulator {
            return create(getInitialState(boardSize), pieRule)
        }

        /**
         * @return hex simulator from arbitrary board state
         */
        fun create(hexState: HexState, pieRule: Boolean): HexSimulator {
            return HexSimulator(hexState, pieRule)
        }

        /**
         * @param boardSize must be an int > 0
         * @return initial board state
         */
        fun getInitialState(boardSize: Int): HexState {
            if (boardSize < MIN_BOARD_SIZE) {
                throw IllegalArgumentException("Invalid board size: " + boardSize)
            }
            return HexState(boardSize, Array(AdversarialSimulator.N_AGENTS) { ByteArray((boardSize * boardSize + java.lang.Byte.SIZE - 1) / java.lang.Byte.SIZE) }, HexState.TURN_BLACK)
        }

    }
}
