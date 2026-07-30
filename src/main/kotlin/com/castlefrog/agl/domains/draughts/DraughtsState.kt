package com.castlefrog.agl.domains.draughts

import com.castlefrog.agl.State

/**
 * English draughts / American checkers on an 8×8 board.
 *
 * Pieces occupy dark squares only (`(x + y) % 2 == 1`).
 * Values: empty 0; man/king for black (side to move first) and white.
 *
 * When [mustContinueFromX] is non-negative, that piece must continue a multi-jump.
 */
class DraughtsState(
    val board: Array<ByteArray> = initialBoard(),
    var agentTurn: Byte = TURN_BLACK,
    var mustContinueFromX: Int = -1,
    var mustContinueFromY: Int = -1
) : State<DraughtsState> {

    companion object {
        const val SIZE = 8
        const val EMPTY: Byte = 0
        const val BLACK_MAN: Byte = 1
        const val BLACK_KING: Byte = 2
        const val WHITE_MAN: Byte = 3
        const val WHITE_KING: Byte = 4
        const val TURN_BLACK: Byte = 0
        const val TURN_WHITE: Byte = 1

        fun initialBoard(): Array<ByteArray> {
            val board = Array(SIZE) { ByteArray(SIZE) }
            for (y in 0 until SIZE) {
                for (x in 0 until SIZE) {
                    if ((x + y) % 2 == 0) continue // light squares unused
                    when (y) {
                        in 0..2 -> board[x][y] = BLACK_MAN
                        in 5..7 -> board[x][y] = WHITE_MAN
                    }
                }
            }
            return board
        }

        fun isDarkSquare(x: Int, y: Int): Boolean = (x + y) % 2 == 1
    }

    override fun copy(): DraughtsState {
        val copyBoard = Array(SIZE) { board[it].copyOf() }
        return DraughtsState(copyBoard, agentTurn, mustContinueFromX, mustContinueFromY)
    }

    fun isOnBoard(x: Int, y: Int): Boolean = x in 0 until SIZE && y in 0 until SIZE

    fun get(x: Int, y: Int): Byte = board[x][y]

    fun set(x: Int, y: Int, value: Byte) {
        board[x][y] = value
    }

    fun isBlackPiece(p: Byte): Boolean = p == BLACK_MAN || p == BLACK_KING

    fun isWhitePiece(p: Byte): Boolean = p == WHITE_MAN || p == WHITE_KING

    fun isKing(p: Byte): Boolean = p == BLACK_KING || p == WHITE_KING

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DraughtsState) return false
        if (agentTurn != other.agentTurn) return false
        if (mustContinueFromX != other.mustContinueFromX || mustContinueFromY != other.mustContinueFromY) return false
        for (i in 0 until SIZE) {
            if (!board[i].contentEquals(other.board[i])) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = agentTurn.toInt()
        result = 31 * result + mustContinueFromX
        result = 31 * result + mustContinueFromY
        for (i in 0 until SIZE) {
            result = 31 * result + board[i].contentHashCode()
        }
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in SIZE - 1 downTo 0) {
            for (x in 0 until SIZE) {
                sb.append(
                    when (board[x][y]) {
                        BLACK_MAN -> 'b'
                        BLACK_KING -> 'B'
                        WHITE_MAN -> 'w'
                        WHITE_KING -> 'W'
                        else -> if (isDarkSquare(x, y)) '.' else ' '
                    }
                )
                if (x < SIZE - 1) sb.append(' ')
            }
            if (y > 0) sb.append('\n')
        }
        return sb.toString()
    }
}
