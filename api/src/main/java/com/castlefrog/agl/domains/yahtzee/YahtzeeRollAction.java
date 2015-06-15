package com.castlefrog.agl.domains.yahtzee;

/**
 * The roll action controls which dice get rolled and which are kept for next
 * state.
 */
public final class YahtzeeRollAction implements YahtzeeAction {
    /** Indicated quantity of each die number to not roll again. */
    private final byte[] selected_;

    private YahtzeeRollAction(byte[] selected) {
        selected_ = new byte[YahtzeeState.N_VALUES];
        System.arraycopy(selected, 0, selected_, 0, YahtzeeState.N_VALUES);
    }

    public static YahtzeeRollAction valueOf(byte[] selected) {
        return new YahtzeeRollAction(selected);
    }

    public YahtzeeRollAction copy() {
        return this;
    }

    public byte[] getSelected() {
        byte[] selected = new byte[selected_.length];
        System.arraycopy(selected_, 0, selected, 0, selected_.length);
        return selected;
    }

    public byte getSelected(int index) {
        return selected_[index];
    }

    @Override
    public int hashCode() {
        int code = 7;
        for (byte value : selected_) {
            code = 11 * code + value;
        }
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof YahtzeeRollAction)) {
            return false;
        }
        YahtzeeRollAction action = (YahtzeeRollAction) object;
        for (int i = 0; i < YahtzeeState.N_VALUES; i++) {
            if (selected_[i] != action.getSelected(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("[ ");
        for (byte value : selected_) {
            output.append(value).append(" ");
        }
        output.append("]");
        return output.toString();
    }
}
