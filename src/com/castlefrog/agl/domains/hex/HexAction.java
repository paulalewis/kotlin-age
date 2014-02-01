package com.castlefrog.agl.domains.hex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.castlefrog.agl.Action;

/**
 * Immutable object that represents a hex action.
 */
public final class HexAction implements Action, Serializable {
    private static final long serialVersionUID = 1L;

    private static List<List<HexAction>> actions_ = new ArrayList<List<HexAction>>();

    private final byte x_;
    private final byte y_;

    private HexAction(int x,
                      int y) {
        x_ = (byte) x;
        y_ = (byte) y;
    }

    /**
     * Returns a hex action.
     * @param x x-coord
     * @param y y-coord
     * @return
     *      action corrisponding to x and y coord
     */
    public static HexAction valueOf(int x, int y) {
        if (x >= actions_.size() || y >= actions_.get(0).size()) {
            generateActions(Math.max(x, y) + 1);
        }
        return actions_.get(x).get(y);
    }

    private static void generateActions(int size) {
        for (int i = 0; i < size; i += 1) {
            if (i >= actions_.size()) {
                actions_.add(new ArrayList<HexAction>());
            }
            for (int j = actions_.get(i).size(); j < size; j += 1) {
                actions_.get(i).add(new HexAction(i, j));
            }
        }
    }

    public HexAction copy() {
        return this;
    }

    public int getX() {
        return x_;
    }

    public int getY() {
        return y_;
    }

    @Override
    public int hashCode() {
        return x_ + 31 * y_;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HexAction)) {
            return false;
        }
        HexAction action = (HexAction) object;
        return x_ == action.getX() && y_ == action.getY();
    }

    @Override
    public String toString() {
        return "(" + ((char) (0x41 + x_)) + y_ + ")";
    }
}
