package com.castlefrog.agl.domains.havannah;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.Simulators;
import com.castlefrog.agl.TurnType;

public class HavannahSimulatorProvider implements SimulatorProvider {
    static {
        Simulators.registerProvider("havannah", HavannahSimulatorProvider.getInstance());
    }

    private HavannahSimulatorProvider() {}

    public static HavannahSimulatorProvider getInstance() {
        return new HavannahSimulatorProvider();
    }

    public Simulator newSimulator(String[] args) {
        return new HavannahSimulator(Integer.valueOf(args[0]), TurnType.valueOf(TurnType.class, args[1]));
    }
}
