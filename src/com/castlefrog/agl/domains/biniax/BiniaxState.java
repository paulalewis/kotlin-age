package com.castlefrog.agl.domains.biniax;

import com.castlefrog.agl.State;

/**
 * Defines a Biniax state.
 * Location value representation:
 * -1: impassable
 *  0: empty
 *  x: single element 0 < x <= MAX_ELEMENTS
 *  xy: element pair 0 < x <= MAX_ELEMENTS and 0 < y <= MAX_ELEMENTS and x < y
 */
public final class BiniaxState implements State<BiniaxState> {
    private static final int WIDTH = 5;
    private static final int HEIGHT = 7;
    private static final int MAX_ELEMENTS = 9;
    private static final int BUFFER = 3;
    public static final int ELEMENT_LIMIT = MAX_ELEMENTS + 1;
    public static final int MAX_FREE_MOVES = 2;

    private final byte[][] locations_;

    private final byte freeMoves_;
    /** Total number of actions taken. */
    private final int nTurns_;

    /**
     * The initial state for biniax starts with the player at the bottom of
     * the grid and some random elements above the player.
     */
    public BiniaxState() {
        locations_ = new byte[BiniaxState.getWidth()][BiniaxState.getHeight()];
        for (int i = 0; i < BiniaxState.getHeight(); i++) {
            int emptyLocation = (int) (Math.random() * BiniaxState.getWidth());
            for (int j = 0; j < BiniaxState.getWidth(); j++) {
                if (j != emptyLocation && i < BiniaxState.getHeight() - BUFFER) {
                    locations_[j][i] = (byte) BiniaxSimulator.generateRandomElementPair(0);
                    if (i == BiniaxState.getHeight() - BUFFER - 1) {
                        locations_[j][i] = (byte) (locations_[j][i] % ELEMENT_LIMIT + ELEMENT_LIMIT);
                    }
                }
            }
        }
        locations_[BiniaxState.getWidth() / 2][BiniaxState.getHeight() - 1] = 1;
        freeMoves_ = MAX_FREE_MOVES;
        nTurns_ = 0;
    }

    public BiniaxState(byte[][] locations, byte freeMoves, int nTurns) {
        locations_ = locations;
        freeMoves_ = freeMoves;
        nTurns_ = nTurns;
    }

    public BiniaxState copy() {
        return this;
    }

    public byte[][] getLocations() {
        byte[][] locations = new byte[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            System.arraycopy(locations_[i], 0, locations[i], 0, HEIGHT);
        }
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

    //public int getNElementTypes() {
    //    return 0;
    //}

    public static int getWidth() {
        return WIDTH;
    }

    public static int getHeight() {
        return HEIGHT;
    }

    public static int getMaxElements() {
        return MAX_ELEMENTS;
    }

    @Override
    public int hashCode() {
        int code = 7 + freeMoves_;
        for (byte[] locations : locations_) {
            for (byte location : locations) {
                code = 11 * code + location;
            }
        }
        code = 11 * code + nTurns_;
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BiniaxState)) {
            return false;
        }
        BiniaxState state = (BiniaxState) object;
        if (freeMoves_ != state.getFreeMoves()) {
            return false;
        }
        for (int i = 0; i < WIDTH; i += 1) {
            for (int j = 0; j < HEIGHT; j += 1) {
                if (locations_[i][j] != state.getLocation(i, j)) {
                    return false;
                }
            }
        }
        return nTurns_ == state.getNTurns();
    }

    @Override
    public String toString() {
        final String elements = " ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder output = new StringBuilder();
        output.append("(Turns: ").append(nTurns_).append(")\n");
        output.append("(Free Moves:").append(freeMoves_).append(")\n");
        for (int i = 0; i < WIDTH; i += 1) {
            output.append("----");
        }
        output.append("-\n");
        for (int j = 0; j < HEIGHT; j += 1) {
            output.append(":");
            for (int i = 0; i < WIDTH; i += 1) {
                if (i != 0) {
                    output.append(" ");
                }
                if (locations_[i][j] == 0) {
                    output.append("   ");
                } else if (locations_[i][j] == -1) {
                    output.append("<X>");
                } else if (locations_[i][j] > 0 && locations_[i][j] < (MAX_ELEMENTS + 1)) {
                    output.append("[");
                    output.append(elements.charAt(locations_[i][j]));
                    output.append("]");
                } else {
                    output.append(elements.charAt(locations_[i][j] / (MAX_ELEMENTS + 1)));
                    output.append("-");
                    output.append(elements.charAt(locations_[i][j] % (MAX_ELEMENTS + 1)));
                }
            }
            output.append(":\n");
        }
        output.append("-");
        for (int i = 0; i < WIDTH; i++) {
            output.append("----");
        }
        return output.toString();
    }
}
