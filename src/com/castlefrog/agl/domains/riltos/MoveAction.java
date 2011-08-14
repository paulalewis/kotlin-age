package com.castlefrog.agl.domains.riltos;

public final class MoveAction {
    /** from x coordinate */
    private byte fx_;
    /** from y coordinate */
    private byte fy_;
    /** to x coordinate */
    private byte tx_;
    /** to y coordinate */
    private byte ty_;
    /** number of units placed */
    private byte quantity_;

    /**
     * Constructor used by static factory
     * method to generate actions.
     */
    private MoveAction(int fx,
                       int fy,
                       int tx,
                       int ty,
                       int quantity) {
        fx_ = (byte) fx;
        fy_ = (byte) fy;
        tx_ = (byte) tx;
        ty_ = (byte) ty;
        quantity_ = (byte) quantity;
    }

    /**
     * Used to retrieve a pre generated action.
     * @param x x-coord
     * @param y y-coord
     * @param quantity number of units placed
     * @return a new PlaceAction object
     */
    public static MoveAction valueOf(int fx,
                                     int fy,
                                     int tx,
                                     int ty,
                                     int quantity) {
        return new MoveAction(fx, fy, tx, ty, quantity);
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

    public int getQuantity() {
        return quantity_;
    }

    @Override
    public int hashCode() {
        int code = 7 + fx_;
        code = 11 * code + fy_;
        code = 11 * code + tx_;
        code = 11 * code + ty_;
        code = 11 * code + quantity_;
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MoveAction))
            return false;
        MoveAction action = (MoveAction) object;
        return fx_ == action.getFX()
            && fy_ == action.getFY()
            && tx_ == action.getTX()
            && ty_ == action.getTY()
            && quantity_ == action.getQuantity();
    }

    @Override
    public String toString() {
        return quantity_ + "(" + fx_ + "," + fy_ + ")-"
                         + "(" + tx_ + "," + ty_ + ")";
    }
}
