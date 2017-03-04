package com.castlefrog.agl.domains.yahtzee

import com.castlefrog.agl.Simulator
import java.util.ArrayList

class YahtzeeSimulator : Simulator<YahtzeeState, YahtzeeAction> {

    override val nPlayers: Int = 2

    override fun getInitialState(): YahtzeeState {
        return YahtzeeState()
    }

    override fun calculateRewards(state: YahtzeeState): IntArray {
        val rewards = intArrayOf(0)
        if (!state.hasCategoriesLeft()) {
            val scores = state.scores
            for (i in 0..5) {
                rewards[0] += scores[i]
            }
            if (rewards[0] >= 63) {
                rewards[0] += 35
            }
            for (i in 6..YahtzeeState.N_SCORES - 1) {
                rewards[0] += scores[i]
            }
        }
        return rewards
    }

    override fun calculateLegalActions(state: YahtzeeState): List<MutableList<YahtzeeAction>> {
        val legalActions = ArrayList<MutableList<YahtzeeAction>>()
        legalActions.add(ArrayList<YahtzeeAction>())
        if (state.hasCategoriesLeft()) {
            if (state.nRolls < 3) {
                val diceValues = state.diceValues
                for (i in 0..diceValues[0]) {
                    for (j in 0..diceValues[1]) {
                        for (k in 0..diceValues[2]) {
                            for (l in 0..diceValues[3]) {
                                for (m in 0..diceValues[4]) {
                                    for (n in 0..diceValues[5]) {
                                        legalActions[0].add(YahtzeeRollAction(byteArrayOf(i.toByte(), j.toByte(),
                                                k.toByte(), l.toByte(), m.toByte(), n.toByte())))
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                val yahtzee = checkYahtzee(state.diceValues)
                if (yahtzee == -1 || state.scores[yahtzee] != -1) {
                    for (i in 0..YahtzeeState.N_SCORES - 1) {
                        if (state.scores[i] == -1) {
                            legalActions[0].add(YahtzeeSelectAction.valueOf(i))
                        }
                    }
                } else {
                    legalActions[0].add(YahtzeeSelectAction.valueOf(yahtzee))
                    if (state.scores[YahtzeeScoreCategory.YAHTZEE.ordinal] == -1) {
                        legalActions[0].add(YahtzeeSelectAction.valueOf(YahtzeeScoreCategory.YAHTZEE))
                    }
                }
            }
        }
        return legalActions
    }

    override fun stateTransition(state: YahtzeeState, actions: Map<Int, YahtzeeAction>): YahtzeeState {
        val action = actions[0]
        val legalActions = calculateLegalActions(state)
        if (action === null || !legalActions[0].contains(action)) {
            throw IllegalArgumentException("Illegal action, $action, from state, $state")
        }

        var diceValues = state.diceValues
        var rolls = state.nRolls.toInt()
        val scores = state.scores
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
        }
        return YahtzeeState(diceValues, rolls.toByte(), scores)
    }

    companion object {

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

        fun YahtzeeState.hasCategoriesLeft(): Boolean {
            for (i in 0..scores.size - 1) {
                if (scores[i] == -1) {
                    return true
                }
            }
            return false
        }

    }

}
