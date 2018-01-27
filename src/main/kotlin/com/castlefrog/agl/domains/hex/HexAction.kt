package com.castlefrog.agl.domains.hex

import java.util.ArrayList

import com.castlefrog.agl.Action

data class HexAction(val x: Byte, val y: Byte) : Action<HexAction> {

    override fun copy(): HexAction = this

    override fun toString(): String {
        return (0x41 + x).toChar() + y.toString()
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
            for (i in 0 until size) {
                if (i >= actions.size) {
                    actions.add(ArrayList())
                }
                for (j in actions[i].size until size) {
                    actions[i].add(HexAction(i.toByte(), j.toByte()))
                }
            }
        }
    }
}
