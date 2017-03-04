package com.castlefrog.agl.domains.biniax

import com.castlefrog.agl.Simulator
import java.util.ArrayList

/**
 * Biniax is a single agent stochastic domain
 */
class BiniaxSimulator(
        val width: Int = 5,
        val height: Int = 7,
        val buffer: Int = 3,
        val initialElements: Int = 4,
        val maxElements: Int = 10,
        val elementIncrementInterval: Int = 32,
        val maxFreeMoves: Int = 2) : Simulator<BiniaxState, BiniaxAction> {

    override val nPlayers: Int = N_PLAYERS

    override val initialState: BiniaxState
        get() {
            val locations = Array(width) { ByteArray(height) }
            for (i in 0..height - 1) {
                val emptyLocation = (Math.random() * width).toInt()
                for (j in 0..width - 1) {
                    if (j != emptyLocation && i < height - buffer) {
                        locations[j][i] = BiniaxSimulator.generateRandomElementPair(initialElements, 0, maxElements, elementIncrementInterval).toByte()
                        if (i == height - buffer - 1) {
                            locations[j][i] = (locations[j][i] % maxElements + maxElements).toByte()
                        }
                    }
                }
            }
            locations[width / 2][height - 1] = 1
            return BiniaxState(locations = locations, maxElements = maxElements, freeMoves = maxFreeMoves.toByte())
        }

    override fun calculateRewards(state: BiniaxState): IntArray {
        return REWARDS
    }

    /**
     * A legal action is one that moves the single element to an empty space or
     * an element pair that contains that element and avoids being pushed off
     * the board.
     */
    override fun calculateLegalActions(state: BiniaxState): List<List<BiniaxAction>> {
        val legalActions = ArrayList<MutableList<BiniaxAction>>()
        legalActions.add(ArrayList<BiniaxAction>())
        val elementLocation = state.elementLocation()
        val x = elementLocation[0]
        val y = elementLocation[1]
        val element = state.locations[x][y].toInt()
        val locations = state.locations

        if (y != 0 && (locations[x][y - 1].toInt() == 0 ||
                locations[x][y - 1] / state.maxElements == element ||
                locations[x][y - 1] % state.maxElements == element)) {
            legalActions[0].add(BiniaxAction.NORTH)
        }

        if (x != state.width - 1) {
            var nextElement = 0
            if (locations[x + 1][y].toInt() == 0) {
                nextElement = element
            } else if (locations[x + 1][y] / state.maxElements == element) {
                nextElement = locations[x + 1][y] % state.maxElements
            } else if (locations[x + 1][y] % 10 == element) {
                nextElement = locations[x + 1][y] / state.maxElements
            }

            if (nextElement != 0) {
                if (state.freeMoves > 1 ||
                        y < state.height - 1 ||
                        locations[x + 1][y - 1].toInt() == 0 ||
                        locations[x + 1][y - 1] / state.maxElements == nextElement ||
                        locations[x + 1][y - 1] % state.maxElements == nextElement) {
                    legalActions[0].add(BiniaxAction.EAST)
                }
            }
        }

        if (y != state.height - 1 && (locations[x][y + 1].toInt() == 0 ||
                locations[x][y + 1] / state.maxElements == element ||
                locations[x][y + 1] % state.maxElements == element)) {
            legalActions[0].add(BiniaxAction.SOUTH)
        }

        if (x != 0) {
            var nextElement = 0
            if (locations[x - 1][y].toInt() == 0) {
                nextElement = element
            } else if (locations[x - 1][y] / state.maxElements == element) {
                nextElement = locations[x - 1][y] % state.maxElements
            } else if (locations[x - 1][y] % state.maxElements == element) {
                nextElement = locations[x - 1][y] / state.maxElements
            }

            if (nextElement != 0) {
                if (state.freeMoves > 1 ||
                        y < state.height - 1 ||
                        locations[x - 1][y - 1].toInt() == 0 ||
                        locations[x - 1][y - 1] / state.maxElements == nextElement ||
                        locations[x - 1][y - 1] % state.maxElements == nextElement) {
                    legalActions[0].add(BiniaxAction.WEST)
                }
            }
        }
        return legalActions
    }

    override fun stateTransition(state: BiniaxState, actions: Map<Int, BiniaxAction>): BiniaxState {
        val action = actions[0]
        val legalActions = calculateLegalActions(state)
        if (action === null || !legalActions[0].contains(action)) {
            throw IllegalArgumentException("Illegal action, $action, from state, $state")
        }

        val locations = state.locations
        var freeMoves = state.freeMoves
        val elementLocation = state.elementLocation()
        var x = elementLocation[0]
        var y = elementLocation[1]
        var element = state.locations[x][y]
        val nTurns = state.nTurns

        locations[x][y] = 0
        when (action) {
            BiniaxAction.NORTH -> y--
            BiniaxAction.EAST -> x++
            BiniaxAction.SOUTH -> y++
            BiniaxAction.WEST -> x--
        }

        if (locations[x][y] / maxElements == element.toInt()) {
            element = (locations[x][y] % maxElements).toByte()
        } else if (locations[x][y] % maxElements == element.toInt()) {
            element = (locations[x][y] / maxElements).toByte()
        }
        locations[x][y] = element

        freeMoves--
        if (freeMoves.toInt() == 0) {
            freeMoves = maxFreeMoves.toByte()
            // Move all elements down
            val emptyLocation = (Math.random() * state.width).toInt()
            for (i in state.height - 1 downTo 0) {
                for (j in 0..state.width - 1) {
                    if (i == 0) {
                        if (j != emptyLocation) {
                            locations[j][i] = generateRandomElementPair(initialElements, nTurns, maxElements,
                                    elementIncrementInterval).toByte()
                        } else {
                            locations[j][i] = 0
                        }
                    } else {
                        locations[j][i] = locations[j][i - 1]
                    }
                }
            }
            // Move element back up if possible
            if (locations[x][y].toInt() == 0) {
                locations[x][y] = element
                if (y < state.height - 1) {
                    locations[x][y + 1] = 0
                }
            } else if (locations[x][y] / maxElements == element.toInt()) {
                locations[x][y] = (locations[x][y] % maxElements).toByte()
                if (y < state.height - 1) {
                    locations[x][y + 1] = 0
                }
            } else if (locations[x][y] % maxElements == element.toInt()) {
                locations[x][y] = (locations[x][y] / maxElements).toByte()
                if (y < state.height - 1) {
                    locations[x][y + 1] = 0
                }
            }
        }
        return BiniaxState(locations, maxElements, freeMoves, nTurns + 1)
    }

    companion object {
        private val REWARDS = intArrayOf(1)
        private val N_PLAYERS = 1

        private fun BiniaxState.elementLocation(): IntArray {
            for (i in 0..width - 1) {
                for (j in 0..height - 1) {
                    if (locations[i][j] in 1..(maxElements - 1)) {
                        return intArrayOf(i, j)
                    }
                }
            }
            throw IllegalStateException("Element does not exist")
        }

        /**
         * Creates a random element pair of dissimilar elements The elements are
         * always in order from smallest to largest
         * @return int of random values from 0 to numElements_ - 1
         */
        private fun generateRandomElementPair(initialElements: Int,
                                      nTurns: Int,
                                      maxElements: Int,
                                      elementIncrementInterval: Int): Int {
            val nElementTypes = getNElementTypes(initialElements, nTurns, maxElements, elementIncrementInterval)
            val element1 = (Math.random() * nElementTypes).toInt() + 1
            val element2 = (Math.random() * (nElementTypes - 1)).toInt() + 1
            if (element1 <= element2) {
                return element1 * maxElements + element2 + 1
            } else {
                return element2 * maxElements + element1
            }
        }

        private fun getNElementTypes(initialElements: Int,
                                     nTurns: Int,
                                     maxElements: Int,
                                     elementIncrementInterval: Int): Int {
            return Math.min(initialElements + nTurns / elementIncrementInterval, maxElements - 1)
        }
    }
}
