package com.castlefrog.agl.agents;

import java.util.List;

import com.castlefrog.agl.Agent;
import com.castlefrog.agl.AgentProvider;
import com.castlefrog.agl.Agents;

public final class ConsoleAgentProvider implements AgentProvider {
    private static ConsoleAgent instance_ = null;

    static {
        Agents.registerProvider("console", ConsoleAgentProvider.getInstance());
    }

    private ConsoleAgentProvider() {
    }

    public static ConsoleAgentProvider getInstance() {
        return new ConsoleAgentProvider();
    }

    public Agent newAgent(List<String> params) {
        if (instance_ == null) {
            instance_ = new ConsoleAgent();
        }
        return instance_;
    }
}
