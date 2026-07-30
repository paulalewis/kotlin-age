package com.castlefrog.agl.domains.chess

import com.castlefrog.agl.Action

/**
 * A chess move from (fromX, fromY) to (toX, toY).
 * Coordinates are 0-based files (x) and ranks (y); white's back rank is y = 0.
 *
 * @param promotion piece type to promote a pawn to ([ChessState.KNIGHT]..[ChessState.QUEEN]),
 *        or 0 if not a promotion move
 */
data class ChessAction(
    val fromX: Int,
    val fromY: Int,
    val toX: Int,
    val toY: Int,
    val promotion: Int = 0
) : Action {

    override fun toString(): String {
        val base = "${file(fromX)}${fromY + 1}${file(toX)}${toY + 1}"
        return if (promotion != 0) base + promotionChar(promotion) else base
    }

    companion object {
        private fun file(x: Int): Char = ('a' + x)

        private fun promotionChar(p: Int): Char = when (kotlin.math.abs(p)) {
            ChessState.KNIGHT -> 'n'
            ChessState.BISHOP -> 'b'
            ChessState.ROOK -> 'r'
            ChessState.QUEEN -> 'q'
            else -> '?'
        }
    }
}
