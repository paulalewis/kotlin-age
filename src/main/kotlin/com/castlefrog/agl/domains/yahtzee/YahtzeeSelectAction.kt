package com.castlefrog.agl.domains.yahtzee

data class YahtzeeSelectAction private constructor(val scoreCategory: YahtzeeScoreCategory) : YahtzeeAction {

    override fun copy(): YahtzeeSelectAction {
        return this
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
            return valueOf(scoreCategory.ordinal)
        }

        private fun generateSelectActions(): List<YahtzeeSelectAction> {
            return YahtzeeScoreCategory.values().map { YahtzeeSelectAction(it) }
        }
    }
}
