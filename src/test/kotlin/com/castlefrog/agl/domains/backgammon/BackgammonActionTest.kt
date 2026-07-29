package com.castlefrog.agl.domains.backgammon

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

class BackgammonActionTest {

    @Test
    fun copy() {
        val action = BackgammonAction(hashSetOf(BackgammonMove.valueOf(1, 2), BackgammonMove.valueOf(2, 1)))
        assertEquals(action.copy(), action)
        assertNotSame(action.copy(), action)
    }
}