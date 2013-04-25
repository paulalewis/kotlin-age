package com.castlefrog.agl.domains.biniax;

import java.util.List;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;

public class BiniaxSimulatorProvider implements SimulatorProvider {
    static {
        Simulators.registerProvider("biniax", BiniaxSimulatorProvider.getInstance());
    }

    private BiniaxSimulatorProvider() {}

    public static BiniaxSimulatorProvider getInstance() {
        return new BiniaxSimulatorProvider();
    }

    public BiniaxSimulator newSimulator(List<String> params) {
        return BiniaxSimulator.create(params);
    }
}
