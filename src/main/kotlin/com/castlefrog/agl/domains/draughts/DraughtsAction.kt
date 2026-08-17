package com.castlefrog.agl.domains.draughts

import com.castlefrog.agl.Action

/**
 * A single step move or jump from one dark square to another.
 * Multi-jumps are played as successive actions while [DraughtsState.mustContinueFromX]
 * forces the same piece to keep capturing. Kings may slide or jump any distance.
 */
data class DraughtsAction(
    val fromX: Int,
    val fromY: Int,
    val toX: Int,
    val toY: Int
) : Action {

    override fun toString(): String = "($fromX,$fromY)->($toX,$toY)"
}
