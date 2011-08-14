package com.castlefrog.agl.domains.biniax;

import java.util.ArrayList;
import java.util.List;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.TurnType;

/**
 * Biniax is a single agent stochastic domain.
 */
public final class BiniaxSimulator
    implements Simulator<BiniaxState, BiniaxAction> {
    private static final int N_AGENTS = 1;
    private static final int BUFFER = 3;
    private static final byte N_FREE_MOVES = 2;
    private static final int INITIAL_ELEMENTS = 4;
    private static final int ELEMENT_INCREMENT_INTERVAL = 32;
    private static final int ELEMENT_LIMIT = BiniaxState.getMaxElements() + 1;
    private static final double IMPASSIBLE_CHANCE = 0;
    private static final TurnType TURN_TYPE = TurnType.SEQUENTIAL_ORDER;

    /** Current state of simulator. */
    private BiniaxState state_;

    /** List of legal actions from current state. */
    private List<BiniaxAction> legalActions_;

    /** The number of distinct element types that can be generated */
    private int nElementTypes_;
    
    /**
     * Create a Biniax simulator.
     */
    public BiniaxSimulator() {
        state_ = getInitialState();
        legalActions_ = new ArrayList<BiniaxAction>();
        computeLegalActions();
    }
    
    /**
     * Create a Biniax simulator and set the state.
     * 
     * @param state
     *            simulator set to given biniax state.
     * @param nElementTypes
     *            number of elements possible in element pairs.
     * @param turns
     *            the total number of turns game has gone on for.
     */
    private BiniaxSimulator(BiniaxState state,
                            List<BiniaxAction> legalActions,
                            int nElementTypes) {
        state_ = state;
        legalActions_ = new ArrayList<BiniaxAction>();
        for (BiniaxAction action: legalActions)
            legalActions_.add(action);
        nElementTypes_ = nElementTypes;
    }

    @Override
    public Simulator<BiniaxState, BiniaxAction> clone() {
        return new BiniaxSimulator(state_, legalActions_, nElementTypes_);
    }

    public void setState(BiniaxState state) {
        state_ = state;
        computeLegalActions();
    }

    public void stateTransition(List<BiniaxAction> actions) {
        BiniaxAction action = actions.get(0);
        if (!legalActions_.contains(action))
            throw new IllegalActionException(action, state_);

        byte[][] locations = state_.getLocations();
        byte freeMoves = state_.getFreeMoves();
        int[] elementLocation = getElementLocation();
        int x = elementLocation[0];
        int y = elementLocation[1];
        byte element = state_.getLocation(x, y);

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
        }

        if (locations[x][y] / ELEMENT_LIMIT == element)
            element = (byte) (locations[x][y] % ELEMENT_LIMIT);
        else if (locations[x][y] % ELEMENT_LIMIT == element)
            element = (byte) (locations[x][y] / ELEMENT_LIMIT);
        locations[x][y] = element;

        freeMoves--;
        if (freeMoves == 0) {
            freeMoves = N_FREE_MOVES;
            // Move all elements down
            int emptyLocation = (int) (Math.random() * BiniaxState.getWidth());
            for (int i = BiniaxState.getHeight() - 1; i >= 0; i--) {
                for (int j = 0; j < BiniaxState.getWidth(); j++) {
                    if (i == 0) {
                        if (j != emptyLocation) {
                            if (Math.random() < IMPASSIBLE_CHANCE)
                                locations[j][i] = -1;
                            else
                                locations[j][i] = (byte) generateRandomElementPair();
                        } else
                            locations[j][i] = 0;
                    } else
                        locations[j][i] = locations[j][i - 1];
                }
            }
            // Move element back up if possible
            if (locations[x][y] == 0) {
                locations[x][y] = element;
                if (y < BiniaxState.getHeight() - 1)
                    locations[x][y + 1] = 0;
            } else if (locations[x][y] / ELEMENT_LIMIT == element) {
                locations[x][y] = (byte) (locations[x][y] % ELEMENT_LIMIT);
                if (y < BiniaxState.getHeight() - 1)
                    locations[x][y + 1] = 0;
            } else if (locations[x][y] % ELEMENT_LIMIT == element) {
                locations[x][y] = (byte) (locations[x][y] / ELEMENT_LIMIT);
                if (y < BiniaxState.getHeight() - 1)
                    locations[x][y + 1] = 0;
            }
        }
        int nTurns = state_.getNTurns() + 1;
        if (nTurns % ELEMENT_INCREMENT_INTERVAL == 0
                && nElementTypes_ < BiniaxState.getMaxElements())
            nElementTypes_ += 1;
        state_ = new BiniaxState(locations, freeMoves, nTurns);
        computeLegalActions();
    }

    /**
     * Creates a random element pair of dissimilar elements The elements are
     * always in order from smallest to largest
     * 
     * @return int of random values from 0 to numElements_ - 1
     */
    private int generateRandomElementPair() {
        int element1 = ((int) (Math.random() * nElementTypes_)) + 1;
        int element2 = ((int) (Math.random() * (nElementTypes_ - 1))) + 1;
        if (element1 <= element2)
            return element1 * ELEMENT_LIMIT + element2 + 1;
        else
            return element2 * ELEMENT_LIMIT + element1;
    }

    private int[] getElementLocation() {
        for (int i = 0; i < BiniaxState.getWidth(); i++)
            for (int j = 0; j < BiniaxState.getHeight(); j++)
                if (state_.getLocation(i, j) > 0
                        && state_.getLocation(i, j) < ELEMENT_LIMIT)
                    return new int[] { i, j };
        throw new IllegalStateException("Element does not exist");
    }

    /**
     * A legal action is one that moves the single element to an empty space or
     * an element pair that contains that element and avoids being pushed off
     * the board.
     * 
     * @return List of legal actions
     */
    private void computeLegalActions() {
        legalActions_.clear();
        int[] elementLocation = getElementLocation();
        int x = elementLocation[0];
        int y = elementLocation[1];
        int element = state_.getLocation(x, y);
        byte[][] locations = state_.getLocations();

        if (y != 0
                && (locations[x][y - 1] == 0
                        || locations[x][y - 1] / ELEMENT_LIMIT == element || locations[x][y - 1]
                        % ELEMENT_LIMIT == element))
            legalActions_.add(BiniaxAction.NORTH);

        if (x != BiniaxState.getWidth() - 1) {
            int nextElement = 0;
            if (locations[x + 1][y] == 0)
                nextElement = element;
            else if (locations[x + 1][y] / ELEMENT_LIMIT == element)
                nextElement = locations[x + 1][y] % ELEMENT_LIMIT;
            else if (locations[x + 1][y] % 10 == element)
                nextElement = locations[x + 1][y] / ELEMENT_LIMIT;

            if (nextElement != 0) {
                if (state_.getFreeMoves() > 1
                        || y < BiniaxState.getHeight() - 1
                        || locations[x + 1][y - 1] == 0
                        || locations[x + 1][y - 1] / ELEMENT_LIMIT == nextElement
                        || locations[x + 1][y - 1] % ELEMENT_LIMIT == nextElement)
                    legalActions_.add(BiniaxAction.EAST);
            }
        }

        if (y != BiniaxState.getHeight() - 1
                && (locations[x][y + 1] == 0
                        || locations[x][y + 1] / ELEMENT_LIMIT == element || locations[x][y + 1]
                        % ELEMENT_LIMIT == element))
            legalActions_.add(BiniaxAction.SOUTH);

        if (x != 0) {
            int nextElement = 0;
            if (locations[x - 1][y] == 0)
                nextElement = element;
            else if (locations[x - 1][y] / ELEMENT_LIMIT == element)
                nextElement = locations[x - 1][y] % ELEMENT_LIMIT;
            else if (locations[x - 1][y] % ELEMENT_LIMIT == element)
                nextElement = locations[x - 1][y] / ELEMENT_LIMIT;

            if (nextElement != 0) {
                if (state_.getFreeMoves() > 1
                        || y < BiniaxState.getHeight() - 1
                        || locations[x - 1][y - 1] == 0
                        || locations[x - 1][y - 1] / ELEMENT_LIMIT == nextElement
                        || locations[x - 1][y - 1] % ELEMENT_LIMIT == nextElement)
                    legalActions_.add(BiniaxAction.WEST);
            }
        }
    }
   
    /**
     * The intial state for biniax starts with the player at the bottom of
     * the grid and some random elements above the player.
     *
     * @return an initial state in the biniax domain.
     */
    public BiniaxState getInitialState() {
        nElementTypes_ = INITIAL_ELEMENTS;
        byte[][] locations = new byte[BiniaxState.getWidth()][BiniaxState
                .getHeight()];
        for (int i = 0; i < BiniaxState.getHeight(); i++) {
            int emptyLocation = (int) (Math.random() * BiniaxState.getWidth());
            for (int j = 0; j < BiniaxState.getWidth(); j++)
                if (j != emptyLocation && i < BiniaxState.getHeight() - BUFFER) {
                    locations[j][i] = (byte) generateRandomElementPair();
                    if (i == BiniaxState.getHeight() - BUFFER - 1)
                        locations[j][i] = (byte) (locations[j][i]
                                % ELEMENT_LIMIT + ELEMENT_LIMIT);
                }
        }
        locations[BiniaxState.getWidth() / 2][BiniaxState.getHeight() - 1] = 1;
        return new BiniaxState(locations, N_FREE_MOVES, 0);
    }

    public int[] getRewards() {
        return new int[] {1};
    }

    public int getReward(int agentId) {
        return 1;
    }

    public boolean isTerminalState() {
        return legalActions_.size() == 0;
    }

    public BiniaxState getState() {
        return state_;
    }
    
    public List<List<BiniaxAction>> getLegalActions() {
        List<List<BiniaxAction>> allLegalActions
            = new ArrayList<List<BiniaxAction>>();
        allLegalActions.add(getLegalActions(0));
        return allLegalActions;
    }

    public List<BiniaxAction> getLegalActions(int agentId) {
        List<BiniaxAction> legalActions
            = new ArrayList<BiniaxAction>();
        for (BiniaxAction action: legalActions_)
            legalActions.add(action);
        return legalActions;
    }

    public boolean hasLegalActions(int agentId) {
        return legalActions_.size() != 0;
    }

    public int getNAgents() {
        return N_AGENTS;
    }

    public TurnType getTurnType() {
        return TURN_TYPE;
    }
}
