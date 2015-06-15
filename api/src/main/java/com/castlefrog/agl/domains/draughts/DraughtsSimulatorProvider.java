package com.castlefrog.agl.domains.draughts;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;

import java.util.List;

public final class DraughtsSimulatorProvider implements SimulatorProvider {
    private static DraughtsSimulatorProvider instance_;

    static {
        Simulators.registerProvider("draughts", DraughtsSimulatorProvider.getInstance());
    }

    private DraughtsSimulatorProvider() {}

    public static DraughtsSimulatorProvider getInstance() {
        if (instance_ == null) {
            instance_ = new DraughtsSimulatorProvider();
        }
        return instance_;
    }

    public DraughtsSimulator newSimulator(List<String> params) {
        return DraughtsSimulator.create();
    }
}
