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
    fun testLocationIsEmpty() {
        assertThat(hexState.isLocationEmpty(0, 1)).isTrue()
    }

    @Test
    fun testLocationIsNotEmpty() {
        assertThat(hexState.isLocationEmpty(1, 1)).isFalse()
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
        assertThat(hexState.toString()).isEqualTo("- - - - - \n - - - O - \n  - - - - - \n   - X X O - \n    - X - - - \n")
    }

}