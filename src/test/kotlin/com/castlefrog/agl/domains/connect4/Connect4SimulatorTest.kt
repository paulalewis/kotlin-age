package com.castlefrog.agl.domains.connect4

import com.castlefrog.agl.IllegalActionException
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class Connect4SimulatorTest {

    @Test
    fun getInitialState() {
        val simulator = Connect4Simulator()
        val initialState = simulator.initialState
        assertEquals("""
        |-----------------
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |: - - - - - - - :
        |-----------------
        """.trimMargin(), initialState.toString())
    }

    @Test
    fun calculateRewardsInitialState() {
        val simulator = Connect4Simulator()
        val state = simulator.initialState
        assertArrayEquals(intArrayOf(0, 0), simulator.calculateRewards(state))
    }

    @Test
    fun calculateRewardsAfterSomeMovesNoWinner() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(1, 16384))
        assertArrayEquals(intArrayOf(0, 0), simulator.calculateRewards(state))
    }

    @Test
    fun calculateRewardsHorizontalWinnerPlayer1() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(2113665, 33026))
        assertArrayEquals(intArrayOf(1, -1), simulator.calculateRewards(state))
    }

    @Test
    fun calculateRewardsHorizontalWinnerPlayer2() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(57209232818176, 8865355661312))
        assertArrayEquals(intArrayOf(-1, 1), simulator.calculateRewards(state))
    }

    @Test
    fun calculateRewardsVerticalWinnerPlayer1() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(31457280, 268451969))
        assertArrayEquals(intArrayOf(1, -1), simulator.calculateRewards(state))
    }

    @Test
    fun calculateRewardsVerticalWinnerPlayer2() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(17280, 15))
        assertArrayEquals(intArrayOf(-1, 1), simulator.calculateRewards(state))
    }

    @Test
    fun calculateLegalActionsInitialState() {
        val simulator = Connect4Simulator()
        val state = simulator.initialState
        assertEquals(arrayListOf(
                setOf(
                    Connect4Action.valueOf(0),
                    Connect4Action.valueOf(1),
                    Connect4Action.valueOf(2),
                    Connect4Action.valueOf(3),
                    Connect4Action.valueOf(4),
                    Connect4Action.valueOf(5),
                    Connect4Action.valueOf(6)
                ),
                setOf()
            ), simulator.calculateLegalActions(state))
    }

    @Test
    fun calculateLegalActionsOneMove() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(1, 0))
        assertEquals(arrayListOf(
                setOf(),
                setOf(
                    Connect4Action.valueOf(0),
                    Connect4Action.valueOf(1),
                    Connect4Action.valueOf(2),
                    Connect4Action.valueOf(3),
                    Connect4Action.valueOf(4),
                    Connect4Action.valueOf(5),
                    Connect4Action.valueOf(6)
                )
            ), simulator.calculateLegalActions(state))
    }

    @Test
    fun calculateLegalActionsFullColumn() {
        val simulator = Connect4Simulator()
        val state = Connect4State(longArrayOf(2688, 5376))
        assertEquals(arrayListOf(
                setOf(
                    Connect4Action.valueOf(0),
                    Connect4Action.valueOf(2),
                    Connect4Action.valueOf(3),
                    Connect4Action.valueOf(4),
                    Connect4Action.valueOf(5),
                    Connect4Action.valueOf(6)
                ),
                setOf()
            ), simulator.calculateLegalActions(state))
    }

    @Test
    fun stateTransitionEmptyActionsList() {
        val simulator = Connect4Simulator()
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(simulator.initialState, emptyList())
        }
    }

    @Test
    fun stateTransitionWrongArityWhenSecondPlayerToMove() {
        val simulator = Connect4Simulator()
        val state = simulator.stateTransition(simulator.initialState, listOf(Connect4Action.valueOf(0), null))
        // Only one entry; agent turn is 1 → size check must throw IllegalActionException (not IndexOutOfBounds).
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(state, listOf(null))
        }
    }

    @Test
    fun stateTransitionIllegalMove() {
        val simulator = Connect4Simulator()
        val state = simulator.stateTransition(simulator.initialState, listOf(Connect4Action.valueOf(2), null))
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(state, listOf(Connect4Action.valueOf(2), null))
        }
    }

    @Test
    fun stateTransitionNullActionForPlayerToMove() {
        val simulator = Connect4Simulator()
        assertThrows(IllegalActionException::class.java) {
            simulator.stateTransition(simulator.initialState, listOf(null, null))
        }
    }

    @Test
    fun stateTransitionMove1() {
        val simulator = Connect4Simulator()
        val state = simulator.stateTransition(simulator.initialState, listOf(Connect4Action.valueOf(3), null))
        val expectedState = Connect4State(longArrayOf(2097152, 0))
        assertEquals(expectedState, state)
    }

    @Test
    fun stateTransitionMove2() {
        val simulator = Connect4Simulator()
        val state2 = simulator.stateTransition(simulator.initialState, listOf(Connect4Action.valueOf(2), null))
        val state3 = simulator.stateTransition(state2, listOf(null, Connect4Action.valueOf(2)))
        val expectedState = Connect4State(longArrayOf(16384, 32768))
        assertEquals(expectedState, state3)
    }

    /**
     * Tree-search style: warm the column-heights cache, transition an equal copy (which
     * must not mutate cached heights via `columnHeights[location]++`), then drop on another
     * equal copy. The piece must land at the true free height, not height+1.
     */
    @Test
    fun stateTransitionOnCopyDoesNotCorruptCachedColumnHeights() {
        // Column 0 rows 0..4 occupied (alternating), row 5 empty. 3 black, 2 white → white to move.
        val state = Connect4State(longArrayOf(0b10101, 0b01010))
        val simulator = Connect4Simulator()
        val dropColumn0 = Connect4Action.valueOf(0)

        // Populate column-heights cache for this position (true free height in col 0 is 5).
        assertTrue(simulator.calculateLegalActions(state)[state.agentTurn].contains(dropColumn0))

        // Branch explores dropping in column 0; must not increment the cached height array.
        val branch = Connect4State(state.bitBoards.copyOf())
        simulator.stateTransition(branch, listOf(null, dropColumn0))

        // Independent line from the same position: drop in column 0 must still use height 5.
        val other = Connect4State(state.bitBoards.copyOf())
        val result = simulator.stateTransition(other, listOf(null, dropColumn0))

        // White (player 1) at bit 5 → scores |= 1<<5. Corrupted height 6 would set bit 6 instead.
        val expected = Connect4State(longArrayOf(0b10101, 0b01010 or (1 shl 5)))
        assertEquals(expected, result)
    }

    /**
     * Same cache-mutation hazard from the empty board: after one branch drops in a column,
     * another branch from the same position must still drop at the bottom of that column.
     */
    @Test
    fun stateTransitionOnCopyDoesNotCorruptSubsequentDropHeight() {
        val simulator = Connect4Simulator()
        val empty = simulator.initialState
        // Warm cache for the empty board.
        simulator.calculateLegalActions(empty)

        // Branch A: drop in column 3 from a copy of empty.
        val branchA = Connect4State(empty.bitBoards.copyOf())
        simulator.stateTransition(branchA, listOf(Connect4Action.valueOf(3), null))

        // Branch B: independent line from the same empty position; first drop in column 3
        // must still use height 0 (bottom), not a corrupted cached height of 1.
        val branchB = Connect4State(empty.bitBoards.copyOf())
        val afterDrop = simulator.stateTransition(branchB, listOf(Connect4Action.valueOf(3), null))

        // Correct bottom-row drop in column 3 is bit 21 → 2097152. A corrupted height of 22
        // would place the piece one row higher instead.
        assertEquals(Connect4State(longArrayOf(2097152, 0)), afterDrop)
    }
}