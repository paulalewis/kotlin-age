package com.castlefrog.agl.domains.backgammon

import com.castlefrog.agl.State
import java.util.Arrays

/**
 * Represents a backgammon state as an array of byte locations.
 * @param locations each location is 0 if no pieces are at that location and positive for
 *                  the number of pieces player 1 has there and negative for the number of
 *                  pieces player 2 has there.
 * @param dice two values in range 0-5 that represent die faces 1 to 6 each; order does not matter
 * @param agentTurn current players turn
 */
class BackgammonState(
        val locations: ByteArray = byteArrayOf(0, 2, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0, 5, -5, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, -2, 0),
        val dice: ByteArray = ByteArray(BackgammonState.N_DICE),
        var agentTurn: Int = 0) : State<BackgammonState> {

    override fun copy(): BackgammonState {
        val locations = ByteArray(N_LOCATIONS)
        System.arraycopy(this.locations, 0, locations, 0, N_LOCATIONS)
        val dice = ByteArray(N_DICE)
        System.arraycopy(this.dice, 0, dice, 0, N_DICE)
        return BackgammonState(locations, dice, agentTurn)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is BackgammonState) {
            return false
        }
        return Arrays.equals(locations, other.locations) &&
                ((dice[0] == other.dice[0] && dice[1] == other.dice[1]) ||
                        (dice[0] == other.dice[1] && dice[1] == other.dice[0])) &&
                agentTurn == other.agentTurn
    }

    override fun hashCode(): Int {
        val dice = if (dice[0] < dice[1]) {
            intArrayOf(dice[1].toInt(), dice[0].toInt())
        } else {
            intArrayOf(dice[0].toInt(), dice[1].toInt())
        }
        return (agentTurn * 11 + Arrays.hashCode(locations)) * 17 + Arrays.hashCode(dice)
    }

    override fun toString(): String {
        val output = StringBuilder(" ").append(agentTurn).append(" - ")
        val dice = if (dice[0] < dice[1]) {
            intArrayOf(dice[1].toInt(), dice[0].toInt())
        } else {
            intArrayOf(dice[0].toInt(), dice[1].toInt())
        }
        output.append("[").append(dice[0] + 1).append("][").append(dice[1] + 1).append("]\n")
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

        val TURN_PLAYER_1 = 0
        val TURN_PLAYER_2 = 1
    }
}
