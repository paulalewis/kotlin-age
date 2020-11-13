package com.castlefrog.agl.domains.hex

import com.castlefrog.agl.Action

data class HexAction(val x: Byte, val y: Byte) : Action<HexAction> {

    override fun copy(): HexAction = this

    override fun toString(): String {
        return (0x41 + x).toChar() + y.toString()
    }

    companion object {
        fun generateActions(size: Int): Array<Array<HexAction>> {
            return Array(size) { i ->
                Array(size) { j ->
                    HexAction(i.toByte(), j.toByte())
                }
            }
        }
    }
}
