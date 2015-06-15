package com.castlefrog.agl.domains.biniax;

import java.util.ArrayList;
import java.util.List;

import com.castlefrog.agl.AbstractSimulator;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.TurnType;

/**
 * Biniax is a single agent stochastic domain.
 */
public final class BiniaxSimulator extends AbstractSimulator<BiniaxState, BiniaxAction> {
    private static final int N_AGENTS = 1;
    private static final TurnType TURN_TYPE = TurnType.SEQUENTIAL;
    private static final int INITIAL_ELEMENTS = 4;
    private static final int ELEMENT_INCREMENT_INTERVAL = 32;
    private static final double IMPASSIBLE_CHANCE = 0;

    private BiniaxSimulator(BiniaxState state) {
        legalActions_ = new ArrayList<>();
        legalActions_.add(new ArrayList<BiniaxAction>());
        setState(state);
        rewards_ = new int[] {1};
    }

    private BiniaxSimulator(BiniaxSimulator simulator) {
        super(simulator);
    }

    public BiniaxSimulator copy() {
        return new BiniaxSimulator(this);
    }

    public static BiniaxSimulator create(BiniaxState state) {
        return new BiniaxSimulator(state);
    }

    public void setState(BiniaxState state) {
        state_ = state;
        computeLegalActions();
    }

    public void stateTransition(List<BiniaxAction> actions) {
        BiniaxAction action = actions.get(0);
        if (!legalActions_.get(0).contains(action)) {
            throw new IllegalActionException(action, state_);
        }

        byte[][] locations = state_.getLocations();
        byte freeMoves = state_.getFreeMoves();
        int[] elementLocation = getElementLocation();
        int x = elementLocation[0];
        int y = elementLocation[1];
        byte element = state_.getLocation(x, y);
        int nTurns = state_.getNTurns();

        locations[x][y] = 0;
        switch (action) {
        case NORTH:
            y--;
            break;
        case EAST:
            x++;
            break;
        case SOUTH:
            y++;
            break;
        case WEST:
            x--;
            break;
        default:
            break;
        }

        if (locations[x][y] / BiniaxState.ELEMENT_LIMIT == element) {
            element = (byte) (locations[x][y] % BiniaxState.ELEMENT_LIMIT);
        } else if (locations[x][y] % BiniaxState.ELEMENT_LIMIT == element) {
            element = (byte) (locations[x][y] / BiniaxState.ELEMENT_LIMIT);
        }
        locations[x][y] = element;

        freeMoves--;
        if (freeMoves == 0) {
            freeMoves = BiniaxState.MAX_FREE_MOVES;
            // Move all elements down
            int emptyLocation = (int) (Math.random() * BiniaxState.getWidth());
            for (int i = BiniaxState.getHeight() - 1; i >= 0; i--) {
                for (int j = 0; j < BiniaxState.getWidth(); j++) {
                    if (i == 0) {
                        if (j != emptyLocation) {
                            if (Math.random() < IMPASSIBLE_CHANCE) {
                                locations[j][i] = -1;
                            } else {
                                locations[j][i] = (byte) generateRandomElementPair(nTurns);
                            }
                        } else {
                            locations[j][i] = 0;
                        }
                    } else {
                        locations[j][i] = locations[j][i - 1];
                    }
                }
            }
            // Move element back up if possible
            if (locations[x][y] == 0) {
                locations[x][y] = element;
                if (y < BiniaxState.getHeight() - 1) {
                    locations[x][y + 1] = 0;
                }
            } else if (locations[x][y] / BiniaxState.ELEMENT_LIMIT == element) {
                locations[x][y] = (byte) (locations[x][y] % BiniaxState.ELEMENT_LIMIT);
                if (y < BiniaxState.getHeight() - 1) {
                    locations[x][y + 1] = 0;
                }
            } else if (locations[x][y] % BiniaxState.ELEMENT_LIMIT == element) {
                locations[x][y] = (byte) (locations[x][y] / BiniaxState.ELEMENT_LIMIT);
                if (y < BiniaxState.getHeight() - 1) {
                    locations[x][y + 1] = 0;
                }
            }
        }
        state_ = new BiniaxState(locations, freeMoves, nTurns + 1);
        computeLegalActions();
    }

