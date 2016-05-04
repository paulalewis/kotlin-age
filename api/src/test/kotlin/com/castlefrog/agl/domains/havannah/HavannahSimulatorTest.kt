package com.castlefrog.agl.domains.havannah

import com.castlefrog.agl.AdversarialSimulator
import com.castlefrog.agl.IllegalActionException
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.ArrayList

class HavannahSimulatorTest {

    @Test
    fun stateTransitionMove1() {
        val simulator = HavannahSimulator.create(5, true)
        val actions = ArrayList<HavannahAction?>()
        actions.add(HavannahAction.valueOf(0, 0))
        actions.add(null)
        simulator.stateTransition(actions)
        val state = HavannahSimulator.getInitialState(5)
        state.locations[0][0] = HavannahState.LOCATION_BLACK
        state.agentTurn = HavannahState.TURN_WHITE
        assertThat(simulator.state).isEqualTo(state)
    }

    @Test(expected = IllegalActionException::class)
    fun stateTransitionIllegalMove() {
        val simulator = HavannahSimulator.create(5, true)
        val actions = ArrayList<HavannahAction?>()
        actions.add(HavannahAction.valueOf(0, 0))
        actions.add(null)
        simulator.stateTransition(actions)
        simulator.stateTransition(actions)
    }

    @Test
    fun stateTransitionMove2SameLocation() {
        val simulator = HavannahSimulator.create(5, true)
        val actions = ArrayList<HavannahAction?>()
        actions.add(HavannahAction.valueOf(0, 0))
        actions.add(null)
        simulator.stateTransition(actions)
        actions.clear()
        actions.add(null)
        actions.add(HavannahAction.valueOf(0, 0))
        simulator.stateTransition(actions)
        val state = HavannahSimulator.getInitialState(5)
        state.locations[0][0] = HavannahState.LOCATION_WHITE
        state.agentTurn = HavannahState.TURN_BLACK
        assertThat(simulator.state).isEqualTo(state)
    }

    @Test
    fun stateTransitionMove2DifferentLocation() {
        val simulator = HavannahSimulator.create(5, true)
        val actions = ArrayList<HavannahAction?>()
        actions.add(HavannahAction.valueOf(0, 0))
        actions.add(null)
        simulator.stateTransition(actions)
        actions.clear()
        actions.add(null)
        actions.add(HavannahAction.valueOf(0, 1))
        simulator.stateTransition(actions)
        val state = HavannahSimulator.getInitialState(5)
        state.locations[0][0] = HavannahState.LOCATION_BLACK
        state.locations[0][1] = HavannahState.LOCATION_WHITE
        state.agentTurn = HavannahState.TURN_BLACK
        assertThat(simulator.state).isEqualTo(state)
    }

    @Test
    fun stateBlackWinsRing() {
        val locations = Array(9) { ByteArray(9) }
        locations[2][2] = HavannahState.LOCATION_BLACK
        locations[2][3] = HavannahState.LOCATION_BLACK
        locations[3][2] = HavannahState.LOCATION_BLACK
        locations[4][4] = HavannahState.LOCATION_BLACK
        locations[4][3] = HavannahState.LOCATION_BLACK
        locations[3][4] = HavannahState.LOCATION_BLACK
        locations[3][3] = HavannahState.LOCATION_WHITE
        locations[0][0] = HavannahState.LOCATION_WHITE
        locations[0][1] = HavannahState.LOCATION_WHITE
        locations[1][0] = HavannahState.LOCATION_WHITE
        locations[1][2] = HavannahState.LOCATION_WHITE
        val state = HavannahState(5, locations, HavannahState.TURN_WHITE)
        val simulator = HavannahSimulator.create(state, false)
        assertThat(simulator.rewards).isEqualTo(intArrayOf(1, -1))
    }

}
