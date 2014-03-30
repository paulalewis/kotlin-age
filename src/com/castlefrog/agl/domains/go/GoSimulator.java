package com.castlefrog.agl.domains.go;

import com.castlefrog.agl.Adversarial2AgentSimulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.TurnType;

import java.util.List;

public final class GoSimulator extends Adversarial2AgentSimulator<GoState, GoAction> {
    //private static final int MIN_BOARD_SIZE = 5;
    static final int MAX_BOARD_SIZE = 19;

    private int boardSize_;
    private TurnType turnType_;

    private GoSimulator(GoState state) {
        setState(state);
    }

    private GoSimulator(GoSimulator simulator) {
        super(simulator);
        boardSize_ = simulator.getSize();
        turnType_ = simulator.getTurnType();
    }

    public GoSimulator copy() {
        return new GoSimulator(this);
    }

    public static GoSimulator create(GoState state) {
        return new GoSimulator(state);
    }

    public void setState(GoState state) {
        state_ = state;
        rewards_ = computeRewards();
        computeLegalActions();
    }

    public void stateTransition(List<GoAction> actions) {
        GoAction action = actions.get(state_.getAgentTurn());
        if (!legalActions_.contains(action)) {
            throw new IllegalActionException(action, state_);
        }
        byte[][] locations = state_.getLocations();
        int passFlag = state_.getPassFlag();
        if (action.isPass()) {
            passFlag += 1;
        } else {
            locations[action.getX()][action.getY()] = (byte) (state_.getAgentTurn() + 1);
            //TODO - remove captured pieces
        }
        state_ = new GoState(locations, (state_.getAgentTurn() + 1) % 2, passFlag);
        rewards_ = computeRewards();
        computeLegalActions();
    }

    private void computeLegalActions() {
        legalActions_.get(0).clear();
        legalActions_.get(1).clear();
        List<GoAction> legalActions = legalActions_.get(state_.getAgentTurn());
        legalActions.add(GoAction.valueOf(-1, -1));
        //TODO - not every open space on the board is always legal action
        for (int i = 0; i < boardSize_; i += 1) {
            for (int j = 0; j < boardSize_; j += 1) {
                if (state_.getLocation(i, j) == 0) {
                    legalActions.add(GoAction.valueOf(i, j));
                }
            }
        }
    }

    /**
     * If both players have passed then rewards may be
     * computed.
     */
    private int[] computeRewards() {
        //if (state_.getPassFlag() == 2) {
            //TODO - compute points
        //}
        return new int[N_AGENTS];
    }

    public int getSize() {
        return boardSize_;
    }

    public TurnType getTurnType() {
        return turnType_;
    }
}
