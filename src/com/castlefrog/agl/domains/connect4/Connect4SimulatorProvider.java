package com.castlefrog.agl.domains.connect4;

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

    public Connect4Simulator newSimulator(String[] args) {
        return new Connect4Simulator();
    }
}
