package com.castlefrog.agl.domains.backgammon;

import java.util.List;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;

public class BackgammonSimulatorProvider implements SimulatorProvider {
    static {
        Simulators.registerProvider("backgammon", BackgammonSimulatorProvider.getInstance());
    }

    private BackgammonSimulatorProvider() {}

    public static BackgammonSimulatorProvider getInstance() {
        return new BackgammonSimulatorProvider();
    }

    public BackgammonSimulator newSimulator(List<String> params) {
        return BackgammonSimulator.create(params);
    }
}
