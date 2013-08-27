package com.castlefrog.agl.domains.havannah;

import java.util.List;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;

public final class HavannahSimulatorProvider implements SimulatorProvider {
    static {
        Simulators.registerProvider("havannah", HavannahSimulatorProvider.getInstance());
    }

    private HavannahSimulatorProvider() {
    }

    public static HavannahSimulatorProvider getInstance() {
        return new HavannahSimulatorProvider();
    }

    public HavannahSimulator newSimulator(List<String> params) {
        return HavannahSimulator.create(params);
    }
}
