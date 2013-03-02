package com.castlefrog.agl.domains.mathax;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
import java.util.LinkedList;
import com.castlefrog.agl.IllegalActionException;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.AbstractSimulator;

/**
 * Mathax is a single agent stochastic domain.
 */
public final class MathaxSimulator
    extends AbstractSimulator<MathaxState, MathaxAction> {
    public static final int BUFFER = 3;
    public static final int MAX_FREE_MOVES = 2;
    public static final int INITIAL_COLORS = 4;
    public static final int MAX_COLORS = 6;
    public static final int COLOR_INCREMENT_INTERVAL = 128;
        
    private static final String SYMBOLS = "0123456789+-*/";

    /**
     * Create a mathax simulator set to an initial state.
     */
    public MathaxSimulator() {
        nAgents_ = 1;
        state_ = getInitialState();
        legalActions_ = new ArrayList<HashSet<MathaxAction>>();
        legalActions_.add(new HashSet<MathaxAction>());
        computeLegalActions();
        rewards_ = new int[1];
    }
    
    private MathaxSimulator(MathaxState state,
                            HashSet<MathaxAction> legalActions,
                            int[] rewards) {
        nAgents_ = 1;
        state_ = state.clone();
        legalActions_ = new ArrayList<HashSet<MathaxAction>>();
        HashSet<MathaxAction> temp = new HashSet<MathaxAction>();
        for (MathaxAction action: legalActions)
            temp.add(action);
        legalActions_.add(temp);
        rewards_ = new int[] {rewards[0]};
    }

    @Override
    public Simulator<MathaxState, MathaxAction> clone() {
        return new MathaxSimulator(state_, legalActions_.get(0), rewards_);
    }

    @Override
    public void setState(MathaxState state) {
        state_ = state.clone();
        computeRewards();
        computeLegalActions();
    }

    public void stateTransition(List<MathaxAction> actions) {
        MathaxAction action = actions.get(0);
        if (!legalActions_.get(0).contains(action))
            throw new IllegalActionException(action, state_);

        int nFreeMoves = state_.getNFreeMoves();
        int nTurns = state_.getNTurns();
        int nColors = state_.getNColors();

        switch (action) {
        case NORTH:
            moveAvatar(0, -1);
            break;
        case EAST:
            moveAvatar(1, 0);
            break;
        case SOUTH:
            moveAvatar(0, 1);
            break;
        case WEST:
            moveAvatar(-1, 0);
            break;
        }

        int x = state_.getAvatarX();
        int y = state_.getAvatarY();

        nFreeMoves -= 1;
        if (nFreeMoves == 0) {
            Element[][] bottomRow = new Element[MathaxState.WIDTH][MathaxState.N_ELEMENTS];
            int nEmptyBottom = MathaxState.WIDTH;
            nFreeMoves = MAX_FREE_MOVES;
            //move avatar up if possible
            moveAvatar(0, -1);
            // save bottom row
            for (int i = 0; i < MathaxState.WIDTH; i += 1) {
                if (!state_.isEmpty(i, MathaxState.HEIGHT - 1) &&
                        !state_.isAvatar(i, MathaxState.HEIGHT - 1)) {
                    bottomRow[i][0] = state_.getLocation(i, MathaxState.HEIGHT - 1)[0];
                    bottomRow[i][1] = state_.getLocation(i, MathaxState.HEIGHT - 1)[1];
                    nEmptyBottom -= 1;
                }
            }
            // move all elements down
            int emptyLocation = (int) (Math.random() * nEmptyBottom);
            for (int i = MathaxState.HEIGHT - 1; i >= 0; i -= 1) {
                for (int j = 0; j < MathaxState.WIDTH; j += 1) {
                    if (i == 0) {
                        if (bottomRow[j][0] != null)
                            state_.setLocation(j, i, bottomRow[j][0], bottomRow[j][1]);
                        else if (emptyLocation == 0) {
                            state_.clearLocation(j, i);
                            emptyLocation -= 1;
                        } else {
                            state_.setLocation(j, i, generateRandomElementPair(nColors));
                            emptyLocation -= 1;
                        }
                    } else {
                        if (state_.isAvatar(j, i - 1))
                            moveAvatar(0, 1);
                        else {
                            Element[] ep = state_.getLocation(j, i - 1);
                            state_.setLocation(j, i, ep);
                        }
                    }
                }
            }
        }
        state_.setNFreeMoves(nFreeMoves);
        nTurns += 1;
        state_.setNTurns(nTurns);
        if (nTurns % COLOR_INCREMENT_INTERVAL == 0 && nColors < MAX_COLORS)
            nColors += 1;
        state_.setNColors(nColors);
        computeRewards();
        computeLegalActions();
    }
    
    private void moveAvatar(int dx, int dy) {
        Element avatar = state_.getAvatar();
        int x = state_.getAvatarX();
        int y = state_.getAvatarY();
        int tx = x + dx;
        int ty = y + dy;
        Element[] ep = state_.getLocation(tx, ty);
        if (state_.isEmpty(tx, ty)) {
            state_.setAvatar(tx, ty, avatar);
            state_.clearLocation(x, y);
        } else if (ep[0].getColor() == avatar.getColor()) {
            state_.addHistory(ep[1]);
            state_.setAvatar(tx, ty, ep[1]);
            state_.clearLocation(x, y);
        } else if (ep[1].getColor() == avatar.getColor()) {
            state_.addHistory(ep[0]);
            state_.setAvatar(tx, ty, ep[0]);
            state_.clearLocation(x, y);
        }
    }

    private Element[] generateRandomElementPair(int nColors) {
        int color1 = (int) (Math.random() * nColors);
        int color2 = (int) (Math.random() * (nColors - 1));
        if (color2 >= color1)
            color2 += 1;
        int symbol1 = (int) (Math.random() * SYMBOLS.length());
        int symbol2 = (int) (Math.random() * SYMBOLS.length());
        return new Element[] {
            new Element(color1, SYMBOLS.charAt(symbol1)),
            new Element(color2, SYMBOLS.charAt(symbol2))
        };
    }
    
    private Element[] generateRandomElementPair(int nColors, int initColor) {
        int color1 = initColor;
        int color2 = (int) (Math.random() * (nColors - 1));
        if (color2 >= color1)
            color2 += 1;
        int symbol1 = (int) (Math.random() * SYMBOLS.length());
        int symbol2 = (int) (Math.random() * SYMBOLS.length());
        return new Element[] {
            new Element(color1, SYMBOLS.charAt(symbol1)),
            new Element(color2, SYMBOLS.charAt(symbol2))
        };
    }

    /**
     * A legal action is one that moves the single element to an empty space or
     * an element pair that contains that element and avoids being pushed off
     * the board.
     * 
     * @return List of legal actions
     */
    private void computeLegalActions() {
        legalActions_.get(0).clear();
        int x = state_.getAvatarX();
        int y = state_.getAvatarY();
        Element avatar = state_.getAvatar();

        if (y != 0 && (state_.isEmpty(x,y - 1)
                || avatar.getColor() == state_.getLocation(x,y - 1)[0].getColor()
                || avatar.getColor() == state_.getLocation(x,y - 1)[1].getColor()))
            legalActions_.get(0).add(MathaxAction.NORTH);

        if (x != MathaxState.WIDTH - 1) {
            int nextColor = -1;
            if (state_.isEmpty(x + 1,y))
                nextColor = avatar.getColor();
            else if (avatar.getColor() == state_.getLocation(x + 1,y)[0].getColor())
                nextColor = state_.getLocation(x + 1,y)[1].getColor();
            else if (avatar.getColor() == state_.getLocation(x + 1,y)[1].getColor())
                nextColor = state_.getLocation(x + 1,y)[0].getColor();

            if (nextColor != -1) {
                if (state_.getNFreeMoves() > 1
                        || y < MathaxState.HEIGHT - 1
                        || state_.isEmpty(x + 1,y - 1)
                        || nextColor == state_.getLocation(x + 1,y - 1)[0].getColor()
                        || nextColor == state_.getLocation(x + 1,y - 1)[1].getColor())
                    legalActions_.get(0).add(MathaxAction.EAST);
            }
        }

        if (y != MathaxState.HEIGHT - 1 && (state_.isEmpty(x,y + 1)
                || avatar.getColor() == state_.getLocation(x,y + 1)[0].getColor()
                || avatar.getColor() == state_.getLocation(x,y + 1)[1].getColor()))
            legalActions_.get(0).add(MathaxAction.SOUTH);

        if (x != 0) {
            int nextColor = -1;
            if (state_.isEmpty(x - 1,y))
                nextColor = avatar.getColor();
            else if (avatar.getColor() == state_.getLocation(x - 1,y)[0].getColor())
                nextColor = state_.getLocation(x - 1,y)[1].getColor();
            else if (avatar.getColor() == state_.getLocation(x - 1,y)[1].getColor())
                nextColor = state_.getLocation(x - 1,y)[0].getColor();

            if (nextColor != -1) {
                if (state_.getNFreeMoves() > 1
                        || y < MathaxState.HEIGHT - 1
                        || state_.isEmpty(x - 1,y - 1)
                        || nextColor == state_.getLocation(x - 1,y - 1)[0].getColor()
                        || nextColor == state_.getLocation(x - 1,y - 1)[1].getColor())
                    legalActions_.get(0).add(MathaxAction.WEST);
            }
        }
    }

    /**
     * Rewards are equal to the value of the longest
     * expression.
     * In the future may add color multiplier
     */
    private void computeRewards() {
        int reward = 0;
        List<Element> history = state_.getHistory();
        Stack<Integer> stack = new Stack<Integer>();
        for (int i = 0; i < state_.getHistorySize(); i += 1) {
            stack.clear();
            for (Element element: history) {
                if (Character.isDigit(element.getSymbol()))
                    stack.push(new Integer(element.getSymbol()));
                else {
                    if (stack.size() != 2)
                        break;
                    switch (element.getSymbol()) {
                    case '+':
                        stack.push(stack.pop() + stack.pop());
                        break;
                    case '-':
                        stack.push(stack.pop() - stack.pop());
                        break;
                    case '\u00D7'://×
                        stack.push(stack.pop() * stack.pop());
                        break;
                    case '\u00f7'://÷
                        stack.push(stack.pop() / stack.pop());
                        break;
                    }
                }
            }
            if (stack.size() == 1) {
                reward = stack.pop();
                break;
            }
        }
        rewards_[0] = reward;
    }
   
    /**
     * The intial state for biniax starts with the player at the bottom of
     * the grid and some random elements above the player.
     *
     * @return an initial state in the biniax domain.
     */
    public MathaxState getInitialState() {
        Element[][][] locations =
                new Element[MathaxState.WIDTH][MathaxState.HEIGHT][MathaxState.N_ELEMENTS];
        int initColor = (int) (Math.random() * INITIAL_COLORS);
        for (int i = 0; i < MathaxState.HEIGHT; i += 1) {
            int emptyLocation = (int) (Math.random() * MathaxState.WIDTH);
            for (int j = 0; j < MathaxState.WIDTH; j += 1)
                if (j != emptyLocation && i < MathaxState.HEIGHT - BUFFER) {
                    if (i == MathaxState.HEIGHT - BUFFER - 1)
                        locations[j][i] = generateRandomElementPair(INITIAL_COLORS, initColor);
                    else
                        locations[j][i] = generateRandomElementPair(INITIAL_COLORS);
                }
        }
        locations[MathaxState.WIDTH / 2][MathaxState.HEIGHT - 1][0] = new Element(initColor, '0');
        return new MathaxState(locations, new LinkedList<Element>(), MAX_FREE_MOVES, 0, INITIAL_COLORS);
    }

    public MathaxState getState() {
        return state_.clone();
    }
}
