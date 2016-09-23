package com.castlefrog.agl.agents

import java.util.concurrent.CountDownLatch

import com.castlefrog.agl.Action
import com.castlefrog.agl.Agent
import com.castlefrog.agl.Simulator
import com.castlefrog.agl.State
import java.util.Optional

class ExternalAgent : Agent {
    /** selected action to return from selectAction  */
    private var action: Any? = null
    /** indicates when external program has updated action_  */
    private var actionReady: CountDownLatch = CountDownLatch(1)

    override fun <S : State<S>, A : Action<A>> selectAction(agentId: Int, state: S, simulator: Simulator<S, A>): Optional<A> {
        simulator.state = state
        actionReady = CountDownLatch(1)
        actionReady.await()
        return Optional.of(this.action as A)
    }

    /**
     * Set the action for this agent.
     */
    @Synchronized fun <A : Action<A>> setAction(action: A) {
        this.action = action
        actionReady.countDown()
    }

    override fun toString(): String {
        return javaClass.simpleName
    }

}
