package com.castlefrog.agl

/**
 * Thrown by [Simulator.stateTransition] when the actions list is invalid for the
 * given state: wrong length, missing action for the player to move, null when an
 * action is required, or an action that is not legal.
 *
 * Callers implementing generic agent/simulator loops can catch this type alone
 * for “illegal transition” errors across all domains.
 */
class IllegalActionException(message: String, cause: Throwable? = null) :
    IllegalArgumentException(message, cause)
