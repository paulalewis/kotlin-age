package com.castlefrog.agl.domains.yahtzee

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

class YahtzeeRollActionTest {

    @Test
    fun testCopy() {
        val action = YahtzeeRollAction()
        assertEquals(action.copy(), action)
        assertNotSame(action.copy(), action)
    }

    @Test
    fun testToString() {
        assertEquals("[ 0 0 0 0 0 0 ]", YahtzeeRollAction().toString())
    }
}
