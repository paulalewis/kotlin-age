package com.castlefrog.agl.domains.hex;

import java.util.List;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;

public class HexSimulatorProvider implements SimulatorProvider {
    static {
        Simulators.registerProvider("hex", HexSimulatorProvider.getInstance());
    }

    private HexSimulatorProvider() {}

    public static HexSimulatorProvider getInstance() {
        return new HexSimulatorProvider();
    }

    public HexSimulator newSimulator(List<String> params) throws IllegalArgumentException {
        return HexSimulator.create(params);
    }
}
