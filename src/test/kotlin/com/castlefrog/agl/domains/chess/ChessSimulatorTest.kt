package com.castlefrog.agl.domains.chess

import com.castlefrog.agl.IllegalActionException
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ChessSimulatorTest {

    @Test
    fun initialLegalMovesIncludePawnAndKnight() {
        val simulator = ChessSimulator()
        val legal = simulator.calculateLegalActions(simulator.initialState)[0]
        assertTrue(legal.contains(ChessAction(4, 1, 4, 3))) // e2-e4
        assertTrue(legal.contains(ChessAction(1, 0, 2, 2))) // Nb1-c3
        assertEquals(20, legal.size)
    }

    @Test
    fun pawnDoublePushSetsEnPassant() {
        val simulator = ChessSimulator()
        val next = simulator.stateTransition(
            simulator.initialState,
            listOf(ChessAction(4, 1, 4, 3), null)
        )
        assertEquals(4, next.enPassantX)
        assertEquals(ChessState.TURN_BLACK, next.agentTurn)
        assertEquals(ChessState.EMPTY, next.get(4, 1))
        assertEquals(ChessState.PAWN.toByte(), next.get(4, 3))
    }

    @Test
    fun scholarsMateCheckmate() {
        val simulator = ChessSimulator()
        var state = simulator.initialState
        // 1. e4 e5 2. Qh5 Nc6 3. Bc4 Nf6?? 4. Qxf7#
        state = simulator.stateTransition(state, listOf(ChessAction(4, 1, 4, 3), null))
        state = simulator.stateTransition(state, listOf(null, ChessAction(4, 6, 4, 4)))
        state = simulator.stateTransition(state, listOf(ChessAction(3, 0, 7, 4), null)) // Qd1-h5
        state = simulator.stateTransition(state, listOf(null, ChessAction(1, 7, 2, 5))) // Nb8-c6
        state = simulator.stateTransition(state, listOf(ChessAction(5, 0, 2, 3), null)) // Bf1-c4
        state = simulator.stateTransition(state, listOf(null, ChessAction(6, 7, 5, 5))) // Ng8-f6
        state = simulator.stateTransition(state, listOf(ChessAction(7, 4, 5, 6), null)) // Qh5xf7
        assertTrue(simulator.isInCheck(state, 1))
        assertTrue(simulator.calculateLegalActions(state)[1].isEmpty())
        assertArrayEquals(intArrayOf(1, -1), simulator.calculateRewards(state))
    }

    @Test
    fun quietCheckmateThatTicksClockTo100IsWinNotDraw() {
        val simulator = ChessSimulator()
        val state = ChessState()
        // KR vs K: white king a6, white rook h7, black king a8. Quiet Rh7-h8# mates.
        for (x in 0 until 8) for (y in 0 until 8) state.set(x, y, ChessState.EMPTY)
        state.set(0, 5, ChessState.KING.toByte())
        state.set(7, 6, ChessState.ROOK.toByte())
        state.set(0, 7, (-ChessState.KING).toByte())
        state.agentTurn = ChessState.TURN_WHITE
        state.castlingRights = 0
        state.enPassantX = -1
        state.halfmoveClock = 99
        val mated = simulator.stateTransition(state, listOf(ChessAction(7, 6, 7, 7), null))
        assertEquals(100, mated.halfmoveClock)
        assertEquals(ChessState.TURN_BLACK, mated.agentTurn)
        assertTrue(simulator.isInCheck(mated, 1))
        assertTrue(simulator.calculateLegalActions(mated)[1].isEmpty())
        assertArrayEquals(intArrayOf(1, -1), simulator.calculateRewards(mated))
    }

    @Test
    fun cannotLeaveKingInCheck() {
        val simulator = ChessSimulator()
        val state = ChessState()
        // Clear and put white king e1, black rook e8 — white to move cannot move unrelated piece
        for (x in 0 until 8) for (y in 0 until 8) state.set(x, y, ChessState.EMPTY)
        state.set(4, 0, ChessState.KING.toByte())
        state.set(4, 7, (-ChessState.ROOK).toByte())
        state.set(0, 0, ChessState.ROOK.toByte())
        state.agentTurn = ChessState.TURN_WHITE
        state.castlingRights = 0
        val legal = simulator.calculateLegalActions(state)[0]
        // Moving rook on a-file does not resolve check
        assertFalse(legal.contains(ChessAction(0, 0, 0, 1)))
        // King can step off the file
        assertTrue(legal.any { it.fromX == 4 && it.fromY == 0 })
    }

    @Test
    fun emptyActionsThrows() {
        val simulator = ChessSimulator()
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(simulator.initialState, emptyList())
        }
    }

    @Test
    fun illegalMoveThrows() {
        val simulator = ChessSimulator()
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(
                simulator.initialState,
                listOf(ChessAction(0, 0, 0, 2), null) // rook blocked
            )
        }
    }

    @Test
    fun rewardsNeutralAtStart() {
        val simulator = ChessSimulator()
        assertArrayEquals(intArrayOf(0, 0), simulator.calculateRewards(simulator.initialState))
    }

    @Test
    fun knightsDevelopWithoutCapture() {
        val simulator = ChessSimulator()
        val state = simulator.stateTransition(
            simulator.initialState,
            listOf(ChessAction(6, 0, 5, 2), null) // Ng1-f3
        )
        assertEquals(ChessState.KNIGHT.toByte(), state.get(5, 2))
        assertEquals(ChessState.EMPTY, state.get(6, 0))
    }
}
