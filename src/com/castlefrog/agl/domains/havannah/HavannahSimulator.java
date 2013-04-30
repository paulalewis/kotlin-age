package com.castlefrog.agl.domains.havannah;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.castlefrog.agl.AbstractSimulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.TurnType;

public final class HavannahSimulator extends AbstractSimulator<HavannahState, HavannahAction> {
    public static final int N_AGENTS = 2;
    public static final int MIN_BASE = 2;

    private TurnType turnType_;
    /** length of a side of board */
    private int base_;
    /** longest row of hexagons on board (always odd) */
    private int size_;
    /** number of locations on board */
    //private static int nLocations_;
    private int[][] corners_;
    private int[][][] sides_;

    public HavannahSimulator(int base,
                             TurnType turnType) {
        if (base < MIN_BASE)
            throw new IllegalArgumentException("Invalid board size: " + base);
        base_ = base;
        size_ = 2 * base_ - 1;
        //nLocations_ = 3 * base_ * base_ - 3 * base_ + 1;
        turnType_ = turnType;
        corners_ = getCorners();
        sides_ = getSides();
        state_ = getInitialState();
        rewards_ = new int[N_AGENTS];
        legalActions_ = new ArrayList<List<HavannahAction>>();
        legalActions_.add(new ArrayList<HavannahAction>());
        legalActions_.add(new ArrayList<HavannahAction>());
        computeLegalActions(null);
    }

    private HavannahSimulator(HavannahSimulator simulator,
                              int[][] corners,
                              int[][][] sides) {
        super(simulator);
        base_ = simulator.getBase();
        size_ = simulator.getSize();
        corners_ = corners;
        sides_ = sides;
        turnType_ = getTurnType();
    }

    private int[][] getCorners() {
        return new int[][] {{0, 0},
                            {0, base_ - 1},
                            {base_ - 1, 0},
                            {base_ - 1, size_ - 1},
                            {size_ - 1, base_ - 1},
                            {size_ - 1, size_ - 1}};
    }

    private int[][][] getSides() {
        int[][][] sides = new int[6][base_ - 2][2];
        for (int i = 0; i < base_ - 2; i += 1) {
            sides[0][i][0] = 0;
            sides[0][i][1] = i + 1;
            sides[1][i][0] = i + 1;
            sides[1][i][1] = 0;
            sides[2][i][0] = i + 1;
            sides[2][i][1] = base_ + i;
            sides[3][i][0] = base_ + i;
            sides[3][i][1] = size_ - 1;
            sides[4][i][0] = size_ - 1;
            sides[4][i][1] = base_ + i;
            sides[5][i][0] = base_ + i;
            sides[5][i][1] = i + 1;
        }
        return sides;
    }

    public HavannahSimulator copy() {
        return new HavannahSimulator(this, corners_, sides_);
    }
    
    public static HavannahSimulator create(List<String> params) throws IllegalArgumentException {
    	try {
    		return new HavannahSimulator(Integer.valueOf(params.get(0)), TurnType.valueOf(TurnType.class, params.get(1)));
    	} catch (Exception e) {
    		throw new IllegalArgumentException(e.toString());
    	}
    }

    public void setState(HavannahState state) {
        state_ = state.clone();
        computeRewards(null);
        computeLegalActions(null);
    }

    public void stateTransition(List<HavannahAction> actions) {
        HavannahAction action = actions.get(state_.getAgentTurn());
        if (!legalActions_.get(state_.getAgentTurn()).contains(action))
            throw new IllegalActionException(action,state_);
        state_.setLocation(action.getX(),action.getY(),state_.getAgentTurn() + 1);
        state_.switchAgentTurn();
        computeRewards(action);
        computeLegalActions(action);
    }

    private void computeLegalActions(HavannahAction prevAction) {
        if (rewards_[0] == 0) {
            int agentTurn = state_.getAgentTurn();
            int otherTurn = (agentTurn + 1) % 2;
            legalActions_.set(agentTurn, legalActions_.get(otherTurn));
            legalActions_.set(otherTurn, new ArrayList<HavannahAction>());
            List<HavannahAction> legalActions = legalActions_.get(agentTurn);
            if (prevAction != null && state_.getNPieces() > 2)
                legalActions.remove(prevAction);
            else {
                legalActions.clear();
                if (state_.getNPieces() == 1 && state_.getAgentTurn() == 1) {
                    for (int y = 0; y < size_; y += 1) {
                        int xMin = 0;
                        int xMax = size_;
                        if (y >= base_)
                            xMin = y - base_ + 1;
                        else
                            xMax = base_ + y;
                        for (int x = xMin; x < xMax; x += 1)
                            legalActions.add(HavannahAction.valueOf(x,y));
                    }
                } else {
                    for (int y = 0; y < size_; y += 1) {
                        int xMin = 0;
                        int xMax = size_;
                        if (y >= base_)
                            xMin = y - base_ + 1;
                        else
                            xMax = base_ + y;
                        for (int x = xMin; x < xMax; x += 1)
                            if (state_.getLocation(x,y) == 0)
                                legalActions.add(HavannahAction.valueOf(x,y));
                    }
                }
            }
        } else
            for (List<HavannahAction> legalActions: legalActions_)
                legalActions.clear();
    }

