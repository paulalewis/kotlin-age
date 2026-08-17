package com.castlefrog.agl.domains.draughts

import com.castlefrog.agl.State

/**
 * International draughts on a 10×10 board.
 *
 * Pieces occupy dark squares only (`(x + y) % 2 == 0`); the near-left corner
 * for each side is dark. Values: empty 0; man/king for white (side to move
 * first, bottom ranks) and black (top ranks).
 *
 * When [mustContinueFromX] is non-negative, that piece must continue a multi-jump.
 * Squares in [pendingCaptures] still hold the jumped pieces until the sequence ends.
 */
class DraughtsState(
    val board: Array<ByteArray> = initialBoard(),
    var agentTurn: Byte = TURN_WHITE,
    var mustContinueFromX: Int = -1,
    var mustContinueFromY: Int = -1,
    val pendingCaptures: MutableSet<Int> = mutableSetOf()
) : State<DraughtsState> {

    companion object {
        const val SIZE = 10
        const val EMPTY: Byte = 0
        const val WHITE_MAN: Byte = 1
        const val WHITE_KING: Byte = 2
        const val BLACK_MAN: Byte = 3
        const val BLACK_KING: Byte = 4
        const val TURN_WHITE: Byte = 0
        const val TURN_BLACK: Byte = 1

        fun initialBoard(): Array<ByteArray> {
            val board = Array(SIZE) { ByteArray(SIZE) }
            for (y in 0 until SIZE) {
                for (x in 0 until SIZE) {
                    if ((x + y) % 2 != 0) continue // light squares unused
                    when (y) {
                        in 0..3 -> board[x][y] = WHITE_MAN
                        in 6..9 -> board[x][y] = BLACK_MAN
                    }
                }
            }
            return board
        }

        fun isDarkSquare(x: Int, y: Int): Boolean = (x + y) % 2 == 0

        fun captureKey(x: Int, y: Int): Int = x * SIZE + y
    }

    override fun copy(): DraughtsState {
        val copyBoard = Array(SIZE) { board[it].copyOf() }
        return DraughtsState(
            copyBoard,
            agentTurn,
            mustContinueFromX,
            mustContinueFromY,
            pendingCaptures.toMutableSet()
        )
    }

    fun isOnBoard(x: Int, y: Int): Boolean = x in 0 until SIZE && y in 0 until SIZE

    fun get(x: Int, y: Int): Byte = board[x][y]

    fun set(x: Int, y: Int, value: Byte) {
        board[x][y] = value
    }

    fun isWhitePiece(p: Byte): Boolean = p == WHITE_MAN || p == WHITE_KING

    fun isBlackPiece(p: Byte): Boolean = p == BLACK_MAN || p == BLACK_KING

    fun isKing(p: Byte): Boolean = p == WHITE_KING || p == BLACK_KING

    fun isPendingCapture(x: Int, y: Int): Boolean = pendingCaptures.contains(captureKey(x, y))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DraughtsState) return false
        if (agentTurn != other.agentTurn) return false
        if (mustContinueFromX != other.mustContinueFromX || mustContinueFromY != other.mustContinueFromY) return false
        if (pendingCaptures != other.pendingCaptures) return false
        for (i in 0 until SIZE) {
            if (!board[i].contentEquals(other.board[i])) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = agentTurn.toInt()
        result = 31 * result + mustContinueFromX
        result = 31 * result + mustContinueFromY
        result = 31 * result + pendingCaptures.hashCode()
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
                        WHITE_MAN -> 'w'
                        WHITE_KING -> 'W'
                        BLACK_MAN -> 'b'
                        BLACK_KING -> 'B'
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
