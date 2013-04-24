package com.castlefrog.agl.domains.yahtzee;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;

public class YahtzeeSimulatorProvider implements SimulatorProvider {
    static {
        Simulators.registerProvider("yahtzee", YahtzeeSimulatorProvider.getInstance());
    }

    private YahtzeeSimulatorProvider() {}

    public static YahtzeeSimulatorProvider getInstance() {
        return new YahtzeeSimulatorProvider();
    }

    public YahtzeeSimulator newSimulator(String[] args) {
        return new YahtzeeSimulator();
    }
}
