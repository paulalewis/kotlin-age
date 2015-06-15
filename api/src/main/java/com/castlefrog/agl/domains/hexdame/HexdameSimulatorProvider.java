package com.castlefrog.agl.domains.hexdame;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;

import java.util.List;

public final class HexdameSimulatorProvider implements SimulatorProvider {
    private static HexdameSimulatorProvider instance_;

    static {
        Simulators.registerProvider("hexdame", HexdameSimulatorProvider.getInstance());
    }

    private HexdameSimulatorProvider() {}

    public static HexdameSimulatorProvider getInstance() {
        if (instance_ == null) {
            instance_ = new HexdameSimulatorProvider();
        }
        return instance_;
    }

    public HexdameSimulator newSimulator(List<String> params) {
        return HexdameSimulator.create(new HexdameState());
    }
}
