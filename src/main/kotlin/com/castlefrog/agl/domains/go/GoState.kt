package com.castlefrog.agl.domains.go

import com.castlefrog.agl.State

/**
 * Go position under Tromp–Taylor rules.
 *
 * Board cells: [LOCATION_EMPTY], [LOCATION_BLACK], [LOCATION_WHITE].
 * [positionHistory] is the sequence of whole-board colorings that have occurred
 * (positional superko). [consecutivePasses] counts successive passes (game ends at 2).
 */
class GoState(
    val boardSize: Int = DEFAULT_BOARD_SIZE,
    val board: Array<ByteArray> = Array(boardSize) { ByteArray(boardSize) },
    var agentTurn: Byte = TURN_BLACK,
    var consecutivePasses: Int = 0,
    var capturedByBlack: Int = 0,
    var capturedByWhite: Int = 0,
    val positionHistory: MutableList<ByteArray> = ArrayList()
) : State<GoState> {

    companion object {
        const val DEFAULT_BOARD_SIZE = 19
        const val LOCATION_EMPTY: Byte = 0
        const val LOCATION_BLACK: Byte = 1
        const val LOCATION_WHITE: Byte = 2
        const val TURN_BLACK: Byte = 0
        const val TURN_WHITE: Byte = 1
    }

    override fun copy(): GoState {
        val copyBoard = Array(boardSize) { board[it].copyOf() }
        val copyHistory = ArrayList<ByteArray>(positionHistory.size)
        for (snapshot in positionHistory) {
            copyHistory.add(snapshot.copyOf())
        }
        return GoState(
            boardSize = boardSize,
            board = copyBoard,
            agentTurn = agentTurn,
            consecutivePasses = consecutivePasses,
            capturedByBlack = capturedByBlack,
            capturedByWhite = capturedByWhite,
            positionHistory = copyHistory
        )
    }

    fun isOnBoard(x: Int, y: Int): Boolean = x in 0 until boardSize && y in 0 until boardSize

    fun get(x: Int, y: Int): Byte = board[x][y]

    fun set(x: Int, y: Int, value: Byte) {
        board[x][y] = value
    }

    /**
     * Flattened row-major snapshot of the current board coloring.
     */
    fun boardSnapshot(): ByteArray {
        val snap = ByteArray(boardSize * boardSize)
        var i = 0
        for (x in 0 until boardSize) {
            System.arraycopy(board[x], 0, snap, i, boardSize)
            i += boardSize
        }
        return snap
    }

    /**
     * True if [snapshot] matches any coloring that has already occurred, including
     * the current board when it is not yet recorded in [positionHistory].
     */
    fun coloringHasOccurred(snapshot: ByteArray): Boolean {
        for (previous in positionHistory) {
            if (previous.contentEquals(snapshot)) return true
        }
        return if (positionHistory.isEmpty() || !positionHistory.last().contentEquals(boardSnapshot())) {
            boardSnapshot().contentEquals(snapshot)
        } else {
            false
        }
    }

    /**
     * Records the current board if it is not already the last history entry.
     */
    fun recordCurrentColoring() {
        val current = boardSnapshot()
        if (positionHistory.isEmpty() || !positionHistory.last().contentEquals(current)) {
            positionHistory.add(current)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GoState) return false
        if (boardSize != other.boardSize || agentTurn != other.agentTurn) return false
        if (consecutivePasses != other.consecutivePasses) return false
        if (capturedByBlack != other.capturedByBlack || capturedByWhite != other.capturedByWhite) return false
        if (positionHistory.size != other.positionHistory.size) return false
        for (i in positionHistory.indices) {
            if (!positionHistory[i].contentEquals(other.positionHistory[i])) return false
        }
        for (i in 0 until boardSize) {
            if (!board[i].contentEquals(other.board[i])) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = boardSize
        result = 31 * result + agentTurn
        result = 31 * result + consecutivePasses
        result = 31 * result + capturedByBlack
        result = 31 * result + capturedByWhite
        for (snapshot in positionHistory) {
            result = 31 * result + snapshot.contentHashCode()
        }
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
