package com.castlefrog.agl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Used as an api for providers to include simulators 
 * and clients to use those simulators.
 */
public class Simulators {
    private Simulators() {}

    private static final Map<String, SimulatorProvider> providers = new ConcurrentHashMap<String, SimulatorProvider>();

    // provider api
    
    /**
     * Providers use this method in AgentProvider class to add new simulators.
     * @param name string to associate with provider
     * @param simulatorProvider class that creates new instances of simulator
     * @see AgentProvider
     */
    public static void registerProvider(String name, SimulatorProvider simulatorProvider) {
        providers.put(name, simulatorProvider);
    }

    // client api

    /**
     * Clients use this method to get a new instance of a simulator.
     * @param name string to associate with provider
     * @param args array of arguments for simulator constructor
     */
    public static Simulator<?, ?> getSimulator(String name, String[] args) {
        SimulatorProvider simulatorProvider = providers.get(name);
        if (simulatorProvider == null)
            throw new IllegalArgumentException("No simulator registered with name: " + name);
        return simulatorProvider.newSimulator(args);
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
