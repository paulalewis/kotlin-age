package com.castlefrog.agl;

import java.util.List;

public interface AgentProvider {
    Agent newAgent(List<String> params);
}
