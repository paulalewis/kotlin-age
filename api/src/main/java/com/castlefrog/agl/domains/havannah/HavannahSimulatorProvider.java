package com.castlefrog.agl.domains.havannah;

import java.util.List;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;
import com.castlefrog.agl.TurnType;

public final class HavannahSimulatorProvider implements SimulatorProvider {
    private static HavannahSimulatorProvider instance_;

    static {
        Simulators.registerProvider("havannah", HavannahSimulatorProvider.getInstance());
    }

    private HavannahSimulatorProvider() {}

    public static HavannahSimulatorProvider getInstance() {
        if (instance_ == null) {
            instance_ = new HavannahSimulatorProvider();
        }
        return instance_;
    }

    public HavannahSimulator newSimulator(List<String> params) {
        int boardSize = Integer.valueOf(params.get(0));
        TurnType turnType = TurnType.valueOf(TurnType.class, params.get(1).toUpperCase());
        return HavannahSimulator.create(boardSize, turnType);
    }
}
