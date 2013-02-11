package com.castlefrog.agl.agents;

import com.castlefrog.agl.AgentProvider;
import com.castlefrog.agl.Agent;
import com.castlefrog.agl.Agents;

public class UctAgentProvider implements AgentProvider {
    static {
        Agents.registerProvider("uct", UctAgentProvider.getInstance());
    }

    private UctAgentProvider() {}

    public static UctAgentProvider getInstance() {
        return new UctAgentProvider();
    }

    public Agent newAgent(String[] args) {
        return new UctAgent(Integer.parseInt(args[0]), Double.parseDouble(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
    }
}
