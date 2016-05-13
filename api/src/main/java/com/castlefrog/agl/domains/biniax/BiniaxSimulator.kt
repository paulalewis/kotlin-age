package com.castlefrog.agl.domains.biniax

import java.util.ArrayList

import com.castlefrog.agl.AbstractSimulator

/**
 * Biniax is a single agent stochastic domain.
 */
class BiniaxSimulator : AbstractSimulator<BiniaxState, BiniaxAction> {

    private val initialElements: Int
    private val maxElements: Int
    private val elementIncrementInterval: Int
    private val maxFreeMoves: Int

    private constructor(state: BiniaxState,
                        initialElements: Int,
                        maxElements: Int,
                        elementIncrementInterval: Int,
                        maxFreeMoves: Int) {
        this.initialElements = initialElements
        this.maxElements = maxElements
        this.elementIncrementInterval = elementIncrementInterval
        this.maxFreeMoves = maxFreeMoves
        legalActions_ = ArrayList<List<BiniaxAction>>()
        legalActions_.add(ArrayList<BiniaxAction>())
        setState(state)
        rewards_ = intArrayOf(1)
    }

    private constructor(simulator: BiniaxSimulator,
                        initialElements: Int,
                        maxElements: Int,
                        elementIncrementInterval: Int,
                        maxFreeMoves: Int) : super(simulator) {
        this.initialElements = initialElements
        this.maxElements = maxElements
        this.elementIncrementInterval = elementIncrementInterval
        this.maxFreeMoves = maxFreeMoves
    }

    override fun copy(): BiniaxSimulator {
        return BiniaxSimulator(this, initialElements, maxElements, elementIncrementInterval, maxFreeMoves)
    }

    override fun setState(state: BiniaxState) {
        state_ = state
        computeLegalActions()
    }

    override fun stateTransition(actions: List<BiniaxAction>) {
        val action = actions[0]
        if (!legalActions_[0].contains(action)) {
            throw IllegalArgumentException("Illegal action, $action, from state, $state_")
        }

        val locations = state_.locations
        var freeMoves = state_.freeMoves
        val elementLocation = elementLocation
        var x = elementLocation[0]
        var y = elementLocation[1]
        var element = state_.locations[x][y]
        val nTurns = state_.nTurns

        locations[x][y] = 0
        when (action) {
            BiniaxAction.NORTH -> y--
            BiniaxAction.EAST -> x++
            BiniaxAction.SOUTH -> y++
            BiniaxAction.WEST -> x--
            else -> {
            }
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
            val emptyLocation = (Math.random() * state_.width).toInt()
            for (i in state_.height - 1 downTo 0) {
                for (j in 0..state_.width - 1) {
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
                if (y < state_.height - 1) {
                    locations[x][y + 1] = 0
                }
            } else if (locations[x][y] / maxElements == element.toInt()) {
                locations[x][y] = (locations[x][y] % maxElements).toByte()
                if (y < state_.height - 1) {
                    locations[x][y + 1] = 0
                }
            } else if (locations[x][y] % maxElements == element.toInt()) {
                locations[x][y] = (locations[x][y] / maxElements).toByte()
                if (y < state_.height - 1) {
                    locations[x][y + 1] = 0
                }
            }
        }
        state_ = BiniaxState(locations, maxElements, freeMoves, nTurns + 1)
        computeLegalActions()
    }

    private val elementLocation: IntArray
        get() {
            for (i in 0..state_.width - 1) {
                for (j in 0..state_.height - 1) {
                    if (state_.locations[i][j] > 0 && state_.locations[i][j] < maxElements) {
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
    private fun computeLegalActions() {
        legalActions_[0].clear()
        val elementLocation = elementLocation
        val x = elementLocation[0]
        val y = elementLocation[1]
        val element = state_.locations[x][y].toInt()
        val locations = state_.locations

        if (y != 0 && (locations[x][y - 1].toInt() == 0 ||
                locations[x][y - 1] / maxElements == element ||
                locations[x][y - 1] % maxElements == element)) {
            legalActions_[0].add(BiniaxAction.NORTH)
        }

        if (x != state_.width - 1) {
            var nextElement = 0
            if (locations[x + 1][y].toInt() == 0) {
                nextElement = element
            } else if (locations[x + 1][y] / maxElements == element) {
                nextElement = locations[x + 1][y] % maxElements
            } else if (locations[x + 1][y] % 10 == element) {
                nextElement = locations[x + 1][y] / maxElements
            }

            if (nextElement != 0) {
                if (state_.freeMoves > 1 ||
                        y < state_.height - 1 ||
                        locations[x + 1][y - 1].toInt() == 0 ||
                        locations[x + 1][y - 1] / maxElements == nextElement ||
                        locations[x + 1][y - 1] % maxElements == nextElement) {
                    legalActions_[0].add(BiniaxAction.EAST)
                }
            }
        }

        if (y != state_.height - 1 && (locations[x][y + 1].toInt() == 0 ||
                locations[x][y + 1] / maxElements == element ||
                locations[x][y + 1] % maxElements == element)) {
            legalActions_[0].add(BiniaxAction.SOUTH)
        }

        if (x != 0) {
            var nextElement = 0
            if (locations[x - 1][y].toInt() == 0) {
                nextElement = element
            } else if (locations[x - 1][y] / maxElements == element) {
                nextElement = locations[x - 1][y] % maxElements
            } else if (locations[x - 1][y] % maxElements == element) {
                nextElement = locations[x - 1][y] / maxElements
            }

            if (nextElement != 0) {
                if (state_.freeMoves > 1 ||
                        y < state_.height - 1 ||
                        locations[x - 1][y - 1].toInt() == 0 ||
                        locations[x - 1][y - 1] / maxElements == nextElement ||
                        locations[x - 1][y - 1] % maxElements == nextElement) {
                    legalActions_[0].add(BiniaxAction.WEST)
                }
            }
        }
    }

    override fun getNAgents(): Int {
        return N_AGENTS
    }

    companion object {
        private val N_AGENTS = 1
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
            return BiniaxSimulator(getInitialState(width, height, initialElements, maxElements,
                    elementIncrementInterval, maxFreeMoves, buffer),
                    initialElements, maxElements, elementIncrementInterval, maxFreeMoves)
        }

        fun create(state: BiniaxState,
                   initialElements: Int,
                   maxElements: Int,
                   elementIncrementInterval: Int,
                   maxFreeMoves: Int): BiniaxSimulator {
            return BiniaxSimulator(state, initialElements, maxElements, elementIncrementInterval, maxFreeMoves)
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
