package com.castlefrog.agl.agents;

import com.castlefrog.agl.Agent;
import com.castlefrog.agl.AgentProvider;
import com.castlefrog.agl.Simulator;

import java.util.List;

/**
 * This agent selects a random action from the list
 * of possible actions from a given state.
 */
public final class RandomAgent implements Agent {
    public RandomAgent() {}

    public <S, A> A selectAction(int agentId,
                                 S state,
                                 Simulator<S, A> simulator) {
        simulator.setState(state);
        List<A> actions = simulator.getLegalActions(agentId);
        return actions.get((int) (Math.random() * actions.size()));
    }

    public String getName() {
        return "random";
    }

    @Override
    public String toString() {
        return getName() + " agent";
    }
}
