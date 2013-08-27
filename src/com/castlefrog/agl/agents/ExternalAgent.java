package com.castlefrog.agl.agents;

import java.util.concurrent.CountDownLatch;

import com.castlefrog.agl.Action;
import com.castlefrog.agl.Agent;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.State;

public final class ExternalAgent implements Agent {
    /** selected action to return from selectAction */
    private Object action;
    /** indicates when external program has updated action_ */
    private CountDownLatch actionReady;

    public ExternalAgent() {
        actionReady = new CountDownLatch(1);
    }

    public <S extends State, A extends Action> A selectAction(int agentId, S state, Simulator<S, A> simulator) {
        simulator.setState(state);
        if (simulator.hasLegalActions(agentId)) {
            try {
                actionReady.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            @SuppressWarnings("unchecked")
            A action = (A) this.action;
            actionReady = new CountDownLatch(1);
            return action;
        }
        return null;
    }

    /**
     * Set the action for this agent.
     */
    public synchronized <A extends Action> void setAction(A action) {
        this.action = action;
        actionReady.countDown();
    }

    public String getName() {
        return "external";
    }

    @Override
    public String toString() {
        return getName() + " agent";
    }
}
