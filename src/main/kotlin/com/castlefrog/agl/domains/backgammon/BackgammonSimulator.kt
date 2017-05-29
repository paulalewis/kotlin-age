package com.castlefrog.agl.domains.backgammon

import com.castlefrog.agl.domains.ADVERSARIAL_REWARDS_BLACK_WINS
import com.castlefrog.agl.domains.ADVERSARIAL_REWARDS_NEUTRAL
import com.castlefrog.agl.domains.ADVERSARIAL_REWARDS_WHITE_WINS
import com.castlefrog.agl.Simulator
import com.castlefrog.agl.domains.nextPlayerTurnSequential
import java.util.ArrayList
import java.util.LinkedList
import java.util.Random

/**
 * Classic game of Backgammon
 */
class BackgammonSimulator(val random: Random = Random()) : Simulator<BackgammonState, BackgammonAction> {

    override val nPlayers: Int = 2

    override val initialState: BackgammonState
        get() {
            val agentTurn: Int = random.nextInt(2)
            val dice: ByteArray = byteArrayOf(
                    when (random.nextInt(15)) {
                        0 -> 1
                        1, 2 -> 2
                        3, 4, 5 -> 3
                        6, 7, 8, 9 -> 4
                        else -> 5
                    },
                    (random.nextInt(BackgammonState.N_DIE_FACES)).toByte())
            return BackgammonState(dice = dice, agentTurn = agentTurn)
        }

    override fun calculateRewards(state: BackgammonState): IntArray {
        var pos = false
        var neg = false
        for (i in 0..BackgammonState.N_LOCATIONS - 1) {
            if (!pos && state.locations[i] > 0) {
                pos = true
            } else if (!neg && state.locations[i] < 0) {
                neg = true
            }
        }
        if (!pos) {
            return ADVERSARIAL_REWARDS_BLACK_WINS
        } else if (!neg) {
            return ADVERSARIAL_REWARDS_WHITE_WINS
        } else {
            return ADVERSARIAL_REWARDS_NEUTRAL
        }
    }

    override fun calculateLegalActions(state: BackgammonState): List<List<BackgammonAction>> {
        val legalActions = arrayListOf<MutableList<BackgammonAction>>(ArrayList(), ArrayList())

        val rewards = calculateRewards(state)
        if (rewards[0] == 0) {
            val piece = if (state.agentTurn == 0) 1 else -1

            val dice = state.dice
            val values = if (dice[0] == dice[1]) {
                intArrayOf(dice[0] + 1)
            } else {
                intArrayOf(dice[0] + 1, dice[1] + 1)
            }

            val depth = if (dice[0] == dice[1]) 4 else 2

            val tempLegalActions = dfs(state.locations, LinkedList<BackgammonMove>(),
                    values, piece, depth, state.agentTurn)

            // only allow actions that are tied for using most moves
            var max = 0
            for ((moves) in tempLegalActions) {
                max = Math.max(max, moves.size)
            }
            tempLegalActions
                    .filter { it.moves.size == max }
                    .forEach { legalActions[state.agentTurn].add(it) }
        }
        return legalActions
    }

    override fun stateTransition(state: BackgammonState, actions: Map<Int, BackgammonAction>): BackgammonState {
        val action = actions[state.agentTurn]
        val legalActions = calculateLegalActions(state)
        if (action === null || !legalActions[state.agentTurn].contains(action)) {
            throw IllegalArgumentException("Illegal action, $action, from state, $state")
        }

        val locations = state.locations

        for ((from, distance) in action.moves) {
            val piece: Byte = if (locations[from] > 0) 1 else -1
            val to = from + distance * piece
            if (to > 0 && to < BackgammonState.N_LOCATIONS - 1) {
                if (locations[to] * piece < 0) {
                    locations[to] = piece
                    if (piece > 0) {
                        locations[25] = (locations[25] - piece).toByte()
                    } else {
                        locations[0] = (locations[0] - piece).toByte()
                    }
                } else {
                    locations[to] = (locations[to] + piece).toByte()
                }
            }
            locations[from] = (locations[from] - piece).toByte()
        }
        val dice = byteArrayOf(random.nextInt(BackgammonState.N_DIE_FACES).toByte(),
                random.nextInt(BackgammonState.N_DIE_FACES).toByte())
        return BackgammonState(locations, dice, nextPlayerTurnSequential(state.agentTurn, nPlayers))
    }

    companion object {

        private val TURN_PLAYER_1 = 0

        private fun dfs(locations: ByteArray,
                        moves: LinkedList<BackgammonMove>,
                        values: IntArray,
                        piece: Int,
                        depth: Int,
                        agentTurn: Int): MutableList<BackgammonAction> {
            val legalActions = ArrayList<BackgammonAction>()
            val limit = if (piece > 0 && locations[0] > 0) 1 else BackgammonState.N_LOCATIONS
            val start = if (piece < 0 && locations[25] < 0) 25 else 0
            val moveOff = canMoveOff(locations, piece)

            for (i in start..limit - 1) {
                if (locations[i] * piece >= 1) {
                    for (j in values.indices) {
                        if (canMove(i, values[j], moveOff, agentTurn, locations)) {
                            val move = BackgammonMove.valueOf(i, values[j])
                            if (moves.isEmpty() || move.compareTo(moves.last) * piece >= 0) {
                                moves.addLast(move)
                                if (depth > 1) {
                                    locations[i] = (locations[i] - piece).toByte()
                                    val next = i + values[j] * piece
                                    if (next > 0 && next < BackgammonState.N_LOCATIONS - 1) {
                                        locations[next] = (locations[next] + piece).toByte()
                                    }
                                    var k = 0
                                    if (values.size == 2) {
                                        if (j == 0) {
                                            k = 1
                                        } else {
                                            k = 0
                                        }
                                    }
                                    legalActions.addAll(dfs(locations, moves, intArrayOf(values[k]),
                                            piece, depth - 1, agentTurn))
                                    if (next > 0 && next < BackgammonState.N_LOCATIONS - 1) {
                                        locations[next] = (locations[next] - piece).toByte()
                                    }
                                    locations[i] = (locations[i] + piece).toByte()
                                } else {
                                    legalActions.add(BackgammonAction(moves.toHashSet()))
                                }
                                moves.removeLast()
                            }
                        }
                    }
                }
            }
            if (legalActions.size == 0) {
                legalActions.add(BackgammonAction(moves.toHashSet()))
            }
            return legalActions
        }

        private fun canMove(location: Int,
                            distance: Int,
                            moveOff: Boolean,
                            agentTurn: Int,
                            locations: ByteArray): Boolean {
            if (agentTurn == TURN_PLAYER_1) {
                val next = location + distance
                return next < BackgammonState.N_LOCATIONS - 1 && locations[next] >= -1 ||
                        moveOff && next >= BackgammonState.N_LOCATIONS - 1
            } else {
                val next = location - distance
                return next > 0 && locations[next] <= 1 || moveOff && next <= 0
            }
        }

        /**
         * Checks if a player can start moving pieces off of the board.
         * @return true if legal to move off board.
         */
        private fun canMoveOff(locations: ByteArray, piece: Int): Boolean {
            if (piece > 0) {
                (0..18)
                        .filter { locations[it] > 0 }
                        .forEach { return false }
            } else {
                (7..BackgammonState.N_LOCATIONS - 1)
                        .filter { locations[it] < 0 }
                        .forEach { return false }
            }
            return true
        }
    }
}
