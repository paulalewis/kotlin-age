package com.castlefrog.agl.agents;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import com.castlefrog.agl.Agent;
import com.castlefrog.agl.Simulator;

public class ExternalAgent implements Agent {
    /** selected action to return from selectAction */
    private Object action_;
    /** indicates when external program has updated action_ */
    private CountDownLatch actionReady_;

    public ExternalAgent() {
        actionReady_ = new CountDownLatch(1);
    }

    public <S, A> A selectAction(int agentId, S state, Simulator<S, A> simulator) {
        simulator.setState(state);
        if (simulator.hasLegalActions(agentId)) {
            try {
                actionReady_.await();
            } catch (InterruptedException e) {}
            @SuppressWarnings("unchecked")
            A action = (A) action_;
            actionReady_ = new CountDownLatch(1);
            return action;
        }
        return null;
    }

    /**
     * Set the action for this agent.
     */
    public synchronized <A> void setAction(A action) {
        action_ = action;
        actionReady_.countDown();
    }

    public String getName() {
        return "external";
    }

    public String toString() {
        return getName() + " agent";
    }
}
