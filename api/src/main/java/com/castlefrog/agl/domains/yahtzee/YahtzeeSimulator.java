package com.castlefrog.agl.domains.yahtzee;

import java.util.ArrayList;
import java.util.List;

import com.castlefrog.agl.AbstractSimulator;

public final class YahtzeeSimulator extends AbstractSimulator<YahtzeeState, YahtzeeAction> {
    private static final int N_AGENTS = 1;

    private int nCategoriesLeft_;

    private YahtzeeSimulator(YahtzeeState state) {
        legalActions_ = new ArrayList<>();
        legalActions_.add(new ArrayList<YahtzeeAction>());
        setState(state);
    }

    private YahtzeeSimulator(YahtzeeSimulator simulator,
                             int nCategoriesLeft) {
        super(simulator);
        nCategoriesLeft_ = nCategoriesLeft;
    }

    public YahtzeeSimulator copy() {
        return new YahtzeeSimulator(this, nCategoriesLeft_);
    }

    public static YahtzeeSimulator create(YahtzeeState state) {
        return new YahtzeeSimulator(state);
    }

    public void setState(YahtzeeState state) {
        state_ = state;
        computeCategoriesLeft();
        computeLegalActions();
        computeRewards();
    }

    private void computeLegalActions() {
        clearLegalActions();
        if (nCategoriesLeft_ != 0) {
            if (state_.getNRolls() < 3) {
                byte[] diceValues = state_.getDiceValues();
                for (byte i = 0; i <= diceValues[0]; i++) {
                    for (byte j = 0; j <= diceValues[1]; j++) {
                        for (byte k = 0; k <= diceValues[2]; k++) {
                            for (byte l = 0; l <= diceValues[3]; l++) {
                                for (byte m = 0; m <= diceValues[4]; m++) {
                                    for (byte n = 0; n <= diceValues[5]; n++) {
                                        legalActions_.get(0).add(YahtzeeRollAction.valueOf(new byte[] {i, j, k, l, m, n}));
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                int[] scores = state_.getScores();
                int yahtzee = checkYahtzee(state_.getDiceValues());
                if (yahtzee == -1 || scores[yahtzee] != -1) {
                    for (int i = 0; i < YahtzeeState.Companion.getN_SCORES(); i++) {
                        if (scores[i] == -1) {
                            legalActions_.get(0).add(YahtzeeSelectAction.valueOf(i));
                        }
                    }
                } else {
                    legalActions_.get(0).add(YahtzeeSelectAction.valueOf(yahtzee));
                    if (scores[YahtzeeScoreCategory.YAHTZEE.ordinal()] == -1) {
                        legalActions_.get(0).add(YahtzeeSelectAction.valueOf(YahtzeeScoreCategory.YAHTZEE));
                    }
                }
            }
        }
    }

    private void computeCategoriesLeft() {
        nCategoriesLeft_ = 0;
        for (int i = 0; i < YahtzeeState.Companion.getN_SCORES(); i++) {
            if (state_.getScores()[i] == -1) {
                nCategoriesLeft_ += 1;
            }
        }
    }

    private int checkYahtzee(byte[] diceValues) {
        for (int i = 0; i < YahtzeeState.Companion.getN_VALUES(); i++) {
            if (diceValues[i] == YahtzeeState.Companion.getN_DICE()) {
                return i;
            }
        }
        return -1;
    }

    private void computeRewards() {
        if (rewards_ == null) {
            rewards_ = new int[N_AGENTS];
        }
        if (isTerminalState()) {
            int[] scores = state_.getScores();
            for (int i = 0; i < 6; i++) {
                rewards_[0] += scores[i];
            }
            if (rewards_[0] >= 63) {
                rewards_[0] += 35;
            }
            for (int i = 6; i < YahtzeeState.Companion.getN_SCORES(); i++) {
                rewards_[0] += scores[i];
            }
        }
    }

    public void stateTransition(List<YahtzeeAction> actions) {
        YahtzeeAction action = actions.get(0);
        if (!legalActions_.get(0).contains(action)) {
            System.out.println("nCategories = " + nCategoriesLeft_);
            throw new IllegalArgumentException("Illegal action, " + action + ", from state, " + state_);
        }

        byte[] diceValues = state_.getDiceValues();
        int rolls = state_.getNRolls();
        int[] scores = state_.getScores();
        int yahtzee = checkYahtzee(diceValues);
        if (yahtzee != -1 && scores[YahtzeeScoreCategory.YAHTZEE.ordinal()] >= 50) {
            scores[YahtzeeScoreCategory.YAHTZEE.ordinal()] += 100;
        }

        if (action instanceof YahtzeeRollAction) {
            YahtzeeRollAction rollAction = (YahtzeeRollAction) action;
            diceValues = rollAction.getSelected();
            int numSelected = 0;
            for (byte diceValue : diceValues) {
                numSelected += diceValue;
            }
            for (int i = numSelected; i < YahtzeeState.Companion.getN_DICE(); i++) {
                diceValues[(int) (Math.random() * YahtzeeState.Companion.getN_VALUES())] += 1;
            }
            rolls += 1;
        } else {
            YahtzeeSelectAction selectAction = (YahtzeeSelectAction) action;
            YahtzeeScoreCategory category = selectAction.getScoreCategory();
            scores[category.ordinal()] = 0;

            switch (category) {
            case ONES:
                scores[category.ordinal()] = diceValues[0];
                break;
            case TWOS:
                scores[category.ordinal()] = diceValues[1] * 2;
                break;
            case THREES:
                scores[category.ordinal()] = diceValues[2] * 3;
                break;
            case FOURS:
                scores[category.ordinal()] = diceValues[3] * 4;
                break;
            case FIVES:
                scores[category.ordinal()] = diceValues[4] * 5;
                break;
            case SIXES:
                scores[category.ordinal()] = diceValues[5] * 6;
                break;
            case THREE_OF_KIND:
                for (int i = 0; i < YahtzeeState.Companion.getN_VALUES(); i++) {
                    if (diceValues[i] >= 3) {
                        for (int j = 0; j < diceValues.length; j++) {
                            scores[category.ordinal()] += diceValues[j] * (j + 1);
                        }
                        break;
                    }
                }
                break;
            case FOUR_OF_KIND:
                for (int i = 0; i < YahtzeeState.Companion.getN_VALUES(); i++) {
                    if (diceValues[i] >= 4) {
                        for (int j = 0; j < diceValues.length; j++) {
                            scores[category.ordinal()] += diceValues[j] * (j + 1);
                        }
                        break;
                    }
                }
                break;
            case FULL_HOUSE:
                boolean two = false;
                boolean three = false;
                for (int i = 0; i < YahtzeeState.Companion.getN_VALUES(); i++) {
                    if (diceValues[i] == 2) {
                        two = true;
                    } else if (diceValues[i] == 3) {
                        three = true;
                    }
                }
                if (two && three) {
                    scores[category.ordinal()] = 25;
                }
                break;
            case SMALL_STRAIGHT:
                int count = 0;
                for (int i = 0; i < YahtzeeState.Companion.getN_VALUES(); i++) {
                    if (diceValues[i] > 0) {
                        count++;
                    } else if (count >= 4) {
                        break;
                    } else {
                        count = 0;
                    }
                }
                if (count >= 4) {
                    scores[category.ordinal()] = 30;
                }
                break;
            case LARGE_STRAIGHT:
                count = 0;
                for (int i = 0; i < YahtzeeState.Companion.getN_VALUES(); i++) {
                    if (diceValues[i] > 0) {
                        count++;
                    } else if (count >= 5) {
                        break;
                    } else {
                        count = 0;
                    }
                }
                if (count == 5) {
                    scores[category.ordinal()] = 40;
                }
                break;
            case YAHTZEE:
                for (int i = 0; i < YahtzeeState.Companion.getN_VALUES(); i++) {
                    if (diceValues[i] == 5) {
                        scores[category.ordinal()] = 50;
                    }
                }
                break;
            case CHANCE:
                for (int i = 0; i < YahtzeeState.Companion.getN_VALUES(); i++) {
                    scores[category.ordinal()] += diceValues[i] * (i + 1);
                }
                break;
            default:
                break;
            }
            diceValues = new byte[YahtzeeState.Companion.getN_VALUES()];
            for (int i = 0; i < YahtzeeState.Companion.getN_DICE(); i++) {
                diceValues[(byte) (Math.random() * YahtzeeState.Companion.getN_VALUES())] += 1;
            }
            rolls = 1;
            nCategoriesLeft_ -= 1;
        }
        state_ = new YahtzeeState(diceValues, (byte)rolls, scores);
        computeLegalActions();
        computeRewards();
    }

    public int getNAgents() {
        return N_AGENTS;
    }

}
