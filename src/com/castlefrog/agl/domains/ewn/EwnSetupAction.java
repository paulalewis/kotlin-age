package com.castlefrog.agl.domains.ewn;

import java.util.Arrays;

public final class EwnSetupAction implements EwnAction {
    private static final int NUMBER_OF_PIECES = 6;

    private byte[] values_;

    public EwnSetupAction(byte[] values) {
        boolean[] used = new boolean[NUMBER_OF_PIECES];
        values_ = new byte[NUMBER_OF_PIECES];
        for (int i = 0; i < values_.length; i++) {
            values_[i] = values[i];
            if (used[values[i] - 1]) {
                throw new IllegalArgumentException("Illegal Setup Action: " + Arrays.toString(values));
            }
            used[values[i] - 1] = true;
        }
    }

    public EwnSetupAction copy() {
        return this;
    }

    public byte getValue(int index) {
        return values_[index];
    }

    @Override
    public int hashCode() {
        int code = 7;
        for (int value : values_) {
            code = 11 * code + value;
        }
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EwnSetupAction)) {
            return false;
        }
        EwnSetupAction action = (EwnSetupAction) object;
        for (int i = 0; i < NUMBER_OF_PIECES; i++) {
            if (values_[i] != action.getValue(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("[ ");
        for (int i = 0; i < NUMBER_OF_PIECES; i++) {
            output.append(values_[i]).append(" ");
        }
        output.append("]");
        return output.toString();
    }
}
