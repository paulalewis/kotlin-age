package com.castlefrog.agl.domains.draughts;

import java.util.ArrayList;
import java.util.List;

import com.castlefrog.agl.AbstractSimulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.TurnType;

public final class DraughtsSimulator
    extends AbstractSimulator<DraughtsState, DraughtsAction> {
	private static final int N_AGENTS = 2;
    private static final TurnType TURN_TYPE = TurnType.SEQUENTIAL;
    //private static final int SIZE = 10;

    public DraughtsSimulator() {
        state_ = getInitialState();
        rewards_ = new int[N_AGENTS];
        legalActions_ = new ArrayList<List<DraughtsAction>>();
        legalActions_.add(new ArrayList<DraughtsAction>());
        legalActions_.add(new ArrayList<DraughtsAction>());
        computeLegalActions();
    }

    private DraughtsSimulator(DraughtsSimulator simulator) {
        super(simulator);
    }

	public DraughtsSimulator copy() {
        return new DraughtsSimulator(this);
	}
	
	public static DraughtsSimulator create(List<String> params) {
		return new DraughtsSimulator();
	}
	
    public void setState(DraughtsState state) {
        state_ = state;
        rewards_ = computeRewards();
        legalActions_ = computeLegalActions();
    }
    
	public void stateTransition(List<DraughtsAction> actions) {
        DraughtsAction action = actions.get(state_.getAgentTurn());
		if (!legalActions_.contains(action))
			throw new IllegalActionException(action, state_);
        byte[][] locations = state_.getLocations();
        //TODO - code here
        state_ = new DraughtsState(locations, (state_.getAgentTurn() + 1) % 2);
        rewards_ = computeRewards();
        legalActions_ = computeLegalActions();
	}

    /**
     */
	private List<List<DraughtsAction>> computeLegalActions() {
        List<List<DraughtsAction>> legalActions = new ArrayList<List<DraughtsAction>>();
        //TODO - code here
        return legalActions;
	}

    public int[] computeRewards() {
        //TODO - scan for no pieces left on one side or the other
        return new int[N_AGENTS];
    }

    public DraughtsState getInitialState() {
        return null;
    }
	
    public DraughtsState getState() {
        return state_;
    }

    public int getNAgents() {
        return N_AGENTS;
    }

    public TurnType getTurnType() {
        return TURN_TYPE;
    }
}
