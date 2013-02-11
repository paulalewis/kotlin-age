package com.castlefrog.agl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.castlefrog.agl.agents.RandomAgentProvider;
import com.castlefrog.agl.agents.ConsoleAgentProvider;

/**
 * Used as an api for providers to include agents
 * and clients to use those agents.
 */
public class Agents {
    private Agents() {}

    private static final Map<String, AgentProvider> providers = new ConcurrentHashMap<String, AgentProvider>();

    static {
        // register default agents
        try {
            Class.forName("com.castlefrog.agl.agents.RandomAgentProvider");
            Class.forName("com.castlefrog.agl.agents.ConsoleAgentProvider");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // provider api
    
    /**
     * Providers use this method in AgentProvider class to add new agents.
     * @param name string to associate with provider
     * @param agentProvider class that creates new instances of agent
     * @see AgentProvider
     */
    public static void registerProvider(String name, AgentProvider agentProvider) {
        providers.put(name, agentProvider);
    }

    // client api

    /**
     * Clients use this method to get a new instance of an agent.
     * @param name string to associate with provider
     * @param args array of arguments for agent constructor
     */
    public static Agent getAgent(String name, String[] args) {
        AgentProvider agentProvider = providers.get(name);
        if (agentProvider == null)
            throw new IllegalArgumentException("No agent registered with name: " + name);
        return agentProvider.newAgent(args);
    }

    /**
     * Clients use this method to get a list of possible
     * providers to choose from.
     */
    public static List<String> getProviderList() {
        List<String> names = new ArrayList<String>();
        Set<String> keySet = providers.keySet();
        for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
            names.add(it.next());
        }
        return names;
    }
}
