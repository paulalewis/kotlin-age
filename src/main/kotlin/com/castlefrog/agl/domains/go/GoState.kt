package com.castlefrog.agl.domains.go

import com.castlefrog.agl.State

/**
 * Go position.
 *
 * Board cells: [LOCATION_EMPTY], [LOCATION_BLACK], [LOCATION_WHITE].
 * [koX]/[koY] mark a single-point ko forbidden for the side to move (-1 = none).
 * [consecutivePasses] counts successive passes (game ends at 2).
 */
class GoState(
    val boardSize: Int = DEFAULT_BOARD_SIZE,
    val board: Array<ByteArray> = Array(boardSize) { ByteArray(boardSize) },
    var agentTurn: Byte = TURN_BLACK,
    var koX: Int = -1,
    var koY: Int = -1,
    var consecutivePasses: Int = 0,
    var capturedByBlack: Int = 0,
    var capturedByWhite: Int = 0
) : State<GoState> {

    companion object {
        const val DEFAULT_BOARD_SIZE = 9
        const val LOCATION_EMPTY: Byte = 0
        const val LOCATION_BLACK: Byte = 1
        const val LOCATION_WHITE: Byte = 2
        const val TURN_BLACK: Byte = 0
        const val TURN_WHITE: Byte = 1
    }

    override fun copy(): GoState {
        val copyBoard = Array(boardSize) { board[it].copyOf() }
        return GoState(
            boardSize = boardSize,
            board = copyBoard,
            agentTurn = agentTurn,
            koX = koX,
            koY = koY,
            consecutivePasses = consecutivePasses,
            capturedByBlack = capturedByBlack,
            capturedByWhite = capturedByWhite
        )
    }

    fun isOnBoard(x: Int, y: Int): Boolean = x in 0 until boardSize && y in 0 until boardSize

    fun get(x: Int, y: Int): Byte = board[x][y]

    fun set(x: Int, y: Int, value: Byte) {
        board[x][y] = value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GoState) return false
        if (boardSize != other.boardSize || agentTurn != other.agentTurn) return false
        if (koX != other.koX || koY != other.koY) return false
        if (consecutivePasses != other.consecutivePasses) return false
        if (capturedByBlack != other.capturedByBlack || capturedByWhite != other.capturedByWhite) return false
        for (i in 0 until boardSize) {
            if (!board[i].contentEquals(other.board[i])) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = boardSize
        result = 31 * result + agentTurn
        result = 31 * result + koX
        result = 31 * result + koY
        result = 31 * result + consecutivePasses
        result = 31 * result + capturedByBlack
        result = 31 * result + capturedByWhite
        for (i in 0 until boardSize) {
            result = 31 * result + board[i].contentHashCode()
        }
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (y in boardSize - 1 downTo 0) {
            for (x in 0 until boardSize) {
                when (board[x][y]) {
                    LOCATION_BLACK -> sb.append('X')
                    LOCATION_WHITE -> sb.append('O')
                    else -> sb.append('.')
                }
                if (x < boardSize - 1) sb.append(' ')
            }
            if (y > 0) sb.append('\n')
        }
        return sb.toString()
    }
}
