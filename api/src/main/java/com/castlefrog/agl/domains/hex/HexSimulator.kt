package com.castlefrog.agl.domains.hex

import com.castlefrog.agl.AdversarialSimulator
import com.castlefrog.agl.IllegalActionException
import com.castlefrog.agl.TurnType
import java.util.ArrayList
import java.util.HashSet
import java.util.Stack

class HexSimulator : AdversarialSimulator<HexState, HexAction> {

    val turnType: TurnType = TurnType.SEQUENTIAL

    private constructor(state: HexState) {
        legalActions_ = ArrayList<List<HexAction>>()
        legalActions_.add(ArrayList<HexAction>())
        legalActions_.add(ArrayList<HexAction>())
        setState(state)
    }

    private constructor(simulator: HexSimulator) : super(simulator) {
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
            if (prevAction != null && state_.isForthMoveOrLater()) {
                legalActions.remove(prevAction)
            } else {
                legalActions.clear()
                var i = 0
                while (i < state_.boardSize) {
                    var j = 0
                    while (j < state_.boardSize) {
                        if (state_.isLocationEmpty(i, j) || state_.isFirstMove()) {
                            legalActions.add(HexAction.valueOf(i, j))
                        }
                        j += 1
                    }
                    i += 1
                }
            }
        } else {
            clearLegalActions()
        }
    }

    private fun computeRewards() {
        val locations = state_.locations
        val visited = Array(state_.boardSize) { BooleanArray(state_.boardSize) }
        var i = 0
        while (i < state_.boardSize) {
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
            i += 1
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

    private val nextAgentTurn: Int
        get() = (state_.agentTurn + 1) % AdversarialSimulator.N_AGENTS

    companion object {
        private val MIN_BOARD_SIZE = 1

        fun create(boardSize: Int, turnType: TurnType): HexSimulator {
            if (boardSize < MIN_BOARD_SIZE) {
                throw IllegalArgumentException("Invalid board size: " + boardSize)
            }
            return HexSimulator(getInitialState(boardSize))
        }

        fun getInitialState(boardSize: Int): HexState {
            return HexState(boardSize, Array(AdversarialSimulator.N_AGENTS) { ByteArray((boardSize * boardSize + java.lang.Byte.SIZE - 1) / java.lang.Byte.SIZE) }, HexState.TURN_BLACK.toByte())
        }


        fun winningConnection(hexState: HexState): Set<Pair<Int, Int>> {
            val connection = HashSet<Pair<Int, Int>>()
            val simulator = HexSimulator(hexState)
            if (simulator.rewards[0] != AdversarialSimulator.REWARDS_NEUTRAL[0] &&
                    simulator.rewards[1] != AdversarialSimulator.REWARDS_NEUTRAL[1]) {
                val state = hexState.copy()
                var i = 0
                while (i < hexState.boardSize) {
                    var j = 0
                    while (j < hexState.boardSize) {
                        val location = state.getLocation(i, j)
                        if (!state.isLocationEmpty(i, j) && location != state.agentTurn + 1) {
                            state.setLocation(i, j, HexState.LOCATION_EMPTY)
                            simulator.state = state
                            if (!simulator.isTerminalState) {
                                connection.add(Pair(i, j))
                                state.setLocation(i, j, location)
                            }
                        }
                        j += 1
                    }
                    i += 1
                }
            }
            return connection
        }

    }
}
