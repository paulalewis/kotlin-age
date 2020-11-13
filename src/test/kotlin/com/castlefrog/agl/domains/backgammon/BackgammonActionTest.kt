package com.castlefrog.agl.domains.backgammon

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class BackgammonActionTest {

    @Test
    fun copy() {
        val action = BackgammonAction(hashSetOf(BackgammonMove.valueOf(1, 2), BackgammonMove.valueOf(2, 1)))
        assertThat(action).isEqualTo(action.copy())
        assertThat(action).isNotSameInstanceAs(action.copy())
    }
}