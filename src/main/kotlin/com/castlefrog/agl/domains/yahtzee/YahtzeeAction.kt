package com.castlefrog.agl.domains.yahtzee

import com.castlefrog.agl.Action

sealed class YahtzeeAction : Action<YahtzeeAction>

/**
 * The roll action controls which dice get rolled and which are kept for next
 * state.
 * Indicated quantity of each die number to not roll again.
 */
data class YahtzeeRollAction(val selected: ByteArray = ByteArray(YahtzeeState.N_VALUES)) : YahtzeeAction() {

    override fun copy(): YahtzeeRollAction = copy(selected = selected)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        return selected.contentEquals((other as YahtzeeRollAction).selected)
    }

    override fun hashCode(): Int = selected.contentHashCode()

    override fun toString(): String {
        val output = StringBuilder()
        output.append("[ ")
        for (value in selected) {
            output.append(value).append(" ")
        }
        output.append("]")
        return output.toString()
    }
}

data class YahtzeeSelectAction(val scoreCategory: YahtzeeScoreCategory) : YahtzeeAction() {

    override fun toString(): String {
        return scoreCategory.toString()
    }

    override fun copy(): YahtzeeSelectAction = this

    companion object {
        /** list of all possible select actions. */
        private val selectActions = generateSelectActions()

        fun valueOf(scoreCategory: Int): YahtzeeAction {
            return selectActions[scoreCategory]
        }

        fun valueOf(scoreCategory: YahtzeeScoreCategory): YahtzeeAction {
            return valueOf(scoreCategory.ordinal)
        }

        private fun generateSelectActions(): List<YahtzeeSelectAction> {
            return YahtzeeScoreCategory.values().map { YahtzeeSelectAction(it) }
        }
    }
}
