package com.castlefrog.agl.domains.go;

public final class GoAction {
    private static final int MAX_BOARD_SIZE = 19;
    /** list of all possible go actions. */
    private static GoAction[][] actions_ = generateActions();
    /** special passing action */
    private static GoAction pass_ = new GoAction(-1, -1);

    /** x coordinate. */
	private int x_;
    /** y coordinate. */
	private int y_;
	
	private GoAction(int x, int y) {
		x_ = x;
		y_ = y;
	}

    public static GoAction valueOf(int x, int y) {
        if (x == -1)
            return pass_;
        return actions_[x][y];
    }

    private static GoAction[][] generateActions() {
        GoAction[][] actions = new GoAction[MAX_BOARD_SIZE][MAX_BOARD_SIZE];
        for (int i = 0; i < MAX_BOARD_SIZE; i++)
            for (int j = 0; j < MAX_BOARD_SIZE; j++)
                actions[i][j] = new GoAction(i,j);
        return actions;
    }
	
	public int getX() {
		return x_;
	}
	
	public int getY() {
		return y_;
	}

    public boolean isPass() {
        return x_ == -1;
    }
	
	@Override
	public int hashCode() {
		return x_ + MAX_BOARD_SIZE * y_;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof GoAction))
			return false;
		GoAction action = (GoAction) object;
		return x_ == action.getX() && y_ == action.getY();
	}
	
	@Override
	public String toString() {
		return "(" + x_ + "," + y_ + ")";
	}
}
