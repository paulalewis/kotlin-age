package com.castlefrog.agl;

/**
 * Indicates the type of turn
 * mechanic the game uses.
 */
public enum TurnType {
    /** next player to move decided at random */
    RANDOM,
    /** players move in order */
    SEQUENTIAL,
    /** player move order random */
    RANDOM_ORDER,
    /** player move order decided by bidding */
    BIDDING,
    /** players move at same time */
    SIMULTANEOUS
    //SIMULTANEOUS_PRIORITY,
}
