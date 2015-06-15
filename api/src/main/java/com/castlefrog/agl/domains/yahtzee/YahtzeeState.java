package com.castlefrog.agl.domains.yahtzee;

import com.castlefrog.agl.State;

public final class YahtzeeState implements State<YahtzeeState> {
    public static final int N_DICE = 5;
    public static final int N_VALUES = 6;
    public static final int N_SCORES = YahtzeeScoreCategory.values().length;

    private final byte[] diceValues_;
    private final byte rolls_;
    private final int[] scores_;

    public YahtzeeState() {
        diceValues_ = new byte[YahtzeeState.N_VALUES];
        for (int i = 0; i < YahtzeeState.N_DICE; i++) {
            diceValues_[(byte) (Math.random() * YahtzeeState.N_VALUES)] += 1;
        }
        rolls_ = 1;
        scores_ = new int[YahtzeeState.N_SCORES];
        for (int i = 0; i < YahtzeeState.N_SCORES; i++) {
            scores_[i] = -1;
        }
    }

    public YahtzeeState(byte[] diceValues, int rolls, int[] scores) {
        diceValues_ = diceValues;
        rolls_ = (byte) rolls;
        scores_ = scores;
    }

    public YahtzeeState copy() {
        return this;
    }

    public int getDiceValue(int index) {
        return diceValues_[index];
    }

    public byte[] getDiceValues() {
        byte[] diceValues = new byte[N_VALUES];
        System.arraycopy(diceValues_, 0, diceValues, 0, N_VALUES);
        return diceValues;
    }

    public int getRolls() {
        return rolls_;
    }

    public int[] getScores() {
        int[] scores = new int[N_SCORES];
        System.arraycopy(scores_, 0, scores, 0, N_SCORES);
        return scores;
    }

    public int getScore(int index) {
        return scores_[index];
    }

    @Override
    public int hashCode() {
        int code = 7 + rolls_;
        for (int i = 0; i < N_VALUES; i++) {
            code = 11 * code + diceValues_[i];
        }
        for (int i = 0; i < N_SCORES; i++) {
            code = 11 * code + scores_[i];
        }
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof YahtzeeState)) {
            return false;
        }
        YahtzeeState state = (YahtzeeState) object;
        for (int i = 0; i < N_VALUES; i++) {
            if (diceValues_[i] != state.getDiceValue(i)) {
                return false;
            }
        }
        if (rolls_ != state.getRolls()) {
            return false;
        }
        for (int i = 0; i < N_SCORES; i++) {
            if (scores_[i] != state.getScore(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        YahtzeeScoreCategory[] scoreCategories = YahtzeeScoreCategory.values();
        for (int i = 0; i < N_SCORES; i++) {
            if (scores_[i] != -1) {
                output.append(scoreCategories[i].name()).append(": ").append(scores_[i]).append("\n");
            }
        }
        output.append("Rolls: ").append(rolls_).append("\n");
        output.append("Dice: [ ");
        for (byte diceValue : diceValues_) {
            output.append(diceValue).append(" ");
        }
        output.append("]\n");
        for (int i = 0; i < N_SCORES; i++) {
            if (scores_[i] == -1) {
                output.append(scoreCategories[i].name()).append(" ");
            }
        }
        return output.toString();
    }
}
