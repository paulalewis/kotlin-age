package com.castlefrog.agl.domains.yahtzee

import com.castlefrog.agl.Simulator
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.any
import kotlin.collections.filter
import kotlin.collections.firstOrNull
import kotlin.collections.forEach
import kotlin.collections.indices
import kotlin.collections.sumBy
import kotlin.random.Random

class YahtzeeSimulator(private val random: Random = Random) : Simulator<YahtzeeState, YahtzeeAction> {

    override val initialState: YahtzeeState
        get() = YahtzeeState(diceValues = rollDice(random))

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
            for (i in 6 until YahtzeeState.N_SCORES) {
                rewards[0] += scores[i]
            }
        }
        return rewards
    }

    override fun calculateLegalActions(state: YahtzeeState): List<MutableList<YahtzeeAction>> {
        val legalActions = ArrayList<MutableList<YahtzeeAction>>()
        legalActions.add(ArrayList())
        if (state.hasCategoriesLeft()) {
            if (state.nRolls < 3) {
                val diceValues = state.diceValues
                for (i in 0..diceValues[0]) {
                    for (j in 0..diceValues[1]) {
                        for (k in 0..diceValues[2]) {
                            for (l in 0..diceValues[3]) {
                                for (m in 0..diceValues[4]) {
                                    for (n in 0..diceValues[5]) {
                                        legalActions[0].add(
                                            YahtzeeRollAction(
                                                byteArrayOf(
                                                    i.toByte(), j.toByte(),
                                                    k.toByte(), l.toByte(), m.toByte(), n.toByte()
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                val yahtzee = checkYahtzee(state.diceValues)
                if (yahtzee == -1 || state.scores[yahtzee] != -1) {
                    (0 until YahtzeeState.N_SCORES)
                        .filter { state.scores[it] == -1 }
                        .forEach { legalActions[0].add(YahtzeeSelectAction.valueOf(it)) }
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

        when (action) {
            is YahtzeeRollAction -> {
                diceValues = action.selected
                val numSelected = diceValues.sumBy { it.toInt() }
                for (i in numSelected until YahtzeeState.N_DICE) {
                    val roll = random.nextInt(diceValues.size)
                    diceValues[roll] = diceValues[roll].inc()
                }
                rolls += 1
            }
            is YahtzeeSelectAction -> {
                val category = action.scoreCategory
                scores[category.ordinal] = 0

                when (category) {
                    YahtzeeScoreCategory.ONES -> scores[category.ordinal] = diceValues[0].toInt()
                    YahtzeeScoreCategory.TWOS -> scores[category.ordinal] = diceValues[1] * 2
                    YahtzeeScoreCategory.THREES -> scores[category.ordinal] = diceValues[2] * 3
                    YahtzeeScoreCategory.FOURS -> scores[category.ordinal] = diceValues[3] * 4
                    YahtzeeScoreCategory.FIVES -> scores[category.ordinal] = diceValues[4] * 5
                    YahtzeeScoreCategory.SIXES -> scores[category.ordinal] = diceValues[5] * 6
                    YahtzeeScoreCategory.THREE_OF_KIND -> for (i in 0 until YahtzeeState.N_VALUES) {
                        if (diceValues[i] >= 3) {
                            for (j in diceValues.indices) {
                                scores[category.ordinal] += diceValues[j] * (j + 1)
                            }
                            break
                        }
                    }
                    YahtzeeScoreCategory.FOUR_OF_KIND -> for (i in 0 until YahtzeeState.N_VALUES) {
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
                        for (i in 0 until YahtzeeState.N_VALUES) {
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
                        for (i in 0 until YahtzeeState.N_VALUES) {
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
                        for (i in 0 until YahtzeeState.N_VALUES) {
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
                    YahtzeeScoreCategory.YAHTZEE -> (0 until YahtzeeState.N_VALUES)
                        .filter { diceValues[it].toInt() == 5 }
                        .forEach { _ -> scores[category.ordinal] = 50 }
                    YahtzeeScoreCategory.CHANCE -> for (i in 0 until YahtzeeState.N_VALUES) {
                        scores[category.ordinal] += diceValues[i] * (i + 1)
                    }
                }
                diceValues = rollDice(random)
                rolls = 1
            }
        }
        return YahtzeeState(diceValues, rolls.toByte(), scores)
    }

    companion object {
        private fun rollDice(random: Random): ByteArray {
            val diceValues = ByteArray(YahtzeeState.N_VALUES)
            for (i in 0 until YahtzeeState.N_DICE) {
                val index = random.nextInt(YahtzeeState.N_VALUES)
                diceValues[index] = diceValues[index].inc()
            }
            return diceValues
        }

        /**
         * @return die number corresponding to yahtzee or -1 if no yahtzee
         */
        private fun checkYahtzee(diceValues: ByteArray): Int {
            return (0 until YahtzeeState.N_VALUES).firstOrNull { diceValues[it].toInt() == YahtzeeState.N_DICE } ?: -1
        }

        fun YahtzeeState.hasCategoriesLeft(): Boolean {
            return (scores.indices).any { scores[it] == -1 }
        }
    }
}
