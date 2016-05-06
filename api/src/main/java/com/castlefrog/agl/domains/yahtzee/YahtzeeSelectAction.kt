package com.castlefrog.agl.domains.yahtzee

import java.util.ArrayList

class YahtzeeSelectAction private constructor(
        /** Each select action selects a particular score category.  */
        val scoreCategory: YahtzeeScoreCategory) : YahtzeeAction {

    override fun copy(): YahtzeeSelectAction {
        return this
    }

    override fun hashCode(): Int {
        return scoreCategory.ordinal
    }

    override fun equals(other: Any?): Boolean {
        if (other !is YahtzeeSelectAction) {
            return false
        }
        return scoreCategory === other.scoreCategory
    }

    override fun toString(): String {
        return scoreCategory.toString()
    }

    companion object {
        /** Holds list of all possible select actions.  */
        private val selectActions = generateSelectActions()

        fun valueOf(scoreCategory: Int): YahtzeeAction {
            return selectActions[scoreCategory]
        }

        fun valueOf(scoreCategory: YahtzeeScoreCategory): YahtzeeAction {
            return selectActions[scoreCategory.ordinal]
        }

        private fun generateSelectActions(): List<YahtzeeSelectAction> {
            val selectActions = ArrayList<YahtzeeSelectAction>()
            for (scoreCategory in YahtzeeScoreCategory.values()) {
                selectActions.add(YahtzeeSelectAction(scoreCategory))
            }
            return selectActions
        }
    }
}
