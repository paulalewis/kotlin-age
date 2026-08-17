package com.castlefrog.agl.domains.draughts

import com.castlefrog.agl.Simulator
import com.castlefrog.agl.domains.AdversarialRewards
import com.castlefrog.agl.domains.nextPlayerTurnSequential
import com.castlefrog.agl.requireLegalAction

/**
 * International draughts: 10×10 board, men move forward and capture in all
 * four diagonal directions, kings fly any distance, captures are mandatory,
 * the longest capture sequence must be taken, and jumped pieces stay on the
 * board until the sequence ends. Multi-jumps continue on the same turn via
 * [DraughtsState.mustContinueFromX]. A man promotes only when its move ends
 * on the last rank.
 */
class DraughtsSimulator : Simulator<DraughtsState, DraughtsAction> {

    override val initialState: DraughtsState
        get() = DraughtsState()

    override fun numberOfPlayers(): Int = NUMBER_OF_PLAYERS

    override fun calculateRewards(state: DraughtsState): IntArray {
        val legal = calculateLegalActions(state)
        val turn = state.agentTurn.toInt()
        if (legal[turn].isNotEmpty()) {
            return AdversarialRewards.neutral()
        }
        // Side to move has no moves — loses (opponent wins).
        return if (turn == 0) AdversarialRewards.secondPlayerWins() else AdversarialRewards.firstPlayerWins()
    }

    override fun calculateLegalActions(state: DraughtsState): List<Set<DraughtsAction>> {
        val legal = listOf(mutableSetOf<DraughtsAction>(), mutableSetOf())
        val turn = state.agentTurn.toInt()
        val captures = collectCaptures(state)
        val moves = if (captures.isNotEmpty()) captures else collectQuietMoves(state)
        legal[turn].addAll(moves)
        return legal
    }

    override fun stateTransition(state: DraughtsState, actions: List<DraughtsAction?>): DraughtsState {
        val agentTurn = state.agentTurn.toInt()
        val legalActions = calculateLegalActions(state)
        val action = requireLegalAction(actions, agentTurn, legalActions[agentTurn], state)
        val next = state.copy()
        val piece = next.get(action.fromX, action.fromY)
        next.set(action.fromX, action.fromY, DraughtsState.EMPTY)
        next.set(action.toX, action.toY, piece)

        val captured = findCapturedPiece(next, action.fromX, action.fromY, action.toX, action.toY)
        if (captured != null) {
            next.pendingCaptures.add(DraughtsState.captureKey(captured.first, captured.second))
            next.mustContinueFromX = action.toX
            next.mustContinueFromY = action.toY
            if (collectCaptures(next).isNotEmpty()) {
                return next
            }
            clearPendingCaptures(next)
        }

        promoteIfOnLastRank(next, action.toX, action.toY)
        next.mustContinueFromX = -1
        next.mustContinueFromY = -1
        next.agentTurn = nextPlayerTurnSequential(agentTurn, NUMBER_OF_PLAYERS).toByte()
        return next
    }

    private fun collectCaptures(state: DraughtsState): Set<DraughtsAction> {
        var bestLen = 0
        val best = mutableSetOf<DraughtsAction>()
        forEachOwnPiece(state) { x, y, piece ->
            for (jump in generateJumps(state, x, y, piece)) {
                val captured = state.pendingCaptures.toMutableSet()
                captured.add(DraughtsState.captureKey(jump.capX, jump.capY))
                val len = 1 + maxCaptureLength(state, jump.toX, jump.toY, piece, captured)
                if (len > bestLen) {
                    bestLen = len
                    best.clear()
                    best.add(jump.action)
                } else if (len == bestLen) {
                    best.add(jump.action)
                }
            }
        }
        return best
    }

    private fun collectQuietMoves(state: DraughtsState): Set<DraughtsAction> {
        val result = mutableSetOf<DraughtsAction>()
        forEachOwnPiece(state) { x, y, piece ->
            if (state.isKing(piece)) {
                for ((dx, dy) in DIAGONALS) {
                    var tx = x + dx
                    var ty = y + dy
                    while (state.isOnBoard(tx, ty) && state.get(tx, ty) == DraughtsState.EMPTY) {
                        result.add(DraughtsAction(x, y, tx, ty))
                        tx += dx
                        ty += dy
                    }
                }
            } else {
                val dy = if (state.isWhitePiece(piece)) 1 else -1
                for (dx in intArrayOf(-1, 1)) {
                    val tx = x + dx
                    val ty = y + dy
                    if (state.isOnBoard(tx, ty) && state.get(tx, ty) == DraughtsState.EMPTY) {
                        result.add(DraughtsAction(x, y, tx, ty))
                    }
                }
            }
        }
        return result
    }

    private fun maxCaptureLength(
        state: DraughtsState,
        x: Int,
        y: Int,
        piece: Byte,
        captured: Set<Int>
    ): Int {
        val jumps = generateJumps(state, x, y, piece, captured)
        if (jumps.isEmpty()) return 0
        var best = 0
        for (jump in jumps) {
            val nextCaptured = captured.toMutableSet()
            nextCaptured.add(DraughtsState.captureKey(jump.capX, jump.capY))
            val len = 1 + maxCaptureLength(state, jump.toX, jump.toY, piece, nextCaptured)
            if (len > best) best = len
        }
        return best
    }

