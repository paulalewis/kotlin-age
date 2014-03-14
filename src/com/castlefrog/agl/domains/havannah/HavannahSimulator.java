package com.castlefrog.agl.domains.havannah;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.castlefrog.agl.AbstractSimulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.TurnType;

public final class HavannahSimulator extends AbstractSimulator<HavannahState, HavannahAction> {
    private static final int N_AGENTS = 2;
    private static final int[] REWARDS_BLACK_WINS = new int[] { 1, -1 };
    private static final int[] REWARDS_WHITE_WINS = new int[] { -1, 1 };
    private static final int[] REWARDS_NEUTRAL = new int[] { 0, 0 };

    private TurnType turnType_;
    /** length of a side of board */
    private final int base_;
    /** longest row of hexagons on board (always odd) */
    private final int size_;
    /** number of locations on board */
    private final int nLocations_;
    private final int[][] corners_;
    private final int[][][] sides_;

    private HavannahSimulator(HavannahState state) {
        legalActions_ = new ArrayList<>();
        legalActions_.add(new ArrayList<HavannahAction>());
        legalActions_.add(new ArrayList<HavannahAction>());
        setState(state);
        base_ = state.getBase();
        size_ = state.getSize();
        nLocations_ = state.getNLocations();
        corners_ = state.getCorners();
        sides_ = state.getSides();
    }

    private HavannahSimulator(HavannahSimulator simulator,
                              int[][] corners,
                              int[][][] sides) {
        super(simulator);
        base_ = simulator.getState().getBase();
        size_ = simulator.getState().getSize();
        nLocations_ = simulator.getState().getNLocations();
        corners_ = corners;
        sides_ = sides;
        turnType_ = getTurnType();
    }

    public HavannahSimulator copy() {
        return new HavannahSimulator(this, corners_, sides_);
    }

    public static HavannahSimulator create(int boardSize, TurnType turnType) {
        return new HavannahSimulator(getInitialState(boardSize));
    }

    public void setState(HavannahState state) {
        state_ = state;
        computeRewards();
        computeLegalActions();
    }

    public void stateTransition(List<HavannahAction> actions) {
        HavannahAction action = actions.get(state_.getAgentTurn());
        if (!legalActions_.get(state_.getAgentTurn()).contains(action)) {
            throw new IllegalActionException(action, state_);
        }
        state_.setLocation(action.getX(), action.getY(), state_.getAgentTurn() + 1);
        state_.setAgentTurn((state_.getAgentTurn() + 1) % N_AGENTS);
        computeRewards(action);
        computeLegalActions(action);
    }

    private void computeLegalActions() {
        computeLegalActions(null);
    }

    private void computeLegalActions(HavannahAction prevAction) {
        if (rewards_ == REWARDS_NEUTRAL) {
            int agentTurn = state_.getAgentTurn();
            int otherTurn = (agentTurn + 1) % N_AGENTS;
            legalActions_.set(agentTurn, legalActions_.get(otherTurn));
            legalActions_.set(otherTurn, new ArrayList<HavannahAction>());
            List<HavannahAction> legalActions = legalActions_.get(agentTurn);
            if (prevAction != null && nLocations_ - legalActions.size() >= 2) {
                legalActions.remove(prevAction);
            } else {
                legalActions.clear();
                int count = 0;
                HavannahAction tempAction = null;
                for (int y = 0; y < size_; y += 1) {
                    int xMin = 0;
                    int xMax = size_;
                    if (y >= base_) {
                        xMin = y - base_ + 1;
                    } else {
                        xMax = base_ + y;
                    }

                    for (int x = xMin; x < xMax; x += 1) {
                        if (state_.getLocation(x, y) == 0) {
                            legalActions.add(HavannahAction.valueOf(x, y));
                        } else if (state_.getAgentTurn() == 1 && count == 0) {
                            count = 1;
                            tempAction = HavannahAction.valueOf(x, y);
                        } else if (state_.getAgentTurn() == 1 && count == 1) {
                            count = 2;
                            tempAction = null;
                        }
                    }
                }
                if (tempAction != null) {
                    legalActions.add(tempAction);
                }
            }
        } else {
            for (List<HavannahAction> legalActions: legalActions_) {
                legalActions.clear();
            }
        }
    }

