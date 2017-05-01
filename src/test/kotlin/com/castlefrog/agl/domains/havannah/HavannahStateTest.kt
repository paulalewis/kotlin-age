package com.castlefrog.agl.domains.havannah

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class HavannahStateTest {

    val base = 5
    val emptyState = HavannahState(base, Array(2 * base - 1) { ByteArray(2 * base - 1) }, HavannahState.TURN_BLACK)
    val state = HavannahState(base, Array(2 * base - 1) { ByteArray(2 * base - 1) }, HavannahState.TURN_BLACK)

    @Before
    fun setup() {
        state.locations[2][1] = HavannahState.LOCATION_BLACK
        state.locations[1][1] = HavannahState.LOCATION_BLACK
        state.locations[1][0] = HavannahState.LOCATION_BLACK
        state.locations[3][1] = HavannahState.LOCATION_WHITE
        state.locations[3][3] = HavannahState.LOCATION_WHITE
    }

    @Test
    fun testCopy() {
        assertThat(state).isEqualTo(state.copy())
        assertThat(state.locations).isNotSameAs(state.copy().locations)
    }

    @Test
    fun testCopyModifyNotEqual() {
        val stateCopy = state.copy()
        stateCopy.locations[2][1] = HavannahState.LOCATION_EMPTY
        assertThat(state).isNotEqualTo(stateCopy)
    }

    @Test
    fun testEquality() {
        val otherHavannahState = emptyState.copy()
        otherHavannahState.locations[2][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][0] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[3][1] = HavannahState.LOCATION_WHITE
        otherHavannahState.locations[3][3] = HavannahState.LOCATION_WHITE
        assertThat(otherHavannahState).isEqualTo(state)
    }

    @Test
    fun testEqualityNotEqual() {
        val otherHavannahState = HavannahSimulator(4).initialState
        otherHavannahState.locations[2][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][0] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[3][1] = HavannahState.LOCATION_WHITE
        otherHavannahState.locations[3][3] = HavannahState.LOCATION_WHITE
        assertThat(otherHavannahState).isNotEqualTo(state)
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetLocationOutOfBounds() {
        state.copy().locations[-1][-1] = HavannahState.LOCATION_EMPTY
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetLocationOutOfBounds2() {
        state.copy().locations[state.locations.size][state.locations.size] = HavannahState.LOCATION_EMPTY
    }

    @Test
    fun testSetLocation() {
        state.copy().locations[state.base - 1][state.base - 1] = HavannahState.LOCATION_EMPTY
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testGetLocationOutOfBounds() {
        state.locations[-1][-1]
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testGetLocationOutOfBounds2() {
        state.locations[state.locations.size][state.locations.size]
    }

    @Test
    fun testGetLocation() {
        assertThat(state.locations[state.base - 1][state.base - 1])
                .isEqualTo(HavannahState.LOCATION_EMPTY)
    }

    @Test
    fun testLocationIsEmpty() {
        assertThat(state.isLocationEmpty(0, 1)).isTrue()
    }

    @Test
    fun testLocationIsNotEmpty() {
        assertThat(state.isLocationEmpty(1, 1)).isFalse()
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testIsLocationEmptyOutOfRange() {
        state.isLocationEmpty(-1, -1)
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testIsLocationEmptyOutOfRange2() {
        state.isLocationEmpty(state.locations.size, state.locations.size)
    }

    @Test
    fun testGetNPiecesEmpty() {
        assertThat(emptyState.nPieces).isEqualTo(0)
    }

    @Test
    fun testGetNPieces() {
        assertThat(state.nPieces).isEqualTo(5)
    }

    @Test
    fun testToString() {
        assertThat(state.toString())
                .isEqualTo("""
                |    - - - - -
                |   - - - - - -
                |  - - - - - - -
                | - - - - - - - -
                |- - - - - - - - -
                | - - - O - - - -
                |  - - - - - - -
                |   - X X O - -
                |    - X - - -
                """.trimMargin())
    }

}
