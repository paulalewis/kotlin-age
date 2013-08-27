package com.castlefrog.agl.domains.draughts;

import com.castlefrog.agl.State;

public final class DraughtsState implements State {
    private byte[][] locations_;
    private byte agentTurn_;

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
            for (int j = 0; j < locations_.length; j++) {
                locations[i][j] = locations_[i][j];
            }
        }
        return locations;
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
        final String PIECES = " XO";
        StringBuffer output = new StringBuffer();
        for (int i = 0; i < locations_.length; i++) {
            for (int j = 0; j < locations_.length; j++) {
                if (i % 2 == 0) {
                    output.append("-");
                    output.append(PIECES.charAt(locations_[i][j]));
                } else {
                    output.append(PIECES.charAt(locations_[i][j]));
                    output.append("-");
                }
            }
        }
        return output.toString();
    }
}
