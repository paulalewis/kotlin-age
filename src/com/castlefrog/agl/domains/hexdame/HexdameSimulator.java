package com.castlefrog.agl.domains.hexdame;

import com.castlefrog.agl.Adversarial2AgentSimulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.TurnType;

import java.util.List;

public final class HexdameSimulator extends Adversarial2AgentSimulator<HexdameState, HexdameAction> {
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
        computeRewards();
        computeLegalActions(null);
    }

    public void stateTransition(List<HexdameAction> actions) {
        HexdameAction action = actions.get(state_.getAgentTurn());
        if (!legalActions_.get(state_.getAgentTurn()).contains(action)) {
            throw new IllegalActionException(action, state_);
        }
        state_.setLocation(action.getX(), action.getY(), state_.getAgentTurn() + 1);
        //state_.switchAgentTurn();
        computeRewards();
        computeLegalActions(action);
    }

    private void computeLegalActions(HexdameAction prevAction) {
    }

    private void computeRewards() {
        byte[][] locations = state_.getLocations();
        boolean hasAgent1Pieces = false;
        boolean hasAgent2Pieces = false;
        for (int i = 0; i < HexdameState.SIZE; i += 1) {
            for (int j = 0; j < HexdameState.SIZE; j += 1) {
                if (locations[i][j] == HexdameState.LOCATION_AGENT1 ||
                        locations[i][j] == HexdameState.LOCATION_AGENT1_KING) {
                    hasAgent1Pieces = true;
                } else if (locations[i][j] == HexdameState.LOCATION_AGENT2 ||
                        locations[i][j] == HexdameState.LOCATION_AGENT2_KING) {
                    hasAgent2Pieces = true;
                }
            }
        }
        if (hasAgent2Pieces) {
            rewards_ = REWARDS_AGENT1_WINS;
        } else if (hasAgent1Pieces) {
            rewards_ = REWARDS_AGENT2_WINS;
        } else {
            rewards_ = REWARDS_NEUTRAL;
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
