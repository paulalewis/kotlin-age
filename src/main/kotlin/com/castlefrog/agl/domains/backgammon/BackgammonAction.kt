package com.castlefrog.agl.domains.backgammon

import com.castlefrog.agl.Action
import java.util.HashSet

data class BackgammonAction(val moves: Set<BackgammonMove>) : Action<BackgammonAction> {

    override fun copy(): BackgammonAction {
        return copy(moves = HashSet<BackgammonMove>(this.moves))
    }

    override fun toString(): String {
        val output = StringBuilder()
        output.append("[ ")
        for (move in moves) {
            output.append(move).append(" ")
        }
        output.append("]")
        return output.toString()
    }
}
