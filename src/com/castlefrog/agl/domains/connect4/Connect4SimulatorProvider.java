package com.castlefrog.agl.domains.connect4;

import java.util.List;

import com.castlefrog.agl.SimulatorProvider;
import com.castlefrog.agl.Simulators;
import com.castlefrog.agl.TurnType;

public final class Connect4SimulatorProvider implements SimulatorProvider {
    private static final String TURN_TYPE_ARG_NAME = "turn";

    private static Connect4SimulatorProvider instance_;

    static {
        Simulators.registerProvider("connect4", Connect4SimulatorProvider.getInstance());
    }

    private Connect4SimulatorProvider() {}

    public static Connect4SimulatorProvider getInstance() {
        if (instance_ == null) {
            instance_ = new Connect4SimulatorProvider();
        }
        return instance_;
    }

    public Connect4Simulator newSimulator(List<String> params) {
        TurnType turnType = TurnType.SEQUENTIAL;
        for (int i = 0; i < params.size(); i += 1) {
            switch (params.get(i).toLowerCase()) {
                case TURN_TYPE_ARG_NAME:
                    break;
            }
            if (params.get(i).equalsIgnoreCase(TURN_TYPE_ARG_NAME)) {
                if (params.size() > i + 1) {
                    turnType = TurnType.valueOf(params.get(i + 1).toUpperCase());
                }
            }
        }
        return Connect4Simulator.create(turnType);
    }

    //public Connect4Simulator newSimulator(Map<String, String> params) {
    //    String turnTypeArg = params.get(TURN_TYPE_ARG_NAME);
    //    TurnType turnType = (turnTypeArg == null) ? TurnType.SEQUENTIAL : TurnType.valueOf(turnTypeArg.toUpperCase());
    //    return Connect4Simulator.create(turnType);
    //}
}
