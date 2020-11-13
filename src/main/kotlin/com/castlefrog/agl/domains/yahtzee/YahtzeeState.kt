package com.castlefrog.agl.domains.yahtzee

import com.castlefrog.agl.State

data class YahtzeeState(
    val diceValues: ByteArray,
    var nRolls: Byte = 1,
    val scores: IntArray = IntArray(N_SCORES) { -1 }
) : State<YahtzeeState> {

    companion object {
        const val N_DICE = 5
        const val N_VALUES = 6
        val N_SCORES = YahtzeeScoreCategory.values().size
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as YahtzeeState
        return nRolls == other.nRolls &&
                diceValues.contentEquals(other.diceValues) &&
                scores.contentEquals(other.scores)
    }

    override fun hashCode(): Int {
        return (nRolls.toInt() * 17 + diceValues.contentHashCode()) * 19 + scores.contentHashCode()
    }

    override fun copy(): YahtzeeState {
        return copy(diceValues = diceValues, nRolls = nRolls, scores = scores)
    }

    override fun toString(): String {
        val output = StringBuilder()
        output.append(nRolls).append(" - [ ")
        for (diceValue in diceValues) {
            output.append(diceValue).append(" ")
        }
        output.append("]\n")
        val scoreCategories = YahtzeeScoreCategory.values()
        for (i in 0 until N_SCORES) {
            output.append(scoreCategories[i].name).append(": ")
            output.append(if (scores[i] != -1) scores[i] else "-")
            if (i != N_SCORES - 1) {
                output.append("\n")
            }
        }
        return output.toString()
    }
}
