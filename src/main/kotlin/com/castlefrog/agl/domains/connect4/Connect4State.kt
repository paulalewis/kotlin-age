package com.castlefrog.agl.domains.connect4

import com.castlefrog.agl.State
import java.util.Arrays

/**
 * State represented by a bitBoard described below:
 * .  .  .  .  .  .  . Row above top row
 * 5 12 19 26 33 40 47
 * 4 11 18 25 32 39 46
 * 3 10 17 24 31 38 45
 * 2  9 16 23 30 37 44
 * 1  8 15 22 29 36 43
 * 0  7 14 21 28 35 42
 */
class Connect4State(val bitBoards: LongArray = LongArray(2)) : State<Connect4State> {

    val agentTurn: Int
            get() {
                return if (java.lang.Long.bitCount(bitBoards[0]) <= java.lang.Long.bitCount(bitBoards[1])) 0 else 1
            }

    override fun copy(): Connect4State = Connect4State(bitBoards.copyOf())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        return Arrays.equals(bitBoards, (other as Connect4State).bitBoards)
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(bitBoards)
    }

    override fun toString(): String {
        val output = StringBuilder(144)
        for (i in 0..2 * WIDTH + 2) {
            output.append("-")
        }
        output.append("\n")
        for (i in HEIGHT - 1 downTo 0) {
            output.append(": ")
            var j = i
            while (j < (HEIGHT + 1) * WIDTH) {
                val mask = 1L shl j
                when {
                    (bitBoards[0] and mask) != 0L -> output.append("X")
                    (bitBoards[1] and mask) != 0L -> output.append("O")
                    else -> output.append("-")
                }
                output.append(" ")
                j += HEIGHT + 1
            }
            output.append(":\n")
        }
        for (i in 0..2 * WIDTH + 2) {
            output.append("-")
        }
        return output.toString()
    }

    companion object {
        const val WIDTH = 7
        const val HEIGHT = 6
    }
}
