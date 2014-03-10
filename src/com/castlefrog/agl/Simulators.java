package com.castlefrog.agl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Used as an api for providers to include simulators
 * and clients to use those simulators.
 */
public final class Simulators {
    private static final Map<String, SimulatorProvider> PROVIDERS = new ConcurrentHashMap<>();

    private Simulators() {}

    // provider api

    /**
     * Providers use this method in AgentProvider class to add new simulators.
     * @param name string to associate with provider
     * @param simulatorProvider class that creates new instances of simulator
     * @see AgentProvider
     */
    public static void registerProvider(String name, SimulatorProvider simulatorProvider) {
        PROVIDERS.put(name, simulatorProvider);
    }

    // client api

    /**
     * Clients use this method to get a new instance of a simulator.
     * @param name string to associate with provider
     * @param params array of arguments for simulator constructor
     */
    public static Simulator<?, ?> getSimulator(String name, List<String> params) {
        SimulatorProvider simulatorProvider = PROVIDERS.get(name);
        if (simulatorProvider == null) {
            throw new IllegalArgumentException("No simulator registered with name: " + name);
        }
        return simulatorProvider.newSimulator(params);
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