    private void computeRewards(HavannahAction prevAction) {
        byte[][] locations = state_.getLocations();
        boolean[][] visited = new boolean[size_][size_];
        int yMin = 0;
        int xMin = 0;
        int yMax = size_;
        int xMax = size_;
        if (prevAction != null) {
            xMin = prevAction.getX();
            yMin = prevAction.getY();
            xMax = xMin + 1;
            yMax = yMin + 1;
        }
        for (int y = yMin; y < yMax; y += 1) {
            for (int x = xMin; x < xMax; x += 1) {
                // Checks: non empty location - hasn't been visited
                if (locations[x][y] != 0 && visited[x][y] == false) {
                    int result = dfsCornersSides(x, y, locations, visited);
                    // count corners
                    int corners = 0;
                    for (int k = 0; k < 6; k += 1) {
                        if (result % 2 == 1)
                            corners += 1;
                        result >>= 1;
                    }
                    // count sides
                    int sides = 0;
                    for (int k = 0; k < 6; k += 1) {
                        if (result % 2 == 1)
                            sides += 1;
                        result >>= 1;
                    }
                    if (corners >= 2 || sides >= 3) {
                        if (locations[x][y] == 1) {
                            rewards_[0] = 1;
                            rewards_[1] = -1;
                            return;
                        } else {
                            rewards_[0] = -1;
                            rewards_[1] = 1;
                            return;
                        }
                    }
                }
            }
        }

        locations = state_.getLocations();
        visited = new boolean[size_][size_];
        for (int y = 0; y < locations.length; y += 1) {
            xMin = 0;
            xMax = size_;
            if (y >= base_)
                xMin = y - base_ + 1;
            else
                xMax = base_ + y;
            for (int x = xMin; x < xMax; x += 1) {
                if (locations[x][y] == 0
                        || locations[x][y] == state_.getAgentTurn() + 1)
                    locations[x][y] = 1;
                else
                    locations[x][y] = 0;
            }
        }

        yMin = 0;
        xMin = 0;
        yMax = size_;
        xMax = size_;
        if (prevAction != null) {
            xMin = Math.max(prevAction.getX() - 1, 0);
            yMin = Math.max(prevAction.getY() - 1, 0);
            xMax = Math.min(prevAction.getX() + 2, size_);
            yMax = Math.min(prevAction.getY() + 2, size_);
        }

        for (int y = yMin; y < yMax; y += 1) {
            for (int x = xMin; x < xMax; x += 1) {
                if (locations[x][y] != 0 && visited[x][y] == false) {
                    if (dfsCornersSides(x, y, locations, visited) == 0) {
                        if (state_.getAgentTurn() == 0) {
                            rewards_[0] = -1;
                            rewards_[1] = 1;
                            return;
                        } else {
                            rewards_[0] = 1;
                            rewards_[1] = -1;
                            return;
                        }
                    }
                }
            }
        }
        rewards_[0] = rewards_[1] = 0;
    }

    private int dfsCornersSides(int x0,
                                int y0,
                                byte[][] locations,
                                boolean[][] visited) {
        int value = 0;
        Stack<HavannahAction> stack = new Stack<HavannahAction>();
        stack.push(HavannahAction.valueOf(x0,y0));
        visited[x0][y0] = true;
        while (!stack.empty()) {
            HavannahAction v = stack.pop();
            int x = v.getX();
            int y = v.getY();
            value |= getCornerMask(x,y) | getSideMask(x,y);
            for (int i = -1; i <= 1; i += 1) {
                for (int j = -1; j <= 1; j += 1) {
                    int xi = x + i;
                    int yi = y + j;
                    if (i + j != 0 && xi >= 0 && yi >= 0 &&
                            xi < size_ && yi < size_ &&
                            (yi < base_ && xi < base_ + yi ||
                                yi >= base_ && xi > yi - base_)) {
                        if (!visited[xi][yi] &&
                                locations[xi][yi] == locations[x][y]) {
                            stack.push(HavannahAction.valueOf(xi,yi));
                            visited[xi][yi] = true;
                        }
                    }
                }
            }
        }
        return value;
    }

    public int[][] getWinningConnection() {
        int[][] connection = new int[size_][size_];
        if (rewards_[0] != 0) {
            Simulator<HavannahState,HavannahAction> simulator = new HavannahSimulator(base_, TurnType.SEQUENTIAL);
            HavannahState state = state_.clone();
            for (int i = 0; i < size_; i += 1) {
                for (int j = 0; j < size_; j += 1) {
                    int location = state.getLocation(i,j);
                    if (!state.isLocationEmpty(i,j) &&
                            location != state.getAgentTurn() + 1) {
                        state.setLocation(i,j,HavannahState.Location.EMPTY);
                        simulator.setState(state);
                        if (!simulator.isTerminalState()) {
                            connection[i][j] = 1;
                            state.setLocation(i,j,location);
                        }
                    }
                }
            }
        }
        return connection;
    }

    private int getCornerMask(int x, int y) {
        for (int i = 0; i < corners_.length; i += 1)
            if (corners_[i][0] == x && corners_[i][1] == y)
                return 1 << i;
        return 0;
    }

    private int getSideMask(int x, int y) {
        for (int i = 0; i < sides_.length; i += 1)
            for (int j = 0; j < sides_[i].length; j += 1)
                if (sides_[i][j][0] == x && sides_[i][j][1] == y)
                    return 1 << (i + 6);
        return 0;
    }

    public HavannahState getInitialState() {
        return new HavannahState(new byte[size_][size_], 0);
    }

    public HavannahState getState() {
        return state_.clone();
    }
    
    public int getBase() {
        return base_;
    }
    
    public int getSize() {
        return size_;
    }

    public int getNAgents() {
        return N_AGENTS;
    }

    public TurnType getTurnType() {
        return turnType_;
    }
}
