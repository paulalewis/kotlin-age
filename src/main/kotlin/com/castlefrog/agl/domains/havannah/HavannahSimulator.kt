package com.castlefrog.agl.domains.havannah

import com.castlefrog.agl.Simulator
import com.castlefrog.agl.domains.AdversarialRewards
import com.castlefrog.agl.domains.nextPlayerTurnSequential
import com.castlefrog.agl.util.LruCache
import java.util.Arrays
import java.util.Stack

class HavannahSimulator(
        val base: Int = 10,
        val pieRule: Boolean = true) : Simulator<HavannahState, HavannahAction> {

    /** keep track of prev action for performance reasons */
    private val prevActionCache = LruCache<HavannahState, HavannahAction>(1)
    private val size = 2 * base - 1
    private data class Bounds(val xMin: Int, val xMax: Int, val yMin: Int, val yMax: Int)

    private val corners: Array<IntArray>
        get() = arrayOf(intArrayOf(0, 0), intArrayOf(0, base - 1), intArrayOf(base - 1, 0),
                intArrayOf(base - 1, size - 1), intArrayOf(size - 1, base - 1), intArrayOf(size - 1, size - 1))

    private val sides: Array<Array<IntArray>>
        get() {
            val sides = Array(6) { Array(base - 2) { IntArray(2) } }
            for (i in 0..base - 3) {
                sides[0][i][0] = 0
                sides[0][i][1] = i + 1
                sides[1][i][0] = i + 1
                sides[1][i][1] = 0
                sides[2][i][0] = i + 1
                sides[2][i][1] = base + i
                sides[3][i][0] = base + i
                sides[3][i][1] = size - 1
                sides[4][i][0] = size - 1
                sides[4][i][1] = base + i
                sides[5][i][0] = base + i
                sides[5][i][1] = i + 1
            }
            return sides
        }

    init {
        if (base < MIN_BASE) {
            throw IllegalArgumentException("Invalid board size: " + base)
        }
    }

    override val nPlayers: Int = N_PLAYERS

    override val initialState: HavannahState
        get() = HavannahState(base, Array(2 * base - 1) { ByteArray(2 * base - 1) }, HavannahState.TURN_BLACK)

    override fun calculateRewards(state: HavannahState): IntArray {
        val prevAction = prevActionCache[state]
        val visited = Array(size) { BooleanArray(size) }
        val bounds = if (prevAction != null) {
            val xMin = prevAction.x.toInt()
            val yMin = prevAction.y.toInt()
            val xMax = xMin + 1
            val yMax = yMin + 1
            Bounds(xMin = xMin, xMax = xMax, yMin = yMin, yMax = yMax)
        } else {
            Bounds(xMin = 0, xMax = size, yMin = 0, yMax = size)
        }
        for (y in bounds.yMin until bounds.yMax) {
            for (x in bounds.xMin until bounds.xMax) {
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
                        return if (state.locations[x][y] == HavannahState.LOCATION_BLACK) {
                            AdversarialRewards.BLACK_WINS
                        } else {
                            AdversarialRewards.WHITE_WINS
                        }
                    }
                }
            }
        }

        val otherState = state.copy()
        for (y in 0 until otherState.locations.size) {
            val xMin = if (y >= state.base) y - state.base + 1 else 0
            val xMax = if (y >= state.base) size else state.base + y
            for (x in xMin until xMax) {
                if (otherState.isLocationEmpty(x, y) || otherState.locations[x][y] == (otherState.agentTurn + 1).toByte()) {
                    otherState.locations[x][y] = HavannahState.LOCATION_BLACK
                } else {
                    otherState.locations[x][y] = HavannahState.LOCATION_EMPTY
                }
            }
        }

        val bounds2 = if (prevAction != null) {
            val xMin = Math.max(prevAction.x - 1, 0)
            val yMin = Math.max(prevAction.y - 1, 0)
            val xMax = Math.min(prevAction.x + 2, size)
            val yMax = Math.min(prevAction.y + 2, size)
            Bounds(xMin = xMin, xMax = xMax, yMin = yMin, yMax = yMax)
        } else {
            Bounds(xMin = 0, xMax = size, yMin = 0, yMax = size)
        }

        val visited2 = Array(size) { BooleanArray(size) }
        for (y in bounds2.yMin until bounds2.yMax) {
            for (x in bounds2.xMin until bounds2.xMax) {
                if (!otherState.isLocationEmpty(x, y) && !visited2[x][y]) {
                    if (dfsCornersSides(x, y, otherState, visited2, corners, sides) == 0) {
                        return if (otherState.agentTurn == HavannahState.TURN_BLACK) {
                            AdversarialRewards.WHITE_WINS
                        } else {
                            AdversarialRewards.BLACK_WINS
                        }
                    }
                }
            }
        }
        return AdversarialRewards.NEUTRAL
    }

    override fun calculateLegalActions(state: HavannahState): List<List<HavannahAction>> {
        val legalActions = arrayListOf<MutableList<HavannahAction>>(arrayListOf(), arrayListOf())
        val rewards = calculateRewards(state)
        if (Arrays.equals(rewards, AdversarialRewards.NEUTRAL)) {
            var count = 0
            var tempAction: HavannahAction? = null
            for (y in 0 until size) {
                val xMin = if (y >= state.base) y - state.base + 1 else 0
                val xMax = if (y >= state.base) size else state.base + y

                for (x in xMin until xMax) {
                    if (state.isLocationEmpty(x, y)) {
                        legalActions[state.agentTurn.toInt()].add(HavannahAction.valueOf(x, y))
                    } else if (pieRule && count == 0 && state.agentTurn == HavannahState.TURN_WHITE) {
                        count = 1
                        tempAction = HavannahAction.valueOf(x, y)
                    } else if (pieRule && count == 1 && state.agentTurn == HavannahState.TURN_WHITE) {
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

    override fun stateTransition(state: HavannahState, actions: Map<Int, HavannahAction>): HavannahState {
        val action = actions[state.agentTurn.toInt()]
        val legalActions = calculateLegalActions(state)
        if (action === null || !legalActions[state.agentTurn.toInt()].contains(action)) {
            throw IllegalArgumentException("Illegal action, $action, from state, $state")
        }
        prevActionCache[state] = action
        state.locations[action.x.toInt()][action.y.toInt()] = (state.agentTurn + 1).toByte()
        state.agentTurn = nextPlayerTurnSequential(state.agentTurn.toInt(), nPlayers).toByte()
        return state
    }

    companion object {
        private const val N_PLAYERS = 2
        private const val MIN_BASE = 2

        private fun dfsCornersSides(x0: Int,
                                    y0: Int,
                                    state: HavannahState,
                                    visited: Array<BooleanArray>,
                                    corners: Array<IntArray>,
                                    sides: Array<Array<IntArray>>): Int {
            val size = 2 * state.base - 1
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
                                xi < size && yi < size &&
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
            return (0 until corners.size)
                    .firstOrNull { corners[it][0] == x && corners[it][1] == y }
                    ?.let { 1 shl it }
                    ?: 0
        }

        private fun getSideMask(x: Int, y: Int, sides: Array<Array<IntArray>>): Int {
            for (i in 0 until sides.size) {
                for (j in 0 until sides[i].size) {
                    if (sides[i][j][0] == x && sides[i][j][1] == y) {
                        return 1 shl i + 6
                    }
                }
            }
            return 0
        }
    }
}
