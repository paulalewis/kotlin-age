package com.castlefrog.agl.domains.go;

import com.castlefrog.agl.Action;

public final class GoAction implements Action {
    private static GoAction[][] actions_ = generateActions();
    /** special passing action */
    private static GoAction pass_ = new GoAction(-1, -1);

	private final int x_;
	private final int y_;
	
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
        int size = GoSimulator.MAX_BOARD_SIZE;
        GoAction[][] actions = new GoAction[size][size];
        for (int i = 0; i < size; i += 1)
            for (int j = 0; j < size; j += 1)
                actions[i][j] = new GoAction(i,j);
        return actions;
    }

    public GoAction copy() {
        return this;
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
		return x_ + GoSimulator.MAX_BOARD_SIZE * y_;
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
