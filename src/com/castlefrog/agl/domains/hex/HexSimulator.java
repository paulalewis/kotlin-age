package com.castlefrog.agl.domains.hex;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.castlefrog.agl.AbstractSimulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.TurnType;

public final class HexSimulator extends AbstractSimulator<HexState, HexAction> {
    private static final int N_AGENTS = 2;
    private static final int MIN_BOARD_SIZE = 1;
    private static final int MAX_BOARD_SIZE = 26;
    
    private int boardSize_;
    private TurnType turnType_;
    
    /**
     * Create a hex simulator.
     * Defaults to an initial board state.
     * @param size
     *      size of board
     * @param turnType
     *      indicates how turns are handled
     */
    public HexSimulator(int boardSize,
                        TurnType turnType) {
        if (boardSize < MIN_BOARD_SIZE || boardSize > MAX_BOARD_SIZE)
            throw new IllegalArgumentException("Invalid board size: (" + boardSize + ") " +
                    MIN_BOARD_SIZE + " <= boardSize <= " + MAX_BOARD_SIZE);
        boardSize_ = boardSize;
        turnType_ = turnType;
        state_ = getInitialState();
        rewards_ = new int[N_AGENTS];
        legalActions_ = new ArrayList<List<HexAction>>();
        legalActions_.add(new ArrayList<HexAction>());
        legalActions_.add(new ArrayList<HexAction>());
        computeLegalActions(null);
    }
    
    private HexSimulator(HexSimulator simulator) {
        super(simulator);
        boardSize_ = simulator.getBoardSize();
        turnType_ = simulator.getTurnType();
    }

    public HexSimulator copy() {
        return new HexSimulator(this);
    }
    
