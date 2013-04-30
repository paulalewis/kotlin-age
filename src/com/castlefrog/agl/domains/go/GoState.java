package com.castlefrog.agl.domains.go;

import com.castlefrog.agl.State;

public final class GoState implements State {
	private byte[][] locations_;
	private byte agentTurn_;
    private byte passFlag_;
	
	public GoState(byte[][] locations,
                   int agentTurn,
                   int passFlag) {
		locations_ = locations;
		agentTurn_ = (byte) agentTurn;
        passFlag_ = (byte) passFlag;
	}

    public GoState copy() {
        return this;
    }

    public byte[][] getLocations() {
		byte[][] locations = new byte[locations_.length][locations_.length];
		for (int i = 0; i < locations_.length; i++)
			for (int j = 0; j < locations_.length; j++)
				locations[i][j] = locations_[i][j];
		return locations;
	}
	
	public int getLocation(int x, int y) {
		return locations_[x][y];
	}
	
	public int getAgentTurn() {
		return agentTurn_;
	}

    public int getPassFlag() {
        return passFlag_;
    }
	
	public int getSize() {
		return locations_.length;
	}
	
	@Override
	public int hashCode() {
        int code = 7 + passFlag_;
        for (byte[] row: locations_)
            for (byte location: row)
                code = 11 * code + location;
        return code;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof GoState))
			return false;
		GoState state = (GoState) object;
		for (int i = 0; i < locations_.length; i++)
			for (int j = 0; j < locations_.length; j++)
				if (locations_[i][j] != state.getLocation(i, j))
					return false;
		return passFlag_ == state.getPassFlag();
	}
	
	@Override
	public String toString() {
		final String PIECES = " XO";
		StringBuffer output = new StringBuffer();
		for (int i = 0; i < locations_.length + 2; i++)
			output.append("-");
		for (int i = 0; i < locations_.length; i++) {
			output.append("|");
			for (int j = 0; j < locations_.length; j++)
				output.append(PIECES.charAt(locations_[i][j]));
			output.append("|\n");
		}
		for (int i = 0; i < locations_.length + 2; i++)
			output.append("-");
		return output.toString();
	}
}
