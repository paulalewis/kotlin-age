package com.castlefrog.agl.domains.havannah;

import java.io.Serializable;

import com.castlefrog.agl.State;

/**
 * A Havannah state consists of a board of hexagon locations that are either
 * empty or have a piece controlled by one of two players.
 */
public final class HavannahState implements State<HavannahState>, Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 0 is empty or not playable
     * 1 is player 1
     * 2 is player 2
     */
    private byte[][] locations_;
    /** id of agent to play next piece */
    private byte agentTurn_;

    public enum Location {
        EMPTY,
        AGENT1,
        AGENT2
    }

    public HavannahState(int size) {
        this(new byte[size][size], 0);
    }

    private HavannahState(byte[][] locations,
                          int agentTurn) {
        locations_ = new byte[locations.length][locations[0].length];
        for (int i = 0; i < locations.length; i += 1) {
            for (int j = 0; j < locations[0].length; j += 1) {
                locations_[i][j] = locations[i][j];
            }
        }
        agentTurn_ = (byte) agentTurn;
    }

    public HavannahState copy() {
        return new HavannahState(locations_, agentTurn_);
    }

    public byte[][] getLocations() {
        byte[][] locations = new byte[locations_.length][locations_.length];
        for (int i = 0; i < locations_.length; i += 1) {
            for (int j = 0; j < locations_.length; j += 1) {
                locations[i][j] = locations_[i][j];
            }
        }
        return locations;
    }

    /**
     * Gets a location on board.
     */
    public byte getLocation(int x, int y) {
        return locations_[x][y];
    }

    public boolean isLocationEmpty(int x, int y) {
        return locations_[x][y] == 0;
    }

    public int getBase() {
        return (locations_.length + 1) / 2;
    }

    public int getSize() {
        return locations_.length;
    }

    public int getNPieces() {
        int nPieces = 0;
        for (int i = 0; i < locations_.length; i += 1) {
            for (int j = 0; j < locations_[0].length; j += 1) {
                if (locations_[i][j] != 0) {
                    nPieces += 1;
                }
            }
        }
        return nPieces;
    }

    public int getNLocations() {
        int base = getBase();
        return 3 * base * base - 3 * base + 1;
    }

    public int getAgentTurn() {
        return agentTurn_;
    }

    public void setLocation(int x,
                            int y,
                            int value) {
        locations_[x][y] = (byte) value;
    }

    public void setLocation(int x,
                            int y,
                            Location value) {
        locations_[x][y] = (byte) value.ordinal();
    }

    public void switchAgentTurn() {
        agentTurn_ = (byte) ((agentTurn_ + 1) % 2);
    }

    @Override
    public int hashCode() {
        int code = 7;
        for (byte[] row : locations_) {
            for (byte location : row) {
                code = 11 * code + location;
            }
        }
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HavannahState)) {
            return false;
        }
        HavannahState state = (HavannahState) object;
        byte[][] locations = state.getLocations();
        for (int i = 0; i < locations_.length; i += 1) {
            for (int j = 0; j < locations_.length; j += 1) {
                if (locations[i][j] != locations_[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = locations_.length - 1; i >= 0; i -= 1) {
            for (int j = 0; j < getBase() - i - 1 || j < i - getBase() + 1; j += 1) {
                output.append(" ");
            }
            int xMin = 0;
            int xMax = locations_.length;
            if (i >= getBase()) {
                xMin = i - getBase() + 1;
            } else {
                xMax = getBase() + i;
            }
            for (int j = xMin; j < xMax; j += 1) {
                if (locations_[j][i] == 1) {
                    output.append("X ");
                } else if (locations_[j][i] == 2) {
                    output.append("O ");
                } else {
                    output.append("- ");
                }
            }
            output.append("\n");
        }
        return output.toString();
    }
}