    private fun generateJumps(
        state: DraughtsState,
        x: Int,
        y: Int,
        piece: Byte,
        captured: Set<Int> = state.pendingCaptures
    ): List<Jump> {
        return if (state.isKing(piece)) {
            generateKingJumps(state, x, y, captured)
        } else {
            generateManJumps(state, x, y, captured)
        }
    }

    private fun generateManJumps(state: DraughtsState, x: Int, y: Int, captured: Set<Int>): List<Jump> {
        val jumps = mutableListOf<Jump>()
        val turn = state.agentTurn.toInt()
        for ((dx, dy) in DIAGONALS) {
            val mx = x + dx
            val my = y + dy
            val jx = x + 2 * dx
            val jy = y + 2 * dy
            if (!state.isOnBoard(jx, jy)) continue
            if (!isOpponent(state, mx, my, turn)) continue
            if (captured.contains(DraughtsState.captureKey(mx, my))) continue
            if (state.get(jx, jy) != DraughtsState.EMPTY) continue
            jumps.add(Jump(x, y, jx, jy, mx, my))
        }
        return jumps
    }

    private fun generateKingJumps(state: DraughtsState, x: Int, y: Int, captured: Set<Int>): List<Jump> {
        val jumps = mutableListOf<Jump>()
        val turn = state.agentTurn.toInt()
        for ((dx, dy) in DIAGONALS) {
            var cx = x + dx
            var cy = y + dy
            while (state.isOnBoard(cx, cy) && state.get(cx, cy) == DraughtsState.EMPTY) {
                cx += dx
                cy += dy
            }
            if (!state.isOnBoard(cx, cy)) continue
            if (!isOpponent(state, cx, cy, turn)) continue
            if (captured.contains(DraughtsState.captureKey(cx, cy))) continue
            val capX = cx
            val capY = cy
            var lx = cx + dx
            var ly = cy + dy
            while (state.isOnBoard(lx, ly) && state.get(lx, ly) == DraughtsState.EMPTY) {
                jumps.add(Jump(x, y, lx, ly, capX, capY))
                lx += dx
                ly += dy
            }
        }
        return jumps
    }

    private fun forEachOwnPiece(state: DraughtsState, body: (x: Int, y: Int, piece: Byte) -> Unit) {
        val turn = state.agentTurn.toInt()
        val xs: IntRange
        val ys: IntRange
        if (state.mustContinueFromX >= 0) {
            xs = state.mustContinueFromX..state.mustContinueFromX
            ys = state.mustContinueFromY..state.mustContinueFromY
        } else {
            xs = 0 until DraughtsState.SIZE
            ys = 0 until DraughtsState.SIZE
        }
        for (x in xs) {
            for (y in ys) {
                val p = state.get(x, y)
                if (turn == 0 && !state.isWhitePiece(p)) continue
                if (turn == 1 && !state.isBlackPiece(p)) continue
                body(x, y, p)
            }
        }
    }

    private fun isOpponent(state: DraughtsState, x: Int, y: Int, turn: Int): Boolean {
        if (!state.isOnBoard(x, y)) return false
        val p = state.get(x, y)
        return if (turn == 0) state.isBlackPiece(p) else state.isWhitePiece(p)
    }

    private fun findCapturedPiece(
        state: DraughtsState,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Pair<Int, Int>? {
        val dx = sign(toX - fromX)
        val dy = sign(toY - fromY)
        var x = fromX + dx
        var y = fromY + dy
        while (x != toX || y != toY) {
            if (state.get(x, y) != DraughtsState.EMPTY) {
                return x to y
            }
            x += dx
            y += dy
        }
        return null
    }

    private fun clearPendingCaptures(state: DraughtsState) {
        for (key in state.pendingCaptures) {
            val x = key / DraughtsState.SIZE
            val y = key % DraughtsState.SIZE
            state.set(x, y, DraughtsState.EMPTY)
        }
        state.pendingCaptures.clear()
    }

    private fun promoteIfOnLastRank(state: DraughtsState, x: Int, y: Int) {
        val piece = state.get(x, y)
        if (piece == DraughtsState.WHITE_MAN && y == DraughtsState.SIZE - 1) {
            state.set(x, y, DraughtsState.WHITE_KING)
        } else if (piece == DraughtsState.BLACK_MAN && y == 0) {
            state.set(x, y, DraughtsState.BLACK_KING)
        }
    }

    private fun sign(v: Int): Int = when {
        v > 0 -> 1
        v < 0 -> -1
        else -> 0
    }

    private data class Jump(
        val fromX: Int,
        val fromY: Int,
        val toX: Int,
        val toY: Int,
        val capX: Int,
        val capY: Int
    ) {
        val action: DraughtsAction get() = DraughtsAction(fromX, fromY, toX, toY)
    }

    companion object {
        private const val NUMBER_OF_PLAYERS = 2
        private val DIAGONALS = listOf(-1 to 1, 1 to 1, -1 to -1, 1 to -1)
    }
}
