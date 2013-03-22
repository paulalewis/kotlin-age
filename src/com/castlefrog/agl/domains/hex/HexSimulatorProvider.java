package com.castlefrog.agl.domains.hex;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;
import com.castlefrog.agl.TurnType;

public class HexSimulatorProvider implements SimulatorProvider {
    static {
        Simulators.registerProvider("hex", HexSimulatorProvider.getInstance());
    }

    private HexSimulatorProvider() {}

    public static HexSimulatorProvider getInstance() {
        return new HexSimulatorProvider();
    }

    public HexSimulator newSimulator(String[] args) {
        return new HexSimulator(Integer.valueOf(args[0]), TurnType.valueOf(TurnType.class, args[1]));
    }
}
