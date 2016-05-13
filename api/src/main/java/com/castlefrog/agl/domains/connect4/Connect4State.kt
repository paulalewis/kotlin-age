package com.castlefrog.agl.domains.connect4

import com.castlefrog.agl.State

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
data class Connect4State(var bitBoardBlack: Long = 0,
                         var bitBoardWhite: Long = 0,
                         var agentTurn: Int = 0) : State<Connect4State> {

    override fun copy(): Connect4State {
        return copy(bitBoardBlack, bitBoardWhite, agentTurn)
    }

    override fun toString(): String {
        val output = StringBuilder()
        for (i in 0..2 * WIDTH + 2) {
            output.append("-")
        }
        output.append("\n")
        for (i in HEIGHT - 1 downTo 0) {
            output.append(": ")
            var j = i
            while (j < (HEIGHT + 1) * WIDTH) {
                val mask = 1L shl j
                if ((bitBoardBlack and mask) != 0L) {
                    output.append("X")
                } else if ((bitBoardWhite and mask) != 0L) {
                    output.append("O")
                } else {
                    output.append("-")
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
        val WIDTH = 7
        val HEIGHT = 6
    }
}
