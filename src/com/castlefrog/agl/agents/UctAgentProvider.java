package com.castlefrog.agl.agents;

import java.util.List;

import com.castlefrog.agl.Agent;
import com.castlefrog.agl.AgentProvider;
import com.castlefrog.agl.Agents;

public final class UctAgentProvider implements AgentProvider {
    private static UctAgentProvider instance_;

    static {
        Agents.registerProvider("uct", UctAgentProvider.getInstance());
    }

    private UctAgentProvider() {}

    public static UctAgentProvider getInstance() {
        if (instance_ == null) {
            instance_ = new UctAgentProvider();
        }
        return instance_;
    }

    public Agent newAgent(List<String> params) {
        return new UctAgent(Integer.parseInt(params.get(0)),
                            Double.parseDouble(params.get(1)),
                            Integer.parseInt(params.get(2)),
                            Integer.parseInt(params.get(3)));
    }
}
