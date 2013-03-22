package com.castlefrog.agl;

public class IllegalActionException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public <S, A> IllegalActionException(A action, S state) {
        super("Illegal action, " + action + ", from state," + state);
    }
}
