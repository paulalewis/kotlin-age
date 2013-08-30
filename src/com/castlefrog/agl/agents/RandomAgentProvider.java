package com.castlefrog.agl.agents;

import java.util.List;

import com.castlefrog.agl.Agent;
import com.castlefrog.agl.AgentProvider;
import com.castlefrog.agl.Agents;

public final class RandomAgentProvider implements AgentProvider {
    private static RandomAgent instance_ = null;

    static {
        Agents.registerProvider("random", RandomAgentProvider.getInstance());
    }

    private RandomAgentProvider() {
    }

    public static RandomAgentProvider getInstance() {
        return new RandomAgentProvider();
    }

    public Agent newAgent(List<String> params) {
        if (instance_ == null) {
            instance_ = new RandomAgent();
        }
        return instance_;
    }
}
