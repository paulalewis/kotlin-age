package com.castlefrog.agl.domains.hexdame;

import java.util.List;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;

public final class HexdameSimulatorProvider implements SimulatorProvider {
    static {
        Simulators.registerProvider("hexdame", HexdameSimulatorProvider.getInstance());
    }

    private HexdameSimulatorProvider() {
    }

    public static HexdameSimulatorProvider getInstance() {
        return new HexdameSimulatorProvider();
    }

    public HexdameSimulator newSimulator(List<String> params) {
        return HexdameSimulator.create(new HexdameState());
    }
}
