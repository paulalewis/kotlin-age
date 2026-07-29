package com.castlefrog.agl.domains.havannah

import org.junit.Before
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class HavannahStateTest {

    private val base = 5
    private val emptyState =
        HavannahState(base, Array(2 * base - 1) { ByteArray(2 * base - 1) }, HavannahState.TURN_BLACK)
    private val state = HavannahState(base, Array(2 * base - 1) { ByteArray(2 * base - 1) }, HavannahState.TURN_BLACK)

    @Before
    fun setup() {
        state.locations[2][1] = HavannahState.LOCATION_BLACK
        state.locations[1][1] = HavannahState.LOCATION_BLACK
        state.locations[1][0] = HavannahState.LOCATION_BLACK
        state.locations[3][1] = HavannahState.LOCATION_WHITE
        state.locations[3][3] = HavannahState.LOCATION_WHITE
    }

    @Test
    fun copy() {
        assertEquals(state.copy(), state)
        assertNotSame(state.copy().locations, state.locations)
    }

    @Test
    fun copyModifyNotEqual() {
        val stateCopy = state.copy()
        stateCopy.locations[2][1] = HavannahState.LOCATION_EMPTY
        assertNotEquals(stateCopy, state)
    }

    @Test
    fun equality() {
        val otherHavannahState = emptyState.copy()
        otherHavannahState.locations[2][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][0] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[3][1] = HavannahState.LOCATION_WHITE
        otherHavannahState.locations[3][3] = HavannahState.LOCATION_WHITE
        assertEquals(state, otherHavannahState)
    }

    @Test
    fun equalityNotEqual() {
        val otherHavannahState = HavannahSimulator(4).initialState
        otherHavannahState.locations[2][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][0] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[3][1] = HavannahState.LOCATION_WHITE
        otherHavannahState.locations[3][3] = HavannahState.LOCATION_WHITE
        assertNotEquals(state, otherHavannahState)
    }

    @Test
    fun setLocationOutOfBounds() {
        assertThrows(
            ArrayIndexOutOfBoundsException::class.java
        ) { state.copy().locations[-1][-1] = HavannahState.LOCATION_EMPTY }
    }

    @Test
    fun setLocationOutOfBounds2() {
        assertThrows(
            ArrayIndexOutOfBoundsException::class.java
        ) { state.copy().locations[state.locations.size][state.locations.size] = HavannahState.LOCATION_EMPTY }
    }

    @Test
    fun setLocation() {
        state.copy().locations[state.base - 1][state.base - 1] = HavannahState.LOCATION_EMPTY
    }

    @Test
    fun getLocationOutOfBounds() {
        assertThrows(
            ArrayIndexOutOfBoundsException::class.java
        ) { state.locations[-1][-1] }
    }

    @Test
    fun getLocationOutOfBounds2() {
        assertThrows(
            ArrayIndexOutOfBoundsException::class.java
        ) { state.locations[state.locations.size][state.locations.size] }
    }

    @Test
    fun getLocation() {
        assertEquals(HavannahState.LOCATION_EMPTY, state.locations[state.base - 1][state.base - 1])
    }

    @Test
    fun locationIsEmpty() {
        assertTrue(state.isLocationEmpty(0, 1))
    }

    @Test
    fun locationIsNotEmpty() {
        assertFalse(state.isLocationEmpty(1, 1))
    }

    @Test
    fun isLocationEmptyOutOfRange() {
        assertThrows(
            ArrayIndexOutOfBoundsException::class.java
        ) { state.isLocationEmpty(-1, -1) }
    }

    @Test
    fun isLocationEmptyOutOfRange2() {
        assertThrows(
            ArrayIndexOutOfBoundsException::class.java
        ) { state.isLocationEmpty(state.locations.size, state.locations.size) }
    }

    @Test
    fun getNPiecesEmpty() {
        assertEquals(0, emptyState.nPieces)
    }

    @Test
    fun getNPieces() {
        assertEquals(5, state.nPieces)
    }

    @Test
    fun `toString value`() {
        assertEquals("""
                |    - - - - -
                |   - - - - - -
                |  - - - - - - -
                | - - - - - - - -
                |- - - - - - - - -
                | - - - O - - - -
                |  - - - - - - -
                |   - X X O - -
                |    - X - - -
                """.trimMargin(), state.toString())
    }
}
