package com.castlefrog.agl.domains.backgammon;

import java.util.ArrayList;
import java.util.List;

import com.castlefrog.agl.Action;

public final class BackgammonAction implements Action {
    /** List of individual moves form an action. */
    private List<BackgammonMove> moves_;

    public BackgammonAction(List<BackgammonMove> moves) {
        moves_ = new ArrayList<BackgammonMove>();
        for (BackgammonMove move : moves) {
            moves_.add(move);
        }
    }

    public BackgammonMove getMove(int index) {
        return moves_.get(index);
    }

    public BackgammonAction copy() {
        return this;
    }

    public int size() {
        return moves_.size();
    }

    @Override
    public int hashCode() {
        int code = 7;
        for (BackgammonMove move : moves_) {
            code = 11 * code + move.hashCode();
        }
        return code;
    }

    /**
     * Two actions are equal if they contain the same set of moves. Thus order
     * of moves does not matter.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BackgammonAction)) {
            return false;
        }
        BackgammonAction action = (BackgammonAction) object;
        if (size() != action.size()) {
            return false;
        }
        boolean[] used = new boolean[size()];
        for (int i = 0; i < size(); i++) {
            boolean found = false;
            for (int j = 0; j < size() && !found; j++) {
                if (!used[j] && getMove(i).equals(action.getMove(j))) {
                    used[j] = true;
                    found = true;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("[ ");
        for (int i = 0; i < size(); i++) {
            output.append(getMove(i) + " ");
        }
        output.append("]");
        return output.toString();
    }
}