    public static HexSimulator create(List<String> params) {
        try {
    		return new HexSimulator(Integer.valueOf(params.get(0)), TurnType.valueOf(TurnType.class, params.get(1)));
        } catch (Exception e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    public void setState(HexState state) {
        state_ = state;
        computeRewards();
        computeLegalActions(null);
    }

    public void stateTransition(List<HexAction> actions) {
        HexAction action = actions.get(state_.getAgentTurn());
        if (!legalActions_.get(state_.getAgentTurn()).contains(action))
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
        computeRewards(action);
        computeLegalActions(action);
    }

    private void computeLegalActions(HexAction prevAction) {
        if (rewards_[0] == 0) {
            int agentTurn = state_.getAgentTurn();
            int otherTurn = (agentTurn + 1) % 2;
            legalActions_.set(agentTurn, legalActions_.get(otherTurn));
            legalActions_.set(otherTurn, new ArrayList<HexAction>());
            List<HexAction> legalActions = legalActions_.get(agentTurn);
            if (prevAction != null && state_.getNPieces() > 2) {
                legalActions.remove(prevAction);
            } else {
                legalActions.clear();
                if (state_.getNPieces() == 1 && state_.getAgentTurn() == 1) {
                    for (int i = 0; i < boardSize_; i += 1)
                        for (int j = 0; j < boardSize_; j += 1)
                            legalActions.add(HexAction.valueOf(i,j));
                } else {
                    for (int i = 0; i < boardSize_; i += 1)
                        for (int j = 0; j < boardSize_; j += 1)
                            if (state_.getLocation(i,j) == 0)
                                legalActions.add(HexAction.valueOf(i,j));
                }
            }
        } else {
            for (List<HexAction> legalActions: legalActions_)
                legalActions.clear();
        }
    }

    private void computeRewards() {
        if (state_.getNPieces() > 2 * boardSize_ - 2) {
            byte[][] locations = state_.getLocations();
            boolean[][] visited = new boolean[boardSize_][boardSize_];
            for (int i = 0; i < boardSize_; i += 1) {
                if (locations[0][i] == 1 && visited[0][i] == false) {
                    if ((dfsSides(0, i, locations, visited) & 3) == 3) {
                        rewards_[0] = 1;
                        rewards_[1] = -1;
                        return;
                    }
                }
                if (locations[i][0] == 2 && visited[i][0] == false) {
                    if ((dfsSides(i, 0, locations, visited) & 12) == 12) {
                        rewards_[0] = -1;
                        rewards_[1] = 1;
                        return;
                    }
                }
            }
        }
        rewards_[0] = rewards_[1] = 0;
    }
    
    private void computeRewards(HexAction action) {
        byte[][] locations = state_.getLocations();
        boolean[][] visited = new boolean[boardSize_][boardSize_];
        int x = action.getX();
        int y = action.getY();
        int value = dfsSides(x,y,locations,visited);
        if (locations[x][y] == 1 && (value & 3) == 3) {
            rewards_[0] = 1;
            rewards_[1] = -1;
        } else if (locations[x][y] == 2 && (value & 12) == 12) {
            rewards_[0] = -1;
            rewards_[1] = 1;
        } else
            rewards_[0] = rewards_[1] = 0;
    }

    private int dfsSides(int x0,
                         int y0,
                         byte[][] locations,
                         boolean[][] visited) {
        int value = 0;
        Stack<HexAction> stack = new Stack<HexAction>();
        stack.push(HexAction.valueOf(x0,y0));
        visited[x0][y0] = true;
        while (!stack.empty()) {
            HexAction v = stack.pop();
            int x = v.getX();
            int y = v.getY();
            value |= getLocationMask(x,y);
            for (int i = -1; i <= 1; i += 1) {
                for (int j = -1; j <= 1; j += 1) {
                    int xi = x + i;
                    int yi = y + j;
                    if (i + j != 0 && xi >= 0 && yi >= 0 &&
                            xi < boardSize_ && yi < boardSize_) {
                        if (!visited[xi][yi] &&
                                locations[xi][yi] == locations[x][y]) {
                            stack.push(HexAction.valueOf(xi, yi));
                            visited[xi][yi] = true;
                        }
                    }
                }
            }
        }
        return value;
    }

    /*private List<HexAction> dfsWin(int x0,
                                   int y0,
                                   byte[][] locations,
                                   boolean[][] visited) {
        int value = 0;
        List<HexAction> connection = new ArrayList<HexAction>();
        Stack<HexAction> stack = new Stack<HexAction>();
        stack.push(HexAction.valueOf(x0,y0));
        connection.add(HexAction.valueOf(x0,y0));
        visited[x0][y0] = true;
        while (!stack.empty()) {
            HexAction v = stack.pop();
            int x = v.getX();
            int y = v.getY();
            value |= getLocationMask(x,y);
            for (int i = -1; i <= 1; i += 1) {
                for (int j = -1; j <= 1; j += 1) {
                    int xi = x + i;
                    int yi = y + j;
                    if (i + j != 0 && xi >= 0 && yi >= 0 &&
                            xi < boardSize_ && yi < boardSize_) {
                        if (!visited[xi][yi] &&
                                locations[xi][yi] == locations[x][y]) {
                            stack.push(HexAction.valueOf(xi,yi));
                            connection.add(HexAction.valueOf(xi,yi));
                            visited[xi][yi] = true;
                        }
                    }
                }
            }
        }
        if (value != 3 && value != 12)
            connection.clear();
        return connection;
    }*/

    private int getLocationMask(int x, int y) {
        int side = 0;
        if (x == 0)
            side |= 1;
        else if (x == boardSize_ - 1)
            side |= 2;
        if (y == 0)
            side |= 4;
        else if (y == boardSize_ - 1)
            side |= 8;
        return side;
    }
    
    public int[][] getWinningConnection() {
        int[][] connection = new int[boardSize_][boardSize_];
        if (rewards_[0] != 0) {
            Simulator<HexState,HexAction> simulator = new HexSimulator(boardSize_, TurnType.SEQUENTIAL);
            HexState state = state_.copy();
            for (int i = 0; i < boardSize_; i += 1) {
                for (int j = 0; j < boardSize_; j += 1) {
                    int location = state.getLocation(i,j);
                    if (!state.isLocationEmpty(i,j) &&
                            location != state.getAgentTurn() + 1) {
                        state.setLocation(i,j,HexState.Location.EMPTY);
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

    public HexState getInitialState() {
        return new HexState(new byte[boardSize_][boardSize_], 0);
    }

    public int getBoardSize() {
        return boardSize_;
    }

    public int getNAgents() {
        return N_AGENTS;
    }

    public TurnType getTurnType() {
        return turnType_;
    }
}
