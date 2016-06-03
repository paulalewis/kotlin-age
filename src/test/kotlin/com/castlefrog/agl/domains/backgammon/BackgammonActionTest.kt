package com.castlefrog.agl.domains.backgammon

import com.google.common.truth.Truth
import org.junit.Test
import java.util.HashSet

class BackgammonActionTest {

    @Test
    fun testCopy() {
        val moves = HashSet<BackgammonMove>()
        moves.add(BackgammonMove.valueOf(1, 2))
        moves.add(BackgammonMove.valueOf(2, 1))
        val action = BackgammonAction(moves)
        Truth.assertThat(action).isEqualTo(action.copy())
        Truth.assertThat(action).isNotSameAs(action.copy())
    }

    @Test
    fun testToString() {
        val moves = HashSet<BackgammonMove>()
        moves.add(BackgammonMove.valueOf(1, 2))
        moves.add(BackgammonMove.valueOf(2, 1))
        val action = BackgammonAction(moves)
        Truth.assertThat(action.toString()).isEqualTo("[ 1/2 2/1 ]")
    }

}