package com.castlefrog.agl.domains.biniax;

/**
 * Defines a Biniax state.
 * Location value representation:
 * -1: impassable
 *  0: empty
 *  x: single element 0 < x <= MAX_ELEMENTS
 *  xy: element pair 0 < x <= MAX_ELEMENTS and 0 < y <= MAX_ELEMENTS and x < y
 */
public final class BiniaxState {
    private static final int WIDTH = 5;
    private static final int HEIGHT = 7;
    private static final int MAX_ELEMENTS = 9;

    private byte[][] locations_;

    private byte freeMoves_;
    /** Total number of actions taken. */
    private int nTurns_;

    public BiniaxState(byte[][] locations, byte freeMoves, int nTurns) {
        locations_ = locations;
        freeMoves_ = freeMoves;
        nTurns_ = nTurns;
    }

    public byte[][] getLocations() {
        byte[][] locations = new byte[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++)
            for (int j = 0; j < HEIGHT; j++)
                locations[i][j] = locations_[i][j];
        return locations;
    }

    public byte getLocation(int x, int y) {
        return locations_[x][y];
    }

    public byte getFreeMoves() {
        return freeMoves_;
    }

    public int getNTurns() {
        return nTurns_;
    }

    public static int getWidth() {
        return WIDTH;
    }

    public static int getHeight() {
        return HEIGHT;
    }

    public static int getMaxElements() {
        return MAX_ELEMENTS;
    }

    public int getAgentTurn() {
        return 0;
    }

    @Override
    public int hashCode() {
        int code = 7 + freeMoves_;
        for (byte[] locations : locations_)
            for (byte location : locations)
                code = 11 * code + location;
        code = 11 * code + nTurns_;
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BiniaxState))
            return false;
        BiniaxState state = (BiniaxState) object;
        if (freeMoves_ != state.getFreeMoves())
            return false;
        for (int i = 0; i < WIDTH; i += 1)
            for (int j = 0; j < HEIGHT; j += 1)
                if (locations_[i][j] != state.getLocation(i, j))
                    return false;
        return nTurns_ == state.getNTurns();
    }

    @Override
    public String toString() {
        final String ELEMENTS = " ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder output = new StringBuilder();
        output.append("(Turns: " + nTurns_ + ")\n");
        output.append("(Free Moves:" + freeMoves_ + ")\n");
        for (int i = 0; i < WIDTH; i += 1)
            output.append("----");
        output.append("-\n");
        for (int j = 0; j < HEIGHT; j += 1) {
            output.append(":");
            for (int i = 0; i < WIDTH; i += 1) {
                if (i != 0)
                    output.append(" ");
                if (locations_[i][j] == 0)
                    output.append("   ");
                else if (locations_[i][j] == -1)
                    output.append("<X>");
                else if (locations_[i][j] > 0
                        && locations_[i][j] < (MAX_ELEMENTS + 1)) {
                    output.append("[");
                    output.append(ELEMENTS.charAt(locations_[i][j]));
                    output.append("]");
                } else {
                    output.append(ELEMENTS.charAt(locations_[i][j]
                            / (MAX_ELEMENTS + 1)));
                    output.append("-");
                    output.append(ELEMENTS.charAt(locations_[i][j]
                            % (MAX_ELEMENTS + 1)));
                }
            }
            output.append(":\n");
        }
        output.append("-");
        for (int i = 0; i < WIDTH; i++)
            output.append("----");
        return output.toString();
    }
}
