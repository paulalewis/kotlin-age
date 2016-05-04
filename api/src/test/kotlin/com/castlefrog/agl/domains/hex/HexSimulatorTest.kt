package com.castlefrog.agl.domains.hex

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.ArrayList

class HexSimulatorTest {

    @Test
    fun stateTransitionMove1() {
        val simulator = HexSimulator.create(5, true)
        val actions = ArrayList<HexAction?>()
        actions.add(HexAction.valueOf(0, 0))
        actions.add(null)
        simulator.stateTransition(actions)
        val state = HexSimulator.getInitialState(5)
        state.setLocation(0, 0, HexState.LOCATION_BLACK)
        state.agentTurn = HexState.TURN_WHITE
        assertThat(simulator.state).isEqualTo(state)
    }

    @Test(expected = IllegalArgumentException::class)
    fun stateTransitionIllegalMove() {
        val simulator = HexSimulator.create(5, true)
        val actions = ArrayList<HexAction?>()
        actions.add(HexAction.valueOf(0, 0))
        actions.add(null)
        simulator.stateTransition(actions)
        simulator.stateTransition(actions)
    }

    @Test
    fun stateTransitionMove2SameLocation() {
        val simulator = HexSimulator.create(5, true)
        val actions = ArrayList<HexAction?>()
        actions.add(HexAction.valueOf(0, 0))
        actions.add(null)
        simulator.stateTransition(actions)
        actions.clear()
        actions.add(null)
        actions.add(HexAction.valueOf(0, 0))
        simulator.stateTransition(actions)
        val state = HexSimulator.getInitialState(5)
        state.setLocation(0, 0, HexState.LOCATION_WHITE)
        state.agentTurn = HexState.TURN_BLACK
        assertThat(simulator.state).isEqualTo(state)
    }

    @Test
    fun stateTransitionMove2DifferentLocation() {
        val simulator = HexSimulator.create(5, true)
        val actions = ArrayList<HexAction?>()
        actions.add(HexAction.valueOf(0, 0))
        actions.add(null)
        simulator.stateTransition(actions)
        actions.clear()
        actions.add(null)
        actions.add(HexAction.valueOf(0, 1))
        simulator.stateTransition(actions)
        val state = HexSimulator.getInitialState(5)
        state.setLocation(0, 0, HexState.LOCATION_BLACK)
        state.setLocation(0, 1, HexState.LOCATION_WHITE)
        state.agentTurn = HexState.TURN_BLACK
        assertThat(simulator.state).isEqualTo(state)
    }

}