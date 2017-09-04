package com.castlefrog.agl.domains.hex

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HexStateTest {

    private val emptyState: HexState = HexState(boardSize = 5)
    private val hexState: HexState = HexState(boardSize = 5)

    @BeforeEach
    fun setup() {
        hexState.setLocation(2, 1, HexState.LOCATION_BLACK)
        hexState.setLocation(1, 1, HexState.LOCATION_BLACK)
        hexState.setLocation(1, 0, HexState.LOCATION_BLACK)
        hexState.setLocation(3, 1, HexState.LOCATION_WHITE)
        hexState.setLocation(3, 3, HexState.LOCATION_WHITE)
    }

    @Test
    fun testCopy() {
        assertThat(hexState).isEqualTo(hexState.copy())
    }

    @Test
    fun testEquality() {
        val otherHexState = emptyState.copy()
        otherHexState.setLocation(2, 1, HexState.LOCATION_BLACK)
        otherHexState.setLocation(1, 1, HexState.LOCATION_BLACK)
        otherHexState.setLocation(1, 0, HexState.LOCATION_BLACK)
        otherHexState.setLocation(3, 1, HexState.LOCATION_WHITE)
        otherHexState.setLocation(3, 3, HexState.LOCATION_WHITE)
        assertThat(otherHexState).isEqualTo(hexState)
    }

    @Test
    fun testEqualityNotEqual() {
        val otherHexState = HexState(boardSize = 4)
        otherHexState.setLocation(2, 1, HexState.LOCATION_BLACK)
        otherHexState.setLocation(1, 1, HexState.LOCATION_BLACK)
        otherHexState.setLocation(1, 0, HexState.LOCATION_BLACK)
        otherHexState.setLocation(3, 1, HexState.LOCATION_WHITE)
        otherHexState.setLocation(3, 3, HexState.LOCATION_WHITE)
        assertThat(otherHexState).isNotEqualTo(hexState)
    }

    @Test
    fun testSetLocationOutOfBounds() {
        assertThrows(IllegalArgumentException::class.java,
                { hexState.copy().setLocation(-1, -1, HexState.LOCATION_EMPTY) })
    }

    @Test
    fun testSetLocationOutOfBounds2() {
        assertThrows(IllegalArgumentException::class.java,
                { hexState.copy().setLocation(hexState.boardSize, hexState.boardSize, HexState.LOCATION_EMPTY) })
    }

    @Test
    fun testSetLocation() {
        hexState.copy().setLocation(hexState.boardSize - 1, hexState.boardSize - 1, HexState.LOCATION_EMPTY)
    }

    @Test
    fun testGetLocationOutOfBounds() {
        assertThrows(IllegalArgumentException::class.java, { hexState.getLocation(-1, -1) })
    }

    @Test
    fun testGetLocationOutOfBounds2() {
        assertThrows(IllegalArgumentException::class.java,
                { hexState.getLocation(hexState.boardSize, hexState.boardSize) })
    }

    @Test
    fun testGetLocation() {
        assertThat(hexState.getLocation(hexState.boardSize - 1, hexState.boardSize - 1)).isEqualTo(HexState.LOCATION_EMPTY)
    }

    @Test
    fun testLocationIsEmpty() {
        assertThat(hexState.isLocationEmpty(0, 1)).isTrue()
    }

    @Test
    fun testLocationIsNotEmpty() {
        assertThat(hexState.isLocationEmpty(1, 1)).isFalse()
    }

    @Test
    fun testIsLocationEmptyOutOfRange() {
        assertThrows(IllegalArgumentException::class.java, { hexState.isLocationEmpty(-1, -1) })
    }

    @Test
    fun testIsLocationEmptyOutOfRange2() {
        assertThrows(IllegalArgumentException::class.java,
                { hexState.isLocationEmpty(hexState.boardSize, hexState.boardSize) })
    }

    @Test
    fun testGetNPiecesEmpty() {
        assertThat(emptyState.nPieces).isEqualTo(0)
    }

    @Test
    fun testGetNPieces() {
        assertThat(hexState.nPieces).isEqualTo(5)
    }

    @Test
    fun testToString() {
        assertThat(hexState.toString())
                .isEqualTo("""
                |- - - - -
                | - - - O -
                |  - - - - -
                |   - X X O -
                |    - X - - -
                """.trimMargin())
    }

}