package com.castlefrog.agl.domains.draughts

import com.castlefrog.agl.IllegalActionException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class DraughtsSimulatorTest {

    @Test
    fun initialLegalMovesNonEmpty() {
        val simulator = DraughtsSimulator()
        val legal = simulator.calculateLegalActions(simulator.initialState)
        assertTrue(legal[0].isNotEmpty())
        assertTrue(legal[1].isEmpty())
    }

    @Test
    fun quietMoveAdvancesPiece() {
        val simulator = DraughtsSimulator()
        val state = simulator.initialState
        // Black man at (1,2) can move to (0,3) or (2,3)
        val action = DraughtsAction(1, 2, 2, 3)
        assertTrue(simulator.calculateLegalActions(state)[0].contains(action))
        val next = simulator.stateTransition(state, listOf(action, null))
        assertEquals(DraughtsState.EMPTY, next.get(1, 2))
        assertEquals(DraughtsState.BLACK_MAN, next.get(2, 3))
        assertEquals(DraughtsState.TURN_WHITE, next.agentTurn)
    }

    @Test
    fun captureIsMandatory() {
        val simulator = DraughtsSimulator()
        val state = DraughtsState()
        // Clear board and set up forced capture for black
        for (x in 0 until 8) for (y in 0 until 8) state.set(x, y, DraughtsState.EMPTY)
        state.set(1, 2, DraughtsState.BLACK_MAN)
        state.set(2, 3, DraughtsState.WHITE_MAN)
        state.agentTurn = DraughtsState.TURN_BLACK
        val legal = simulator.calculateLegalActions(state)[0]
        assertTrue(legal.contains(DraughtsAction(1, 2, 3, 4)))
        assertFalse(legal.any { abs(it.toX - it.fromX) == 1 })
    }

    @Test
    fun captureRemovesPiece() {
        val simulator = DraughtsSimulator()
        val state = DraughtsState()
        for (x in 0 until 8) for (y in 0 until 8) state.set(x, y, DraughtsState.EMPTY)
        state.set(1, 2, DraughtsState.BLACK_MAN)
        state.set(2, 3, DraughtsState.WHITE_MAN)
        state.agentTurn = DraughtsState.TURN_BLACK
        val next = simulator.stateTransition(state, listOf(DraughtsAction(1, 2, 3, 4), null))
        assertEquals(DraughtsState.EMPTY, next.get(2, 3))
        assertEquals(DraughtsState.BLACK_MAN, next.get(3, 4))
    }

    @Test
    fun promotionOnLastRank() {
        val simulator = DraughtsSimulator()
        val state = DraughtsState()
        for (x in 0 until 8) for (y in 0 until 8) state.set(x, y, DraughtsState.EMPTY)
        state.set(1, 6, DraughtsState.BLACK_MAN)
        state.agentTurn = DraughtsState.TURN_BLACK
        val next = simulator.stateTransition(state, listOf(DraughtsAction(1, 6, 0, 7), null))
        assertEquals(DraughtsState.BLACK_KING, next.get(0, 7))
    }

    @Test
    fun noMovesMeansLoss() {
        val simulator = DraughtsSimulator()
        val state = DraughtsState()
        for (x in 0 until 8) for (y in 0 until 8) state.set(x, y, DraughtsState.EMPTY)
        state.set(0, 1, DraughtsState.WHITE_MAN)
        state.agentTurn = DraughtsState.TURN_BLACK
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

    private fun abs(v: Int): Int = if (v < 0) -v else v
}
