package com.castlefrog.agl.agents;

import java.util.List;

import com.castlefrog.agl.Agent;
import com.castlefrog.agl.AgentProvider;
import com.castlefrog.agl.Agents;

public final class ExternalAgentProvider implements AgentProvider {
    private static ExternalAgentProvider instance_;

    static {
        Agents.registerProvider("external", ExternalAgentProvider.getInstance());
    }

    private ExternalAgentProvider() {}

    public static ExternalAgentProvider getInstance() {
        if (instance_ == null) {
            instance_ = new ExternalAgentProvider();
        }
        return instance_;
    }

    public Agent newAgent(List<String> params) {
        return new ExternalAgent();
    }
}
