package com.castlefrog.agl.agents;

import java.util.List;

import com.castlefrog.agl.Agent;
import com.castlefrog.agl.AgentProvider;
import com.castlefrog.agl.Agents;

public final class ConsoleAgentProvider implements AgentProvider {
    private static ConsoleAgentProvider instance_;

    static {
        Agents.registerProvider("console", ConsoleAgentProvider.getInstance());
    }

    private ConsoleAgentProvider() {}

    public static ConsoleAgentProvider getInstance() {
        if (instance_ == null) {
            instance_ = new ConsoleAgentProvider();
        }
        return instance_;
    }

    public Agent newAgent(List<String> params) {
        return new ConsoleAgent();
    }
}
