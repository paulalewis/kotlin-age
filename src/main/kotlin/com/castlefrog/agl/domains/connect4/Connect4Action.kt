package com.castlefrog.agl.domains.connect4

import com.castlefrog.agl.Action

/**
 * @param location slot location to place piece
 */
data class Connect4Action(val location: Int) : Action<Connect4Action> {

    override fun copy(): Connect4Action = this

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        return location == (other as Connect4Action).location
    }

    override fun hashCode(): Int {
        return location
    }

    override fun toString(): String {
        return (location + 1).toString()
    }

    companion object {
        /** Holds list of all possible actions. */
        private val actions = (0 until Connect4State.WIDTH).map(::Connect4Action)

        /**
         * Returns the Connect 4 action representation of the slot location.
         * @param location slot location to place piece.
         *                 value range from 0 to Connect4State.WIDTH - 1.
         * @return a Connect 4 action.
         */
        fun valueOf(location: Int): Connect4Action {
            return actions[location]
        }
    }
}
