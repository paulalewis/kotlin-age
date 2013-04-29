package com.castlefrog.agl.domains.yahtzee;

/**
 * The roll action controls which dice get rolled and which are kept for next
 * state.
 */
public class YahtzeeRollAction extends YahtzeeAction {
    /** Indicated quantity of each die number to not roll again. */
    private byte[] selected_;

    private YahtzeeRollAction(byte[] selected) {
        selected_ = new byte[YahtzeeState.N_VALUES];
        for (int i = 0; i < YahtzeeState.N_VALUES; i++)
            selected_[i] = selected[i];
    }

    public static YahtzeeRollAction valueOf(byte[] selected) {
        return new YahtzeeRollAction(selected);
    }

    public byte[] getSelected() {
        byte[] selected = new byte[selected_.length];
        for (int i = 0; i < selected_.length; i++)
            selected[i] = selected_[i];
        return selected;
    }

    public byte getSelected(int index) {
        return selected_[index];
    }

    @Override
    public int hashCode() {
        int code = 7;
        for (int i = 0; i < selected_.length; i++)
            code = 11 * code + selected_[i];
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof YahtzeeRollAction))
            return false;
        YahtzeeRollAction action = (YahtzeeRollAction) object;
        for (int i = 0; i < YahtzeeState.N_VALUES; i++)
            if (selected_[i] != action.getSelected(i))
                return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("[ ");
        for (int i = 0; i < selected_.length; i++)
            output.append(selected_[i] + " ");
        output.append("]");
        return output.toString();
    }
}
