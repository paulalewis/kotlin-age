package com.castlefrog.agl.domains.havannah

import org.junit.Before
import org.junit.Test

import com.google.common.truth.Truth.assertThat

class HavannahStateTest {

    val emptyState: HavannahState = HavannahSimulator.getInitialState(5)
    val havannahState: HavannahState = HavannahSimulator.getInitialState(5)

    @Before
    fun setup() {
        havannahState.locations[2][1] = HavannahState.LOCATION_BLACK
        havannahState.locations[1][1] = HavannahState.LOCATION_BLACK
        havannahState.locations[1][0] = HavannahState.LOCATION_BLACK
        havannahState.locations[3][1] = HavannahState.LOCATION_WHITE
        havannahState.locations[3][3] = HavannahState.LOCATION_WHITE
    }

    @Test
    fun testCopy() {
        assertThat(havannahState).isEqualTo(havannahState.copy())
        assertThat(havannahState.locations).isNotSameAs(havannahState.copy().locations)
    }

    @Test
    fun testCopyModifyNotEqual() {
        val stateCopy = havannahState.copy()
        stateCopy.locations[2][1] = HavannahState.LOCATION_EMPTY
        assertThat(havannahState).isNotEqualTo(stateCopy)
    }

    @Test
    fun testEquality() {
        val otherHavannahState = emptyState.copy()
        otherHavannahState.locations[2][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][0] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[3][1] = HavannahState.LOCATION_WHITE
        otherHavannahState.locations[3][3] = HavannahState.LOCATION_WHITE
        assertThat(otherHavannahState).isEqualTo(havannahState)
    }

    @Test
    fun testEqualityNotEqual() {
        val otherHavannahState = HavannahSimulator.getInitialState(4)
        otherHavannahState.locations[2][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][0] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[3][1] = HavannahState.LOCATION_WHITE
        otherHavannahState.locations[3][3] = HavannahState.LOCATION_WHITE
        assertThat(otherHavannahState).isNotEqualTo(havannahState)
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetLocationOutOfBounds() {
        havannahState.copy().locations[-1][-1] = HavannahState.LOCATION_EMPTY
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testSetLocationOutOfBounds2() {
        havannahState.copy().locations[havannahState.size][havannahState.size] = HavannahState.LOCATION_EMPTY
    }

    @Test
    fun testSetLocation() {
        havannahState.copy().locations[havannahState.base - 1][havannahState.base - 1] = HavannahState.LOCATION_EMPTY
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testGetLocationOutOfBounds() {
        havannahState.locations[-1][-1]
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testGetLocationOutOfBounds2() {
        havannahState.locations[havannahState.size][havannahState.size]
    }

    @Test
    fun testGetLocation() {
        assertThat(havannahState.locations[havannahState.base - 1][havannahState.base - 1]).isEqualTo(HavannahState.LOCATION_EMPTY)
    }

    @Test
    fun testLocationIsEmpty() {
        assertThat(havannahState.isLocationEmpty(0, 1)).isTrue()
    }

    @Test
    fun testLocationIsNotEmpty() {
        assertThat(havannahState.isLocationEmpty(1, 1)).isFalse()
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testIsLocationEmptyOutOfRange() {
        havannahState.isLocationEmpty(-1, -1)
    }

    @Test(expected = ArrayIndexOutOfBoundsException::class)
    fun testIsLocationEmptyOutOfRange2() {
        havannahState.isLocationEmpty(havannahState.size, havannahState.size)
    }

    @Test
    fun testGetNPiecesEmpty() {
        assertThat(emptyState.nPieces).isEqualTo(0)
    }

    @Test
    fun testGetNPieces() {
        assertThat(havannahState.nPieces).isEqualTo(5)
    }

    @Test
    fun testToString() {
        assertThat(havannahState.toString())
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
                """.trimMargin());
    }

}
