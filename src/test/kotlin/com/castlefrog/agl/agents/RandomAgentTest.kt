package com.castlefrog.agl.agents

import com.castlefrog.agl.domains.hex.HexAction
import com.castlefrog.agl.domains.hex.HexSimulator
import com.google.common.truth.Truth
import org.junit.Test

import java.util.ArrayList
import java.util.Random

class RandomAgentTest {

    @Test
    fun testSelectAction() {
        val expectedActions = arrayListOf(HexAction.valueOf(2, 1),
                HexAction.valueOf(4, 1),
                HexAction.valueOf(1, 0),
                HexAction.valueOf(2, 2),
                HexAction.valueOf(4, 2))
        val actualActions = ArrayList<HexAction>()
        val agent = RandomAgent(Random(6345))
        val simulator = HexSimulator.create(5, false)
        for (i in 0..expectedActions.size - 1) {
            actualActions.add(agent.selectAction(0, simulator.state, simulator))
        }
        Truth.assertThat(actualActions).isEqualTo(expectedActions)
    }

    @Test
    fun testToString() {
        val agent = RandomAgent()
        Truth.assertThat(agent.toString()).isEqualTo(agent.javaClass.simpleName)
    }
}