package com.castlefrog.agl.domains.draughts;

import java.util.List;
import java.util.ArrayList;

import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.TurnType;
import com.castlefrog.agl.IllegalActionException;

public final class DraughtsSimulator
    implements Simulator<DraughtsState, DraughtsAction> {
	private static final int N_AGENTS = 2;
    private static final TurnType TURN_TYPE = TurnType.SEQUENTIAL_ORDER;
    private static final int SIZE = 10;

    private DraughtsState state_;
    private List<DraughtsAction> legalActions_;
    private int[] rewards_;

    public DraughtsSimulator() {}

	private DraughtsSimulator(DraughtsState state,
                              List<DraughtsAction> legalActions,
                              int[] rewards) {
		state_ = state;
        if (legalActions != null) {
            legalActions_ = new ArrayList<DraughtsAction>();
            for (DraughtsAction action: legalActions)
                legalActions_.add(action);
        }
        if (rewards != null) {
            rewards_ = new int[N_AGENTS];
            for (int i = 0; i < N_AGENTS; i += 1)
                rewards_[i] = rewards[i];
        }
	}
	
    @Override
	public Simulator<DraughtsState, DraughtsAction> clone() {
        return new DraughtsSimulator(state_, legalActions_, rewards_);
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
	private List<DraughtsAction> computeLegalActions() {
        List<DraughtsAction> legalActions = new ArrayList<DraughtsAction>();
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
	
    public int[] getRewards() {
        int[] rewards = new int[2];
        for (int i = 0; i < N_AGENTS; i += 1)
            rewards[i] = rewards_[i];
        return rewards;
	}

    public int getReward(int agentId) {
        return rewards_[agentId];
    }

    public boolean isTerminalState() {
        return legalActions_.size() == 0;
    }

    public DraughtsState getState() {
        return state_;
    }
    
    public List<List<DraughtsAction>> getLegalActions() {
        List<List<DraughtsAction>> allLegalActions
            = new ArrayList<List<DraughtsAction>>();
        for (int i = 0; i < N_AGENTS; i += 1)
            allLegalActions.add(getLegalActions(i));
        return allLegalActions;
    }

    public List<DraughtsAction> getLegalActions(int agentId) {
        List<DraughtsAction> legalActions = new ArrayList<DraughtsAction>();
        if (state_.getAgentTurn() == agentId)
            for (DraughtsAction action: legalActions_)
                legalActions.add(action);
        else
            legalActions.add(null);
        return legalActions;
    }
    
    public boolean hasLegalActions(int agentId) {
        return state_.getAgentTurn() == agentId && legalActions_.size() != 0;
    }

	public int getNAgents() {
		return N_AGENTS;
	}

    public TurnType getTurnType() {
        return TURN_TYPE;
    }
}
