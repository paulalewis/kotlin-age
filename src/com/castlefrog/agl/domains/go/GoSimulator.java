package com.castlefrog.agl.domains.go;

import java.util.ArrayList;
import java.util.List;

import com.castlefrog.agl.AbstractSimulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.TurnType;

public final class GoSimulator extends AbstractSimulator<GoState, GoAction> {
    public static final int N_AGENTS = 2;
    public static final int MIN_BOARD_SIZE = 5;
    public static final int MAX_BOARD_SIZE = 19;
    
    private int boardSize_;
    private TurnType turnType_;

    public GoSimulator(int boardSize,
                       TurnType turnType) {
        boardSize_ = boardSize;
        turnType_ = turnType;
        state_ = getInitialState();
        rewards_ = new int[N_AGENTS];
        legalActions_ = new ArrayList<List<GoAction>>();
        legalActions_.add(new ArrayList<GoAction>());
        legalActions_.add(new ArrayList<GoAction>());
        computeLegalActions();
    }

    private GoSimulator(GoSimulator simulator) {
        super(simulator);
        boardSize_ = simulator.getSize();
        turnType_ = simulator.getTurnType();
    }

	public GoSimulator clone() {
        return new GoSimulator(this);
	}
	
	public static GoSimulator create(List<String> params) throws IllegalArgumentException {
		try {
			return new GoSimulator(Integer.parseInt(params.get(0)), TurnType.valueOf(TurnType.class, params.get(1)));
		} catch (Exception e) {
			throw new IllegalArgumentException(e.toString());
		}
	}
	
    public void setState(GoState state) {
        state_ = state;
        rewards_ = computeRewards();
        computeLegalActions();
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
        computeLegalActions();
	}

    /**
     */
	private void computeLegalActions() {
        legalActions_.get(0).clear();
        legalActions_.get(1).clear();
        List<GoAction> legalActions = legalActions_.get(state_.getAgentTurn());
        legalActions.add(GoAction.valueOf(-1,-1));
        //TODO - not every open space on the board is always legal action
        for (int i = 0; i < boardSize_; i += 1)
            for (int j = 0; j < boardSize_; j += 1)
                if (state_.getLocation(i, j) == 0)
                    legalActions.add(GoAction.valueOf(i, j));
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
