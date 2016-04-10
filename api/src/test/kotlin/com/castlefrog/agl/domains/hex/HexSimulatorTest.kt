package com.castlefrog.agl.domains.hex

import com.castlefrog.agl.IllegalActionException
import com.castlefrog.agl.TurnType
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.ArrayList
import java.util.HashSet

class HexSimulatorTest {

    @Test
    fun stateTransitionMove1() {
        val simulator = HexSimulator.create(5, TurnType.SEQUENTIAL)
        val actions = ArrayList<HexAction?>()
        actions.add(HexAction.valueOf(0, 0))
        actions.add(null)
        simulator.stateTransition(actions)
        val state = HexSimulator.getInitialState(5)
        state.setLocation(0, 0, HexState.LOCATION_BLACK)
        state.agentTurn = HexState.TURN_WHITE
        assertThat(simulator.state.isSecondMove()).isTrue()
        assertThat(simulator.state).isEqualTo(state)
    }

    @Test(expected = IllegalActionException::class)
    fun stateTransitionIllegalMove() {
        val simulator = HexSimulator.create(5, TurnType.SEQUENTIAL)
        val actions = ArrayList<HexAction?>()
        actions.add(HexAction.valueOf(0, 0))
        actions.add(null)
        simulator.stateTransition(actions)
        simulator.stateTransition(actions)
    }

    @Test
    fun stateTransitionMove2SameLocation() {
        val simulator = HexSimulator.create(5, TurnType.SEQUENTIAL)
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
        val simulator = HexSimulator.create(5, TurnType.SEQUENTIAL)
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

    @Test
    fun winningConnectionNoWinner() {
        val state = HexSimulator.getInitialState(5)
        assertThat(HexSimulator.winningConnection(state)).isEmpty()
    }

    @Test
    fun winningConnectionNoWinner2() {
        val state = HexSimulator.getInitialState(5)
        state.setLocation(0, 0, HexState.LOCATION_WHITE)
        state.setLocation(1, 0, HexState.LOCATION_WHITE)
        state.setLocation(2, 0, HexState.LOCATION_WHITE)
        state.setLocation(3, 0, HexState.LOCATION_WHITE)
        state.setLocation(4, 0, HexState.LOCATION_WHITE)
        assertThat(HexSimulator.winningConnection(state)).isEmpty()
    }

    @Test
    fun winningConnectionNoWinner3() {
        val state = HexSimulator.getInitialState(5)
        state.setLocation(0, 0, HexState.LOCATION_BLACK)
        state.setLocation(0, 1, HexState.LOCATION_BLACK)
        state.setLocation(0, 2, HexState.LOCATION_BLACK)
        state.setLocation(0, 3, HexState.LOCATION_BLACK)
        state.setLocation(0, 4, HexState.LOCATION_BLACK)
        assertThat(HexSimulator.winningConnection(state)).isEmpty()
    }

    @Test
    fun winningConnectionBlack() {
        val state = HexSimulator.getInitialState(5)
        state.setLocation(0, 0, HexState.LOCATION_BLACK)
        state.setLocation(1, 0, HexState.LOCATION_BLACK)
        state.setLocation(2, 0, HexState.LOCATION_BLACK)
        state.setLocation(3, 0, HexState.LOCATION_BLACK)
        state.setLocation(4, 0, HexState.LOCATION_BLACK)
        state.setLocation(0, 1, HexState.LOCATION_WHITE)
        state.setLocation(1, 1, HexState.LOCATION_WHITE)
        state.setLocation(2, 1, HexState.LOCATION_WHITE)
        state.setLocation(3, 1, HexState.LOCATION_WHITE)
        state.agentTurn = HexState.TURN_WHITE
        val connection = HashSet<Pair<Int, Int>>()
        connection.add(Pair(0, 0))
        connection.add(Pair(1, 0))
        connection.add(Pair(2, 0))
        connection.add(Pair(3, 0))
        connection.add(Pair(4, 0))
        assertThat(HexSimulator.winningConnection(state)).isEqualTo(connection)
    }

    @Test
    fun winningConnectionWhite() {
        val state = HexSimulator.getInitialState(5)
        state.setLocation(0, 0, HexState.LOCATION_WHITE)
        state.setLocation(0, 1, HexState.LOCATION_WHITE)
        state.setLocation(0, 2, HexState.LOCATION_WHITE)
        state.setLocation(0, 3, HexState.LOCATION_WHITE)
        state.setLocation(0, 4, HexState.LOCATION_WHITE)
        state.setLocation(1, 0, HexState.LOCATION_BLACK)
        state.setLocation(1, 1, HexState.LOCATION_BLACK)
        state.setLocation(1, 2, HexState.LOCATION_BLACK)
        state.setLocation(1, 3, HexState.LOCATION_BLACK)
        val connection = HashSet<Pair<Int, Int>>()
        connection.add(Pair(0, 0))
        connection.add(Pair(0, 1))
        connection.add(Pair(0, 2))
        connection.add(Pair(0, 3))
        connection.add(Pair(0, 4))
        assertThat(HexSimulator.winningConnection(state)).isEqualTo(connection)
    }

}