package com.castlefrog.agl.domains.havannah;

import java.io.Serializable;

import com.castlefrog.agl.State;

/**
 * A Havannah state consists of a board of hexagon locations that are either
 * empty or have a piece controlled by one of two players.
 */
public final class HavannahState implements State<HavannahState>, Serializable {
    private static final int MIN_BASE = 2;

    private byte base_;
    private byte[][] locations_;
    private byte agentTurn_;

    public static final int
        LOCATION_EMPTY = 0,
        LOCATION_BLACK = 1,
        LOCATION_WHITE = 2;

    public HavannahState(int base,
                         byte[][] locations,
                         int agentTurn) {
        if (base < MIN_BASE) {
            throw new IllegalArgumentException("Invalid board size: " + base);
        }
        base_ = (byte) base;
        locations_ = new byte[locations.length][locations[0].length];
        for (int i = 0; i < locations.length; i += 1) {
            System.arraycopy(locations[i], 0, locations_[i], 0, locations[0].length);
        }
        agentTurn_ = (byte) agentTurn;
    }

    public HavannahState copy() {
        return new HavannahState(base_, locations_, agentTurn_);
    }

    public byte[][] getLocations() {
        byte[][] locations = new byte[locations_.length][locations_.length];
        for (int i = 0; i < locations_.length; i += 1) {
            System.arraycopy(locations_[i], 0, locations[i], 0, locations_.length);
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
        return locations_[x][y] == LOCATION_EMPTY;
    }

    public int getBase() {
        return base_;
    }

    public int getSize() {
        return locations_.length;
    }

    public int getNLocations() {
        return 3 * base_ * base_ - 3 * base_ + 1;
    }

    public int[][] getCorners() {
        return new int[][] {{0, 0},
                            {0, base_ - 1},
                            {base_ - 1, 0},
                            {base_ - 1, getSize() - 1},
                            {getSize() - 1, base_ - 1},
                            {getSize() - 1, getSize() - 1}};
    }

    public int[][][] getSides() {
        int[][][] sides = new int[6][base_ - 2][2];
        for (int i = 0; i < base_ - 2; i += 1) {
            sides[0][i][0] = 0;
            sides[0][i][1] = i + 1;
            sides[1][i][0] = i + 1;
            sides[1][i][1] = 0;
            sides[2][i][0] = i + 1;
            sides[2][i][1] = base_ + i;
            sides[3][i][0] = base_ + i;
            sides[3][i][1] = getSize() - 1;
            sides[4][i][0] = getSize() - 1;
            sides[4][i][1] = base_ + i;
            sides[5][i][0] = base_ + i;
            sides[5][i][1] = i + 1;
        }
        return sides;
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

    public int getAgentTurn() {
        return agentTurn_;
    }

    public void setLocation(int x,
                            int y,
                            int value) {
        locations_[x][y] = (byte) value;
    }

    public void setAgentTurn(int agentTurn) {
        agentTurn_ = (byte) agentTurn;
    }

    @Override
    public int hashCode() {
        int code = 7;
        code = code * 13 + base_;
        code = code * 23 + agentTurn_;
        for (byte[] row : locations_) {
            for (byte location : row) {
                code = 31 * code + location;
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
        return agentTurn_ == state.getAgentTurn();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = locations_.length - 1; i >= 0; i -= 1) {
            for (int j = 0; j < base_ - i - 1 || j < i - base_ + 1; j += 1) {
                output.append(" ");
            }
            int xMin = 0;
            int xMax = locations_.length;
            if (i >= base_) {
                xMin = i - base_ + 1;
            } else {
                xMax = base_ + i;
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
