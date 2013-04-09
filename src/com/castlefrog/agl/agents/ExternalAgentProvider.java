package com.castlefrog.agl.agents;

import com.castlefrog.agl.AgentProvider;
import com.castlefrog.agl.Agent;
import com.castlefrog.agl.Agents;

public class ExternalAgentProvider implements AgentProvider {
    private static ExternalAgent instance = null;

    static {
        Agents.registerProvider("external", ExternalAgentProvider.getInstance());
    }

    private ExternalAgentProvider() {}

    public static ExternalAgentProvider getInstance() {
        return new ExternalAgentProvider();
    }

    public Agent newAgent(String[] args) {
        if (instance == null)
            instance = new ExternalAgent();
        return instance;
    }
}
