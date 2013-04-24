package com.castlefrog.agl.domains.go;

import java.util.ArrayList;
import java.util.List;

import com.castlefrog.agl.AbstractSimulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.TurnType;

public final class GoSimulator extends AbstractSimulator<GoState, GoAction> {
    public static final int N_AGENTS = 2;
    public static final int MIN_BOARD_SIZE = 5;
    public static final int MAX_BOARD_SIZE = 19;
    
    private int boardSize_;
    private TurnType turnType_;

    private GoState state_;
    private List<GoAction> legalActions_;
    private int[] rewards_;

    public GoSimulator(int boardSize,
                       TurnType turnType) {
        boardSize_ = boardSize;
        turnType_ = turnType;
    }

	private GoSimulator(int boardSize,
                        TurnType turnType,
                        GoState state,
                        List<GoAction> legalActions,
                        int[] rewards) {
        boardSize_ = boardSize;
        turnType_ = turnType;
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
        return new GoSimulator(boardSize_, turnType_, state_, legalActions_, rewards_);
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
        for (int i = 0; i < boardSize_; i += 1)
            for (int j = 0; j < boardSize_; j += 1)
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
        return new GoState(new byte[boardSize_][boardSize_], 0, 0);
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

    public int getSize() {
        return boardSize_;
    }
    
    public boolean hasLegalActions(int agentId) {
        return state_.getAgentTurn() == agentId && legalActions_.size() != 0;
    }

    public int getNAgents() {
        return N_AGENTS;
    }

    public TurnType getTurnType() {
        return turnType_;
    }
}
