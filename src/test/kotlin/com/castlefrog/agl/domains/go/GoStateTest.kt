package com.castlefrog.agl.domains.go

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

class GoStateTest {

    @Test
    fun copyEqualsAndIsDistinct() {
        val state = GoState(boardSize = 5)
        state.set(1, 1, GoState.LOCATION_BLACK)
        val copy = state.copy()
        assertEquals(state, copy)
        assertNotSame(state, copy)
        assertNotSame(state.board, copy.board)
        assertNotSame(state.board[0], copy.board[0])
    }

    @Test
    fun copyIsolation() {
        val state = GoState(boardSize = 5)
        val copy = state.copy()
        copy.set(0, 0, GoState.LOCATION_WHITE)
        assertNotEquals(state, copy)
        assertEquals(GoState.LOCATION_EMPTY, state.get(0, 0))
    }

    @Test
    fun toStringEmptyBoard() {
        val state = GoState(boardSize = 2)
        assertEquals(". .\n. .", state.toString())
    }
}
