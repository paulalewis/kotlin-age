package com.castlefrog.agl.domains.riltos;

import java.util.List;
import java.util.ArrayList;

public final class RiltosAction implements Cloneable {
    /**
     * list of placement actions
     **/
    private List<PlaceAction> placeActions_;
    /**
     * list of movement actions
     **/
    private List<MoveAction> moveActions_;

    public RiltosAction() {
        placeActions_ = new ArrayList<PlaceAction>();
        moveActions_ = new ArrayList<MoveAction>();
    }

    private RiltosAction(List<PlaceAction> placeActions,
                         List<MoveAction> moveActions) {
        this();
        for (PlaceAction action: placeActions)
            placeActions_.add(action);
        for (MoveAction action: moveActions)
            moveActions_.add(action);
    }

    @Override
    public RiltosAction clone() {
        return new RiltosAction(placeActions_, moveActions_);
    }

    public PlaceAction getPlaceAction(int index) {
        return placeActions_.get(index);
    }

    public MoveAction getMoveAction(int index) {
        return moveActions_.get(index);
    }

    public int getPlaceSize() {
        return placeActions_.size();
    }

    public int getMoveSize() {
        return moveActions_.size();
    }

    public void addPlaceAction(PlaceAction placeAction) {
        //when a new placement action is added it checks
        //for other placement actions with equal x and y coordinates
        //if one exists then this placement action adds its quantity
        //value to that of the other placement action at this location
        //the same is true for a move action that has the same fx,fy,tx and ty.
        placeActions_.add(placeAction);
    }

    public void addMoveAction(MoveAction moveAction) {
        moveActions_.add(moveAction);
    }

    @Override
    public int hashCode() {
        int code = 7;
        for (PlaceAction action: placeActions_)
            code = code * 11 + action.hashCode();
        for (MoveAction action: moveActions_)
            code = code * 11 + action.hashCode();
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RiltosAction))
            return false;
        RiltosAction action = (RiltosAction) object;
        for (int i = 0; i < placeActions_.size(); i += 1)
            if (placeActions_.get(i).equals(action.getPlaceAction(i)) == false)
                return false;
        for (int i = 0; i < moveActions_.size(); i += 1)
            if (moveActions_.get(i).equals(action.getMoveAction(i)) == false)
                return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("[ ");
        for (PlaceAction action: placeActions_)
            output.append(action.toString() + " ");
        for (MoveAction action: moveActions_)
            output.append(action.toString() + " ");
        output.append("]");
        return output.toString();
    }
}
