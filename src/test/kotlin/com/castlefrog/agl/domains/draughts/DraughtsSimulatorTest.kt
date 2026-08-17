package com.castlefrog.agl.domains.draughts

import com.castlefrog.agl.IllegalActionException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class DraughtsSimulatorTest {

    @Test
    fun initialLegalMovesAreWhitesNineQuietMoves() {
        val simulator = DraughtsSimulator()
        val legal = simulator.calculateLegalActions(simulator.initialState)
        assertEquals(9, legal[0].size)
        assertTrue(legal[1].isEmpty())
        assertTrue(legal[0].contains(DraughtsAction(3, 3, 4, 4)))
    }

    @Test
    fun quietMoveAdvancesPiece() {
        val simulator = DraughtsSimulator()
        val state = simulator.initialState
        val action = DraughtsAction(3, 3, 4, 4)
        assertTrue(simulator.calculateLegalActions(state)[0].contains(action))
        val next = simulator.stateTransition(state, listOf(action, null))
        assertEquals(DraughtsState.EMPTY, next.get(3, 3))
        assertEquals(DraughtsState.WHITE_MAN, next.get(4, 4))
        assertEquals(DraughtsState.TURN_BLACK, next.agentTurn)
    }

    @Test
    fun captureIsMandatory() {
        val simulator = DraughtsSimulator()
        val state = emptyBoard()
        state.set(3, 3, DraughtsState.WHITE_MAN)
        state.set(4, 4, DraughtsState.BLACK_MAN)
        state.agentTurn = DraughtsState.TURN_WHITE
        val legal = simulator.calculateLegalActions(state)[0]
        assertTrue(legal.contains(DraughtsAction(3, 3, 5, 5)))
        assertFalse(legal.any { kotlin.math.abs(it.toX - it.fromX) == 1 })
    }

    @Test
    fun captureRemovesPieceAtEndOfSequence() {
        val simulator = DraughtsSimulator()
        val state = emptyBoard()
        state.set(3, 3, DraughtsState.WHITE_MAN)
        state.set(4, 4, DraughtsState.BLACK_MAN)
        state.agentTurn = DraughtsState.TURN_WHITE
        val next = simulator.stateTransition(state, listOf(DraughtsAction(3, 3, 5, 5), null))
        assertEquals(DraughtsState.EMPTY, next.get(4, 4))
        assertEquals(DraughtsState.WHITE_MAN, next.get(5, 5))
        assertTrue(next.pendingCaptures.isEmpty())
        assertEquals(DraughtsState.TURN_BLACK, next.agentTurn)
    }

    @Test
    fun manCanCaptureBackward() {
        val simulator = DraughtsSimulator()
        val state = emptyBoard()
        state.set(5, 5, DraughtsState.WHITE_MAN)
        state.set(4, 4, DraughtsState.BLACK_MAN)
        state.agentTurn = DraughtsState.TURN_WHITE
        val legal = simulator.calculateLegalActions(state)[0]
        assertTrue(legal.contains(DraughtsAction(5, 5, 3, 3)))
    }

    @Test
    fun majorityCaptureIsForced() {
        val simulator = DraughtsSimulator()
        val state = emptyBoard()
        // Single capture to the left; double capture to the right.
        state.set(5, 3, DraughtsState.WHITE_MAN)
        state.set(4, 4, DraughtsState.BLACK_MAN)
        state.set(6, 4, DraughtsState.BLACK_MAN)
        state.set(8, 6, DraughtsState.BLACK_MAN)
        state.agentTurn = DraughtsState.TURN_WHITE
        val legal = simulator.calculateLegalActions(state)[0]
        assertTrue(legal.contains(DraughtsAction(5, 3, 7, 5)))
        assertFalse(legal.contains(DraughtsAction(5, 3, 3, 5)))
    }

    @Test
    fun capturedPieceStaysDuringMultiJump() {
        val simulator = DraughtsSimulator()
        val state = emptyBoard()
        state.set(1, 1, DraughtsState.WHITE_MAN)
        state.set(2, 2, DraughtsState.BLACK_MAN)
        state.set(4, 4, DraughtsState.BLACK_MAN)
        state.agentTurn = DraughtsState.TURN_WHITE
        val mid = simulator.stateTransition(state, listOf(DraughtsAction(1, 1, 3, 3), null))
        assertEquals(DraughtsState.BLACK_MAN, mid.get(2, 2))
        assertEquals(DraughtsState.TURN_WHITE, mid.agentTurn)
        assertEquals(3, mid.mustContinueFromX)
        assertEquals(3, mid.mustContinueFromY)
        assertTrue(mid.isPendingCapture(2, 2))

        val end = simulator.stateTransition(mid, listOf(DraughtsAction(3, 3, 5, 5), null))
        assertEquals(DraughtsState.EMPTY, end.get(2, 2))
        assertEquals(DraughtsState.EMPTY, end.get(4, 4))
        assertEquals(DraughtsState.WHITE_MAN, end.get(5, 5))
        assertEquals(DraughtsState.TURN_BLACK, end.agentTurn)
        assertTrue(end.pendingCaptures.isEmpty())
    }

    @Test
    fun flyingKingSlidesAnyDistance() {
        val simulator = DraughtsSimulator()
        val state = emptyBoard()
        state.set(0, 0, DraughtsState.WHITE_KING)
        state.agentTurn = DraughtsState.TURN_WHITE
        val legal = simulator.calculateLegalActions(state)[0]
        assertTrue(legal.contains(DraughtsAction(0, 0, 1, 1)))
        assertTrue(legal.contains(DraughtsAction(0, 0, 5, 5)))
        assertTrue(legal.contains(DraughtsAction(0, 0, 9, 9)))
    }

    @Test
    fun flyingKingCapturesAtDistanceAndChoosesLanding() {
        val simulator = DraughtsSimulator()
        val state = emptyBoard()
        state.set(0, 0, DraughtsState.WHITE_KING)
        state.set(4, 4, DraughtsState.BLACK_MAN)
        state.agentTurn = DraughtsState.TURN_WHITE
        val legal = simulator.calculateLegalActions(state)[0]
        assertTrue(legal.contains(DraughtsAction(0, 0, 5, 5)))
        assertTrue(legal.contains(DraughtsAction(0, 0, 7, 7)))
        assertTrue(legal.contains(DraughtsAction(0, 0, 9, 9)))
        assertFalse(legal.contains(DraughtsAction(0, 0, 2, 2)))

        val next = simulator.stateTransition(state, listOf(DraughtsAction(0, 0, 7, 7), null))
        assertEquals(DraughtsState.EMPTY, next.get(4, 4))
        assertEquals(DraughtsState.WHITE_KING, next.get(7, 7))
    }

    @Test
    fun flyingKingMustLandWhereLongerCaptureContinues() {
        val simulator = DraughtsSimulator()
        val state = emptyBoard()
        state.set(0, 2, DraughtsState.WHITE_KING)
        state.set(2, 4, DraughtsState.BLACK_MAN)
        state.set(5, 7, DraughtsState.BLACK_MAN)
        state.agentTurn = DraughtsState.TURN_WHITE
        val legal = simulator.calculateLegalActions(state)[0]
        // Landing on (3,5) or (4,6) can continue over (5,7); (6,8) and (7,9) cannot.
        assertTrue(legal.contains(DraughtsAction(0, 2, 3, 5)))
        assertTrue(legal.contains(DraughtsAction(0, 2, 4, 6)))
        assertFalse(legal.contains(DraughtsAction(0, 2, 6, 8)))
        assertFalse(legal.contains(DraughtsAction(0, 2, 7, 9)))
    }

    @Test
    fun promotionOnLastRank() {
        val simulator = DraughtsSimulator()
        val state = emptyBoard()
        state.set(0, 8, DraughtsState.WHITE_MAN)
        state.agentTurn = DraughtsState.TURN_WHITE
        val next = simulator.stateTransition(state, listOf(DraughtsAction(0, 8, 1, 9), null))
        assertEquals(DraughtsState.WHITE_KING, next.get(1, 9))
    }

    @Test
    fun manDoesNotPromoteMidCapture() {
        val simulator = DraughtsSimulator()
        val state = emptyBoard()
        state.set(5, 7, DraughtsState.WHITE_MAN)
        state.set(6, 8, DraughtsState.BLACK_MAN)
        state.set(8, 8, DraughtsState.BLACK_MAN)
        state.agentTurn = DraughtsState.TURN_WHITE
        val mid = simulator.stateTransition(state, listOf(DraughtsAction(5, 7, 7, 9), null))
        assertEquals(DraughtsState.WHITE_MAN, mid.get(7, 9))
        assertEquals(DraughtsState.TURN_WHITE, mid.agentTurn)
        assertTrue(mid.mustContinueFromX == 7 && mid.mustContinueFromY == 9)

        val end = simulator.stateTransition(mid, listOf(DraughtsAction(7, 9, 9, 7), null))
        assertEquals(DraughtsState.WHITE_MAN, end.get(9, 7))
        assertEquals(DraughtsState.TURN_BLACK, end.agentTurn)
    }

    @Test
    fun manPromotesWhenCaptureEndsOnLastRank() {
        val simulator = DraughtsSimulator()
        val state = emptyBoard()
        state.set(5, 7, DraughtsState.WHITE_MAN)
        state.set(6, 8, DraughtsState.BLACK_MAN)
        state.agentTurn = DraughtsState.TURN_WHITE
        val next = simulator.stateTransition(state, listOf(DraughtsAction(5, 7, 7, 9), null))
        assertEquals(DraughtsState.WHITE_KING, next.get(7, 9))
        assertEquals(DraughtsState.TURN_BLACK, next.agentTurn)
    }

    @Test
    fun noMovesMeansLoss() {
        val simulator = DraughtsSimulator()
        val state = emptyBoard()
        state.set(1, 1, DraughtsState.BLACK_MAN)
        state.agentTurn = DraughtsState.TURN_WHITE
        val rewards = simulator.calculateRewards(state)
        assertEquals(-1, rewards[0])
        assertEquals(1, rewards[1])
    }

    @Test
    fun emptyActionsThrows() {
        val simulator = DraughtsSimulator()
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(simulator.initialState, emptyList())
        }
    }

    @Test
    fun illegalMoveThrows() {
        val simulator = DraughtsSimulator()
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(
                simulator.initialState,
                listOf(DraughtsAction(0, 0, 1, 1), null)
            )
        }
    }

    private fun emptyBoard(): DraughtsState {
        val state = DraughtsState()
        for (x in 0 until DraughtsState.SIZE) {
            for (y in 0 until DraughtsState.SIZE) {
                state.set(x, y, DraughtsState.EMPTY)
            }
        }
        return state
    }
}
