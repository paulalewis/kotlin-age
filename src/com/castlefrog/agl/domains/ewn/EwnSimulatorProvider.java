package com.castlefrog.agl.domains.ewn;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;

public class EwnSimulatorProvider implements SimulatorProvider {
    static {
        Simulators.registerProvider("ewn", EwnSimulatorProvider.getInstance());
    }

    private EwnSimulatorProvider() {}

    public static EwnSimulatorProvider getInstance() {
        return new EwnSimulatorProvider();
    }

    public EwnSimulator newSimulator(String[] args) {
        return new EwnSimulator();
    }
}
