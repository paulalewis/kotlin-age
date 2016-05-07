package com.castlefrog.agl.domains.yahtzee

import java.util.ArrayList

import com.castlefrog.agl.AbstractSimulator

class YahtzeeSimulator : AbstractSimulator<YahtzeeState, YahtzeeAction> {

    private var nCategoriesLeft: Int = 0

    private constructor(state: YahtzeeState) {
        legalActions_ = ArrayList<List<YahtzeeAction>>()
        legalActions_.add(ArrayList<YahtzeeAction>())
        setState(state)
    }

    private constructor(simulator: YahtzeeSimulator,
                        nCategoriesLeft: Int) : super(simulator) {
        this.nCategoriesLeft = nCategoriesLeft
    }

    override fun copy(): YahtzeeSimulator {
        return YahtzeeSimulator(this, nCategoriesLeft)
    }

    override fun setState(state: YahtzeeState) {
        state_ = state
        rewards_ = IntArray(N_AGENTS)
        nCategoriesLeft = computeCategoriesLeft(state.scores)
        computeLegalActions()
        computeRewards()
    }

    private fun computeLegalActions() {
        clearLegalActions()
        if (nCategoriesLeft != 0) {
            if (state_.nRolls < 3) {
                val diceValues = state_.diceValues
                for (i in 0..diceValues[0]) {
                    for (j in 0..diceValues[1]) {
                        for (k in 0..diceValues[2]) {
                            for (l in 0..diceValues[3]) {
                                for (m in 0..diceValues[4]) {
                                    for (n in 0..diceValues[5]) {
                                        legalActions_[0].add(YahtzeeRollAction(byteArrayOf(i.toByte(), j.toByte(), k.toByte(), l.toByte(), m.toByte(), n.toByte())))
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                val yahtzee = checkYahtzee(state_.diceValues)
                if (yahtzee == -1 || state_.scores[yahtzee] != -1) {
                    for (i in 0..YahtzeeState.N_SCORES - 1) {
                        if (state_.scores[i] == -1) {
                            legalActions_[0].add(YahtzeeSelectAction.valueOf(i))
                        }
                    }
                } else {
                    legalActions_[0].add(YahtzeeSelectAction.valueOf(yahtzee))
                    if (state_.scores[YahtzeeScoreCategory.YAHTZEE.ordinal] == -1) {
                        legalActions_[0].add(YahtzeeSelectAction.valueOf(YahtzeeScoreCategory.YAHTZEE))
                    }
                }
            }
        }
    }

    private fun computeCategoriesLeft(scores: IntArray): Int {
        var nCategoriesLeft = 0
        for (i in 0..scores.size - 1) {
            if (scores[i] == -1) {
                nCategoriesLeft += 1
            }
        }
        return nCategoriesLeft
    }

    /**
     * @return die number corresponding to yahtzee or -1 if no yahtzee
     */
    private fun checkYahtzee(diceValues: ByteArray): Int {
        for (i in 0..YahtzeeState.N_VALUES - 1) {
            if (diceValues[i].toInt() == YahtzeeState.N_DICE) {
                return i
            }
        }
        return -1
    }

    private fun computeRewards() {
        if (isTerminalState) {
            val scores = state_.scores
            for (i in 0..5) {
                rewards_[0] += scores[i]
            }
            if (rewards_[0] >= 63) {
                rewards_[0] += 35
            }
            for (i in 6..YahtzeeState.N_SCORES - 1) {
                rewards_[0] += scores[i]
            }
        }
    }

    override fun stateTransition(actions: List<YahtzeeAction>) {
        val action = actions[0]
        if (!legalActions_[0].contains(action)) {
            println("nCategories = " + nCategoriesLeft)
            throw IllegalArgumentException("Illegal action, $action, from state, $state_")
        }

        var diceValues = state_.diceValues
        var rolls = state_.nRolls.toInt()
        val scores = state_.scores
        val yahtzee = checkYahtzee(diceValues)
        if (yahtzee != -1 && scores[YahtzeeScoreCategory.YAHTZEE.ordinal] >= 50) {
            scores[YahtzeeScoreCategory.YAHTZEE.ordinal] += 100
        }

        if (action is YahtzeeRollAction) {
            diceValues = action.selected
            var numSelected = 0
            for (diceValue in diceValues) {
                numSelected += diceValue.toInt()
            }
            for (i in numSelected..YahtzeeState.N_DICE - 1) {
                diceValues[(Math.random() * YahtzeeState.N_VALUES).toInt()].inc()
            }
            rolls += 1
        } else {
            val selectAction = action as YahtzeeSelectAction
            val category = selectAction.scoreCategory
            scores[category.ordinal] = 0

            when (category) {
                YahtzeeScoreCategory.ONES -> scores[category.ordinal] = diceValues[0].toInt()
                YahtzeeScoreCategory.TWOS -> scores[category.ordinal] = diceValues[1] * 2
                YahtzeeScoreCategory.THREES -> scores[category.ordinal] = diceValues[2] * 3
                YahtzeeScoreCategory.FOURS -> scores[category.ordinal] = diceValues[3] * 4
                YahtzeeScoreCategory.FIVES -> scores[category.ordinal] = diceValues[4] * 5
                YahtzeeScoreCategory.SIXES -> scores[category.ordinal] = diceValues[5] * 6
                YahtzeeScoreCategory.THREE_OF_KIND -> for (i in 0..YahtzeeState.N_VALUES - 1) {
                    if (diceValues[i] >= 3) {
                        for (j in diceValues.indices) {
                            scores[category.ordinal] += diceValues[j] * (j + 1)
                        }
                        break
                    }
                }
                YahtzeeScoreCategory.FOUR_OF_KIND -> for (i in 0..YahtzeeState.N_VALUES - 1) {
                    if (diceValues[i] >= 4) {
                        for (j in diceValues.indices) {
                            scores[category.ordinal] += diceValues[j] * (j + 1)
                        }
                        break
                    }
                }
                YahtzeeScoreCategory.FULL_HOUSE -> {
                    var two = false
                    var three = false
                    for (i in 0..YahtzeeState.N_VALUES - 1) {
                        if (diceValues[i].toInt() == 2) {
                            two = true
                        } else if (diceValues[i].toInt() == 3) {
                            three = true
                        }
                    }
                    if (two && three) {
                        scores[category.ordinal] = 25
                    }
                }
                YahtzeeScoreCategory.SMALL_STRAIGHT -> {
                    var count = 0
                    for (i in 0..YahtzeeState.N_VALUES - 1) {
                        if (diceValues[i] > 0) {
                            count++
                        } else if (count >= 4) {
                            break
                        } else {
                            count = 0
                        }
                    }
                    if (count >= 4) {
                        scores[category.ordinal] = 30
                    }
                }
                YahtzeeScoreCategory.LARGE_STRAIGHT -> {
                    var count = 0
                    for (i in 0..YahtzeeState.N_VALUES - 1) {
                        if (diceValues[i] > 0) {
                            count++
                        } else if (count >= 5) {
                            break
                        } else {
                            count = 0
                        }
                    }
                    if (count == 5) {
                        scores[category.ordinal] = 40
                    }
                }
                YahtzeeScoreCategory.YAHTZEE -> for (i in 0..YahtzeeState.N_VALUES - 1) {
                    if (diceValues[i].toInt() == 5) {
                        scores[category.ordinal] = 50
                    }
                }
                YahtzeeScoreCategory.CHANCE -> for (i in 0..YahtzeeState.N_VALUES - 1) {
                    scores[category.ordinal] += diceValues[i] * (i + 1)
                }
                else -> {
                }
            }
            diceValues = ByteArray(YahtzeeState.N_VALUES)
            for (i in 0..YahtzeeState.N_DICE - 1) {
                diceValues[(Math.random() * YahtzeeState.N_VALUES).toInt()].inc()
            }
            rolls = 1
            nCategoriesLeft -= 1
        }
        state_ = YahtzeeState(diceValues, rolls.toByte(), scores)
        computeLegalActions()
        computeRewards()
    }

    override fun getNAgents(): Int {
        return N_AGENTS
    }

    companion object {
        private val N_AGENTS = 1

        fun create(state: YahtzeeState): YahtzeeSimulator {
            return YahtzeeSimulator(state)
        }
    }

}
