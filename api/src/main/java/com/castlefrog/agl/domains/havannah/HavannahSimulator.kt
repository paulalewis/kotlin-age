package com.castlefrog.agl.domains.havannah

import com.castlefrog.agl.AdversarialSimulator
import com.castlefrog.agl.TurnType

import java.util.ArrayList
import java.util.Stack

class HavannahSimulator(state: HavannahState,
                        legalActions: List<MutableList<HavannahAction>>? = null,
                        rewards: IntArray? = null,
                        val pieRule: Boolean,
                        private val corners: Array<IntArray> = state.corners,
                        private val sides: Array<Array<IntArray>> = state.sides) :
        AdversarialSimulator<HavannahState, HavannahAction>() {

    override var state: HavannahState = state
        set(value) {
            field = value
            _legalActions = null
            _rewards = null
        }

    private var _legalActions: List<MutableList<HavannahAction>>? = null
    override val legalActions: List<MutableList<HavannahAction>>
        get() {
            if (_legalActions == null) {
                _legalActions = computeLegalActions(state, rewards)
            }
            return _legalActions ?: computeLegalActions(state, rewards)
        }

    private var _rewards: IntArray? = null
    override val rewards: IntArray
        get() {
            if (_rewards == null) {
                _rewards = computeRewards(state, corners, sides)
            }
            return _rewards ?: computeRewards(state, corners, sides)
        }

    override fun copy(): HavannahSimulator {
        return HavannahSimulator(state.copy(), _legalActions?.copy(), _rewards?.copyOf(), pieRule, corners, sides)
    }

    override fun stateTransition(actions: List<HavannahAction?>) {
        val action = actions[state.agentTurn.toInt()]
        if (action == null || !legalActions[state.agentTurn.toInt()].contains(action)) {
            throw IllegalArgumentException("Illegal action, $action, from state, $state")
        }
        state.locations[action.x.toInt()][action.y.toInt()] = (state.agentTurn + 1).toByte()
        state.agentTurn = ((state.agentTurn + 1) % AdversarialSimulator.N_AGENTS).toByte()
        _legalActions = null
        _rewards = null
    }

    companion object {
        private val MIN_BASE = 2

        fun create(baseSize: Int, pieRule: Boolean): HavannahSimulator {
            return HavannahSimulator(state = getInitialState(baseSize), pieRule = pieRule)
        }

        fun getInitialState(base: Int): HavannahState {
            if (base < MIN_BASE) {
                throw IllegalArgumentException("Invalid board size: " + base)
            }
            return HavannahState(base, Array(2 * base - 1) { ByteArray(2 * base - 1) }, HavannahState.TURN_BLACK)
        }

        private fun computeLegalActions(state: HavannahState,
                                        rewards: IntArray): List<MutableList<HavannahAction>> {
            val legalActions = ArrayList<MutableList<HavannahAction>>()
            legalActions.add(ArrayList<HavannahAction>())
            legalActions.add(ArrayList<HavannahAction>())
            if (rewards == AdversarialSimulator.REWARDS_NEUTRAL) {
                var count = 0
                var tempAction: HavannahAction? = null
                for (y in 0..state.size - 1) {
                    var xMin = 0
                    var xMax = state.size
                    if (y >= state.base) {
                        xMin = y - state.base + 1
                    } else {
                        xMax = state.base + y
                    }

                    for (x in xMin..xMax - 1) {
                        if (state.isLocationEmpty(x, y)) {
                            legalActions[state.agentTurn.toInt()].add(HavannahAction.valueOf(x, y))
                        } else if (state.agentTurn == HavannahState.TURN_WHITE && count == 0) {
                            count = 1
                            tempAction = HavannahAction.valueOf(x, y)
                        } else if (state.agentTurn == HavannahState.TURN_WHITE && count == 1) {
                            count = 2
                            tempAction = null
                        }
                    }
                }
                if (tempAction != null) {
                    legalActions[state.agentTurn.toInt()].add(tempAction)
                }
            }
            return legalActions
        }

        private fun computeRewards(state: HavannahState,
                                   corners: Array<IntArray>,
                                   sides: Array<Array<IntArray>>,
                                   prevAction: HavannahAction? = null): IntArray {
            var visited = Array(state.size) { BooleanArray(state.size) }
            var yMin = 0
            var xMin = 0
            var yMax = state.size
            var xMax = state.size
            if (prevAction != null) {
                xMin = prevAction.x.toInt()
                yMin = prevAction.y.toInt()
                xMax = xMin + 1
                yMax = yMin + 1
            }
            for (y in yMin..yMax - 1) {
                for (x in xMin..xMax - 1) {
                    // Checks: non empty location - hasn't been visited
                    if (!state.isLocationEmpty(x, y) && !visited[x][y]) {
                        var result = dfsCornersSides(x, y, state, visited, corners, sides)
                        // count corners
                        var nCorners = 0
                        for (k in 0..5) {
                            if (result % 2 == 1) {
                                nCorners += 1
                            }
                            result = result shr 1
                        }
                        // count sides
                        var nSides = 0
                        for (k in 0..5) {
                            if (result % 2 == 1) {
                                nSides += 1
                            }
                            result = result shr 1
                        }
                        if (nCorners >= 2 || nSides >= 3) {
                            if (state.locations[x][y] == HavannahState.LOCATION_BLACK) {
                                return AdversarialSimulator.REWARDS_BLACK_WINS
                            } else {
                                return AdversarialSimulator.REWARDS_WHITE_WINS
                            }
                        }
                    }
                }
            }

            val otherState = state.copy()
            visited = Array(state.size) { BooleanArray(state.size) }
            for (y in 0..otherState.locations.size - 1) {
                xMin = 0
                xMax = state.size
                if (y >= state.base) {
                    xMin = y - state.base + 1
                } else {
                    xMax = state.base + y
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
            yMax = state.size
            xMax = state.size
            if (prevAction != null) {
                xMin = Math.max(prevAction.x - 1, 0)
                yMin = Math.max(prevAction.y - 1, 0)
                xMax = Math.min(prevAction.x + 2, state.size)
                yMax = Math.min(prevAction.y + 2, state.size)
            }

            for (y in yMin..yMax - 1) {
                for (x in xMin..xMax - 1) {
                    if (!otherState.isLocationEmpty(x, y) && !visited[x][y]) {
                        if (dfsCornersSides(x, y, otherState, visited, corners, sides) == 0) {
                            if (otherState.agentTurn == HavannahState.TURN_BLACK) {
                                return AdversarialSimulator.REWARDS_WHITE_WINS
                            } else {
                                return AdversarialSimulator.REWARDS_BLACK_WINS
                            }
                        }
                    }
                }
            }
            return AdversarialSimulator.REWARDS_NEUTRAL
        }

        private fun dfsCornersSides(x0: Int,
                                    y0: Int,
                                    state: HavannahState,
                                    visited: Array<BooleanArray>,
                                    corners: Array<IntArray>,
                                    sides: Array<Array<IntArray>>): Int {
            var value = 0
            val stack = Stack<HavannahAction>()
            stack.push(HavannahAction.valueOf(x0, y0))
            visited[x0][y0] = true
            while (!stack.empty()) {
                val v = stack.pop()
                val x = v.x.toInt()
                val y = v.y.toInt()
                value = value or (getCornerMask(x, y, corners) or getSideMask(x, y, sides))
                for (i in -1..1) {
                    for (j in -1..1) {
                        val xi = x + i
                        val yi = y + j
                        if (i + j != 0 && xi >= 0 && yi >= 0 &&
                                xi < state.size && yi < state.size &&
                                (yi < state.base && xi < state.base + yi || yi >= state.base && xi > yi - state.base)) {
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

        private fun getCornerMask(x: Int, y: Int, corners: Array<IntArray>): Int {
            for (i in 0..corners.size - 1) {
                if (corners[i][0] == x && corners[i][1] == y) {
                    return 1 shl i
                }
            }
            return 0
        }

        private fun getSideMask(x: Int, y: Int, sides: Array<Array<IntArray>>): Int {
            for (i in 0..sides.size - 1) {
                for (j in 0..sides[i].size - 1) {
                    if (sides[i][j][0] == x && sides[i][j][1] == y) {
                        return 1 shl i + 6
                    }
                }
            }
            return 0
        }
    }
}
