package com.castlefrog.agl.domains.havannah

import com.castlefrog.agl.AdversarialSimulator
import com.castlefrog.agl.TurnType

import java.util.ArrayList
import java.util.Stack

class HavannahSimulator : AdversarialSimulator<HavannahState, HavannahAction> {

    val pieRule: Boolean
    /** length of a side of board  */
    private val base: Int
    /** longest row of hexagons on board (always odd)  */
    private val size: Int
    /** number of locations on board  */
    private val nLocations: Int
    private val corners: Array<IntArray>
    private val sides: Array<Array<IntArray>>

    private constructor(state: HavannahState, pieRule: Boolean) {
        this.pieRule = pieRule
        legalActions_ = ArrayList<List<HavannahAction>>()
        legalActions_.add(ArrayList<HavannahAction>())
        legalActions_.add(ArrayList<HavannahAction>())
        base = state.base
        size = state.size
        nLocations = state.nLocations
        corners = state.corners
        sides = state.sides
        setState(state)
    }

    private constructor(simulator: HavannahSimulator,
                        corners: Array<IntArray>,
                        sides: Array<Array<IntArray>>) : super(simulator) {
        base = simulator.state.base
        size = simulator.state.size
        nLocations = simulator.state.nLocations
        pieRule = simulator.pieRule
        this.corners = corners
        this.sides = sides
    }

    override fun copy(): HavannahSimulator {
        return HavannahSimulator(this, corners, sides)
    }

    override fun setState(state: HavannahState) {
        state_ = state
        computeRewards()
        computeLegalActions()
    }

    override fun stateTransition(actions: List<HavannahAction?>) {
        val action = actions[state_.agentTurn.toInt()]
        if (action == null || !legalActions_[state_.agentTurn.toInt()].contains(action)) {
            throw IllegalArgumentException("Illegal action, $action, from state, $state_")
        }
        state_.locations[action.x.toInt()][action.y.toInt()] = (state_.agentTurn + 1).toByte()
        state_.agentTurn = ((state_.agentTurn + 1) % AdversarialSimulator.N_AGENTS).toByte()
        computeRewards(action)
        computeLegalActions(action)
    }

    private fun computeLegalActions(prevAction: HavannahAction? = null) {
        if (rewards_ == AdversarialSimulator.REWARDS_NEUTRAL) {
            val agentTurn = state_.agentTurn.toInt()
            val otherTurn = (agentTurn + 1) % AdversarialSimulator.N_AGENTS
            legalActions_[agentTurn] = legalActions_[otherTurn]
            legalActions_[otherTurn] = ArrayList<HavannahAction>()
            val legalActions = legalActions_[agentTurn]
            if (prevAction != null && nLocations - legalActions.size >= 2) {
                legalActions.remove(prevAction)
            } else {
                legalActions.clear()
                var count = 0
                var tempAction: HavannahAction? = null
                for (y in 0..size - 1) {
                    var xMin = 0
                    var xMax = size
                    if (y >= base) {
                        xMin = y - base + 1
                    } else {
                        xMax = base + y
                    }

                    for (x in xMin..xMax - 1) {
                        if (state_.isLocationEmpty(x, y)) {
                            legalActions.add(HavannahAction.valueOf(x, y))
                        } else if (state_.agentTurn == HavannahState.TURN_WHITE && count == 0) {
                            count = 1
                            tempAction = HavannahAction.valueOf(x, y)
                        } else if (state_.agentTurn == HavannahState.TURN_WHITE && count == 1) {
                            count = 2
                            tempAction = null
                        }
                    }
                }
                if (tempAction != null) {
                    legalActions.add(tempAction)
                }
            }
        } else {
            for (legalActions in legalActions_) {
                legalActions.clear()
            }
        }
    }

