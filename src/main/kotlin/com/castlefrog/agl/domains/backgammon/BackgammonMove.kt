package com.castlefrog.agl.domains.backgammon

import java.util.ArrayList

/**
 * Represents moving a single piece from one location to another. It is a
 * partial action as an action may be made up of multiple moves.
 */
data class BackgammonMove private constructor(val from: Int, val distance: Int) : Comparable<BackgammonMove> {

    override fun compareTo(other: BackgammonMove): Int {
        if (from < other.from) {
            return -1
        } else if (from > other.from) {
            return 1
        } else if (distance < other.distance) {
            return -1
        } else if (distance > other.distance) {
            return 1
        }
        return 0
    }

    override fun toString(): String {
        return from.toString() + "/" + distance.toString()
    }

    companion object {
        /** List of all legal moves.  */
        private val moves = generateMoves()

        fun valueOf(from: Int, distance: Int): BackgammonMove {
            return moves[from][distance - 1]
        }

        private fun generateMoves(): List<List<BackgammonMove>> {
            val moves = ArrayList<MutableList<BackgammonMove>>()
            for (i in 0..BackgammonState.N_LOCATIONS - 1) {
                moves.add(ArrayList<BackgammonMove>())
                for (j in 0..BackgammonState.N_DIE_FACES - 1) {
                    moves[i].add(BackgammonMove(i, j + 1))
                }
            }
            return moves
        }
    }
}
