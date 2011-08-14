package com.castlefrog.agl.domains.havannah;

import java.util.Vector;

public final class HavannahAction {
    /** list of all possible Havannah actions */
    private static Vector<Vector<HavannahAction>> actions_
        = new Vector<Vector<HavannahAction>>();

    /** x coordinate */
    private final byte x_;
    /** y coordinate */
    private final byte y_;

    private HavannahAction(int x, int y) {
        x_ = (byte) x;
        y_ = (byte) y;
    }

    public static HavannahAction valueOf(int x, int y) {
        if (x >= actions_.size() || y >= actions_.size())
            generateActions(Math.max(x + 1, y + 1));
        return actions_.get(x).get(y);
    }

    /**
     * Contains all possible actions and a few impossible actions.
     * 
     * @return set of possible actions.
     */
    private static void generateActions(int size) {
        actions_.setSize(size);
        for (int i = 0; i < size; i += 1) {
            if (actions_.get(i) == null)
                actions_.set(i, new Vector<HavannahAction>());
            actions_.get(i).setSize(size);
            for (int j = 0; j < size; j += 1)
                if (actions_.get(i).get(j) == null)
                    actions_.get(i).set(j, new HavannahAction(i, j));
        }
    }

    public int getX() {
        return x_;
    }

    public int getY() {
        return y_;
    }

    @Override
    public int hashCode() {
        return 11 * (7 + x_) + y_;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HavannahAction))
            return false;
        HavannahAction action = (HavannahAction) object;
        return x_ == action.getX() && y_ == action.getY();
    }

    @Override
    public String toString() {
        return "(" + x_ + "," + y_ + ")";
    }
}
