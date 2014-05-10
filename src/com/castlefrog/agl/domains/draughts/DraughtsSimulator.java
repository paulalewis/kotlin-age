package com.castlefrog.agl.domains.draughts;

import com.castlefrog.agl.Adversarial2AgentSimulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.TurnType;

import java.util.ArrayList;
import java.util.List;

public final class DraughtsSimulator extends Adversarial2AgentSimulator<DraughtsState, DraughtsAction> {
    private static final TurnType TURN_TYPE = TurnType.SEQUENTIAL;
    //private static final int SIZE = 10;

    private DraughtsSimulator(DraughtsState state) {
        setState(state);
    }

    private DraughtsSimulator(DraughtsSimulator simulator) {
        super(simulator);
    }

    public DraughtsSimulator copy() {
        return new DraughtsSimulator(this);
    }

    public static DraughtsSimulator create(DraughtsState state) {
        return new DraughtsSimulator(state);
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

    public int[] computeRewards() {
        //TODO - scan for no pieces left on one side or the other
        return new int[N_AGENTS];
    }

    public TurnType getTurnType() {
        return TURN_TYPE;
    }
}
