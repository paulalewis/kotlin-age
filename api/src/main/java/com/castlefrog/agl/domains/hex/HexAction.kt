package com.castlefrog.agl.domains.hex

import java.io.Serializable
import java.util.ArrayList

import com.castlefrog.agl.Action

/**
 * Immutable object that represents a hex action.
 */
data class HexAction private constructor(val x: Byte, val y: Byte) : Action, Serializable {

    override fun copy(): HexAction {
        return this
    }

    override fun toString(): String {
        return "" + (0x41 + x).toChar() + y
    }

    companion object {
        private val actions = ArrayList<MutableList<HexAction>>()

        /**
         * Returns a hex action.
         * @param x x-coordinate
         * @param y y-coordinate
         * @return action corresponding to x and y coordinate
         */
        fun valueOf(x: Int, y: Int): HexAction {
            if (x >= actions.size || y >= actions[0].size) {
                generateActions(Math.max(x, y) + 1)
            }
            return actions[x][y]
        }

        private fun generateActions(size: Int) {
            for (i in 0..size - 1) {
                if (i >= actions.size) {
                    actions.add(ArrayList<HexAction>())
                }
                for (j in actions[i].size..size - 1) {
                    actions[i].add(HexAction(i.toByte(), j.toByte()))
                }
            }
        }
    }
}
