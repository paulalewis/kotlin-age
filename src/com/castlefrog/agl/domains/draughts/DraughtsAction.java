package com.castlefrog.agl.domains.draughts;

import com.castlefrog.agl.Action;

public final class DraughtsAction implements Action {
    private int fx;
    private int fy;
    private int tx;
    private int ty;

    private DraughtsAction(int fx, int fy,
                           int tx, int ty) {
        this.fx = fx;
        this.fy = fy;
        this.tx = tx;
        this.ty = ty;
    }

    public static DraughtsAction valueOf(int fx, int fy,
                                         int tx, int ty) {
        return new DraughtsAction(fx, fy, tx, ty);
    }

    public DraughtsAction copy() {
        return this;
    }

    public int getFX() {
        return fx;
    }

    public int getFY() {
        return fy;
    }

    public int getTX() {
        return tx;
    }

    public int getTY() {
        return ty;
    }

    @Override
    public int hashCode() {
        int code = 7 + fx;
        code = 11 * code + fy;
        code = 11 * code + tx;
        code = 11 * code + ty;
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DraughtsAction)) {
            return false;
        }
        DraughtsAction action = (DraughtsAction) object;
        return fx == action.getFX() && fy == action.getFY() &&
               tx == action.getTX() && ty == action.getTY();
    }

    @Override
    public String toString() {
        return "(" + fx + "," + fy + "-" + tx + "," + ty + ")";
    }
}
