package com.castlefrog.agl.agents;

import java.util.List;

import com.castlefrog.agl.Agent;
import com.castlefrog.agl.AgentProvider;
import com.castlefrog.agl.Agents;

public final class RandomAgentProvider implements AgentProvider {
    private static RandomAgentProvider instance_;

    static {
        Agents.registerProvider("random", RandomAgentProvider.getInstance());
    }

    private RandomAgentProvider() {}

    public static RandomAgentProvider getInstance() {
        if (instance_ == null) {
            instance_ = new RandomAgentProvider();
        }
        return instance_;
    }

    public Agent newAgent(List<String> params) {
        return new RandomAgent();
    }
}
