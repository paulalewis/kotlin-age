package com.castlefrog.agl.domains.connect4

import java.util.ArrayList

import com.castlefrog.agl.Action

/**
 * @param location slot location to place piece
 */
data class Connect4Action private constructor(val location: Int) : Action {

    override fun copy(): Connect4Action {
        return this
    }

    override fun toString(): String {
        return (location + 1).toString()
    }

    companion object {
        /** Holds list of all possible actions.  */
        private val actions = generateActions()

        private fun generateActions(): List<Connect4Action> {
            val actions = ArrayList<Connect4Action>()
            for (i in 0..Connect4State.WIDTH - 1) {
                actions.add(Connect4Action(i))
            }
            return actions
        }

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
