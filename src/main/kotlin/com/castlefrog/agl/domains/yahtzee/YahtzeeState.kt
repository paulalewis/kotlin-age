package com.castlefrog.agl.domains.yahtzee

import com.castlefrog.agl.State
import java.util.Arrays

data class YahtzeeState(val diceValues: ByteArray = ByteArray(N_DICE),
                        var nRolls: Byte = 0,
                        val scores: IntArray = IntArray(N_SCORES) {-1}) : State<YahtzeeState> {

    companion object {
        val N_DICE = 5
        val N_VALUES = 6
        val N_SCORES = YahtzeeScoreCategory.values().size
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as YahtzeeState
        return nRolls == other.nRolls &&
                Arrays.equals(diceValues, other.diceValues) &&
                Arrays.equals(scores, other.scores)
    }

    override fun hashCode(): Int {
        return (nRolls.toInt() * 17 + Arrays.hashCode(diceValues)) * 19 + Arrays.hashCode(scores)
    }

    override fun copy(): YahtzeeState {
        return copy(diceValues, nRolls, scores)
    }

    override fun toString(): String {
        val output = StringBuilder()
        output.append(nRolls).append(" - [ ")
        for (diceValue in diceValues) {
            output.append(diceValue).append(" ")
        }
        output.append("]\n")
        val scoreCategories = YahtzeeScoreCategory.values()
        for (i in 0..N_SCORES - 1) {
            output.append(scoreCategories[i].name).append(": ")
            output.append(if (scores[i] != -1) scores[i] else "-")
            if (i != N_SCORES - 1) {
                output.append("\n")
            }
        }
        return output.toString()
    }

}
