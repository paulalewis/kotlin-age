package com.castlefrog.agl.domains.backgammon

import java.util.ArrayList

/**
 * Represents moving a single piece from one location to another. It is a
 * partial action as an action may be made up of multiple moves.
 */
data class BackgammonMove(val from: Int, val distance: Int) : Comparable<BackgammonMove> {

    override fun compareTo(other: BackgammonMove): Int {
        return when {
            from < other.from -> -1
            from > other.from -> 1
            distance < other.distance -> -1
            distance > other.distance -> 1
            else -> 0
        }
    }

    override fun toString(): String {
        return from.toString() + "/" + distance.toString()
    }

    companion object {
        /** List of all legal moves. */
        private val moves = generateMoves()

        fun valueOf(from: Int, distance: Int): BackgammonMove {
            return moves[from][distance - 1]
        }

        private fun generateMoves(): List<List<BackgammonMove>> {
            val moves = ArrayList<MutableList<BackgammonMove>>()
            for (i in 0 until BackgammonState.N_LOCATIONS) {
                moves.add(ArrayList())
                for (j in 0 until BackgammonState.N_DIE_FACES) {
                    moves[i].add(BackgammonMove(i, j + 1))
                }
            }
            return moves
        }
    }
}
