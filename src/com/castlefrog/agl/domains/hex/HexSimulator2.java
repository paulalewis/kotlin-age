package com.castlefrog.agl.domains.hex;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.TurnType;

public final class HexSimulator2
        implements Simulator<HexState, HexAction> {
    /** number of agents in Hex domain */
    public static final int N_AGENTS = 2;
    /** default Hex board size */
    public static final int DEFAULT_SIZE = 14;

    private static TurnType turnType_;
    /** Hex board size */
    private static int size_;
    
    /** current Hex state */
    private HexState state_;
    /** legal actions from current state */
    private HashSet<HexAction> legalActions_;
    /** rewards for each agent in current state */
    private int[] rewards_;
    /** list of connected groups */
    private List<ValueGroup> valueGroups_;

    private class ValueGroup {
        /** Win value of the connected set */
        private byte value_;
        /** Set of connected locations on board */
        private Set<HexAction> group_;

        public ValueGroup(Set<HexAction> group,
                          int value) {
            group_ = new HashSet<HexAction>();
            for (HexAction action: group)
                group_.add(action);
            value_ = (byte) value;
        }

        public int getValue() {
            return value_;
        }

        public Set<HexAction> getGroup() {
            return group_;
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof ValueGroup))
                return false;
            ValueGroup valueGroup = (ValueGroup) object;
            Set<HexAction> group = valueGroup.getGroup();
            for (HexAction action: group)
                if (!group_.contains(action))
                    return false;
            return value_ == valueGroup.getValue();
        }
    }

    /**
     * Create a default Hex simulator.
     */
    public HexSimulator2() {
        this(DEFAULT_SIZE, TurnType.SEQUENTIAL_ORDER);
    }

    /**
     * Create a custom hex simulator.
     * Defaults to an initial board state.
     * @param size
     *      size of board
     */
    public HexSimulator2(int size,
                         TurnType turnType) {
        size_ = size;
        turnType_ = turnType;
        state_ = getInitialState();
        rewards_ = new int[N_AGENTS];
        legalActions_ = new HashSet<HexAction>();
        valueGroups_ = new ArrayList<ValueGroup>();
        computeLegalActions(null);
    }

    /**
     * Contructor is used by clone().
     */
    private HexSimulator2(HexState state,
                          HashSet<HexAction> legalActions,
                          int[] rewards,
                          List<ValueGroup> valueGroups) {
        state_ = state.clone();
        legalActions_ = new HashSet<HexAction>();
        for (HexAction action: legalActions)
            legalActions_.add(action);
        rewards_ = new int[N_AGENTS];
        for (int i = 0; i < N_AGENTS; i += 1)
            rewards_[i] = rewards[i];
        valueGroups_ = new ArrayList<ValueGroup>();
        for (ValueGroup group: valueGroups)
            valueGroups_.add(new ValueGroup(group.getGroup(), group.getValue()));
    }

    @Override
    public Simulator<HexState, HexAction> clone() {
        return new HexSimulator2(state_,legalActions_,rewards_,valueGroups_);
    }

    public void setState(HexState state) {
        state_ = state.clone();
        computeValueGroups(null);
        computeRewards();
        computeLegalActions(null);
    }

    public void stateTransition(List<HexAction> actions) {
        HexAction action = actions.get(state_.getAgentTurn());
        if (!legalActions_.contains(action))
            throw new IllegalActionException(action,state_);
        int x = action.getX();
        int y = action.getY();
        if (state_.getLocation(x,y) == 0)
            state_.setLocation(x,y,state_.getAgentTurn() + 1);
        else {
            state_.setLocation(x,y,0);
            state_.setLocation(y,x,state_.getAgentTurn() + 1);
        }
        state_.switchAgentTurn();
        computeValueGroups(action);
        computeRewards();
        computeLegalActions(action);
    }

    private void computeValueGroups(HexAction prevAction) {
        if (prevAction != null)
            computeValueGroup(prevAction.getX(), prevAction.getY());
        else {
            for (int i = 0; i < size_; i += 1)
                for (int j = 0; j < size_; j += 1)
                    if (state_.getLocation(i, j) != 0)
                        computeValueGroup(i, j);
        }
    }
    
    private void computeValueGroup(int x, int y) {
        List<ValueGroup> adjacentGroups = new ArrayList<ValueGroup>();
        HashSet<HexAction> mergeGroup = new HashSet<HexAction>();
        mergeGroup.add(HexAction.valueOf(x, y));
        int value = checkSide(x, y, state_.getLocation(x, y) - 1);
        for (int i = -1; i <= 1; i += 1) {
            for (int j = -1; j <= 1; j += 1) {
                int xi = x + i;
                int yi = y + j;
                if (i + j != 0 && xi >= 0 && yi >= 0 &&
                        xi < size_ && yi < size_) {
                    for (ValueGroup group: valueGroups_) {
                        if (group.getGroup().contains(HexAction.valueOf(xi, yi)) &&
                                state_.getLocation(x, y) == state_.getLocation(xi, yi)) {
                            adjacentGroups.add(group);
                        }
                    }
                }
            }
        }
        for (ValueGroup group: adjacentGroups) {
            mergeGroup.addAll(group.getGroup());
            value |= group.getValue();
            valueGroups_.remove(group);
        }
        valueGroups_.add(new ValueGroup(mergeGroup, value));
    }
    
    private void computeLegalActions(HexAction prevAction) {
        if (rewards_[0] == 0) {
            if (prevAction != null && state_.getNPieces() > 2) {
                    legalActions_.remove(prevAction);
            } else {
                legalActions_.clear();
                if (state_.getNPieces() == 1 && state_.getAgentTurn() == 1) {
                    for (int i = 0; i < size_; i += 1)
                        for (int j = 0; j < size_; j += 1)
                            legalActions_.add(HexAction.valueOf(i,j));
                } else {
                    for (int i = 0; i < size_; i += 1)
                        for (int j = 0; j < size_; j += 1)
                            if (state_.getLocation(i,j) == 0)
                                legalActions_.add(HexAction.valueOf(i,j));
                }
            }
        } else
            legalActions_.clear();
    }

    public int getNLocations() {
        int nLocations = 0;
        for (ValueGroup group: valueGroups_)
            nLocations += group.getGroup().size();
        return nLocations;
    }

    /*private void computeLegalActions(HexAction prevAction) {
        HexAction lastAction = null;
        if (rewards_[0] == 0) {
            if (prevAction != null &&
                    legalActions_.size() <= size_ * size_ - 3)
                legalActions_.remove(prevAction);
            else {
                legalActions_.clear();
                for (int i = 0; i < size_; i += 1)
                    for (int j = 0; j < size_; j += 1) {
                        if (state_.getLocation(i,j) == 0)
                            legalActions_.add(HexAction.valueOf(i,j));
                        else
                            lastAction = HexAction.valueOf(i,j);
                    }
                if (legalActions_.size() == size_ * size_ - 1
                        && state_.getAgentTurn() == 1)
                    legalActions_.add(lastAction);
            }
        } else
            legalActions_.clear();
    }*/
    
    private void computeRewards() {
        for (ValueGroup group: valueGroups_) {
            if (group.getValue() == 3) {
                rewards_[0] = 1;
                rewards_[1] = -1;
                return;
            } else if (group.getValue() == 12) {
                rewards_[0] = -1;
                rewards_[1] = 1;
                return;
            }
        }
        rewards_[0] = rewards_[1] = 0;
    }

    public boolean isSide(int x, int y, int agent) {
        if (agent == 0)
            return (x == 0 || x == size_ - 1);
        else if (agent == 1)
            return (y == 0 || y == size_ - 1);
        return false;
    }
    
    private int checkSide(int x,
                          int y,
                          int agent) {
        int side = 0;
        if (agent == 0) {
            if (x == 0)
                side = 1;
            else if (x == size_ - 1)
                side = 2;
        } else if (agent == 1) {
            if (y == 0)
                side = 4;
            else if (y == size_ - 1)
                side = 8;
        }
        return side;
    }
    
    /*private int checkSide(int x, int y, int agent) {
        int side = 0;
        if (agent == 0) {
            if (x == 0)
                side = 1;
            else if (x == size_ - 1)
                side = 2;
        } else if (agent == 1) {
            if (y == 0)
                side = 1;
            else if (y == size_ - 1)
                side = 2;
        }
        return side;
    }*/

    /**
     * @return list of actions representing a
     *      winning connection, is empty if non
     *      exists
     */
    public List<HexAction> getWinningConnection() {
        List<HexAction> connection = new ArrayList<HexAction>();
        for (ValueGroup group: valueGroups_) {
            if (group.getValue() == 3 || group.getValue() == 12) {
                for (HexAction action: group.getGroup()) {
                    connection.add(action);
                }
                return connection;
            }
        }
        return connection;
    }

    public HexState getInitialState() {
        return new HexState(new byte[size_][size_], 0);
    }

    public int[] getRewards() {
        int[] rewards = new int[N_AGENTS];
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

    public HexState getState() {
        return state_.clone();
    }
    
    public List<List<HexAction>> getLegalActions() {
        List<List<HexAction>> allLegalActions
            = new ArrayList<List<HexAction>>();
        for (int i = 0; i < N_AGENTS; i += 1)
            allLegalActions.add(getLegalActions(i));
        return allLegalActions;
    }

    public List<HexAction> getLegalActions(int agentId) {
        List<HexAction> legalActions = new ArrayList<HexAction>();
        if (state_.getAgentTurn() == agentId)
            for (HexAction action: legalActions_)
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
    
    public int getSize() {
        return size_;
    }

    public TurnType getTurnType() {
        return turnType_;
    }
}
