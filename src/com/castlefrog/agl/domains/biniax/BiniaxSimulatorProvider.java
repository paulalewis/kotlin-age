package com.castlefrog.agl.domains.biniax;

import java.util.List;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;

public final class BiniaxSimulatorProvider implements SimulatorProvider {
    private static BiniaxSimulatorProvider instance_;

    static {
        Simulators.registerProvider("biniax", BiniaxSimulatorProvider.getInstance());
    }

    private BiniaxSimulatorProvider() {}

    public static BiniaxSimulatorProvider getInstance() {
        if (instance_ == null) {
            instance_ = new BiniaxSimulatorProvider();
        }
        return instance_;
    }

    public BiniaxSimulator newSimulator(List<String> params) {
        return BiniaxSimulator.create(new BiniaxState());
    }
}
