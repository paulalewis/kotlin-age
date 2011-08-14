package com.castlefrog.agl;

public class IllegalActionException extends IllegalArgumentException {
    public <S, A> IllegalActionException(A action, S state) {
        super("Illegal action, " + action + ", from state," + state);
    }
}
