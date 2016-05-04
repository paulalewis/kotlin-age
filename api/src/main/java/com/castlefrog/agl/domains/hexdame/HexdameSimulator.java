package com.castlefrog.agl.domains.hexdame;

import com.castlefrog.agl.AdversarialSimulator;

import java.util.ArrayList;
import java.util.List;

public final class HexdameSimulator extends AdversarialSimulator<HexdameState, HexdameAction> {
    private HexdameSimulator(HexdameState state) {
        setState(state);
    }

    private HexdameSimulator(HexdameSimulator simulator) {
        super(simulator);
    }

    public static HexdameSimulator create(HexdameState state) {
        return new HexdameSimulator(state);
    }

    public HexdameSimulator copy() {
        return new HexdameSimulator(this);
    }

    public void setState(HexdameState state) {
        state_ = state;
        legalActions_ = new ArrayList<>();
        legalActions_.add(new ArrayList<HexdameAction>());
        legalActions_.add(new ArrayList<HexdameAction>());
        computeLegalActions();
        rewards_ = computeRewards(legalActions_);
    }

    public void stateTransition(List<HexdameAction> actions) {
        HexdameAction action = actions.get(state_.getAgentTurn());
        if (!legalActions_.get(state_.getAgentTurn()).contains(action)) {
            throw new IllegalArgumentException("Illegal action, " + action + ", from state, " + state_);
        }
        HexdameAction.Location initial = action.getInitial();
        int piece = state_.getLocation(initial.x, initial.y);
        HexdameMoveAction moveAction = (HexdameMoveAction) action;
        HexdameAction.Location move = moveAction.getMove();
        //promote to king?
        if ((state_.getAgentTurn() == HexdameState.TURN_BLACK &&
                (move.x == HexdameState.SIZE - 1 || move.y == HexdameState.SIZE - 1)) ||
                (state_.getAgentTurn() == HexdameState.TURN_WHITE && (move.x == 0 || move.y == 0))) {
            piece += 2;
        }
        state_.setLocation(move.x, move.y, piece);
        state_.setLocation(initial.x, initial.y, HexdameState.LOCATION_EMPTY);
        if (action instanceof HexdameJumpAction) {
            HexdameJumpAction jumpAction = (HexdameJumpAction) action;
            for (int i = 0; i < jumpAction.getNJumps(); i += 1) {
                HexdameAction.Location jump = jumpAction.getJump(i);
                state_.setLocation(jump.x, jump.y, HexdameState.LOCATION_EMPTY);
            }
        }
        state_.setAgentTurn((state_.getAgentTurn() + 1) % N_AGENTS);
        computeLegalActions();
        rewards_ = computeRewards(legalActions_);
    }

    private void computeLegalActions() {
        clearLegalActions();
        List<HexdameAction> actions = legalActions_.get(state_.getAgentTurn());
        boolean jumps = false;
        if (jumps) {
            //TODO
        } else {
            //move actions
            byte[][] locations = state_.getLocations();
            for (int i = 0; i < locations.length; i += 1) {
                for (int j = 0; j < locations[0].length; j += 1) {
                    if (locations[i][j] == state_.getAgentTurn() + 1) {
                        int nexti = (state_.getAgentTurn() == HexdameState.TURN_BLACK) ? (i + 1) : (i - 1);
                        int nextj = (state_.getAgentTurn() == HexdameState.TURN_BLACK) ? (j + 1) : (j - 1);
                        if (nexti >= 0 && nexti < HexdameState.SIZE && locations[nexti][j] == HexdameState.LOCATION_EMPTY) {
                            actions.add(new HexdameMoveAction(new HexdameAction.Location(i, j), new HexdameAction.Location(nexti, j)));
                        }
                        if (nextj >= 0 && nextj < HexdameState.SIZE && locations[i][nextj] == HexdameState.LOCATION_EMPTY) {
                            actions.add(new HexdameMoveAction(new HexdameAction.Location(i, j), new HexdameAction.Location(i, nextj)));
                        }
                        if (nexti >= 0 && nexti < HexdameState.SIZE && nextj >= 0 &&
                                   nextj < HexdameState.SIZE && locations[nexti][nextj] == HexdameState.LOCATION_EMPTY) {
                            actions.add(new HexdameMoveAction(new HexdameAction.Location(i, j), new HexdameAction.Location(nexti, nextj)));
                        }
                    }
                }
            }
        }
    }

    /**
     * A player with no legal moves (i.e. the player
     * has no pieces to move) loses the game.
     * If neither play has legal actions then the game is
     * a draw.
     */
    private int[] computeRewards(List<List<HexdameAction>> actions) {
        if (actions.get(0).isEmpty() && !actions.get(1).isEmpty()) {
            return REWARDS_WHITE_WINS;
        } else if (actions.get(1).isEmpty() && !actions.get(0).isEmpty()) {
            return REWARDS_BLACK_WINS;
        } else {
            return REWARDS_NEUTRAL;
        }
    }
}
