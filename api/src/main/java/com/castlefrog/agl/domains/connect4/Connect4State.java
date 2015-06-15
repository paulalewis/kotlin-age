package com.castlefrog.agl.domains.connect4;

import com.castlefrog.agl.State;

/**
 * State represented by a bitBoard described below:
 *  .  .  .  .  .  .  . Row above top row
 *  5 12 19 26 33 40 47
 *  4 11 18 25 32 39 46
 *  3 10 17 24 31 38 45
 *  2  9 16 23 30 37 44
 *  1  8 15 22 29 36 43
 *  0  7 14 21 28 35 42
 */
public final class Connect4State implements State<Connect4State> {
    public static final int WIDTH = 7;
    public static final int HEIGHT = 6;

    private final long[] bitBoards_;
    private final byte agentTurn_;

    public Connect4State(long[] bitBoards, int agentTurn) {
        bitBoards_ = bitBoards;
        agentTurn_ = (byte) agentTurn;
    }

    public Connect4State copy() {
        return this;
    }

    public long[] getBitBoards() {
        long[] bitBoards = new long[bitBoards_.length];
        System.arraycopy(bitBoards_, 0, bitBoards, 0, bitBoards_.length);
        return bitBoards;
    }

    public int getAgentTurn() {
        return agentTurn_;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 17 + (int) bitBoards_[0];
        hashCode = hashCode * 31 + (int) (bitBoards_[0] >> Integer.SIZE);
        hashCode = hashCode * 13 + (int) bitBoards_[1];
        hashCode = hashCode * 29 + (int) (bitBoards_[1] >> Integer.SIZE);
        return hashCode;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Connect4State)) {
            return false;
        }
        Connect4State state = (Connect4State) object;
        long[] bitBoards = state.getBitBoards();
        return bitBoards[0] == bitBoards_[0] && bitBoards[1] == bitBoards_[1];
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < 2 * WIDTH + 3; i += 1) {
            output.append("-");
        }
        output.append("\n");
        for (int i = HEIGHT - 1; i >= 0; i -= 1) {
            output.append(": ");
            for (int j = i; j < ((HEIGHT + 1) * WIDTH); j += (HEIGHT + 1)) {
                long mask = 1L << j;
                if ((bitBoards_[0] & mask) != 0) {
                    output.append("X");
                } else if ((bitBoards_[1] & mask) != 0) {
                    output.append("O");
                } else {
                    output.append("-");
                }
                output.append(" ");
            }
            output.append(":\n");
        }
        for (int i = 0; i < 2 * WIDTH + 3; i += 1) {
            output.append("-");
        }
        return output.toString();
    }
}
