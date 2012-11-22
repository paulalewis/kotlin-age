package com.castlefrog.agl.domains.backgammon;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.castlefrog.agl.AbstractSimulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.TurnType;

/**
 * Backgammon has about 100 actions per game.
 * 
 */
public final class BackgammonSimulator
    extends AbstractSimulator<BackgammonState, BackgammonAction> {
    private static final int N_AGENTS = 2;

    private List<BackgammonAction> legalActions_;

    public BackgammonSimulator() {
        nAgents_ = N_AGENTS;
        turnType_ = TurnType.SEQUENTIAL;
        state_ = getInitialState();
        rewards_ = new int[N_AGENTS];
        legalActions_ = new ArrayList<BackgammonAction>();
        computeLegalActions();
    }

    private BackgammonSimulator(BackgammonState state,
                                List<BackgammonAction> legalActions,
                                int[] rewards) {
        state_ = state;
        legalActions_ = new ArrayList<BackgammonAction>();
        for (BackgammonAction action: legalActions)
            legalActions_.add(action);
        rewards_ = new int[N_AGENTS];
        for (int i = 0; i < N_AGENTS; i += 1)
            rewards_[i] = rewards[i];
    }

    @Override
    public Simulator<BackgammonState, BackgammonAction> clone() {
        return new BackgammonSimulator(state_, legalActions_, rewards_);
    }

    public void setState(BackgammonState state) {
        state_ = state;
        computeRewards();
        computeLegalActions();
    }

    public void stateTransition(List<BackgammonAction> actions) {
        BackgammonAction action = actions.get(state_.getAgentTurn());
        if (!legalActions_.contains(action))
            throw new IllegalActionException(action, state_);

        byte[] locations = state_.getLocations();

        for (int i = 0; i < action.size(); i++) {
            int from = action.getMove(i).getFrom();
            int distance = action.getMove(i).getDistance();
            byte piece;

            if (locations[from] > 0)
                piece = 1;
            else
                piece = -1;
            int to = from + distance * piece;
            if (to > 0 && to < BackgammonState.getNumberOfLocations() - 1) {
                if (locations[to] * piece < 0) {
                    locations[to] = piece;
                    if (piece > 0)
                        locations[25] -= piece;
                    else
                        locations[0] -= piece;
                } else
                    locations[to] += piece;
            }
            locations[from] -= piece;
        }
        byte[] dice = new byte[] {
                (byte) (Math.random() * BackgammonState.getNumberOfDieFaces() + 1),
                (byte) (Math.random() * BackgammonState.getNumberOfDieFaces() + 1) };
        state_ = new BackgammonState(locations, dice, (state_.getAgentTurn() + 1) % 2);
        computeRewards();
        computeLegalActions();
    }

    private void computeLegalActions() {
        legalActions_.clear();
        byte[] locations = state_.getLocations();
        byte[] dice = state_.getDice();
        int piece;
        byte[] values;
        int depth;

        if (getRewards()[0] == 0) {
            if (state_.getAgentTurn() == 0)
                piece = 1;
            else
                piece = -1;
            if (dice[0] == dice[1])
                values = new byte[] { dice[0] };
            else
                values = dice;

            if (dice[0] == dice[1])
                depth = 4;
            else
                depth = 2;

            // Simplify the board
            for (int i = 0; i < BackgammonState.getNumberOfLocations(); i++)
                if (locations[i] * piece == -1)
                    locations[i] = 0;

            legalActions_ = dfs(locations, new LinkedList<BackgammonMove>(), values, piece, depth);

            // Prune moves that are too small
            int max = 0;
            for (BackgammonAction legalAction : legalActions_)
                if (legalAction.size() > max)
                    max = legalAction.size();
            for (int i = 0; i < legalActions_.size(); i++)
                if (legalActions_.get(i).size() != max)
                    legalActions_.remove(i--);
        }
    }

    private List<BackgammonAction> dfs(byte[] locations,
            LinkedList<BackgammonMove> moves, byte[] values, int piece,
            int depth) {
        List<BackgammonAction> legalActions = new ArrayList<BackgammonAction>();
        int limit = BackgammonState.getNumberOfLocations();
        int start = 0;

        if (piece > 0 && locations[0] > 0)
            limit = 1;
        else if (piece < 0 && locations[25] < 0)
            start = 25;

        boolean moveOff = canMoveOff(locations, piece);
        for (int i = start; i < limit; i++) {
            if (locations[i] * piece >= 1) {
                for (int j = 0; j < values.length; j++) {
                    if (canMove(i, values[j], moveOff)) {
                        BackgammonMove move = BackgammonMove.valueOf(i,
                                values[j]);
                        if (moves.isEmpty()
                                || move.compareTo(moves.getLast()) * piece >= 0) {
                            moves.addLast(move);
                            if (depth > 1) {
                                locations[i] -= piece;
                                int next = i + values[j] * piece;
                                if (next > 0
                                        && next < BackgammonState
                                                .getNumberOfLocations() - 1)
                                    locations[next] += piece;
                                int k = 0;
                                if (values.length == 2) {
                                    if (j == 0)
                                        k = 1;
                                    else
                                        k = 0;
                                }
                                legalActions.addAll(dfs(locations, moves,
                                        new byte[] { values[k] }, piece,
                                        depth - 1));
                                if (next > 0
                                        && next < BackgammonState
                                                .getNumberOfLocations() - 1)
                                    locations[next] -= piece;
                                locations[i] += piece;
                            } else
                                legalActions.add(new BackgammonAction(moves));
                            moves.removeLast();
                        }
                    }
                }
            }
        }
        if (legalActions.size() == 0)
            legalActions.add(new BackgammonAction(moves));
        return legalActions;
    }

    private boolean canMove(int location, int distance, boolean moveOff) {
        if (state_.getAgentTurn() == 0) {
            int next = location + distance;
            return (next < BackgammonState.getNumberOfLocations() - 1 && state_
                    .getLocation(next) >= -1)
                    || (moveOff && next >= BackgammonState
                            .getNumberOfLocations() - 1);
        } else {
            int next = location - distance;
            return (next > 0 && state_.getLocation(next) <= 1)
                    || (moveOff && next <= 0);
        }
    }

    /**
     * Checks if a player can start moving pieces off of the board.
     * 
     * @param locations
     * @param piece
     * @return true if legal to move off board.
     */
    private boolean canMoveOff(byte[] locations, int piece) {
        if (piece > 0) {
            for (int i = 0; i < 19; i++)
                if (locations[i] > 0)
                    return false;
        } else {
            for (int i = 7; i < BackgammonState.getNumberOfLocations(); i++)
                if (locations[i] < 0)
                    return false;
        }
        return true;
    }

    // private int moveOffDistance(byte[] locations, int piece) {
    // int distance = 0;
    // if (piece > 0) {
    // for (int i = 0; i < 19; i++)
    // if (locations[i] > 0)
    // distance += 1;
    // } else {
    // for (int i = 7; i < BackgammonState.getNumberOfLocations(); i++)
    // if (locations[i] < 0)
    // distance += 1;
    // }
    // return distance;
    // }

    /**
     * @return {-1,1} for loss and {1,-1} for win at terminal state otherwise
     *         returns {0,0}
     */
    public void computeRewards() {
        boolean pos = false, neg = false;
        for (int i = 0; i < BackgammonState.getNumberOfLocations(); i += 1) {
            if (state_.getLocation(i) > 0)
                pos = true;
            else if (state_.getLocation(i) < 0)
                neg = true;
        }
        if (!pos) {
            rewards_[0] = 1;
            rewards_[1] = -1;
        } else if (!neg) {
            rewards_[0] = -1;
            rewards_[1] = 1;
        } else {
            rewards_[0] = 0;
            rewards_[1] = 0;
        }
    }

    public BackgammonState getInitialState() {
        byte[] locations = new byte[] { 0, 2, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0,
                5, -5, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, -2, 0 };
        byte[] dice = new byte[BackgammonState.getNumberOfDice()];
        int agentTurn;

        do {
            dice[0] = (byte) (Math.random()
                    * BackgammonState.getNumberOfDieFaces() + 1);
            dice[1] = (byte) (Math.random()
                    * BackgammonState.getNumberOfDieFaces() + 1);
        } while (dice[0] == dice[1]);

        if (dice[0] > dice[1]) {
            dice[1] = (byte) (Math.random()
                    * BackgammonState.getNumberOfDieFaces() + 1);
            agentTurn = 0;
        } else {
            dice[0] = (byte) (Math.random()
                    * BackgammonState.getNumberOfDieFaces() + 1);
            agentTurn = 1;
        }
        return new BackgammonState(locations, dice, agentTurn);
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

    public BackgammonState getState() {
        return state_;
    }
    
    public List<List<BackgammonAction>> getLegalActions() {
        List<List<BackgammonAction>> allLegalActions
            = new ArrayList<List<BackgammonAction>>();
        for (int i = 0; i < N_AGENTS; i += 1)
            allLegalActions.add(getLegalActions(i));
        return allLegalActions;
    }

    public List<BackgammonAction> getLegalActions(int agentId) {
        List<BackgammonAction> legalActions = new ArrayList<BackgammonAction>();
        if (state_.getAgentTurn() == agentId)
            for (BackgammonAction action: legalActions_)
                legalActions.add(action);
        else
            legalActions.add(null);
        return legalActions;
    }

    public boolean hasLegalActions(int agentId) {
        return state_.getAgentTurn() == agentId && legalActions_.size() != 0;
    }
}
