package com.castlefrog.agl.domains.riltos;

import java.util.List;

public final class Region {
    /** indicates agent in control of region */
    private byte agentId_;
    /** all connected locations in region */
    private List<Location> locations_;
    /** credits stored in region */
    private int credits_;
	
	public Region(int agentId,
                  List<Location> locations,
                  int credits) {
		agentId_ = (byte) agentId;
		locations_ = locations;
		credits_ = credits;
	}
	
	public int getAgentId() {
		return agentId_;
	}

    public List<Location> getLocations() {
        return locations_;
    }
	
	public int getCredits() {
		return credits_;
	}
	
    public int getSize() {
		return locations_.size();
	}

    public void generateIncome() {
        for (Location location: locations_)
            credits_ += location.getIncome();
    }

    public void payMaintenance() {
        for (Location location: locations_)
            credits_ -= location.getArmySize();
    }

    public void disbandArmy() {
        if (credits_ < 0) {
            //reduce army size by negative credits
            credits_ = 0;
        }
    }
}
