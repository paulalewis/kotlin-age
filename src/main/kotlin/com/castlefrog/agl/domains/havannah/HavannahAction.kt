package com.castlefrog.agl.domains.havannah

import com.castlefrog.agl.Action

data class HavannahAction(val x: Byte, val y: Byte) : Action<HavannahAction> {

    override fun copy(): HavannahAction = this

    override fun toString(): String {
        return "" + (0x41 + x).toChar() + y
    }

    companion object {
        fun generateActions(size: Int): Array<Array<HavannahAction>> {
            return Array(size) { i ->
                Array(size) { j ->
                    HavannahAction(i.toByte(), j.toByte())
                }
            }
        }
    }
}
