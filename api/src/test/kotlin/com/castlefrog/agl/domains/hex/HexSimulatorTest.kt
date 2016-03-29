package com.castlefrog.agl.domains.hex

import com.castlefrog.agl.TurnType
import org.junit.Test

import com.google.common.truth.Truth.assertThat
import java.util.*

class HexSimulatorTest {

    @Test
    fun stateTransition() {
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
        state.agentTurn = HexState.TURN_WHITE.toByte()
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
        val connection = HashSet<Pair<Int, Int>>()
        connection.add(Pair(0, 0))
        connection.add(Pair(0, 1))
        connection.add(Pair(0, 2))
        connection.add(Pair(0, 3))
        connection.add(Pair(0, 4))
        assertThat(HexSimulator.winningConnection(state)).isEqualTo(connection)
    }

}