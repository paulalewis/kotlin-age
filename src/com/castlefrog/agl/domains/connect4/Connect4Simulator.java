package com.castlefrog.agl.domains.connect4;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.castlefrog.agl.AbstractSimulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.TurnType;

public class Connect4Simulator extends AbstractSimulator<Connect4State, Connect4Action> {
    private static final int N_AGENTS = 2;
    private static final long ALL_LOCATIONS = (1L << ((Connect4State.getHeight() + 1) * Connect4State.getWidth())) - 1;
    private static final long FIRST_COLUMN = (1L << Connect4State.getHeight() + 1) - 1;
    private static final long BOTTOM_ROW = ALL_LOCATIONS / FIRST_COLUMN;
    private static final long ABOVE_TOP_ROW = BOTTOM_ROW << Connect4State.getHeight();

    private int[] height_ = null;

    public Connect4Simulator() {
        state_ = getInitialState();
        rewards_ = new int[N_AGENTS];
        legalActions_ = new ArrayList<HashSet<Connect4Action>>();
        legalActions_.add(new HashSet<Connect4Action>());
        legalActions_.add(new HashSet<Connect4Action>());
        computeLegalActions();
    }

    private Connect4Simulator(Connect4State state,
                              List<HashSet<Connect4Action>> legalActions,
                              int[] rewards,
                              int[] height) {
        state_ = state;
        legalActions_ = new ArrayList<HashSet<Connect4Action>>();
        for (HashSet<Connect4Action> actions: legalActions) {
            HashSet<Connect4Action> temp = new HashSet<Connect4Action>();
            for (Connect4Action action: actions)
                temp.add(action);
            legalActions_.add(temp);
        }
        rewards_ = new int[N_AGENTS];
        for (int i = 0; i < N_AGENTS; i += 1)
            rewards_[i] = rewards[i];
        height_ = new int[height.length];
        for (int i = 0; i < height.length; i += 1)
            height_[i] = height[i];
    }

    @Override
    public Simulator<Connect4State, Connect4Action> clone() {
        return new Connect4Simulator(state_, legalActions_, rewards_, height_);
    }

    public void setState(Connect4State state) {
        state_ = state;
        computeRewards();
        computeLegalActions();
    }

    public void setState(Connect4State state, List<HashSet<Connect4Action>> legalActions) {
        state_ = state;
        legalActions_ = legalActions;
        if (legalActions_.size() == 0)
            computeRewards();
        else
            rewards_ = new int[N_AGENTS];
        computeHeight();
    }

    private void computeHeight() {
        height_ = new int[Connect4State.getWidth()];
        long[] bitBoards = state_.getBitBoards();
        long bitBoard = bitBoards[0] | bitBoards[1];
        for (int i = 0; i < Connect4State.getWidth(); i++) {
            height_[i] = (Connect4State.getHeight() + 1) * i;
            while ((bitBoard & (1L << height_[i])) != 0)
                height_[i]++;
        }
    }

    public void stateTransition(List<Connect4Action> actions) {
    	Connect4Action action = actions.get(state_.getAgentTurn());
        if (!legalActions_.get(state_.getAgentTurn()).contains(action))
            throw new IllegalActionException(action,state_);
        long[] bitBoards = state_.getBitBoards();
        bitBoards[state_.getAgentTurn()] ^= (1L << (height_[action.getLocation()]++));
        state_ = new Connect4State(bitBoards, state_.getNextAgentTurn());
        computeRewards();
        computeLegalActions();
    }

    private void computeLegalActions() {
        legalActions_.get(0).clear();
        legalActions_.get(1).clear();
        computeHeight();
        if (rewards_[0] == 0) {
            long bitBoard = state_.getBitBoards()[state_.getAgentTurn()];
            for (int i = 0; i < Connect4State.getWidth(); i += 1)
                if (((bitBoard | (1L << height_[i])) & ABOVE_TOP_ROW) == 0)
                    legalActions_.get(state_.getAgentTurn()).add(Connect4Action.valueOf(i));
        }
    }

    public void computeRewards() {
        long[] bitBoards = state_.getBitBoards();
        int height = Connect4State.getHeight();

        for (int i = 0; i < bitBoards.length; i++) {
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
                    rewards_ = new int[] { 1, -1 };
                    return;
                } else {
                    rewards_ = new int[] { -1, 1 };
                    return;
                }
            }
        }
        rewards_ = new int[N_AGENTS];
    }
    
    public Connect4State getInitialState() {
        return new Connect4State(new long[2], 0);
    }

    public Connect4State getState() {
        return state_;
    }
    

    public int getNAgents() {
        return N_AGENTS;
    }
    
    public TurnType getTurnType() {
        return TurnType.SEQUENTIAL;
    }
}
