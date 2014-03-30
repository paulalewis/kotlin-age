package com.castlefrog.agl.domains.connect4;

import com.castlefrog.agl.Adversarial2AgentSimulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.TurnType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public final class Connect4Simulator extends Adversarial2AgentSimulator<Connect4State, Connect4Action> {
    private static final long ALL_LOCATIONS = (1L << ((Connect4State.HEIGHT + 1) * Connect4State.WIDTH)) - 1;
    private static final long FIRST_COLUMN = (1L << Connect4State.HEIGHT + 1) - 1;
    private static final long BOTTOM_ROW = ALL_LOCATIONS / FIRST_COLUMN;
    private static final long ABOVE_TOP_ROW = BOTTOM_ROW << Connect4State.HEIGHT;

    private final int[] columnHeights_;
    private final TurnType turnType_;

    private Connect4Simulator(Connect4State state,
                              TurnType turnType) {
        legalActions_ = new ArrayList<>();
        legalActions_.add(new ArrayList<Connect4Action>());
        legalActions_.add(new ArrayList<Connect4Action>());
        columnHeights_ = new int[Connect4State.WIDTH];
        turnType_ = turnType;
        setState(state);
    }

    private Connect4Simulator(Connect4Simulator simulator,
                              int[] height) {
        super(simulator);
        turnType_ = simulator.getTurnType();
        columnHeights_ = new int[height.length];
        System.arraycopy(height, 0, columnHeights_, 0, height.length);
    }

    public Connect4Simulator copy() {
        return new Connect4Simulator(this, columnHeights_);
    }

    public static Connect4Simulator create(TurnType turnType) {
        return new Connect4Simulator(getInitialState(turnType), turnType);
    }

    public static Connect4Simulator create(Connect4State state,
                                           TurnType turnType) {
        return new Connect4Simulator(state, turnType);
    }

    public void setState(Connect4State state) {
        state_ = state;
        long[] bitBoards = state_.getBitBoards();
        computeRewards(bitBoards);
        computeLegalActions(bitBoards);
    }

    private void computeHeights(long[] bitBoards) {
        long bitBoard = bitBoards[0] | bitBoards[1];
        for (int i = 0; i < Connect4State.WIDTH; i += 1) {
            columnHeights_[i] = (Connect4State.HEIGHT + 1) * i;
            while ((bitBoard & (1L << columnHeights_[i])) != 0) {
                columnHeights_[i] += 1;
            }
        }
    }

    public void stateTransition(List<Connect4Action> actions) {
        Connect4Action action = actions.get(state_.getAgentTurn());
        if (!legalActions_.get(state_.getAgentTurn()).contains(action)) {
            throw new IllegalActionException(action, state_);
        }
        long[] bitBoards = state_.getBitBoards();
        bitBoards[state_.getAgentTurn()] ^= (1L << (columnHeights_[action.getLocation()]++));
        state_ = new Connect4State(bitBoards, getNextAgentTurn(state_.getAgentTurn()));
        computeRewards(bitBoards);
        computeLegalActions(action);
    }

    private void computeLegalActions(Connect4Action action) {
        if (legalActions_.get(state_.getAgentTurn()).isEmpty()) {
            List<Connect4Action> temp = legalActions_.get(0);
            legalActions_.set(0, legalActions_.get(1));
            legalActions_.set(1, temp);
        }
        if (rewards_ == REWARDS_NEUTRAL) {
            if (((1L << columnHeights_[action.getLocation()]) & ABOVE_TOP_ROW) != 0) {
                legalActions_.get(state_.getAgentTurn()).remove(action);
            }
        } else {
            clearLegalActions();
        }
    }

    private void computeLegalActions(long[] bitBoards) {
        clearLegalActions();
        computeHeights(bitBoards);
        if (rewards_ == REWARDS_NEUTRAL) {
            for (int i = 0; i < Connect4State.WIDTH; i += 1) {
                if (((1L << columnHeights_[i]) & ABOVE_TOP_ROW) == 0) {
                    legalActions_.get(state_.getAgentTurn()).add(Connect4Action.valueOf(i));
                }
            }
        }
    }

    private void computeRewards(long[] bitBoards) {
        int height = Connect4State.HEIGHT;
        for (int i = 0; i < bitBoards.length; i += 1) {
            long bitBoard = bitBoards[i];
            long diagonal1 = bitBoard & (bitBoard >> height);
            long horizontal = bitBoard & (bitBoard >> (height + 1));
            long diagonal2 = bitBoard & (bitBoard >> (height + 2));
            long vertical = bitBoard & (bitBoard >> 1);
            if (((diagonal1 & (diagonal1 >> 2 * height)) |
                    (horizontal & (horizontal >> 2 * (height + 1))) |
                    (diagonal2 & (diagonal2 >> 2 * (height + 2))) |
                    (vertical & (vertical >> 2))) != 0) {
                if (i == 0) {
                    rewards_ = REWARDS_AGENT1_WINS;
                    return;
                } else {
                    rewards_ = REWARDS_AGENT2_WINS;
                    return;
                }
            }
        }
        rewards_ = REWARDS_NEUTRAL;
    }

    private int getNextAgentTurn(int agentTurn) {
        switch (turnType_) {
            case RANDOM:
                return (int)(Math.random() * N_AGENTS);
            case SEQUENTIAL:
                return (agentTurn + 1) % N_AGENTS;
            case RANDOM_ORDER:
                throw new NotImplementedException();
            case BIDDING:
                throw new NotImplementedException();
        }
        throw new IllegalArgumentException("Invalid TurnType " + turnType_);
    }

    public static Connect4State getInitialState(TurnType turnType) {
        switch (turnType) {
            case RANDOM:
                return new Connect4State(new long[N_AGENTS], (int)(Math.random() * N_AGENTS));
            case SEQUENTIAL:
                return new Connect4State(new long[N_AGENTS], 0);
            case RANDOM_ORDER:
                throw new NotImplementedException();
            case BIDDING:
                throw new NotImplementedException();
        }
        throw new IllegalArgumentException("Invalid TurnType " + turnType);
    }

    public TurnType getTurnType() {
        return turnType_;
    }
}
