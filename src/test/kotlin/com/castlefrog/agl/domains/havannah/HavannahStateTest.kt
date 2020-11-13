package com.castlefrog.agl.domains.havannah

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HavannahStateTest {

    private val base = 5
    private val emptyState =
        HavannahState(base, Array(2 * base - 1) { ByteArray(2 * base - 1) }, HavannahState.TURN_BLACK)
    private val state = HavannahState(base, Array(2 * base - 1) { ByteArray(2 * base - 1) }, HavannahState.TURN_BLACK)

    @BeforeEach
    fun setup() {
        state.locations[2][1] = HavannahState.LOCATION_BLACK
        state.locations[1][1] = HavannahState.LOCATION_BLACK
        state.locations[1][0] = HavannahState.LOCATION_BLACK
        state.locations[3][1] = HavannahState.LOCATION_WHITE
        state.locations[3][3] = HavannahState.LOCATION_WHITE
    }

    @Test
    fun copy() {
        assertThat(state).isEqualTo(state.copy())
        assertThat(state.locations).isNotSameInstanceAs(state.copy().locations)
    }

    @Test
    fun copyModifyNotEqual() {
        val stateCopy = state.copy()
        stateCopy.locations[2][1] = HavannahState.LOCATION_EMPTY
        assertThat(state).isNotEqualTo(stateCopy)
    }

    @Test
    fun equality() {
        val otherHavannahState = emptyState.copy()
        otherHavannahState.locations[2][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][0] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[3][1] = HavannahState.LOCATION_WHITE
        otherHavannahState.locations[3][3] = HavannahState.LOCATION_WHITE
        assertThat(otherHavannahState).isEqualTo(state)
    }

    @Test
    fun equalityNotEqual() {
        val otherHavannahState = HavannahSimulator(4).initialState
        otherHavannahState.locations[2][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][1] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[1][0] = HavannahState.LOCATION_BLACK
        otherHavannahState.locations[3][1] = HavannahState.LOCATION_WHITE
        otherHavannahState.locations[3][3] = HavannahState.LOCATION_WHITE
        assertThat(otherHavannahState).isNotEqualTo(state)
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
        assertThat(state.locations[state.base - 1][state.base - 1])
            .isEqualTo(HavannahState.LOCATION_EMPTY)
    }

    @Test
    fun locationIsEmpty() {
        assertThat(state.isLocationEmpty(0, 1)).isTrue()
    }

    @Test
    fun locationIsNotEmpty() {
        assertThat(state.isLocationEmpty(1, 1)).isFalse()
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
        assertThat(emptyState.nPieces).isEqualTo(0)
    }

    @Test
    fun getNPieces() {
        assertThat(state.nPieces).isEqualTo(5)
    }

    @Test
    fun `toString value`() {
        assertThat(state.toString())
            .isEqualTo(
                """
                |    - - - - -
                |   - - - - - -
                |  - - - - - - -
                | - - - - - - - -
                |- - - - - - - - -
                | - - - O - - - -
                |  - - - - - - -
                |   - X X O - -
                |    - X - - -
                """.trimMargin()
            )
    }
}
