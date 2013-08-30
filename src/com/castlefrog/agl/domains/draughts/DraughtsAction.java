package com.castlefrog.agl.domains.draughts;

import com.castlefrog.agl.Action;

public final class DraughtsAction implements Action {
    private int xi_;
    private int yi_;
    private int xf_;
    private int yf_;

    private DraughtsAction(int xi, int yi, int xf, int yf) {
        xi_ = xi;
        yi_ = yi;
        xf_ = xf;
        yf_ = yf;
    }

    public static DraughtsAction valueOf(int xi, int yi, int xf, int yf) {
        return new DraughtsAction(xi, yi, xf, yf);
    }

    public DraughtsAction copy() {
        return this;
    }

    public int getXi() {
        return xi_;
    }

    public int getYi() {
        return yi_;
    }

    public int getXf() {
        return xf_;
    }

    public int getYf() {
        return yf_;
    }

    @Override
    public int hashCode() {
        int code = 7 + xi_;
        code = 11 * code + yi_;
        code = 11 * code + xf_;
        code = 11 * code + yf_;
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DraughtsAction)) {
            return false;
        }
        DraughtsAction action = (DraughtsAction) object;
        return xi_ == action.getXi() && yi_ == action.getYi() &&
               xf_ == action.getXf() && yf_ == action.getYf();
    }

    @Override
    public String toString() {
        return "(" + xi_ + "," + yi_ + "-" + xf_ + "," + yf_ + ")";
    }
}
