package com.castlefrog.agl.domains.hexdame;

import com.castlefrog.agl.Action;

import java.io.Serializable;

public abstract class HexdameAction implements Action, Serializable {
    public static class Location {
        public final byte x;
        public final byte y;

        public Location(int x, int y) {
            this.x = (byte) x;
            this.y = (byte) y;
        }

        public Location copy() {
            return new Location(x, y);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Location)) {
                return false;
            }
            Location other = (Location) object;
            return x == other.x && y == other.y;
        }
    }

    protected final Location initial_;

    protected HexdameAction(Location initial) {
        initial_ = initial.copy();
    }

    public Location getInitial() {
        return initial_;
    }
}
