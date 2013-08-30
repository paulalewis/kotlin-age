package com.castlefrog.agl.domains.hexdame;

import java.io.Serializable;

import com.castlefrog.agl.State;

/**
 * A Hexdame state consists of a board of hexagon locations that are either
 * empty or have a piece controlled by one of two players.
 */
public final class HexdameState implements State<HexdameState>, Serializable {
    private static final long serialVersionUID = 1L;

    private byte[][] locations_;
    /** id of agent to play next piece */
    private byte agentTurn_;
    /** number of pieces on board */
    private int nPieces_;

    public enum Location {
        EMPTY,
        AGENT1,
        AGENT1_KING,
        AGENT2,
        AGENT2_KING
    }

    public HexdameState(byte[][] locations,
                         int agentTurn) {
        locations_ = new byte[locations.length][locations[0].length];
        for (int i = 0; i < locations.length; i += 1) {
            for (int j = 0; j < locations[0].length; j += 1) {
                locations_[i][j] = locations[i][j];
                if (locations_[i][j] != 0) {
                    nPieces_ += 1;
                }
            }
        }
        agentTurn_ = (byte) agentTurn;
    }

    private HexdameState(byte[][] locations,
                          int agentTurn,
                          int nPieces) {
        locations_ = new byte[locations.length][locations[0].length];
        for (int i = 0; i < locations.length; i += 1) {
            for (int j = 0; j < locations[0].length; j += 1) {
                locations_[i][j] = locations[i][j];
            }
        }
        agentTurn_ = (byte) agentTurn;
        nPieces_ = nPieces;
    }

    public HexdameState copy() {
        return new HexdameState(locations_, agentTurn_, nPieces_);
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
        return nPieces_;
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
        if (locations_[x][y] == 0 && value != 0) {
            nPieces_ += 1;
        } else if (locations_[x][y] != 0 && value == 0) {
            nPieces_ -= 1;
        }
        locations_[x][y] = (byte) value;
    }

    public void setLocation(int x,
                            int y,
                            Location value) {
        if (locations_[x][y] == 0 && value != Location.EMPTY) {
            nPieces_ += 1;
        } else if (locations_[x][y] != 0 && value == Location.EMPTY) {
            nPieces_ -= 1;
        }
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
        if (!(object instanceof HexdameState)) {
            return false;
        }
        HexdameState state = (HexdameState) object;
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
