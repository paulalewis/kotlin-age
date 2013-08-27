package com.castlefrog.agl.domains.yahtzee;

import java.util.List;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;

public final class YahtzeeSimulatorProvider implements SimulatorProvider {
    static {
        Simulators.registerProvider("yahtzee", YahtzeeSimulatorProvider.getInstance());
    }

    private YahtzeeSimulatorProvider() {
    }

    public static YahtzeeSimulatorProvider getInstance() {
        return new YahtzeeSimulatorProvider();
    }

    public YahtzeeSimulator newSimulator(List<String> params) {
        return new YahtzeeSimulator();
    }
}
