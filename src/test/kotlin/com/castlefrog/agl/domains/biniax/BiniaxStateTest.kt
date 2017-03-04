package com.castlefrog.agl.domains.biniax

import com.google.common.truth.Truth
import org.junit.Test

class BiniaxStateTest {

    @Test
    fun testCopy() {
        val state = BiniaxSimulator().getInitialState()
        Truth.assertThat(state).isEqualTo(state.copy())
    }

    @Test
    fun testCopyModification() {
        val state = BiniaxSimulator().getInitialState()
        val copyState = state.copy()
        state.locations[0][0] = -8
        Truth.assertThat(copyState.locations[0][0]).isNotEqualTo(-8)
    }

    @Test
    fun testToString() {
        val locations = Array(5) { byteArrayOf(0, 0, -1, 4, 18, 0, 11)}
        val state = BiniaxState(locations, 10, 2, 0)
        Truth.assertThat(state.toString()).isEqualTo("""
        |Turns: 0
        |Free Moves: 2
        |---------------------
        |:                   :
        |:                   :
        |:<X> <X> <X> <X> <X>:
        |:[D] [D] [D] [D] [D]:
        |:A-H A-H A-H A-H A-H:
        |:                   :
        |:A-A A-A A-A A-A A-A:
        |---------------------
        """.trimMargin())
    }

}