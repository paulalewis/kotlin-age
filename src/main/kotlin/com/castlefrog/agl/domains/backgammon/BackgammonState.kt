package com.castlefrog.agl.domains.backgammon

import com.castlefrog.agl.State

/**
 * Represents a backgammon state as an array of byte locations. Each location is
 * 0 if no pieces are at that location and positive if player 1 has pieces there
 * and negative for the number of pieces player 2 has there.
 */
data class BackgammonState(val locations: ByteArray = byteArrayOf(0, 2, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0, 5, -5, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, -2, 0),
                           val dice: ByteArray = ByteArray(BackgammonState.N_DICE),
                           var agentTurn: Int = 0) : State<BackgammonState> {

    override fun copy(): BackgammonState {
        val locations = ByteArray(N_LOCATIONS)
        System.arraycopy(this.locations, 0, locations, 0, N_LOCATIONS)
        val dice = ByteArray(N_DICE)
        System.arraycopy(this.dice, 0, dice, 0, N_DICE)
        return copy(locations, dice, agentTurn)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BackgammonState) {
            return false
        }
        for (i in 0..N_LOCATIONS - 1) {
            if (locations[i] != other.locations[i]) {
                return false
            }
        }
        return dice[0] == other.dice[0] && dice[1] == other.dice[1]
    }

    override fun hashCode(): Int {
        var code = 11 * (7 + dice[0]) + dice[1]
        for (i in 0..N_LOCATIONS - 1) {
            code = 11 * code + locations[i]
        }
        code = 11 * code + agentTurn
        return code
    }

    override fun toString(): String {
        val output = StringBuilder()
        output.append("[").append(dice[0].toInt()).append("][").append(dice[1].toInt()).append("]\n")
        for (i in 12 downTo 7) {
            if (locations[i] >= 0) {
                output.append(" ")
            }
            output.append(locations[i].toInt())
        }
        output.append("|")
        for (i in 6 downTo 1) {
            if (locations[i] >= 0) {
                output.append(" ")
            }
            output.append(locations[i].toInt())
        }
        output.append(" [").append(locations[0].toInt()).append("]\n")
        output.append("------------|------------\n")
        for (i in 13..18) {
            if (locations[i] >= 0) {
                output.append(" ")
            }
            output.append(locations[i].toInt())
        }
        output.append("|")
        var i = 19
        while (i < 25) {
            if (locations[i] >= 0) {
                output.append(" ")
            }
            output.append(locations[i].toInt())
            i += 1
        }
        output.append(" [").append(locations[25].toInt()).append("]")
        return output.toString()
    }

    companion object {
        val N_DICE = 2
        val N_DIE_FACES = 6
        val N_LOCATIONS = 26

        val TURN_BLACK = 0
        val TURN_WHITE = 1
    }
}
