package com.castlefrog.agl.agents;

import com.castlefrog.agl.Agent;
import com.castlefrog.agl.AgentProvider;
import com.castlefrog.agl.Agents;

public class RandomAgentProvider implements AgentProvider {
    private static RandomAgent instance = null;

    static {
        Agents.registerProvider("random", RandomAgentProvider.getInstance());
    }

    private RandomAgentProvider() {}

    public static RandomAgentProvider getInstance() {
        return new RandomAgentProvider();
    }

    public Agent newAgent(String[] args) {
        if (instance == null)
            instance = new RandomAgent();
        return instance;
    }
}
