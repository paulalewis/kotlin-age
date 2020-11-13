package com.castlefrog.agl.domains.yahtzee

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal class YahtzeeSimulatorTest {

    private val simulator: YahtzeeSimulator = YahtzeeSimulator(Random(322))

    @Test
    fun getNPlayers() {
        assertThat(simulator.nPlayers).isEqualTo(1)
    }

    @Test
    fun getInitialState() {
        val state = YahtzeeState(byteArrayOf(0, 1, 2, 0, 1, 1))
        assertThat(simulator.initialState).isEqualTo(state)
    }

    @Test
    fun calculateRewardsInitialState() {
        val state = simulator.initialState
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(0))
    }

    @Test
    fun calculateRewardsPartialScoreGameNotOver() {
        val state = YahtzeeState(byteArrayOf(1, 1, 1, 1, 0, 0))
        state.scores[YahtzeeScoreCategory.ONES.ordinal] = 5
        state.scores[YahtzeeScoreCategory.TWOS.ordinal] = 6
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(0))
    }

    @Test
    fun calculateRewardsGameCompleteScore() {
        val state = YahtzeeState(byteArrayOf(1, 1, 1, 1, 0, 0))
        state.scores[YahtzeeScoreCategory.ONES.ordinal] = 5
        state.scores[YahtzeeScoreCategory.TWOS.ordinal] = 6
        state.scores[YahtzeeScoreCategory.THREES.ordinal] = 6
        state.scores[YahtzeeScoreCategory.FOURS.ordinal] = 6
        state.scores[YahtzeeScoreCategory.FIVES.ordinal] = 6
        state.scores[YahtzeeScoreCategory.SIXES.ordinal] = 6
        state.scores[YahtzeeScoreCategory.THREE_OF_KIND.ordinal] = 6
        state.scores[YahtzeeScoreCategory.FOUR_OF_KIND.ordinal] = 6
        state.scores[YahtzeeScoreCategory.FULL_HOUSE.ordinal] = 6
        state.scores[YahtzeeScoreCategory.SMALL_STRAIGHT.ordinal] = 6
        state.scores[YahtzeeScoreCategory.LARGE_STRAIGHT.ordinal] = 6
        state.scores[YahtzeeScoreCategory.YAHTZEE.ordinal] = 0
        state.scores[YahtzeeScoreCategory.CHANCE.ordinal] = 6
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(71))
    }

    @Test
    fun calculateRewardsGameCompleteScoreWithBonus() {
        val state = YahtzeeState(byteArrayOf(1, 1, 1, 1, 0, 0))
        state.scores[YahtzeeScoreCategory.ONES.ordinal] = 5
        state.scores[YahtzeeScoreCategory.TWOS.ordinal] = 6
        state.scores[YahtzeeScoreCategory.THREES.ordinal] = 6
        state.scores[YahtzeeScoreCategory.FOURS.ordinal] = 12
        state.scores[YahtzeeScoreCategory.FIVES.ordinal] = 20
        state.scores[YahtzeeScoreCategory.SIXES.ordinal] = 30
        state.scores[YahtzeeScoreCategory.THREE_OF_KIND.ordinal] = 6
        state.scores[YahtzeeScoreCategory.FOUR_OF_KIND.ordinal] = 6
        state.scores[YahtzeeScoreCategory.FULL_HOUSE.ordinal] = 6
        state.scores[YahtzeeScoreCategory.SMALL_STRAIGHT.ordinal] = 6
        state.scores[YahtzeeScoreCategory.LARGE_STRAIGHT.ordinal] = 6
        state.scores[YahtzeeScoreCategory.YAHTZEE.ordinal] = 0
        state.scores[YahtzeeScoreCategory.CHANCE.ordinal] = 6
        assertThat(simulator.calculateRewards(state)).isEqualTo(intArrayOf(150))
    }

    @Test
    fun calculateLegalActionsChooseToReRoll() {
        val expectedActions = arrayListOf(
            YahtzeeRollAction(byteArrayOf(0, 0, 0, 0, 0, 0)),
            YahtzeeRollAction(byteArrayOf(0, 0, 0, 0, 0, 1)),
            YahtzeeRollAction(byteArrayOf(0, 0, 0, 0, 1, 0)),
            YahtzeeRollAction(byteArrayOf(0, 0, 0, 0, 1, 1)),
            YahtzeeRollAction(byteArrayOf(0, 0, 1, 0, 0, 0)),
            YahtzeeRollAction(byteArrayOf(0, 0, 1, 0, 0, 1)),
            YahtzeeRollAction(byteArrayOf(0, 0, 1, 0, 1, 0)),
            YahtzeeRollAction(byteArrayOf(0, 0, 1, 0, 1, 1)),
            YahtzeeRollAction(byteArrayOf(0, 0, 2, 0, 0, 0)),
            YahtzeeRollAction(byteArrayOf(0, 0, 2, 0, 0, 1)),
            YahtzeeRollAction(byteArrayOf(0, 0, 2, 0, 1, 0)),
            YahtzeeRollAction(byteArrayOf(0, 0, 2, 0, 1, 1)),
            YahtzeeRollAction(byteArrayOf(0, 1, 0, 0, 0, 0)),
            YahtzeeRollAction(byteArrayOf(0, 1, 0, 0, 0, 1)),
            YahtzeeRollAction(byteArrayOf(0, 1, 0, 0, 1, 0)),
            YahtzeeRollAction(byteArrayOf(0, 1, 0, 0, 1, 1)),
            YahtzeeRollAction(byteArrayOf(0, 1, 1, 0, 0, 0)),
            YahtzeeRollAction(byteArrayOf(0, 1, 1, 0, 0, 1)),
            YahtzeeRollAction(byteArrayOf(0, 1, 1, 0, 1, 0)),
            YahtzeeRollAction(byteArrayOf(0, 1, 1, 0, 1, 1)),
            YahtzeeRollAction(byteArrayOf(0, 1, 2, 0, 0, 0)),
            YahtzeeRollAction(byteArrayOf(0, 1, 2, 0, 0, 1)),
            YahtzeeRollAction(byteArrayOf(0, 1, 2, 0, 1, 0)),
            YahtzeeRollAction(byteArrayOf(0, 1, 2, 0, 1, 1)),
        )
        assertThat(simulator.calculateLegalActions(simulator.initialState))
            .isEqualTo(arrayListOf(expectedActions))
    }

    @Test
    fun calculateLegalActionsSelectScore() {
        val state = YahtzeeState(diceValues = byteArrayOf(1, 1, 3, 0, 0, 0), nRolls = 3)
        val expectedActions = ArrayList<YahtzeeAction>()
        (YahtzeeScoreCategory.values().indices)
            .forEach { expectedActions.add(YahtzeeSelectAction.valueOf(it)) }
        assertThat(simulator.calculateLegalActions(state))
            .isEqualTo(arrayListOf(expectedActions))
    }

    @Test
    fun stateTransitionReRoll2Dice() {
        val expectedState = YahtzeeState(diceValues = byteArrayOf(0, 2, 3, 0, 0, 0), nRolls = 2)
        assertThat(
            simulator.stateTransition(
                simulator.initialState,
                mapOf(Pair(0, YahtzeeRollAction(byteArrayOf(0, 1, 2, 0, 0, 0))))
            )
        ).isEqualTo(expectedState)
    }
}