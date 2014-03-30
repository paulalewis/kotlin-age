package com.castlefrog.agl.domains.hexdame;

import com.castlefrog.agl.Action;

import java.io.Serializable;
import java.util.Vector;

public final class HexdameAction implements Action, Serializable {
    /** list of all possible Hexdame actions */
    private static Vector<Vector<HexdameAction>> actions_ = new Vector<Vector<HexdameAction>>();

    /** x coordinate */
    private final byte x_;
    /** y coordinate */
    private final byte y_;

    private HexdameAction(int x, int y) {
        x_ = (byte) x;
        y_ = (byte) y;
    }

    public static HexdameAction valueOf(int x, int y) {
        if (x >= actions_.size() || y >= actions_.size()) {
            generateActions(Math.max(x + 1, y + 1));
        }
        return actions_.get(x).get(y);
    }

    public HexdameAction copy() {
        return this;
    }

    /**
     * Contains all possible actions and a few impossible actions.
     * @return set of possible actions.
     */
    private static void generateActions(int size) {
        actions_.setSize(size);
        for (int i = 0; i < size; i += 1) {
            if (actions_.get(i) == null) {
                actions_.set(i, new Vector<HexdameAction>());
            }
            actions_.get(i).setSize(size);
            for (int j = 0; j < size; j += 1) {
                if (actions_.get(i).get(j) == null) {
                    actions_.get(i).set(j, new HexdameAction(i, j));
                }
            }
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
        if (!(object instanceof HexdameAction)) {
            return false;
        }
        HexdameAction action = (HexdameAction) object;
        return x_ == action.getX() && y_ == action.getY();
    }

    @Override
    public String toString() {
        return "(" + ((char) (0x41 + x_)) + y_ + ")";
    }
}
