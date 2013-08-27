package com.castlefrog.agl.domains.ewn;

import java.util.ArrayList;
import java.util.List;

public final class EwnMoveAction implements EwnAction {
    /** List of all possible moves. */
    private static List<List<List<EwnMoveAction>>> moveActions_ = generateMoveActions();

    private int xLocation_;
    private int yLocation_;
    private Direction direction_;

    public enum Direction {
        NORTH, EAST, SE, SOUTH, WEST, NW
    }

    private EwnMoveAction(int xLocation, int yLocation, Direction direction) {
        xLocation_ = xLocation;
        yLocation_ = yLocation;
        direction_ = direction;
    }

    public static EwnMoveAction valueOf(int xLocation, int yLocation,
            Direction direction) {
        return moveActions_.get(xLocation).get(yLocation).get(
                direction.ordinal());
    }

    public EwnMoveAction copy() {
        return this;
    }

    /**
     * Generates a list of all possible moves. It also adds a few impossible
     * moves but not too many worth worrying about. Total moves in list is 150
     * while total possible moves is 126.
     * @return list of all possible moves.
     */
    private static List<List<List<EwnMoveAction>>> generateMoveActions() {
        List<List<List<EwnMoveAction>>> moveActions = new ArrayList<List<List<EwnMoveAction>>>();
        for (int i = 0; i < EwnState.getSize(); i++) {
            moveActions.add(new ArrayList<List<EwnMoveAction>>());
            for (int j = 0; j < EwnState.getSize(); j++) {
                moveActions.get(i).add(new ArrayList<EwnMoveAction>());
                for (int k = 0; k < Direction.values().length; k++) {
                    moveActions.get(i).get(j).add(
                            new EwnMoveAction(i, j, Direction.values()[k]));
                }
            }
        }
        return moveActions;
    }

    public int getXLocation() {
        return xLocation_;
    }

    public int getYLocation() {
        return yLocation_;
    }

    public Direction getDirection() {
        return direction_;
    }

    @Override
    public int hashCode() {
        int code = 7 + xLocation_;
        code = 11 * code + yLocation_;
        code = 11 * code + direction_.ordinal();
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EwnMoveAction)) {
            return false;
        }
        EwnMoveAction action = (EwnMoveAction) object;
        return direction_ == action.getDirection() &&
               xLocation_ == action.getXLocation() &&
               yLocation_ == action.getYLocation();
    }

    @Override
    public String toString() {
        return "(" + xLocation_ + "," + yLocation_ + ") " + direction_.toString();
    }
}
