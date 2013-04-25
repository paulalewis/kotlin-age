package com.castlefrog.agl.domains.connect4;

import java.util.List;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;

public class Connect4SimulatorProvider implements SimulatorProvider {
    static {
        Simulators.registerProvider("connect4", Connect4SimulatorProvider.getInstance());
    }

    private Connect4SimulatorProvider() {}

    public static Connect4SimulatorProvider getInstance() {
        return new Connect4SimulatorProvider();
    }

    public Connect4Simulator newSimulator(List<String> params) {
        return Connect4Simulator.create(params);
    }
}
