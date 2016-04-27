package com.castlefrog.agl.domains.hex

import org.junit.Before
import org.junit.Test

import com.google.common.truth.Truth.assertThat

class HexStateTest {

    val emptyState: HexState = HexSimulator.getInitialState(5)
    val hexState: HexState = HexSimulator.getInitialState(5)

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
        val otherHexState = HexSimulator.getInitialState(4)
        otherHexState.setLocation(2, 1, HexState.LOCATION_BLACK)
        otherHexState.setLocation(1, 1, HexState.LOCATION_BLACK)
        otherHexState.setLocation(1, 0, HexState.LOCATION_BLACK)
        otherHexState.setLocation(3, 1, HexState.LOCATION_WHITE)
        otherHexState.setLocation(3, 3, HexState.LOCATION_WHITE)
        assertThat(otherHexState).isNotEqualTo(hexState)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetLocationOutOfBounds() {
        hexState.copy().setLocation(-1, -1, HexState.LOCATION_EMPTY)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSetLocationOutOfBounds2() {
        hexState.copy().setLocation(hexState.boardSize, hexState.boardSize, HexState.LOCATION_EMPTY)
    }

    @Test
    fun testSetLocation() {
        hexState.copy().setLocation(hexState.boardSize - 1, hexState.boardSize - 1, HexState.LOCATION_EMPTY)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testGetLocationOutOfBounds() {
        hexState.getLocation(-1, -1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testGetLocationOutOfBounds2() {
        hexState.getLocation(hexState.boardSize, hexState.boardSize)
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

    @Test(expected = IllegalArgumentException::class)
    fun testIsLocationEmptyOutOfRange() {
        hexState.isLocationEmpty(-1, -1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testIsLocationEmptyOutOfRange2() {
        hexState.isLocationEmpty(hexState.boardSize, hexState.boardSize)
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
                .isEqualTo("turn = 0\n- - - - - \n - - - O - \n  - - - - - \n   - X X O - \n    - X - - - \n")
    }

}