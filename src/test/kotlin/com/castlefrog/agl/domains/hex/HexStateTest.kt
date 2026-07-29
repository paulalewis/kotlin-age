package com.castlefrog.agl.domains.hex

import org.junit.Before
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class HexStateTest {

    private val emptyState: HexState = HexState(boardSize = 5)
    private val hexState: HexState = HexState(boardSize = 5)

    @Before
    fun setup() {
        hexState.setLocation(2, 1, HexState.LOCATION_BLACK)
        hexState.setLocation(1, 1, HexState.LOCATION_BLACK)
        hexState.setLocation(1, 0, HexState.LOCATION_BLACK)
        hexState.setLocation(3, 1, HexState.LOCATION_WHITE)
        hexState.setLocation(3, 3, HexState.LOCATION_WHITE)
    }

    @Test
    fun testCopy() {
        assertEquals(hexState.copy(), hexState)
    }

    @Test
    fun testEquality() {
        val otherHexState = emptyState.copy()
        otherHexState.setLocation(2, 1, HexState.LOCATION_BLACK)
        otherHexState.setLocation(1, 1, HexState.LOCATION_BLACK)
        otherHexState.setLocation(1, 0, HexState.LOCATION_BLACK)
        otherHexState.setLocation(3, 1, HexState.LOCATION_WHITE)
        otherHexState.setLocation(3, 3, HexState.LOCATION_WHITE)
        assertEquals(hexState, otherHexState)
    }

    @Test
    fun testEqualityNotEqual() {
        val otherHexState = HexState(boardSize = 4)
        otherHexState.setLocation(2, 1, HexState.LOCATION_BLACK)
        otherHexState.setLocation(1, 1, HexState.LOCATION_BLACK)
        otherHexState.setLocation(1, 0, HexState.LOCATION_BLACK)
        otherHexState.setLocation(3, 1, HexState.LOCATION_WHITE)
        otherHexState.setLocation(3, 3, HexState.LOCATION_WHITE)
        assertNotEquals(hexState, otherHexState)
    }

    @Test
    fun testSetLocationOutOfBounds() {
        assertThrows(
            IllegalArgumentException::class.java
        ) { hexState.copy().setLocation(-1, -1, HexState.LOCATION_EMPTY) }
    }

    @Test
    fun testSetLocationOutOfBounds2() {
        assertThrows(
            IllegalArgumentException::class.java
        ) { hexState.copy().setLocation(hexState.boardSize, hexState.boardSize, HexState.LOCATION_EMPTY) }
    }

    @Test
    fun testSetLocation() {
        hexState.copy().setLocation(hexState.boardSize - 1, hexState.boardSize - 1, HexState.LOCATION_EMPTY)
    }

    @Test
    fun testGetLocationOutOfBounds() {
        assertThrows(IllegalArgumentException::class.java) { hexState.getLocation(-1, -1) }
    }

    @Test
    fun testGetLocationOutOfBounds2() {
        assertThrows(
            IllegalArgumentException::class.java
        ) { hexState.getLocation(hexState.boardSize, hexState.boardSize) }
    }

    @Test
    fun testGetLocation() {
        assertEquals(HexState.LOCATION_EMPTY, hexState.getLocation(
                hexState.boardSize - 1,
                hexState.boardSize - 1
            ))
    }

    @Test
    fun testLocationIsEmpty() {
        assertTrue(hexState.isLocationEmpty(0, 1))
    }

    @Test
    fun testLocationIsNotEmpty() {
        assertFalse(hexState.isLocationEmpty(1, 1))
    }

    @Test
    fun testIsLocationEmptyOutOfRange() {
        assertThrows(IllegalArgumentException::class.java) { hexState.isLocationEmpty(-1, -1) }
    }

    @Test
    fun testIsLocationEmptyOutOfRange2() {
        assertThrows(
            IllegalArgumentException::class.java
        ) { hexState.isLocationEmpty(hexState.boardSize, hexState.boardSize) }
    }

    @Test
    fun testGetNPiecesEmpty() {
        assertEquals(0, emptyState.nPieces)
    }

    @Test
    fun testGetNPieces() {
        assertEquals(5, hexState.nPieces)
    }

    @Test
    fun testToString() {
        assertEquals("""
                |- - - - -
                | - - - O -
                |  - - - - -
                |   - X X O -
                |    - X - - -
                """.trimMargin(), hexState.toString())
    }
}