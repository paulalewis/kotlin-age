package com.castlefrog.agl

import com.castlefrog.agl.domains.hex.HexAction
import com.castlefrog.agl.domains.hex.HexSimulator
import com.google.common.truth.Truth
import org.junit.Test

class SimulatorTest {

    @Test
    fun nAgents() {
        val simulator = HexSimulator.create(5, true)
        Truth.assertThat(simulator.nAgents).isEqualTo(2)
    }

    @Test
    fun isTerminalStateFalse() {
        val simulator = HexSimulator.create(5, true)
        Truth.assertThat(simulator.isTerminalState).isFalse()
    }

    @Test
    fun isTerminalStateTrue() {
        val simulator = HexSimulator.create(3, false)
        simulator.stateTransition(arrayListOf(HexAction.valueOf(0, 0), null))
        simulator.stateTransition(arrayListOf(null, HexAction.valueOf(0, 1)))
        simulator.stateTransition(arrayListOf(HexAction.valueOf(1, 1), null))
        simulator.stateTransition(arrayListOf(null, HexAction.valueOf(0, 2)))
        simulator.stateTransition(arrayListOf(HexAction.valueOf(2, 2), null))
        Truth.assertThat(simulator.isTerminalState).isTrue()
    }

}