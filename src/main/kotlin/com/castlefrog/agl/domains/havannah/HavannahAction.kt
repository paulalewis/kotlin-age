package com.castlefrog.agl.domains.havannah

import java.util.Vector

import com.castlefrog.agl.Action

data class HavannahAction(val x: Byte, val y: Byte) : Action<HavannahAction> {

    override fun copy(): HavannahAction = this

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
            (0 until size).forEach { i ->
                if (actions[i] == null) {
                    actions[i] = Vector()
                }
                actions[i].setSize(size)
                (0 until size)
                        .filter { j -> actions[i][j] == null }
                        .forEach { actions[i][it] = HavannahAction(i.toByte(), it.toByte()) }
            }
        }
    }
}
