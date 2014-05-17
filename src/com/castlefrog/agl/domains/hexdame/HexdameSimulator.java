package com.castlefrog.agl.domains.hexdame;

import com.castlefrog.agl.AdversarialSimulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.TurnType;

import java.util.ArrayList;
import java.util.List;

public final class HexdameSimulator extends AdversarialSimulator<HexdameState, HexdameAction> {
    private static final TurnType TURN_TYPE = TurnType.SEQUENTIAL;

    private static int[][] corners_;
    private static int[][][] sides_;

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
        computeLegalActions(legalActions_);
        rewards_ = computeRewards(legalActions_);
    }

    public void stateTransition(List<HexdameAction> actions) {
        HexdameAction action = actions.get(state_.getAgentTurn());
        if (!legalActions_.get(state_.getAgentTurn()).contains(action)) {
            throw new IllegalActionException(action, state_);
        }
        if (action instanceof HexdameMoveAction) {
            HexdameMoveAction moveAction = (HexdameMoveAction) action;
            HexdameAction.Location initial = moveAction.getInitial();
            HexdameAction.Location move = moveAction.getMove();
            int piece = state_.getLocation(initial.x, initial.y);
            state_.setLocation(move.x, move.y, piece);
            state_.setLocation(initial.x, initial.y, HexdameState.LOCATION_EMPTY);
        }
        if (action instanceof HexdameJumpAction) {
            HexdameJumpAction jumpAction = (HexdameJumpAction) action;
            HexdameAction.Location initial = jumpAction.getInitial();
            HexdameAction.Location move = jumpAction.getMove();
            int piece = state_.getLocation(initial.x, initial.y);
            state_.setLocation(move.x, move.y, piece);
            state_.setLocation(initial.x, initial.y, HexdameState.LOCATION_EMPTY);
            for (int i = 0; i < jumpAction.getNJumps(); i += 1) {
                HexdameAction.Location jump = jumpAction.getJump(i);
                state_.setLocation(jump.x, jump.y, HexdameState.LOCATION_EMPTY);
            }
        }
        state_.setAgentTurn((state_.getAgentTurn() + 1) % N_AGENTS);
        computeLegalActions(legalActions_);
        rewards_ = computeRewards(legalActions_);
    }

    private void computeLegalActions(List<List<HexdameAction>> actions) {
        //TODO
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

    public TurnType getTurnType() {
        return TURN_TYPE;
    }
}
