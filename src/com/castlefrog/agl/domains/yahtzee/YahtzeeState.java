package com.castlefrog.agl.domains.yahtzee;

import com.castlefrog.agl.State;

public final class YahtzeeState implements State<YahtzeeState> {
    public static final int N_DICE = 5;
    public static final int N_VALUES = 6;
    public static final int N_SCORES = YahtzeeScoreCategory.values().length;

    private byte[] diceValues_;
    private byte rolls_;
    private int[] scores_;

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
        for (int i = 0; i < N_VALUES; i++) {
            diceValues[i] = diceValues_[i];
        }
        return diceValues;
    }

    public int getRolls() {
        return rolls_;
    }

    public int[] getScores() {
        int[] scores = new int[N_SCORES];
        for (int i = 0; i < N_SCORES; i++) {
            scores[i] = scores_[i];
        }
        return scores;
    }

    public int getScore(int index) {
        return scores_[index];
    }

    public int getAgentTurn() {
        return 0;
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
                output.append(scoreCategories[i].name() + ": " + scores_[i] + "\n");
            }
        }
        output.append("Rolls: " + rolls_ + "\n");
        output.append("Dice: [ ");
        for (int i = 0; i < diceValues_.length; i++) {
            output.append(diceValues_[i] + " ");
        }
        output.append("]\n");
        for (int i = 0; i < N_SCORES; i++) {
            if (scores_[i] == -1) {
                output.append(scoreCategories[i].name() + " ");
            }
        }
        return output.toString();
    }
}
