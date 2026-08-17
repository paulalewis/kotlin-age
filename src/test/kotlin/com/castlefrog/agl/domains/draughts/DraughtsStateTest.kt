package com.castlefrog.agl.domains.draughts

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test

class DraughtsStateTest {

    @Test
    fun initialBoardHasTwentyMenEach() {
        val state = DraughtsState()
        var white = 0
        var black = 0
        for (x in 0 until DraughtsState.SIZE) {
            for (y in 0 until DraughtsState.SIZE) {
                when (state.get(x, y)) {
                    DraughtsState.WHITE_MAN -> white++
                    DraughtsState.BLACK_MAN -> black++
                }
            }
        }
        assertEquals(20, white)
        assertEquals(20, black)
    }

    @Test
    fun piecesOnlyOnDarkSquares() {
        val state = DraughtsState()
        for (x in 0 until DraughtsState.SIZE) {
            for (y in 0 until DraughtsState.SIZE) {
                if (state.get(x, y) != DraughtsState.EMPTY) {
                    assertTrue(DraughtsState.isDarkSquare(x, y))
                }
            }
        }
    }

    @Test
    fun copyIsolation() {
        val state = DraughtsState()
        val copy = state.copy()
        assertEquals(state, copy)
        assertNotSame(state.board, copy.board)
        copy.set(0, 0, DraughtsState.EMPTY)
        assertNotEquals(state, copy)
    }

    @Test
    fun pendingCapturesCopiedIndependently() {
        val state = DraughtsState()
        state.pendingCaptures.add(DraughtsState.captureKey(2, 2))
        val copy = state.copy()
        assertEquals(state, copy)
        copy.pendingCaptures.add(DraughtsState.captureKey(4, 4))
        assertNotEquals(state, copy)
        assertEquals(1, state.pendingCaptures.size)
    }
}
