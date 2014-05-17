package com.castlefrog.agl.domains.draughts;

import com.castlefrog.agl.Adversarial2AgentSimulator;
import com.castlefrog.agl.IllegalActionException;

import java.util.ArrayList;
import java.util.List;

public final class DraughtsSimulator extends Adversarial2AgentSimulator<DraughtsState, DraughtsAction> {
    private static final int SIZE = 10;

    private DraughtsSimulator(DraughtsState state) {
        setState(state);
    }

    private DraughtsSimulator(DraughtsSimulator simulator) {
        super(simulator);
    }

    public DraughtsSimulator copy() {
        return new DraughtsSimulator(this);
    }

    public static DraughtsSimulator create() {
        return new DraughtsSimulator(getInitialState());
    }

    public void setState(DraughtsState state) {
        state_ = state;
        rewards_ = computeRewards();
        legalActions_ = computeLegalActions();
    }

    public void stateTransition(List<DraughtsAction> actions) {
        DraughtsAction action = actions.get(state_.getAgentTurn());
        if (!legalActions_.contains(action)) {
            throw new IllegalActionException(action, state_);
        }
        byte[][] locations = state_.getLocations();
        //TODO - code here
        state_ = new DraughtsState(locations, (state_.getAgentTurn() + 1) % 2);
        rewards_ = computeRewards();
        legalActions_ = computeLegalActions();
    }

    private List<List<DraughtsAction>> computeLegalActions() {
        List<List<DraughtsAction>> legalActions = new ArrayList<>();
        //TODO - code here
        return legalActions;
    }

    private int[] computeRewards() {
        if (state_.getNPieces(DraughtsState.TURN_BLACK) == 0) {
            return REWARDS_WHITE_WINS;
        } else if (state_.getNPieces(DraughtsState.TURN_WHITE) == 0) {
            return REWARDS_BLACK_WINS;
        }
        return REWARDS_NEUTRAL;
    }

    public static DraughtsState getInitialState() {
        byte[][] locations = new byte[SIZE / 2][SIZE / 2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < SIZE / 2; j++) {
                locations[i][j] = DraughtsState.LOCATION_BLACK;
                locations[SIZE / 2 - 1 - i][j] = DraughtsState.LOCATION_WHITE;
            }
        }
        return new DraughtsState(locations, DraughtsState.TURN_BLACK);
    }
}