    /**
     * Creates a random element pair of dissimilar elements The elements are
     * always in order from smallest to largest
     * @return int of random values from 0 to numElements_ - 1
     */
    public static int generateRandomElementPair(int nTurns) {
        int nElementTypes = getNElementTypes(nTurns);
        int element1 = ((int) (Math.random() * nElementTypes)) + 1;
        int element2 = ((int) (Math.random() * (nElementTypes - 1))) + 1;
        if (element1 <= element2) {
            return element1 * BiniaxState.ELEMENT_LIMIT + element2 + 1;
        } else {
            return element2 * BiniaxState.ELEMENT_LIMIT + element1;
        }
    }

    private static int getNElementTypes(int nTurns) {
        return Math.min(INITIAL_ELEMENTS + nTurns / ELEMENT_INCREMENT_INTERVAL, BiniaxState.getMaxElements());
    }

    private int[] getElementLocation() {
        for (int i = 0; i < BiniaxState.getWidth(); i++) {
            for (int j = 0; j < BiniaxState.getHeight(); j++) {
                if (state_.getLocation(i, j) > 0 && state_.getLocation(i, j) < BiniaxState.ELEMENT_LIMIT) {
                    return new int[] {i, j};
                }
            }
        }
        throw new IllegalStateException("Element does not exist");
    }

    /**
     * A legal action is one that moves the single element to an empty space or
     * an element pair that contains that element and avoids being pushed off
     * the board.
     */
    private void computeLegalActions() {
        legalActions_.get(0).clear();
        int[] elementLocation = getElementLocation();
        int x = elementLocation[0];
        int y = elementLocation[1];
        int element = state_.getLocation(x, y);
        byte[][] locations = state_.getLocations();

        if (y != 0 && (locations[x][y - 1] == 0 ||
                        locations[x][y - 1] / BiniaxState.ELEMENT_LIMIT == element ||
                        locations[x][y - 1] % BiniaxState.ELEMENT_LIMIT == element)) {
            legalActions_.get(0).add(BiniaxAction.NORTH);
        }

        if (x != BiniaxState.getWidth() - 1) {
            int nextElement = 0;
            if (locations[x + 1][y] == 0) {
                nextElement = element;
            } else if (locations[x + 1][y] / BiniaxState.ELEMENT_LIMIT == element) {
                nextElement = locations[x + 1][y] % BiniaxState.ELEMENT_LIMIT;
            } else if (locations[x + 1][y] % 10 == element) {
                nextElement = locations[x + 1][y] / BiniaxState.ELEMENT_LIMIT;
            }

            if (nextElement != 0) {
                if (state_.getFreeMoves() > 1 ||
                        y < BiniaxState.getHeight() - 1 ||
                        locations[x + 1][y - 1] == 0 ||
                        locations[x + 1][y - 1] / BiniaxState.ELEMENT_LIMIT == nextElement ||
                        locations[x + 1][y - 1] % BiniaxState.ELEMENT_LIMIT == nextElement) {
                    legalActions_.get(0).add(BiniaxAction.EAST);
                }
            }
        }

        if (y != BiniaxState.getHeight() - 1 && (locations[x][y + 1] == 0 ||
                        locations[x][y + 1] / BiniaxState.ELEMENT_LIMIT == element ||
                        locations[x][y + 1] % BiniaxState.ELEMENT_LIMIT == element)) {
            legalActions_.get(0).add(BiniaxAction.SOUTH);
        }

        if (x != 0) {
            int nextElement = 0;
            if (locations[x - 1][y] == 0) {
                nextElement = element;
            } else if (locations[x - 1][y] / BiniaxState.ELEMENT_LIMIT == element) {
                nextElement = locations[x - 1][y] % BiniaxState.ELEMENT_LIMIT;
            } else if (locations[x - 1][y] % BiniaxState.ELEMENT_LIMIT == element) {
                nextElement = locations[x - 1][y] / BiniaxState.ELEMENT_LIMIT;
            }

            if (nextElement != 0) {
                if (state_.getFreeMoves() > 1 ||
                        y < BiniaxState.getHeight() - 1 ||
                        locations[x - 1][y - 1] == 0 ||
                        locations[x - 1][y - 1] / BiniaxState.ELEMENT_LIMIT == nextElement ||
                        locations[x - 1][y - 1] % BiniaxState.ELEMENT_LIMIT == nextElement) {
                    legalActions_.get(0).add(BiniaxAction.WEST);
                }
            }
        }
    }

    public int getNAgents() {
        return N_AGENTS;
    }

    public TurnType getTurnType() {
        return TURN_TYPE;
    }
}
