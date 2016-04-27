package com.castlefrog.agl.domains.havannah

import java.io.Serializable
import java.util.Vector

import com.castlefrog.agl.Action

data class HavannahAction private constructor(val x: Byte, val y: Byte) : Action, Serializable {

    override fun copy(): HavannahAction {
        return this
    }

    override fun toString(): String {
        return "" + (0x41 + x).toChar() + y
    }

    companion object {
        private val actions = Vector<Vector<HavannahAction>>()

        /**
         * Returns a havannah action.
         * @param x x-coordinate
         * @param y y-coordinate
         * @return action corresponding to x and y coordinate
         */
        fun valueOf(x: Int, y: Int): HavannahAction {
            if (x >= actions.size || y >= actions.size) {
                generateActions(Math.max(x + 1, y + 1))
            }
            return actions[x][y]
        }

        private fun generateActions(size: Int) {
            actions.setSize(size)
            for (i in 0..size - 1) {
                if (actions[i] == null) {
                    actions[i] = Vector<HavannahAction>()
                }
                actions[i].setSize(size)
                for (j in 0..size - 1) {
                    if (actions[i][j] == null) {
                        actions[i][j] = HavannahAction(i.toByte(), j.toByte())
                    }
                }
            }
        }
    }
}
