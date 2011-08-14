package com.castlefrog.agl.domains.go;

import java.util.List;
import java.util.ArrayList;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.TurnType;
import com.castlefrog.agl.IllegalActionException;

public final class GoSimulator implements Simulator<GoState, GoAction> {
	private static final int N_AGENTS = 2;
    private static final TurnType TURN_TYPE = TurnType.SEQUENTIAL_ORDER;
    
    private static int size_;
    private static boolean simultaneous_;

    private GoState state_;
    private List<GoAction> legalActions_;
    private int[] rewards_;

    public GoSimulator() {
        this(19, false);
    }

    public GoSimulator(int size,
                       boolean simultaneous) {
        size_ = size;
        simultaneous_ = simultaneous;
    }

	private GoSimulator(GoState state,
                        List<GoAction> legalActions,
                        int[] rewards) {
		state_ = state;
        if (legalActions != null) {
            legalActions_ = new ArrayList<GoAction>();
            for (GoAction action: legalActions)
                legalActions_.add(action);
        }
        if (rewards != null) {
            rewards_ = new int[N_AGENTS];
            for (int i = 0; i < N_AGENTS; i += 1)
                rewards_[i] = rewards[i];
        }
	}

    @Override
	public Simulator<GoState, GoAction> clone() {
        return new GoSimulator(state_, legalActions_, rewards_);
	}
	
    public void setState(GoState state) {
        state_ = state;
        rewards_ = computeRewards();
        legalActions_ = computeLegalActions();
    }
    
	public void stateTransition(List<GoAction> actions) {
        GoAction action = actions.get(state_.getAgentTurn());
		if (!legalActions_.contains(action))
			throw new IllegalActionException(action, state_);
        byte[][] locations = state_.getLocations();
        int passFlag = state_.getPassFlag();
        if (action.isPass())
            passFlag += 1;
        else {
            locations[action.getX()][action.getY()] = (byte) (state_.getAgentTurn() + 1);
            //TODO - remove captured pieces
        }
        state_ = new GoState(locations, (state_.getAgentTurn() + 1) % 2, passFlag);
        rewards_ = computeRewards();
        legalActions_ = computeLegalActions();
	}

    /**
     */
	private List<GoAction> computeLegalActions() {
        List<GoAction> legalActions = new ArrayList<GoAction>();
        legalActions.add(GoAction.valueOf(-1,-1));
        //TODO - not every open space on the board is always legal action
        for (int i = 0; i < size_; i++)
            for (int j = 0; j < size_; j++)
                if (state_.getLocation(i, j) == 0)
                    legalActions.add(GoAction.valueOf(i, j));
        return legalActions;
	}

    /**
     * If both players have passed then rewards may be
     * computed.
     */
    public int[] computeRewards() {
        if (state_.getPassFlag() == 2) {
            //TODO - compute points
        }
        return new int[N_AGENTS];
    }

    public GoState getInitialState() {
        return new GoState(new byte[size_][size_], 0, 0);
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

    /**
     * The state is terminal when two consecutive
     * pass actions have been taken.
     */
    public boolean isTerminalState() {
        return state_.getPassFlag() == 2;
    }

    public GoState getState() {
        return state_;
    }
    
    public List<List<GoAction>> getLegalActions() {
        List<List<GoAction>> allLegalActions
            = new ArrayList<List<GoAction>>();
        for (int i = 0; i < N_AGENTS; i += 1)
            allLegalActions.add(getLegalActions(i));
        return allLegalActions;
    }

    public List<GoAction> getLegalActions(int agentId) {
        List<GoAction> legalActions = new ArrayList<GoAction>();
        if (state_.getAgentTurn() == agentId)
            for (GoAction action: legalActions_)
                legalActions.add(action);
        else
            legalActions.add(null);
        return legalActions;
    }

	public int getNAgents() {
		return N_AGENTS;
	}

    public int getSize() {
        return size_;
    }
    
    public boolean hasLegalActions(int agentId) {
        return state_.getAgentTurn() == agentId && legalActions_.size() != 0;
    }

    public boolean isSimultaneous() {
        return simultaneous_;
    }

    public TurnType getTurnType() {
        return TURN_TYPE;
    }
}
