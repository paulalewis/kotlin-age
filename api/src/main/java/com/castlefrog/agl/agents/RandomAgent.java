package com.castlefrog.agl.agents;

import java.util.List;

import com.castlefrog.agl.Action;
import com.castlefrog.agl.Agent;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.State;

/**
 * This agent selects a random action from the list
 * of possible actions from a given state.
 */
public final class RandomAgent implements Agent {
    private static RandomAgent instance = null;

    private RandomAgent() {}

    public static RandomAgent getInstance() {
        if (instance == null) {
            instance = new RandomAgent();
        }
        return instance;
    }

    public <S extends State<S>, A extends Action> A selectAction(int agentId, S state, Simulator<S, A> simulator) {
        simulator.setState(state);
        List<A> actions = simulator.getLegalActions(agentId);
        return actions.get((int) (Math.random() * actions.size()));
    }

    @Override
    public String toString() {
        return RandomAgent.class.getSimpleName();
    }
}
