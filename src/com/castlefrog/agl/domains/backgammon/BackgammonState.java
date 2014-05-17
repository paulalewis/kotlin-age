package com.castlefrog.agl.domains.backgammon;

import com.castlefrog.agl.State;

/**
 * Represents a backgammon state as an array of byte locations. Each location is
 * 0 if no pieces are at that location and positive if player 1 has pieces there
 * and negative for the number of pieces player 2 has there.
 */
public final class BackgammonState implements State<BackgammonState> {
    public static final int N_DICE = 2;
    public static final int N_DIE_FACES = 6;
    public static final int N_LOCATIONS = 26;

    private byte[] locations_;
    private byte[] dice_;
    private int agentTurn_;

    public static final int
        TURN_BLACK = 0,
        TURN_WHITE = 1;

    public BackgammonState() {
        locations_ = new byte[] {0, 2, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0, 5, -5, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, -2, 0};
        dice_ = new byte[BackgammonState.N_DICE];

        do {
            dice_[0] = (byte) (Math.random() * BackgammonState.N_DIE_FACES + 1);
            dice_[1] = (byte) (Math.random() * BackgammonState.N_DIE_FACES + 1);
        } while (dice_[0] == dice_[1]);

        if (dice_[0] > dice_[1]) {
            dice_[1] = (byte) (Math.random() * BackgammonState.N_DIE_FACES + 1);
            agentTurn_ = TURN_BLACK;
        } else {
            dice_[0] = (byte) (Math.random() * BackgammonState.N_DIE_FACES + 1);
            agentTurn_ = TURN_WHITE;
        }
    }

    public BackgammonState(byte[] locations, byte[] dice, int agentTurn) {
        locations_ = locations;
        dice_ = dice;
        agentTurn_ = agentTurn;
    }

    public BackgammonState copy() {
        return this;
    }

    public byte[] getLocations() {
        byte[] locations = new byte[N_LOCATIONS];
        System.arraycopy(locations_, 0, locations, 0, N_LOCATIONS);
        return locations;
    }

    public byte getLocation(int index) {
        return locations_[index];
    }

    public byte[] getDice() {
        byte[] dice = new byte[N_DICE];
        System.arraycopy(dice_, 0, dice, 0, N_DICE);
        return dice;
    }

    public byte getDie(int number) {
        return dice_[number];
    }

    public int getAgentTurn() {
        return agentTurn_;
    }

    @Override
    public int hashCode() {
        int code = 11 * (7 + dice_[0]) + dice_[1];
        for (int i = 0; i < N_LOCATIONS; i += 1) {
            code = 11 * code + locations_[i];
        }
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BackgammonState)) {
            return false;
        }
        BackgammonState state = (BackgammonState) object;
        for (int i = 0; i < N_LOCATIONS; i += 1) {
            if (locations_[i] != state.getLocation(i)) {
                return false;
            }
        }
        return dice_[0] == state.getDie(0) && dice_[1] == state.getDie(1);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("[").append(dice_[0]).append("][").append(dice_[1]).append("]\n");
        for (int i = 12; i > 6; i--) {
            if (locations_[i] >= 0) {
                output.append(" ");
            }
            output.append(locations_[i]);
        }
        output.append("|");
        for (int i = 6; i > 0; i--) {
            if (locations_[i] >= 0) {
                output.append(" ");
            }
            output.append(locations_[i]);
        }
        output.append(" [").append(locations_[0]).append("]\n");
        output.append("------------|------------\n");
        for (int i = 13; i < 19; i++) {
            if (locations_[i] >= 0) {
                output.append(" ");
            }
            output.append(locations_[i]);
        }
        output.append("|");
        for (int i = 19; i < 25; i += 1) {
            if (locations_[i] >= 0) {
                output.append(" ");
            }
            output.append(locations_[i]);
        }
        output.append(" [").append(locations_[25]).append("]");
        return output.toString();
    }
}