    private void computeRewards() {
        computeRewards(null);
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
                if (locations[x][y] != 0 && !visited[x][y]) {
                    int result = dfsCornersSides(x, y, locations, visited);
                    // count corners
                    int corners = 0;
                    for (int k = 0; k < 6; k += 1) {
                        if (result % 2 == 1) {
                            corners += 1;
                        }
                        result >>= 1;
                    }
                    // count sides
                    int sides = 0;
                    for (int k = 0; k < 6; k += 1) {
                        if (result % 2 == 1) {
                            sides += 1;
                        }
                        result >>= 1;
                    }
                    if (corners >= 2 || sides >= 3) {
                        if (locations[x][y] == 1) {
                            rewards_ = REWARDS_BLACK_WINS;
                            return;
                        } else {
                            rewards_ = REWARDS_WHITE_WINS;
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
            if (y >= base_) {
                xMin = y - base_ + 1;
            } else {
                xMax = base_ + y;
            }
            for (int x = xMin; x < xMax; x += 1) {
                if (locations[x][y] == 0 || locations[x][y] == state_.getAgentTurn() + 1) {
                    locations[x][y] = 1;
                } else {
                    locations[x][y] = 0;
                }
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
                if (locations[x][y] != 0 && !visited[x][y]) {
                    if (dfsCornersSides(x, y, locations, visited) == 0) {
                        if (state_.getAgentTurn() == 0) {
                            rewards_ = REWARDS_WHITE_WINS;
                            return;
                        } else {
                            rewards_ = REWARDS_BLACK_WINS;
                            return;
                        }
                    }
                }
            }
        }
        rewards_ = REWARDS_NEUTRAL;
    }

    private int dfsCornersSides(int x0,
                                int y0,
                                byte[][] locations,
                                boolean[][] visited) {
        int value = 0;
        Stack<HavannahAction> stack = new Stack<>();
        stack.push(HavannahAction.valueOf(x0, y0));
        visited[x0][y0] = true;
        while (!stack.empty()) {
            HavannahAction v = stack.pop();
            int x = v.getX();
            int y = v.getY();
            value |= getCornerMask(x, y) | getSideMask(x, y);
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
                            stack.push(HavannahAction.valueOf(xi, yi));
                            visited[xi][yi] = true;
                        }
                    }
                }
            }
        }
        return value;
    }

    public List<HavannahAction> getWinningConnection() {
        List<HavannahAction> connection = new ArrayList<>();
        if (rewards_ != REWARDS_NEUTRAL) {
            HavannahSimulator simulator = HavannahSimulator.create(base_, turnType_);
            HavannahState state = state_.copy();
            for (int i = 0; i < size_; i += 1) {
                for (int j = 0; j < size_; j += 1) {
                    int location = state.getLocation(i, j);
                    if (!state.isLocationEmpty(i, j) &&
                            location != state.getAgentTurn() + 1) {
                        state.setLocation(i, j, HavannahState.LOCATION_EMPTY);
                        simulator.setState(state);
                        if (!simulator.isTerminalState()) {
                            connection.add(HavannahAction.valueOf(i, j));
                            state.setLocation(i, j, location);
                        }
                    }
                }
            }
        }
        return connection;
    }

    private int getCornerMask(int x, int y) {
        for (int i = 0; i < corners_.length; i += 1) {
            if (corners_[i][0] == x && corners_[i][1] == y) {
                return 1 << i;
            }
        }
        return 0;
    }

    private int getSideMask(int x, int y) {
        for (int i = 0; i < sides_.length; i += 1) {
            for (int j = 0; j < sides_[i].length; j += 1) {
                if (sides_[i][j][0] == x && sides_[i][j][1] == y) {
                    return 1 << (i + 6);
                }
            }
        }
        return 0;
    }

    public static HavannahState getInitialState(int base) {
        return new HavannahState(base, new byte[2 * base - 1][2 * base - 1], 0);
    }

    public int getNAgents() {
        return N_AGENTS;
    }

    public TurnType getTurnType() {
        return turnType_;
    }
}
