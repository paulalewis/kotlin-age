package com.castlefrog.agl.domains.draughts

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test

class DraughtsStateTest {

    @Test
    fun initialBoardHasTwelveMenEach() {
        val state = DraughtsState()
        var black = 0
        var white = 0
        for (x in 0 until DraughtsState.SIZE) {
            for (y in 0 until DraughtsState.SIZE) {
                when (state.get(x, y)) {
                    DraughtsState.BLACK_MAN -> black++
                    DraughtsState.WHITE_MAN -> white++
                }
            }
        }
        assertEquals(12, black)
        assertEquals(12, white)
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
        copy.set(1, 0, DraughtsState.EMPTY)
        assertNotEquals(state, copy)
    }
}
