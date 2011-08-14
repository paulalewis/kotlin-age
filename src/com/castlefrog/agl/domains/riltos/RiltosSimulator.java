package com.castlefrog.agl.domains.riltos;

import java.util.List;
import java.util.ArrayList;

import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.TurnType;

/**
 *
 */
public class RiltosSimulator implements Simulator<RiltosState, RiltosAction> {
    /** number of agents in game */
	private static int nAgents_;
    /** type of riltos game being played */
    private static GameType gameType_;
    private static TurnType turnType_;

    private RiltosState state_;
    private List<List<RiltosAction>> legalActions_;
    /** indicates winner of game or -1 if no winner */
    private byte winner_;

    public enum GameType {
        HEX_TILE
    }
	
	public RiltosSimulator(int nAgents,
                           GameType gameType,
                           TurnType turnType) {
		nAgents_ = nAgents;
        gameType_ = gameType;
        turnType_ = turnType;
		setState(getInitialState());
	}

	private RiltosSimulator(RiltosState state) {
		state_ = state.clone();
	}
	
    @Override
	public Simulator<RiltosState, RiltosAction> clone() {
		return new RiltosSimulator(state_);
	}

	public void setState(RiltosState state) {
        state_ = state.clone();
	}
	
    /**
     * Income - calculated increase in money
     * Disband - if negative credits disband units to compensate
     * Place New Units
     */
	public void stateTransition(List<RiltosAction> actions) {
		/*Location[][] locations = state_.getLocations();
		
        //Purchase Units
        for (RiltosAction action: actions) {
            for (int i = 0; i < action.placeSize(); i++) {
                int location = action.getPlaceLocation(i);
                int quantity = action.getPlaceQuantity(i);
                int ownerId = locations.get(location).getOwnerId();
                int size = locations.get(location).getSize();
                int income = locations.get(location).getIncome();
                int credits = locations.get(location).getCredits();
                int armySize = locations.get(location).getArmySize();
                //this needs to come from regional credits not location credits
                credits -= quantity;
                //locations.remove(location);
                //locations.add(location, new Territory(ownerId, size, income, credits, armySize));
            }
        }
        //Place Units
        for (RiltosAction action: actions) {
            for (int i = 0; i < action.placeSize(); i++) {
                int location = action.getPlaceLocation(i);
                int quantity = action.getPlaceQuantity(i);
                int ownerId = locations.get(location).getOwnerId();
                int size = locations.get(location).getSize();
                int income = locations.get(location).getIncome();
                int credits = locations.get(location).getCredits();
                int armySize = locations.get(location).getArmySize();
                //need to check if placing agent matches current agent controlling territory
                armySize += quantity;
                //credits -= quantity;
                locations.remove(location);
                locations.add(location, new Territory(ownerId, size, income, credits, armySize));
            }
        }*/
        //Moves - friendly - attacks
        //Battles
        //Income / Disband
		//state_ = new RiltosState(locations, adjacencyMatrix);
	}
    
    public int[] getRewards() {
		int[] rewards = new int[nAgents_];
        if (winner_ != -1) {
            for (int i = 0; i < nAgents_; i += 1) {
                if (winner_ == i)
                    rewards[i] = 1;
                else
                    rewards[i] = -1;
            }
        }
		return rewards;
	}

    public int getReward(int agentId) {
        if (winner_ == -1)
            return 0;
        if (winner_ == agentId)
            return 1;
        return -1;
    }

    public boolean isTerminalState() {
        return winner_ != -1;
    }

    public RiltosState getState() {
        return state_.clone();
    }

    public RiltosState getInitialState() {
        return null;
    }

    public List<List<RiltosAction>> getLegalActions() {
        List<List<RiltosAction>> allLegalActions
            = new ArrayList<List<RiltosAction>>();
        for (int i = 0; i < nAgents_; i += 1)
            allLegalActions.add(getLegalActions(i));
        return allLegalActions;
    }
    
    public boolean hasLegalActions(int agentId) {
        return legalActions_.get(agentId).size() != 0;
    }

	public List<RiltosAction> getLegalActions(int agentId) {
		return null;
	}

	public int getNAgents() {
		return nAgents_;
	}

    public TurnType getTurnType() {
        return turnType_;
    }
}
