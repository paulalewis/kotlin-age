package com.castlefrog.agl.domains.yahtzee;

import java.util.List;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;

public final class YahtzeeSimulatorProvider implements SimulatorProvider {
    private static YahtzeeSimulatorProvider instance_;

    static {
        Simulators.registerProvider("yahtzee", YahtzeeSimulatorProvider.getInstance());
    }

    private YahtzeeSimulatorProvider() {}

    public static YahtzeeSimulatorProvider getInstance() {
        if (instance_ == null) {
            instance_ = new YahtzeeSimulatorProvider();
        }
        return instance_;
    }

    public YahtzeeSimulator newSimulator(List<String> params) {
        return YahtzeeSimulator.create(new YahtzeeState());
    }
}
