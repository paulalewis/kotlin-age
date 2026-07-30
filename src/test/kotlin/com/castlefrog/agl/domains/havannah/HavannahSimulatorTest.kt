package com.castlefrog.agl.domains.havannah

import com.castlefrog.agl.util.LruCache
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Test

class HavannahSimulatorTest {

    @Test
    fun stateTransitionMove1() {
        val simulator = HavannahSimulator(5)
        val state2 = simulator.stateTransition(simulator.initialState, listOf(HavannahAction(0, 0), null))
        val expectedState = simulator.initialState
        expectedState.locations[0][0] = HavannahState.LOCATION_BLACK
        expectedState.agentTurn = HavannahState.TURN_WHITE
        assertEquals(expectedState, state2)
    }

    @Test
    fun stateTransitionIllegalMove() {
        val simulator = HavannahSimulator(5)
        val state2 = simulator.stateTransition(simulator.initialState, listOf(HavannahAction(0, 0), null))
        assertThrows(
            IllegalArgumentException::class.java
        ) { simulator.stateTransition(state2, listOf(HavannahAction(0, 0), null)) }
    }

    @Test
    fun stateTransitionMove2SameLocationPieRuleTrue() {
        val simulator = HavannahSimulator(base = 5, pieRule = true)
        val state2 = simulator.stateTransition(simulator.initialState, listOf(HavannahAction(0, 0), null))
        val state3 = simulator.stateTransition(state2, listOf(null, HavannahAction(0, 0)))
        val expectedState = simulator.initialState
        expectedState.locations[0][0] = HavannahState.LOCATION_WHITE
        expectedState.agentTurn = HavannahState.TURN_BLACK
        assertEquals(expectedState, state3)
    }

    @Test
    fun stateTransitionMove2SameLocationPieRuleFalse() {
        val simulator = HavannahSimulator(base = 5, pieRule = false)
        val state2 = simulator.stateTransition(simulator.initialState, listOf(HavannahAction(0, 0), null))
        assertThrows(
            IllegalArgumentException::class.java
        ) { simulator.stateTransition(state2, listOf(null, HavannahAction(0, 0))) }
    }

    @Test
    fun stateTransitionMove2DifferentLocation() {
        val simulator = HavannahSimulator(5)
        val state2 = simulator.stateTransition(simulator.initialState, listOf(HavannahAction(0, 0), null))
        val state3 = simulator.stateTransition(state2, listOf(null, HavannahAction(0, 1)))
        val expectedState = simulator.initialState
        expectedState.locations[0][0] = HavannahState.LOCATION_BLACK
        expectedState.locations[0][1] = HavannahState.LOCATION_WHITE
        expectedState.agentTurn = HavannahState.TURN_BLACK
        assertEquals(expectedState, state3)
    }

    /**
     * After a move, prev-action must be retrievable for the post-move state content.
     * Caching under the pre-move state (then mutating that object in place) orphans the
     * map entry so the optimization never hits; keying a deep copy of the post-move
     * state keeps the entry findable.
     */
    @Test
    fun stateTransitionCachesPrevActionUnderPostMoveState() {
        val simulator = HavannahSimulator(5)
        val action = HavannahAction(2, 3)
        val result = simulator.stateTransition(simulator.initialState, listOf(action, null))

        @Suppress("UNCHECKED_CAST")
        val cache = prevActionCache(simulator) as LruCache<HavannahState, HavannahAction>

        assertEquals(
            "prev action must be cached under post-move state content",
            action,
            cache[result]
        )
        // Equal snapshot (not the same instance) must also hit — keys must be value-based.
        assertEquals(action, cache[result.copy()])
        assertNotNull(cache[result.copy()])
    }

    /**
     * Further in-place mutation of the returned state must not destroy the cached entry
     * for the previous post-move position (stable copy as key).
     */
    @Test
    fun prevActionCacheSurvivesFurtherMutationOfReturnedState() {
        val simulator = HavannahSimulator(5)
        val firstAction = HavannahAction(0, 0)
        val afterFirst = simulator.stateTransition(simulator.initialState, listOf(firstAction, null))
        val snapshotAfterFirst = afterFirst.copy()

        // Apply a second move by mutating the same state instance returned from the first.
        val secondAction = HavannahAction(1, 0)
        simulator.stateTransition(afterFirst, listOf(null, secondAction))

        @Suppress("UNCHECKED_CAST")
        val cache = prevActionCache(simulator) as LruCache<HavannahState, HavannahAction>

        // Size-1 cache keeps only the latest entry (second move).
        assertEquals(secondAction, cache[afterFirst])
        // The first post-move snapshot is no longer the sole entry, but must not have been
        // used as a live mutable key (which would corrupt the map). Latest entry still works.
        assertEquals(1, cache.size)
        assertEquals(secondAction, cache[afterFirst.copy()])
        // Sanity: snapshot of first position is a different key than current state.
        assertEquals(false, snapshotAfterFirst == afterFirst)
    }

    private fun prevActionCache(simulator: HavannahSimulator): Any {
        val field = HavannahSimulator::class.java.getDeclaredField("prevActionCache")
        field.isAccessible = true
        return field.get(simulator)
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
        val simulator = HavannahSimulator(base = 5, pieRule = false)
        assertArrayEquals(intArrayOf(1, -1), simulator.calculateRewards(state))
    }

    @Test
    fun stateWhiteWinsCorners() {
        val locations = Array(9) { ByteArray(9) }
        locations[2][2] = HavannahState.LOCATION_BLACK
        locations[3][2] = HavannahState.LOCATION_BLACK
        locations[4][4] = HavannahState.LOCATION_BLACK
        locations[4][3] = HavannahState.LOCATION_BLACK
        locations[3][4] = HavannahState.LOCATION_BLACK
        locations[0][0] = HavannahState.LOCATION_WHITE
        locations[0][1] = HavannahState.LOCATION_WHITE
        locations[0][2] = HavannahState.LOCATION_WHITE
        locations[0][3] = HavannahState.LOCATION_WHITE
        locations[0][4] = HavannahState.LOCATION_WHITE
        val state = HavannahState(5, locations, HavannahState.TURN_BLACK)
        val simulator = HavannahSimulator(base = 5, pieRule = false)
        assertArrayEquals(intArrayOf(-1, 1), simulator.calculateRewards(state))
    }

    @Test
    fun stateBlackWinsSides() {
        val locations = Array(9) { ByteArray(9) }
        locations[0][1] = HavannahState.LOCATION_BLACK
        locations[1][0] = HavannahState.LOCATION_BLACK
        locations[1][1] = HavannahState.LOCATION_BLACK
        locations[1][2] = HavannahState.LOCATION_BLACK
        locations[1][3] = HavannahState.LOCATION_BLACK
        locations[1][4] = HavannahState.LOCATION_BLACK
        locations[1][5] = HavannahState.LOCATION_BLACK
        locations[4][4] = HavannahState.LOCATION_WHITE
        locations[0][2] = HavannahState.LOCATION_WHITE
        locations[0][3] = HavannahState.LOCATION_WHITE
        locations[3][3] = HavannahState.LOCATION_WHITE
        val state = HavannahState(5, locations, HavannahState.TURN_WHITE)
        val simulator = HavannahSimulator(base = 5, pieRule = false)
        assertArrayEquals(intArrayOf(1, -1), simulator.calculateRewards(state))
    }
}
