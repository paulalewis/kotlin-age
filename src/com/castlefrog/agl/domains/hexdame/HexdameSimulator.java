package com.castlefrog.agl.domains.hexdame;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.castlefrog.agl.AbstractSimulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.TurnType;

public final class HexdameSimulator extends AbstractSimulator<HexdameState, HexdameAction> {
    private static final int N_AGENTS = 2;
    private static final int BASE = 5;
    private static final int SIZE = 2 * BASE - 1;
    //private static final int N_LOCATIONS = 3 * BASE * BASE - 3 * BASE + 1;
    private static final TurnType TURN_TYPE = TurnType.SEQUENTIAL;
    
    private static int[][] corners_;
    private static int[][][] sides_;

    public HexdameSimulator() {
        corners_ = getCorners();
        sides_ = getSides();
        state_ = getInitialState();
        rewards_ = new int[N_AGENTS];
        legalActions_ = new ArrayList<List<HexdameAction>>();
        legalActions_.add(new ArrayList<HexdameAction>());
        legalActions_.add(new ArrayList<HexdameAction>());
        computeLegalActions(null);
    }

    private HexdameSimulator(HexdameSimulator simulator) {
        super(simulator);
    }

    private int[][] getCorners() {
        return new int[][] {{0, 0},
                            {0, BASE - 1},
                            {BASE - 1, 0},
                            {BASE - 1, SIZE - 1},
                            {SIZE - 1, BASE - 1},
                            {SIZE - 1, SIZE - 1}};
    }

    private int[][][] getSides() {
        int[][][] sides = new int[6][BASE - 2][2];
        for (int i = 0; i < BASE - 2; i += 1) {
            sides[0][i][0] = 0;
            sides[0][i][1] = i + 1;
            sides[1][i][0] = i + 1;
            sides[1][i][1] = 0;
            sides[2][i][0] = i + 1;
            sides[2][i][1] = BASE + i;
            sides[3][i][0] = BASE + i;
            sides[3][i][1] = SIZE - 1;
            sides[4][i][0] = SIZE - 1;
            sides[4][i][1] = BASE + i;
            sides[5][i][0] = BASE + i;
            sides[5][i][1] = i + 1;
        }
        return sides;
    }

    public HexdameSimulator copy() {
        return new HexdameSimulator(this);
    }
    
    public void setState(HexdameState state) {
        state_ = state;
        computeRewards(null);
        computeLegalActions(null);
    }

    public void stateTransition(List<HexdameAction> actions) {
        HexdameAction action = actions.get(state_.getAgentTurn());
        if (!legalActions_.get(state_.getAgentTurn()).contains(action))
            throw new IllegalActionException(action,state_);
        state_.setLocation(action.getX(),action.getY(),state_.getAgentTurn() + 1);
        state_.switchAgentTurn();
        computeRewards(action);
        computeLegalActions(action);
    }

    private void computeLegalActions(HexdameAction prevAction) {
        if (rewards_[0] == 0) {
            int agentTurn = state_.getAgentTurn();
            int otherTurn = (agentTurn + 1) % 2;
            legalActions_.set(agentTurn, legalActions_.get(otherTurn));
            legalActions_.set(otherTurn, new ArrayList<HexdameAction>());
            List<HexdameAction> legalActions = legalActions_.get(agentTurn);
            if (prevAction != null && state_.getNPieces() > 2)
                legalActions.remove(prevAction);
            else {
                legalActions.clear();
                if (state_.getNPieces() == 1 && state_.getAgentTurn() == 1) {
                    for (int y = 0; y < SIZE; y += 1) {
                        int xMin = 0;
                        int xMax = SIZE;
                        if (y >= BASE)
                            xMin = y - BASE + 1;
                        else
                            xMax = BASE + y;
                        for (int x = xMin; x < xMax; x += 1)
                            legalActions.add(HexdameAction.valueOf(x,y));
                    }
                } else {
                    for (int y = 0; y < SIZE; y += 1) {
                        int xMin = 0;
                        int xMax = SIZE;
                        if (y >= BASE)
                            xMin = y - BASE + 1;
                        else
                            xMax = BASE + y;
                        for (int x = xMin; x < xMax; x += 1)
                            if (state_.getLocation(x,y) == 0)
                                legalActions.add(HexdameAction.valueOf(x,y));
                    }
                }
            }
        } else
            for (List<HexdameAction> legalActions: legalActions_)
                legalActions.clear();
    }

    private void computeRewards(HexdameAction prevAction) {
        byte[][] locations = state_.getLocations();
        boolean[][] visited = new boolean[SIZE][SIZE];
        int yMin = 0;
        int xMin = 0;
        int yMax = SIZE;
        int xMax = SIZE;
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
        visited = new boolean[SIZE][SIZE];
        for (int y = 0; y < locations.length; y += 1) {
            xMin = 0;
            xMax = SIZE;
            if (y >= BASE)
                xMin = y - BASE + 1;
            else
                xMax = BASE + y;
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
        yMax = SIZE;
        xMax = SIZE;
        if (prevAction != null) {
            xMin = Math.max(prevAction.getX() - 1, 0);
            yMin = Math.max(prevAction.getY() - 1, 0);
            xMax = Math.min(prevAction.getX() + 2, SIZE);
            yMax = Math.min(prevAction.getY() + 2, SIZE);
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
        Stack<HexdameAction> stack = new Stack<HexdameAction>();
        stack.push(HexdameAction.valueOf(x0,y0));
        visited[x0][y0] = true;
        while (!stack.empty()) {
            HexdameAction v = stack.pop();
            int x = v.getX();
            int y = v.getY();
            value |= getCornerMask(x,y) | getSideMask(x,y);
            for (int i = -1; i <= 1; i += 1) {
                for (int j = -1; j <= 1; j += 1) {
                    int xi = x + i;
                    int yi = y + j;
                    if (i + j != 0 && xi >= 0 && yi >= 0 &&
                            xi < SIZE && yi < SIZE &&
                            (yi < BASE && xi < BASE + yi ||
                                yi >= BASE && xi > yi - BASE)) {
                        if (!visited[xi][yi] &&
                                locations[xi][yi] == locations[x][y]) {
                            stack.push(HexdameAction.valueOf(xi,yi));
                            visited[xi][yi] = true;
                        }
                    }
                }
            }
        }
        return value;
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

    public HexdameState getInitialState() {
        return new HexdameState(new byte[SIZE][SIZE], 0);
    }

    public int getNAgents() {
        return N_AGENTS;
    }

    public TurnType getTurnType() {
        return TURN_TYPE;
    }
}
