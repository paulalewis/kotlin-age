package com.castlefrog.agl.domains.biniax

import com.castlefrog.agl.Simulator
import java.util.ArrayList

/**
 * Biniax is a single agent stochastic domain
 */
class BiniaxSimulator(state: BiniaxState,
                      legalActions: List<MutableList<BiniaxAction>>? = null,
                      private val initialElements: Int,
                      private val maxElements: Int,
                      private val elementIncrementInterval: Int,
                      private val maxFreeMoves: Int) :
        Simulator<BiniaxState, BiniaxAction> {

    override var state: BiniaxState = state
        set(value) {
            field = value
            _legalActions = null
        }

    private var _legalActions: List<MutableList<BiniaxAction>>? = null
    override val legalActions: List<MutableList<BiniaxAction>>
        get() {
            if (_legalActions == null) {
                _legalActions = computeLegalActions(state)
            }
            return _legalActions ?: computeLegalActions(state)
        }

    override val rewards: IntArray = intArrayOf(1)

    init {
        _legalActions = legalActions
    }

    override fun copy(): BiniaxSimulator {
        return BiniaxSimulator(state.copy(), _legalActions?.copy(), initialElements, maxElements, elementIncrementInterval, maxFreeMoves)
    }

    override fun stateTransition(actions: List<BiniaxAction?>) {
        assert(actions.size == nAgents)
        val action = actions[0]
        if (action == null || !legalActions[0].contains(action)) {
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
                            if (Math.random() < IMPASSIBLE_CHANCE) {
                                locations[j][i] = -1
                            } else {
                                locations[j][i] = generateRandomElementPair(initialElements, nTurns, maxElements,
                                        elementIncrementInterval).toByte()
                            }
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
        state = BiniaxState(locations, maxElements, freeMoves, nTurns + 1)
    }

    override val nAgents: Int = 1

    companion object {
        private val IMPASSIBLE_CHANCE = 0.0
        private val DEFAULT_WIDTH = 5
        private val DEFAULT_HEIGHT = 7
        private val DEFAULT_INITIAL_ELEMENTS = 4
        private val DEFAULT_ELEMENT_INCREMENT_INTERVAL = 32
        private val DEFAULT_MAX_ELEMENTS = 10
        private val DEFAULT_BUFFER = 3
        private val DEFAULT_MAX_FREE_MOVES = 2

        fun create(width: Int = DEFAULT_WIDTH,
                   height: Int = DEFAULT_HEIGHT,
                   initialElements: Int = DEFAULT_INITIAL_ELEMENTS,
                   maxElements: Int = DEFAULT_MAX_ELEMENTS,
                   elementIncrementInterval: Int = DEFAULT_ELEMENT_INCREMENT_INTERVAL,
                   maxFreeMoves: Int = DEFAULT_MAX_FREE_MOVES,
                   buffer: Int = DEFAULT_BUFFER): BiniaxSimulator {
            return BiniaxSimulator(state = getInitialState(width, height, initialElements, maxElements,
                    elementIncrementInterval, maxFreeMoves, buffer),
                    initialElements = initialElements,
                    maxElements = maxElements,
                    elementIncrementInterval = elementIncrementInterval,
                    maxFreeMoves = maxFreeMoves)
        }

        fun getInitialState(width: Int = DEFAULT_WIDTH,
                            height: Int = DEFAULT_HEIGHT,
                            initialElements: Int = DEFAULT_INITIAL_ELEMENTS,
                            maxElements: Int = DEFAULT_MAX_ELEMENTS,
                            elementIncrementInterval: Int = DEFAULT_ELEMENT_INCREMENT_INTERVAL,
                            maxFreeMoves: Int = DEFAULT_MAX_FREE_MOVES,
                            buffer: Int = DEFAULT_BUFFER): BiniaxState {
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

        private fun BiniaxState.elementLocation(): IntArray {
            for (i in 0..width - 1) {
                for (j in 0..height - 1) {
                    if (locations[i][j] > 0 && locations[i][j] < maxElements) {
                        return intArrayOf(i, j)
                    }
                }
            }
            throw IllegalStateException("Element does not exist")
        }

        /**
         * A legal action is one that moves the single element to an empty space or
         * an element pair that contains that element and avoids being pushed off
         * the board.
         */
        private fun computeLegalActions(state: BiniaxState): List<MutableList<BiniaxAction>> {
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

        /**
         * Creates a random element pair of dissimilar elements The elements are
         * always in order from smallest to largest
         * @return int of random values from 0 to numElements_ - 1
         */
        fun generateRandomElementPair(initialElements: Int,
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
