package com.castlefrog.agl.domains.biniax;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.Simulators;

public class BiniaxSimulatorProvider implements SimulatorProvider {
    static {
        Simulators.registerProvider("biniax", BiniaxSimulatorProvider.getInstance());
    }

    private BiniaxSimulatorProvider() {}

    public static BiniaxSimulatorProvider getInstance() {
        return new BiniaxSimulatorProvider();
    }

    public Simulator newSimulator(String[] args) {
        return new BiniaxSimulator();
    }
}