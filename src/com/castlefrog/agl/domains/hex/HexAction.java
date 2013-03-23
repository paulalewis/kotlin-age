package com.castlefrog.agl.domains.hex;

import java.io.Serializable;

/**
 * Immutable object that represents a hex action.
 */
public final class HexAction implements Serializable {
	private static final long serialVersionUID = 1L;

	private static HexAction[][] actions_ = generateActions();

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
        return actions_[x][y];
    }
    
    private static HexAction[][] generateActions() {
        int size = HexSimulator.MAX_BOARD_SIZE;
        HexAction[][] actions = new HexAction[size][size];
        for (int i = 0; i < size; i += 1)
            for (int j = 0; j < size; j += 1)
                actions[i][j] = new HexAction(i,j);
        return actions;
    }

    public int getX() {
        return x_;
    }

    public int getY() {
        return y_;
    }

    @Override
    public int hashCode() {
        return x_ + HexSimulator.MAX_BOARD_SIZE * y_;
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
        return "(" + ((char)(0x41 + x_)) + y_ + ")";
    }
}
