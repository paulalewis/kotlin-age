package com.castlefrog.agl.agents;

import java.util.concurrent.CountDownLatch;

import com.castlefrog.agl.Action;
import com.castlefrog.agl.Agent;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.State;

public final class ExternalAgent implements Agent {
    /** selected action to return from selectAction */
    private Object action_;
    /** indicates when external program has updated action_ */
    private CountDownLatch actionReady_;

    public ExternalAgent() {
        actionReady_ = new CountDownLatch(1);
    }

    public <S extends State<S>, A extends Action> A selectAction(int agentId, S state, Simulator<S, A> simulator) {
        simulator.setState(state);
        if (simulator.hasLegalActions(agentId)) {
            try {
                actionReady_.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
    public synchronized <A extends Action> void setAction(A action) {
        action_ = action;
        actionReady_.countDown();
    }

    public String getName() {
        return "external";
    }

    @Override
    public String toString() {
        return getName() + " agent";
    }
}
