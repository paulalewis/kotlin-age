package com.castlefrog.agl.domains.hex;

import java.util.Vector;

/**
 * A Hex action is an immutable object that
 * contains the x and y coordinates of a move location.
 */
public final class HexAction {
    /** list of all possible Hex actions */
    private static Vector<Vector<HexAction>> actions_
        = new Vector<Vector<HexAction>>();

    /** x coordinate */
    private final byte x_;
    /** y coordinate */
    private final byte y_;

    /**
     * Constructor used by static factory
     * method to generate actions.
     */
    private HexAction(int x,
                      int y) {
        x_ = (byte) x;
        y_ = (byte) y;
    }

    /**
     * Used to retrieve a pre generated action.
     * @param x x-coord
     * @param y y-coord
     * @return action corrisponding to x and y coord
     */
    public static HexAction valueOf(int x, int y) {
        if (x >= actions_.size() || y >= actions_.size())
            generateActions(Math.max(x + 1, y + 1));
        return actions_.get(x).get(y);
    }

    /**
     * Generates set of all possible actions for a given
     * board size.
     *
     * This method generates new actions if they don't
     * already exist and copies already existing actions
     * over to the larger array.
     *
     * @return
     *      list of all possible actions
     */
    private static void generateActions(int size) {
        actions_.setSize(size);
        for (int i = 0; i < size; i += 1) {
            if (actions_.get(i) == null)
                actions_.set(i, new Vector<HexAction>());
            actions_.get(i).setSize(size);
            for (int j = 0; j < size; j += 1)
                if (actions_.get(i).get(j) == null)
                    actions_.get(i).set(j, new HexAction(i, j));
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
        return x_ + 17 * y_;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HexAction))
            return false;
        HexAction action = (HexAction) object;
        return x_ == action.getX() && y_ == action.getY();
    }

    @Override
    public String toString() {
        return "(" + x_ + "," + y_ + ")";
    }
}
