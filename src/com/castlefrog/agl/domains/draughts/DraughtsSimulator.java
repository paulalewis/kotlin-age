package com.castlefrog.agl.domains.draughts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.castlefrog.agl.AbstractSimulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.TurnType;

public final class DraughtsSimulator
    extends AbstractSimulator<DraughtsState, DraughtsAction> {
	private static final int N_AGENTS = 2;
    private static final TurnType TURN_TYPE = TurnType.SEQUENTIAL;
    private static final int SIZE = 10;

    public DraughtsSimulator() {}

	private DraughtsSimulator(DraughtsState state,
                              List<HashSet<DraughtsAction>> legalActions,
                              int[] rewards) {
		state_ = state;
        legalActions_ = new ArrayList<HashSet<DraughtsAction>>();
        for (HashSet<DraughtsAction> actions: legalActions) {
            HashSet<DraughtsAction> temp = new HashSet<DraughtsAction>();
            for (DraughtsAction action: actions)
                temp.add(action);
            legalActions_.add(temp);
        }
        if (rewards != null) {
            rewards_ = new int[N_AGENTS];
            for (int i = 0; i < N_AGENTS; i += 1)
                rewards_[i] = rewards[i];
        }
	}

	public DraughtsSimulator clone() {
        return new DraughtsSimulator(state_, legalActions_, rewards_);
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
	private List<HashSet<DraughtsAction>> computeLegalActions() {
        List<HashSet<DraughtsAction>> legalActions = new ArrayList<HashSet<DraughtsAction>>();
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
