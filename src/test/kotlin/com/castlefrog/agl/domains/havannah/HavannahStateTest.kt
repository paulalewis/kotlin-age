package com.castlefrog.agl.domains.havannah

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test

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
        Truth.assertThat(havannahState).isEqualTo(havannahState.copy())
        Truth.assertThat(havannahState.locations).isNotSameAs(havannahState.copy().locations)
    }

    @Test
    fun testCopyModifyNotEqual() {
        val stateCopy = havannahState.copy()
        stateCopy.locations[2][1] = HavannahState.LOCATION_EMPTY
        Truth.assertThat(havannahState).isNotEqualTo(stateCopy)
    }

    @Test
    fun testEquality() {
        val otherHavannahState = emptyState.copy()
        otherHavannahState.locations[2][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][0] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[3][1] = HavannahState.LOCATION_WHITE
        otherHavannahState.locations[3][3] = HavannahState.LOCATION_WHITE
        Truth.assertThat(otherHavannahState).isEqualTo(havannahState)
    }

    @Test
    fun testEqualityNotEqual() {
        val otherHavannahState = HavannahSimulator.getInitialState(4)
        otherHavannahState.locations[2][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][0] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[3][1] = HavannahState.LOCATION_WHITE
        otherHavannahState.locations[3][3] = HavannahState.LOCATION_WHITE
        Truth.assertThat(otherHavannahState).isNotEqualTo(havannahState)
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
        Truth.assertThat(havannahState.locations[havannahState.base - 1][havannahState.base - 1])
                .isEqualTo(HavannahState.LOCATION_EMPTY)
    }

    @Test
    fun testLocationIsEmpty() {
        Truth.assertThat(havannahState.isLocationEmpty(0, 1)).isTrue()
    }

    @Test
    fun testLocationIsNotEmpty() {
        Truth.assertThat(havannahState.isLocationEmpty(1, 1)).isFalse()
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
        Truth.assertThat(emptyState.nPieces).isEqualTo(0)
    }

    @Test
    fun testGetNPieces() {
        Truth.assertThat(havannahState.nPieces).isEqualTo(5)
    }

    @Test
    fun testCorners() {
        val state = HavannahSimulator.getInitialState(6)
        Truth.assertThat(state.corners).isEqualTo(
                arrayOf(intArrayOf(0, 0), intArrayOf(0, 5), intArrayOf(5, 0),
                        intArrayOf(5, 10), intArrayOf(10, 5), intArrayOf(10, 10))
        )
    }

    @Test
    fun testSides() {
        val state = HavannahSimulator.getInitialState(6)
        Truth.assertThat(state.sides).isEqualTo(
                arrayOf(
                        arrayOf(
                                intArrayOf(0, 1),
                                intArrayOf(0, 2),
                                intArrayOf(0, 3),
                                intArrayOf(0, 4)
                        ),
                        arrayOf(
                                intArrayOf(1, 0),
                                intArrayOf(2, 0),
                                intArrayOf(3, 0),
                                intArrayOf(4, 0)
                        ),
                        arrayOf(
                                intArrayOf(1, 6),
                                intArrayOf(2, 7),
                                intArrayOf(3, 8),
                                intArrayOf(4, 9)
                        ),
                        arrayOf(
                                intArrayOf(6, 10),
                                intArrayOf(7, 10),
                                intArrayOf(8, 10),
                                intArrayOf(9, 10)
                        ),
                        arrayOf(
                                intArrayOf(10, 6),
                                intArrayOf(10, 7),
                                intArrayOf(10, 8),
                                intArrayOf(10, 9)
                        ),
                        arrayOf(
                                intArrayOf(6, 1),
                                intArrayOf(7, 2),
                                intArrayOf(8, 3),
                                intArrayOf(9, 4)
                        )
                )
        )
    }

    @Test
    fun testToString() {
        Truth.assertThat(havannahState.toString())
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
