package com.castlefrog.agl.domains.ewn;

public class EwnState {
    private static final int SIZE = 5;

    /**
     * 0: empty x: value of agent 1 location -x: value of agent 2 location
     */
    private byte[][] locations_;

    private byte dieRoll_;

    private byte agentTurn_;

    public EwnState(byte[][] locations, int dieRoll, int agentTurn) {
        locations_ = locations;
        dieRoll_ = (byte) dieRoll;
        agentTurn_ = (byte) agentTurn;
    }

    public byte getLocation(int x, int y) {
        return locations_[x][y];
    }

    public byte[][] getLocations() {
        byte[][] locations = new byte[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                locations[i][j] = locations_[i][j];
        return locations;
    }

    public int getDieRoll() {
        return dieRoll_;
    }

    public static int getSize() {
        return SIZE;
    }

    public int getAgentTurn() {
        return agentTurn_;
    }

    @Override
    public int hashCode() {
        int code = 7 + dieRoll_;
        for (byte[] locations : locations_)
            for (byte location : locations)
                code = 11 * code + location;
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EwnState))
            return false;
        EwnState state = (EwnState) object;
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (locations_[i][j] == state.getLocation(i, j))
                    return false;
        return dieRoll_ == state.getDieRoll();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("(" + dieRoll_ + ")\n");
        for (int i = 0; i < 3 * SIZE + 1; i++)
            output.append("-");
        output.append("\n");
        for (int i = SIZE - 1; i >= 0; i--) {
            output.append(":");
            for (int j = 0; j < SIZE; j++) {
                if (j != 0)
                    output.append("|");
                int value = locations_[j][i];
                if (value > 0)
                    output.append("X" + value);
                else if (value < 0)
                    output.append("O" + -1 * value);
                else
                    output.append("  ");
            }
            output.append(":\n");
        }
        for (int i = 0; i < 3 * SIZE + 1; i++)
            output.append("-");
        return output.toString();
    }
}
