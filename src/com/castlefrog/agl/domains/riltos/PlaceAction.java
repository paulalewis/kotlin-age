package com.castlefrog.agl.domains.riltos;

public final class PlaceAction {
    /** x coordinate */
    private byte x_;
    /** y coordinate */
    private byte y_;
    /** number of units placed */
    private byte quantity_;

    /**
     * Constructor used by static factory
     * method to generate actions.
     */
    private PlaceAction(int x, int y, int quantity) {
        x_ = (byte) x;
        y_ = (byte) y;
        quantity_ = (byte) quantity;
    }

    /**
     * Used to retrieve a pre generated action.
     * @param x x-coord
     * @param y y-coord
     * @param quantity number of units placed
     * @return a new PlaceAction object
     */
    public static PlaceAction valueOf(int x, int y, int quantity) {
        return new PlaceAction(x, y, quantity);
    }

    public int getX() {
        return x_;
    }

    public int getY() {
        return y_;
    }

    public int getQuantity() {
        return quantity_;
    }

    @Override
    public int hashCode() {
        int code = 7 + x_;
        code = 11 * code + y_;
        code = 11 * code + quantity_;
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PlaceAction))
            return false;
        PlaceAction action = (PlaceAction) object;
        return x_ == action.getX()
            && y_ == action.getY()
            && quantity_ == action.getQuantity();
    }

    @Override
    public String toString() {
        return quantity_ + "(" + x_ + "," + y_ + ")";
    }
}
