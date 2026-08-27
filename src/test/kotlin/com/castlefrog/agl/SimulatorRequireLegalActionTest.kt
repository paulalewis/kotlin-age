package com.castlefrog.agl

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulatorRequireLegalActionTest {

    private data class DummyAction(val id: Int) : Action

    @Test
    fun returnsActionWhenLegal() {
        val action = DummyAction(1)
        val result = requireLegalAction(
            actions = listOf(action, null),
            agentTurn = 0,
            legalForPlayer = setOf(action),
            state = "state"
        )
        assertEquals(action, result)
    }

    @Test
    fun throwsWhenActionsListTooShort() {
        assertThrows(IllegalActionException::class.java) {
            requireLegalAction(
                actions = emptyList(),
                agentTurn = 0,
                legalForPlayer = setOf(DummyAction(1)),
                state = "state"
            )
        }
    }

    @Test
    fun throwsWhenActionIsNull() {
        assertThrows(IllegalActionException::class.java) {
            requireLegalAction(
                actions = listOf(null, DummyAction(1)),
                agentTurn = 0,
                legalForPlayer = setOf(DummyAction(1)),
                state = "state"
            )
        }
    }

    @Test
    fun throwsWhenActionNotLegal() {
        assertThrows(IllegalActionException::class.java) {
            requireLegalAction(
                actions = listOf(DummyAction(2), null),
                agentTurn = 0,
                legalForPlayer = setOf(DummyAction(1)),
                state = "state"
            )
        }
    }

    @Test
    fun isIllegalArgumentExceptionSubclass() {
        val thrown = assertThrows(IllegalActionException::class.java) {
            requireLegalAction(
                actions = emptyList<DummyAction?>(),
                agentTurn = 0,
                legalForPlayer = emptySet(),
                state = "state"
            )
        }
        assertTrue(thrown is IllegalArgumentException)
    }
}
