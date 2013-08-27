package com.castlefrog.agl.domains.yahtzee;

import java.util.ArrayList;
import java.util.List;

public final class YahtzeeSelectAction implements YahtzeeAction {
    /** Holds list of all possible select actions. */
    private static List<YahtzeeSelectAction> selectActions_ = generateSelectActions();

    /** Each select action selects a particular score category. */
    private YahtzeeScoreCategory scoreCategory_;

    private YahtzeeSelectAction(YahtzeeScoreCategory scoreCategory) {
        scoreCategory_ = scoreCategory;
    }

    public static YahtzeeAction valueOf(int scoreCategory) {
        return selectActions_.get(scoreCategory);
    }

    public static YahtzeeAction valueOf(YahtzeeScoreCategory scoreCategory) {
        return selectActions_.get(scoreCategory.ordinal());
    }

    public YahtzeeSelectAction copy() {
        return this;
    }

    private static List<YahtzeeSelectAction> generateSelectActions() {
        List<YahtzeeSelectAction> selectActions = new ArrayList<YahtzeeSelectAction>();
        for (YahtzeeScoreCategory scoreCategory : YahtzeeScoreCategory.values()) {
            selectActions.add(new YahtzeeSelectAction(scoreCategory));
        }
        return selectActions;
    }

    public YahtzeeScoreCategory getScoreCategory() {
        return scoreCategory_;
    }

    @Override
    public int hashCode() {
        return scoreCategory_.ordinal();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof YahtzeeSelectAction)) {
            return false;
        }
        YahtzeeSelectAction action = (YahtzeeSelectAction) object;
        return scoreCategory_ == action.getScoreCategory();
    }

    @Override
    public String toString() {
        return scoreCategory_.toString();
    }
}
