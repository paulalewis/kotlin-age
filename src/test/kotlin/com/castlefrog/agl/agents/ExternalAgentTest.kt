package com.castlefrog.agl.agents

import com.google.common.truth.Truth
import org.junit.Test

class ExternalAgentTest {

    @Test
    fun testToString() {
        val agent = ExternalAgent()
        Truth.assertThat(agent.toString()).isEqualTo(agent.javaClass.simpleName)
    }

}