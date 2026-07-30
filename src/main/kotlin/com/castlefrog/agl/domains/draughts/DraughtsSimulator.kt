package com.castlefrog.agl.domains.draughts

import com.castlefrog.agl.Simulator
import com.castlefrog.agl.domains.AdversarialRewards
import com.castlefrog.agl.domains.nextPlayerTurnSequential
import com.castlefrog.agl.requireLegalAction
import kotlin.math.abs

/**
 * English draughts (American checkers): men move/capture forward diagonally,
 * kings move/capture in all diagonal directions, captures are mandatory,
 * multi-jumps continue on the same turn via [DraughtsState.mustContinueFromX].
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
        val captures = collectMoves(state, capturesOnly = true)
        val moves = if (captures.isNotEmpty()) captures else collectMoves(state, capturesOnly = false)
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

        val isCapture = abs(action.toX - action.fromX) == 2
        if (isCapture) {
            val mx = (action.fromX + action.toX) / 2
            val my = (action.fromY + action.toY) / 2
            next.set(mx, my, DraughtsState.EMPTY)
        }

        // Promotion on last rank.
        var moved = next.get(action.toX, action.toY)
        if (moved == DraughtsState.BLACK_MAN && action.toY == DraughtsState.SIZE - 1) {
            moved = DraughtsState.BLACK_KING
            next.set(action.toX, action.toY, moved)
        } else if (moved == DraughtsState.WHITE_MAN && action.toY == 0) {
            moved = DraughtsState.WHITE_KING
            next.set(action.toX, action.toY, moved)
        }

        if (isCapture) {
            // Further captures with the same piece?
            next.mustContinueFromX = action.toX
            next.mustContinueFromY = action.toY
            val further = collectMoves(next, capturesOnly = true)
            if (further.isNotEmpty()) {
                // Same player continues.
                return next
            }
        }
        next.mustContinueFromX = -1
        next.mustContinueFromY = -1
        next.agentTurn = nextPlayerTurnSequential(agentTurn, NUMBER_OF_PLAYERS).toByte()
        return next
    }

    private fun collectMoves(state: DraughtsState, capturesOnly: Boolean): Set<DraughtsAction> {
        val result = mutableSetOf<DraughtsAction>()
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
                if (turn == 0 && !state.isBlackPiece(p)) continue
                if (turn == 1 && !state.isWhitePiece(p)) continue
                val dirs = moveDirections(p)
                for ((dx, dy) in dirs) {
                    val tx = x + dx
                    val ty = y + dy
                    if (!state.isOnBoard(tx, ty) || !DraughtsState.isDarkSquare(tx, ty)) continue
                    if (!capturesOnly && state.get(tx, ty) == DraughtsState.EMPTY && abs(dx) == 1) {
                        result.add(DraughtsAction(x, y, tx, ty))
                    }
                    // Capture: jump over opponent to empty square.
                    if (abs(dx) == 1) {
                        val jx = x + 2 * dx
                        val jy = y + 2 * dy
                        if (!state.isOnBoard(jx, jy) || !DraughtsState.isDarkSquare(jx, jy)) continue
                        val mid = state.get(tx, ty)
                        val isOpp = if (turn == 0) state.isWhitePiece(mid) else state.isBlackPiece(mid)
                        if (isOpp && state.get(jx, jy) == DraughtsState.EMPTY) {
                            result.add(DraughtsAction(x, y, jx, jy))
                        }
                    }
                }
            }
        }
        return result
    }

    private fun moveDirections(piece: Byte): List<Pair<Int, Int>> {
        return when (piece) {
            DraughtsState.BLACK_MAN -> listOf(-1 to 1, 1 to 1)
            DraughtsState.WHITE_MAN -> listOf(-1 to -1, 1 to -1)
            DraughtsState.BLACK_KING, DraughtsState.WHITE_KING ->
                listOf(-1 to 1, 1 to 1, -1 to -1, 1 to -1)
            else -> emptyList()
        }
    }

    companion object {
        private const val NUMBER_OF_PLAYERS = 2
    }
}
