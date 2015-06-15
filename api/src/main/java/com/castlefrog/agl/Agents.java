package com.castlefrog.agl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Used as an api for providers to include agents
 * and clients to use those agents.
 */
public final class Agents {
    private static final Map<String, AgentProvider> PROVIDERS = new ConcurrentHashMap<>();

    private Agents() {}

    // provider api

    /**
     * Providers use this method in AgentProvider class to add new agents.
     * @param name string to associate with provider
     * @param agentProvider class that creates new instances of agent
     * @see AgentProvider
     */
    public static void registerProvider(String name, AgentProvider agentProvider) {
        PROVIDERS.put(name, agentProvider);
    }

    // client api

    /**
     * Clients use this method to get a new instance of an agent.
     * @param name string to associate with provider
     * @param params list of arguments for agent constructor
     */
    public static Agent getAgent(String name, List<String> params) {
        AgentProvider agentProvider = PROVIDERS.get(name);
        if (agentProvider == null) {
            throw new IllegalArgumentException("No agent registered with name: " + name);
        }
        return agentProvider.newAgent(params);
    }

    /**
     * Clients use this method to get a list of possible
     * providers to choose from.
     */
    public static List<String> getProviderList() {
        List<String> names = new ArrayList<>();
        Set<String> keySet = PROVIDERS.keySet();
        for (String name : keySet) {
            names.add(name);
        }
        return names;
    }
}
