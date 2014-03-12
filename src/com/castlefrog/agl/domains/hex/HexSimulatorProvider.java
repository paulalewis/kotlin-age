package com.castlefrog.agl.domains.hex;

import java.util.List;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;
import com.castlefrog.agl.TurnType;

public final class HexSimulatorProvider implements SimulatorProvider {
    private static HexSimulatorProvider instance_;

    static {
        Simulators.registerProvider("hex", HexSimulatorProvider.getInstance());
    }

    private HexSimulatorProvider() {}

    public static HexSimulatorProvider getInstance() {
        if (instance_ == null) {
            instance_ = new HexSimulatorProvider();
        }
        return instance_;
    }

    public HexSimulator newSimulator(List<String> params) {
        int boardSize = Integer.valueOf(params.get(0));
        TurnType turnType = TurnType.valueOf(TurnType.class, params.get(1).toUpperCase());
        return HexSimulator.create(boardSize, turnType);
    }
}
