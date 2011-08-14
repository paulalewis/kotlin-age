package com.castlefrog.agl.domains.riltos;

public final class Location /*implements Cloneable*/ {
    public static final int MAX_INCOME = 3;
    /** indicates agent in control of territory (-1 is neutral) */
    //private byte agentId_;
    /** indicates max production */
    private int size_;
    /** number of credits earned each turn */
    private int income_;
    /** number of army units in territory */
    private int armySize_;
	
	public Location(//int agentId,
                    int size,
                    int income,
                    //int credits,
                    int armySize) {
		//agentId_ = (byte) agentId;
		size_ = size;
		income_ = income;
		//credits_ = credits;
		armySize_ = armySize;
	}

    /*@Override
    public Location clone() {
        return new Location(agentId_, size_, income_, credits_, armySize_);
    }*/
	
	//public int getAgentId() {
	//	return agentId_;
	//}
	
	public int getSize() {
		return size_;
	}
	
	public int getIncome() {
		return income_;
	}
	
	//public int getCredits() {
//		return credits_;
////	}
	
	public int getArmySize() {
		return armySize_;
	}
}
