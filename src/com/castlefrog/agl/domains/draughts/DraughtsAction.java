package com.castlefrog.agl.domains.draughts;

import com.castlefrog.agl.Action;

public final class DraughtsAction implements Action {
	private int fx_;
	private int fy_;
    private int tx_;
    private int ty_;
	
	private DraughtsAction(int fx, int fy,
                           int tx, int ty) {
		fx_ = fx;
		fy_ = fy;
		tx_ = tx;
		ty_ = ty;
	}

    public static DraughtsAction valueOf(int fx, int fy,
                                         int tx, int ty) {
        return new DraughtsAction(fx, fy, tx, ty);
    }

    public DraughtsAction copy() {
        return this;
    }

	public int getFX() {
		return fx_;
	}
	
	public int getFY() {
		return fy_;
	}
	
    public int getTX() {
		return tx_;
	}
	
	public int getTY() {
		return ty_;
	}

	@Override
	public int hashCode() {
        int code = 7 + fx_;
        code = 11 * code + fy_;
        code = 11 * code + tx_;
        code = 11 * code + ty_;
		return code;
	}
	
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DraughtsAction))
            return false;
        DraughtsAction action = (DraughtsAction) object;
        return fx_ == action.getFX() && fy_ == action.getFY()
               && tx_ == action.getTX() && ty_ == action.getTY();
    }

    @Override
    public String toString() {
        return "(" + fx_ + "," + fy_ + "-" + tx_ + "," + ty_ + ")";
    }
}
