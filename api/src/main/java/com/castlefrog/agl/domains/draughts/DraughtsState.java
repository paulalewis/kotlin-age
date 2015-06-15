package com.castlefrog.agl.domains.draughts;

import com.castlefrog.agl.State;

public final class DraughtsState implements State<DraughtsState> {
    private byte[][] locations_;
    private byte agentTurn_;

    public static final int
        LOCATION_EMPTY = 0,
        LOCATION_BLACK = 1,
        LOCATION_BLACK_KING = 2,
        LOCATION_WHITE = 4,
        LOCATION_WHITE_KING = 8;

    public static final int
        TURN_BLACK = 0,
        TURN_WHITE = 1;

    public DraughtsState(byte[][] locations,
                         int agentTurn) {
        locations_ = locations;
        agentTurn_ = (byte) agentTurn;
    }

    public DraughtsState copy() {
        return this;
    }

    public byte[][] getLocations() {
        byte[][] locations = new byte[locations_.length][locations_.length];
        for (int i = 0; i < locations_.length; i++) {
            System.arraycopy(locations_[i], 0, locations[i], 0, locations_.length);
        }
        return locations;
    }

    public int getNPieces(int agentTurn) {
        int count = 0;
        for (int i = 0; i < locations_.length; i += 1) {
            for (int j = 0; j < locations_[0].length; j += 1) {
                if (agentTurn == TURN_BLACK && ((locations_[i][j] & 3) != 0)) {
                    count += 1;
                } else if (agentTurn == TURN_WHITE && ((locations_[i][j] & 12) != 0)) {
                    count += 1;
                }
            }
        }
        return count;
    }

    public int getLocation(int x, int y) {
        return locations_[x][y];
    }

    public int getAgentTurn() {
        return agentTurn_;
    }

    public int getSize() {
        return locations_.length;
    }

    @Override
    public int hashCode() {
        int code = 7;
        for (byte[] row: locations_) {
            for (byte location: row) {
                code = 11 * code + location;
            }
        }
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DraughtsState)) {
            return false;
        }
        DraughtsState state = (DraughtsState) object;
        for (int i = 0; i < locations_.length; i++) {
            for (int j = 0; j < locations_.length; j++) {
                if (locations_[i][j] != state.getLocation(i, j)) {
                    return false;
                }
            }
        }
        return agentTurn_ == state.getAgentTurn();
    }

    @Override
    public String toString() {
        final String pieces = " XO";
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < locations_.length; i++) {
            for (int j = 0; j < locations_.length; j++) {
                if (i % 2 == 0) {
                    output.append("-");
                    output.append(pieces.charAt(locations_[i][j]));
                } else {
                    output.append(pieces.charAt(locations_[i][j]));
                    output.append("-");
                }
            }
        }
        return output.toString();
    }
}
