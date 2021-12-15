package com.castlefrog.agl.domains.biniax

import com.castlefrog.agl.Simulator
import kotlin.math.min
import kotlin.random.Random

/**
 * Biniax is a single agent stochastic domain
 */
class BiniaxSimulator(
    private val random: Random = Random,
    val buffer: Int = 3,
    val initialElements: Int = 4,
    val maxElements: Int = 10,
    val elementIncrementInterval: Int = 32,
    val maxFreeMoves: Int = 2
) : Simulator<BiniaxState, BiniaxAction> {

    override val initialState: BiniaxState
        get() {
            val locations = ByteArray(WIDTH * HEIGHT)
            for (i in 0 until HEIGHT) {
                val emptyLocation = random.nextInt(WIDTH)
                for (j in 0 until WIDTH) {
                    if (j != emptyLocation && i < HEIGHT - buffer) {
                        locations[i * WIDTH + j] = generateRandomElementPair(
                            random,
                            initialElements,
                            0,
                            maxElements,
                            elementIncrementInterval
                        ).toByte()
                        if (i == HEIGHT - buffer - 1) {
                            locations[i * WIDTH + j] = (locations[i * WIDTH + j] % maxElements + maxElements).toByte()
                        }
                    }
                }
            }
            locations[(HEIGHT - 1) * WIDTH + WIDTH / 2] = 1
            return BiniaxState(
                locations = locations,
                maxElements = maxElements.toByte(),
                freeMoves = maxFreeMoves.toByte()
            )
        }

    override fun calculateRewards(state: BiniaxState): IntArray {
        return intArrayOf(1)
    }

    /**
     * A legal action is one that moves the single element to an empty space or
     * an element pair that contains that element and avoids being pushed off
     * the board.
     */
    override fun calculateLegalActions(state: BiniaxState): List<Set<BiniaxAction>> {
        val legalActions = ArrayList<MutableSet<BiniaxAction>>()
        legalActions.add(mutableSetOf())
        val elementLocation = state.elementLocation()
        val x = elementLocation[0]
        val y = elementLocation[1]
        val element = state.locations[x + y * WIDTH].toInt()
        val locations = state.locations

        if (y != 0 && (locations[x + (y - 1) * WIDTH].toInt() == 0 ||
                    locations[x + (y - 1) * WIDTH] / state.maxElements == element ||
                    locations[x + (y - 1) * WIDTH] % state.maxElements == element)
        ) {
            legalActions[0].add(BiniaxAction.NORTH)
        }

        if (x != WIDTH - 1) {
            var nextElement = 0
            when {
                locations[x + 1 + y * WIDTH].toInt() == 0 -> nextElement = element
                locations[x + 1 + y * WIDTH] / state.maxElements == element -> nextElement =
                    locations[x + 1 + y * WIDTH] % state.maxElements
                locations[x + 1 + y * WIDTH] % 10 == element -> nextElement =
                    locations[x + 1 + y * WIDTH] / state.maxElements
            }

            if (nextElement != 0) {
                if (state.freeMoves > 1 ||
                    y < HEIGHT - 1 ||
                    locations[x + 1 + (y - 1) * WIDTH].toInt() == 0 ||
                    locations[x + 1 + (y - 1) * WIDTH] / state.maxElements == nextElement ||
                    locations[x + 1 + (y - 1) * WIDTH] % state.maxElements == nextElement
                ) {
                    legalActions[0].add(BiniaxAction.EAST)
                }
            }
        }

        if (y != HEIGHT - 1 && (locations[x + (y + 1) * WIDTH].toInt() == 0 ||
                    locations[x + (y + 1) * WIDTH] / state.maxElements == element ||
                    locations[x + (y + 1) * WIDTH] % state.maxElements == element)
        ) {
            legalActions[0].add(BiniaxAction.SOUTH)
        }

        if (x != 0) {
            var nextElement = 0
            when {
                locations[x - 1 + y * WIDTH].toInt() == 0 -> nextElement = element
                locations[x - 1 + y * WIDTH] / state.maxElements == element -> nextElement =
                    locations[x - 1 + y * WIDTH] % state.maxElements
                locations[x - 1 + y * WIDTH] % state.maxElements == element -> nextElement =
                    locations[x - 1 + y * WIDTH] / state.maxElements
            }

            if (nextElement != 0) {
                if (state.freeMoves > 1 ||
                    y < HEIGHT - 1 ||
                    locations[x - 1 + (y - 1) * WIDTH].toInt() == 0 ||
                    locations[x - 1 + (y - 1) * WIDTH] / state.maxElements == nextElement ||
                    locations[x - 1 + (y - 1) * WIDTH] % state.maxElements == nextElement
                ) {
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
        var element = state.locations[x + y * WIDTH]
        val nTurns = state.nTurns

        locations[x + y * WIDTH] = 0
        when (action) {
            BiniaxAction.NORTH -> y--
            BiniaxAction.EAST -> x++
            BiniaxAction.SOUTH -> y++
            BiniaxAction.WEST -> x--
        }

        if (locations[x + y * WIDTH] / maxElements == element.toInt()) {
            element = (locations[x + y * WIDTH] % maxElements).toByte()
        } else if (locations[x + y * WIDTH] % maxElements == element.toInt()) {
            element = (locations[x + y * WIDTH] / maxElements).toByte()
        }
        locations[x + y * WIDTH] = element

        freeMoves--
        if (freeMoves.toInt() == 0) {
            freeMoves = maxFreeMoves.toByte()
            // Move all elements down
            val emptyLocation = random.nextInt(WIDTH)
            for (i in HEIGHT - 1 downTo 0) {
                for (j in 0 until WIDTH) {
                    if (i == 0) {
                        if (j != emptyLocation) {
                            locations[j + i * WIDTH] = generateRandomElementPair(
                                random, initialElements, nTurns, maxElements,
                                elementIncrementInterval
                            ).toByte()
                        } else {
                            locations[j + i * WIDTH] = 0
                        }
                    } else {
                        locations[j + i * WIDTH] = locations[j + (i - 1) * WIDTH]
                    }
                }
            }
            // Move element back up if possible
            if (locations[x + y * WIDTH].toInt() == 0) {
                locations[x + y * WIDTH] = element
                if (y < HEIGHT - 1) {
                    locations[x + (y + 1) * WIDTH] = 0
                }
            } else if (locations[x + y * WIDTH] / maxElements == element.toInt()) {
                locations[x + y * WIDTH] = (locations[x + y * WIDTH] % maxElements).toByte()
                if (y < HEIGHT - 1) {
                    locations[x + (y + 1) * WIDTH] = 0
                }
            } else if (locations[x + y * WIDTH] % maxElements == element.toInt()) {
                locations[x + y * WIDTH] = (locations[x + y * WIDTH] / maxElements).toByte()
                if (y < HEIGHT - 1) {
                    locations[x + (y + 1) * WIDTH] = 0
                }
            }
        }
        return BiniaxState(locations, maxElements.toByte(), freeMoves, nTurns + 1)
    }

    companion object {
        const val WIDTH = 5
        const val HEIGHT = 7

        private fun BiniaxState.elementLocation(): IntArray {
            for (i in 0 until WIDTH) {
                for (j in 0 until HEIGHT) {
                    if (locations[i + j * WIDTH] in 1 until maxElements) {
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
        private fun generateRandomElementPair(
            random: Random,
            initialElements: Int,
            nTurns: Int,
            maxElements: Int,
            elementIncrementInterval: Int
        ): Int {
            val nElementTypes = getNElementTypes(initialElements, nTurns, maxElements, elementIncrementInterval)
            val element1 = random.nextInt(nElementTypes) + 1
            val element2 = random.nextInt(nElementTypes - 1) + 1
            return if (element1 <= element2) {
                element1 * maxElements + element2 + 1
            } else {
                element2 * maxElements + element1
            }
        }

        private fun getNElementTypes(
            initialElements: Int,
            nTurns: Int,
            maxElements: Int,
            elementIncrementInterval: Int
        ): Int {
            return min(initialElements + nTurns / elementIncrementInterval, maxElements - 1)
        }
    }
}
