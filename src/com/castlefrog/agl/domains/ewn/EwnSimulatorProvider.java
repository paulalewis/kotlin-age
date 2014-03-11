package com.castlefrog.agl.domains.ewn;

import java.util.List;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;

public final class EwnSimulatorProvider implements SimulatorProvider {
    static {
        Simulators.registerProvider("ewn", EwnSimulatorProvider.getInstance());
    }

    private EwnSimulatorProvider() {
    }

    public static EwnSimulatorProvider getInstance() {
        return new EwnSimulatorProvider();
    }

    public EwnSimulator newSimulator(List<String> params) {
        return EwnSimulator.create(new EwnState());
    }
}