    private fun computeRewards(prevAction: HavannahAction? = null) {
        var visited = Array(size) { BooleanArray(size) }
        var yMin = 0
        var xMin = 0
        var yMax = size
        var xMax = size
        if (prevAction != null) {
            xMin = prevAction.x.toInt()
            yMin = prevAction.y.toInt()
            xMax = xMin + 1
            yMax = yMin + 1
        }
        for (y in yMin..yMax - 1) {
            for (x in xMin..xMax - 1) {
                // Checks: non empty location - hasn't been visited
                if (!state_.isLocationEmpty(x, y) && !visited[x][y]) {
                    var result = dfsCornersSides(x, y, state_, visited)
                    // count corners
                    var corners = 0
                    for (k in 0..5) {
                        if (result % 2 == 1) {
                            corners += 1
                        }
                        result = result shr 1
                    }
                    // count sides
                    var sides = 0
                    for (k in 0..5) {
                        if (result % 2 == 1) {
                            sides += 1
                        }
                        result = result shr 1
                    }
                    if (corners >= 2 || sides >= 3) {
                        if (state_.locations[x][y] == HavannahState.LOCATION_BLACK) {
                            rewards_ = AdversarialSimulator.REWARDS_BLACK_WINS
                            return
                        } else {
                            rewards_ = AdversarialSimulator.REWARDS_WHITE_WINS
                            return
                        }
                    }
                }
            }
        }

        val otherState = state_.copy()
        visited = Array(size) { BooleanArray(size) }
        for (y in 0..otherState.locations.size - 1) {
            xMin = 0
            xMax = size
            if (y >= base) {
                xMin = y - base + 1
            } else {
                xMax = base + y
            }
            for (x in xMin..xMax - 1) {
                if (otherState.isLocationEmpty(x, y) || otherState.locations[x][y] == (otherState.agentTurn + 1).toByte()) {
                    otherState.locations[x][y] = HavannahState.LOCATION_BLACK
                } else {
                    otherState.locations[x][y] = HavannahState.LOCATION_EMPTY
                }
            }
        }

        yMin = 0
        xMin = 0
        yMax = size
        xMax = size
        if (prevAction != null) {
            xMin = Math.max(prevAction.x - 1, 0)
            yMin = Math.max(prevAction.y - 1, 0)
            xMax = Math.min(prevAction.x + 2, size)
            yMax = Math.min(prevAction.y + 2, size)
        }

        for (y in yMin..yMax - 1) {
            for (x in xMin..xMax - 1) {
                if (!otherState.isLocationEmpty(x, y) && !visited[x][y]) {
                    if (dfsCornersSides(x, y, otherState, visited) == 0) {
                        if (otherState.agentTurn == HavannahState.TURN_BLACK) {
                            rewards_ = AdversarialSimulator.REWARDS_WHITE_WINS
                            return
                        } else {
                            rewards_ = AdversarialSimulator.REWARDS_BLACK_WINS
                            return
                        }
                    }
                }
            }
        }
        rewards_ = AdversarialSimulator.REWARDS_NEUTRAL
    }

    private fun dfsCornersSides(x0: Int,
                                y0: Int,
                                state: HavannahState,
                                visited: Array<BooleanArray>): Int {
        var value = 0
        val stack = Stack<HavannahAction>()
        stack.push(HavannahAction.valueOf(x0, y0))
        visited[x0][y0] = true
        while (!stack.empty()) {
            val v = stack.pop()
            val x = v.x.toInt()
            val y = v.y.toInt()
            value = value or (getCornerMask(x, y) or getSideMask(x, y))
            for (i in -1..1) {
                for (j in -1..1) {
                    val xi = x + i
                    val yi = y + j
                    if (i + j != 0 && xi >= 0 && yi >= 0 &&
                            xi < size && yi < size &&
                            (yi < base && xi < base + yi || yi >= base && xi > yi - base)) {
                        if (!visited[xi][yi] && state.locations[xi][yi] == state.locations[x][y]) {
                            stack.push(HavannahAction.valueOf(xi, yi))
                            visited[xi][yi] = true
                        }
                    }
                }
            }
        }
        return value
    }

    private fun getCornerMask(x: Int, y: Int): Int {
        for (i in 0..corners.size - 1) {
            if (corners[i][0] == x && corners[i][1] == y) {
                return 1 shl i
            }
        }
        return 0
    }

    private fun getSideMask(x: Int, y: Int): Int {
        for (i in 0..sides.size - 1) {
            for (j in 0..sides[i].size - 1) {
                if (sides[i][j][0] == x && sides[i][j][1] == y) {
                    return 1 shl i + 6
                }
            }
        }
        return 0
    }

    companion object {
        private val MIN_BASE = 2

        fun create(baseSize: Int, pieRule: Boolean): HavannahSimulator {
            return HavannahSimulator(getInitialState(baseSize), pieRule)
        }

        fun create(state: HavannahState, pieRule: Boolean): HavannahSimulator {
            return HavannahSimulator(state, pieRule)
        }

        fun getInitialState(base: Int): HavannahState {
            if (base < MIN_BASE) {
                throw IllegalArgumentException("Invalid board size: " + base)
            }
            return HavannahState(base, Array(2 * base - 1) { ByteArray(2 * base - 1) }, HavannahState.TURN_BLACK)
        }
    }
}
