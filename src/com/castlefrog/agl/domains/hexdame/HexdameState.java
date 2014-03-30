package com.castlefrog.agl.domains.hexdame;

import com.castlefrog.agl.State;

import java.io.Serializable;

public final class HexdameState implements State<HexdameState>, Serializable {
    public static final int BASE = 5;
    public static final int SIZE = 2 * BASE - 1;
    public static final int N_LOCATIONS = 3 * BASE * BASE - 3 * BASE + 1;

    private byte[][] locations_;
    private byte agentTurn_;

    public static final int
        LOCATION_EMPTY = 0,
        LOCATION_AGENT1 = 1,
        LOCATION_AGENT1_KING = 2,
        LOCATION_AGENT2 = 3,
        LOCATION_AGENT2_KING = 4;

    public HexdameState() {
        locations_ = new byte[SIZE][SIZE];
        for (int i = 0; i < 4; i += 1) {
            for (int j = 0; j < 4; j += 1) {
                locations_[i][j] = LOCATION_AGENT1;
                locations_[SIZE - i][SIZE - j] = LOCATION_AGENT2;
            }
        }
        agentTurn_ = 0;
    }

    public HexdameState(byte[][] locations,
                        int agentTurn) {
        locations_ = new byte[SIZE][SIZE];
        for (int i = 0; i < SIZE; i += 1) {
            System.arraycopy(locations, 0, locations_, 0, SIZE);
        }
        agentTurn_ = (byte) agentTurn;
    }

    public HexdameState copy() {
        return new HexdameState(locations_, agentTurn_);
    }

    public byte[][] getLocations() {
        return locations_;
    }

    public byte getLocation(int x, int y) {
        return locations_[x][y];
    }

    public int getAgentTurn() {
        return agentTurn_;
    }

    public void setLocation(int x,
                            int y,
                            int value) {
        locations_[x][y] = (byte) value;
    }

    private int[][] getCorners() {
        return new int[][] {{0, 0},
                            {0, BASE - 1},
                            {BASE - 1, 0},
                            {BASE - 1, SIZE - 1},
                            {SIZE - 1, BASE - 1},
                            {SIZE - 1, SIZE - 1}};
    }

    private int[][][] getSides() {
        int[][][] sides = new int[6][BASE - 2][2];
        for (int i = 0; i < BASE - 2; i += 1) {
            sides[0][i][0] = 0;
            sides[0][i][1] = i + 1;
            sides[1][i][0] = i + 1;
            sides[1][i][1] = 0;
            sides[2][i][0] = i + 1;
            sides[2][i][1] = BASE + i;
            sides[3][i][0] = BASE + i;
            sides[3][i][1] = SIZE - 1;
            sides[4][i][0] = SIZE - 1;
            sides[4][i][1] = BASE + i;
            sides[5][i][0] = BASE + i;
            sides[5][i][1] = i + 1;
        }
        return sides;
    }

    @Override
    public int hashCode() {
        int code = 7;
        code = 31 * code + agentTurn_;
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
        for (int i = 0; i < SIZE; i += 1) {
            for (int j = 0; j < SIZE; j += 1) {
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
            for (int j = 0; j < BASE - i - 1 || j < i - BASE + 1; j += 1) {
                output.append(" ");
            }
            int xMin = 0;
            int xMax = locations_.length;
            if (i >= BASE) {
                xMin = i - BASE + 1;
            } else {
                xMax = BASE + i;
            }
            for (int j = xMin; j < xMax; j += 1) {
                if (locations_[j][i] == LOCATION_AGENT1) {
                    output.append("b ");
                } else if (locations_[j][i] == LOCATION_AGENT1_KING) {
                    output.append("B ");
                } else if (locations_[j][i] == LOCATION_AGENT2) {
                    output.append("w ");
                } else if (locations_[j][i] == LOCATION_AGENT2_KING) {
                    output.append("W ");
                } else {
                    output.append("- ");
                }
            }
            output.append("\n");
        }
        return output.toString();
    }
}
