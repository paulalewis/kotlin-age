package com.castlefrog.agl.domains.backgammon;

import java.util.List;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;

public final class BackgammonSimulatorProvider implements SimulatorProvider {
    private static BackgammonSimulatorProvider instance_;

    static {
        Simulators.registerProvider("backgammon", BackgammonSimulatorProvider.getInstance());
    }

    private BackgammonSimulatorProvider() {}

    public static BackgammonSimulatorProvider getInstance() {
        if (instance_ == null) {
            instance_ = new BackgammonSimulatorProvider();
        }
        return instance_;
    }

    public BackgammonSimulator newSimulator(List<String> params) {
        return BackgammonSimulator.create(new BackgammonState());
    }
}
