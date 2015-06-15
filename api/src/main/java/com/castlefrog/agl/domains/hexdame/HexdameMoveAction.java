package com.castlefrog.agl.domains.hexdame;

public class HexdameMoveAction extends HexdameAction {
    private final Location move_;

    public HexdameMoveAction(Location initial, Location move) {
        super(initial);
        move_ = move.copy();
    }

    public HexdameMoveAction copy() {
        return this;
    }

    public Location getMove() {
        return move_;
    }

    @Override
    public int hashCode() {
        int code = 7;
        code = code * 11 + initial_.x;
        code = code * 17 + initial_.y;
        code = code * 23 + move_.x;
        code = code * 31 + move_.y;
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HexdameMoveAction)) {
            return false;
        }
        HexdameMoveAction action = (HexdameMoveAction) object;
        return initial_.x == action.getInitial().x && initial_.y == action.getInitial().y &&
               move_.x == action.getMove().x && move_.y == action.getMove().y;
    }

    @Override
    public String toString() {
        return "" + ((char)(0x41 + initial_.x)) + initial_.y + "-" + ((char)(0x41 + move_.x)) + move_.y;
    }
}
