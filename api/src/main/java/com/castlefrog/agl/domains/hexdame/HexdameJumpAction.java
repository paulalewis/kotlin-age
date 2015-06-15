package com.castlefrog.agl.domains.hexdame;

import java.util.ArrayList;
import java.util.List;

public final class HexdameJumpAction extends HexdameMoveAction {
    private final List<Location> jumps_ = new ArrayList<>();

    public HexdameJumpAction(Location initial,
                             Location move,
                             List<Location> jumps) {
        super(initial, move);
        for (Location jump : jumps) {
            jumps_.add(jump.copy());
        }
    }

    public HexdameJumpAction copy() {
        return this;
    }

    public int getNJumps() {
        return jumps_.size();
    }

    public Location getJump(int index) {
        return jumps_.get(index);
    }

    @Override
    public int hashCode() {
        int code = 7;
        code = code * 11 + initial_.x;
        code = code * 17 + initial_.y;
        for (Location jump : jumps_) {
            code = code * 23 + jump.x;
            code = code * 31 + jump.y;
        }
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HexdameJumpAction)) {
            return false;
        }
        HexdameJumpAction action = (HexdameJumpAction) object;
        if (getNJumps() != action.getNJumps()) {
            return false;
        }
        for (int i = 0; i < getNJumps(); i += 1) {
            if (!action.getJump(i).equals(jumps_.get(i))) {
                return false;
            }
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append((char) (0x41 + initial_.x)).append(initial_.y);
        for (Location jump : jumps_) {
            out.append("-").append((char) (0x41 + jump.x)).append(jump.y);
        }
        return out.toString();
    }
}
