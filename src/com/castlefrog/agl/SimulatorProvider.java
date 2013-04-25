package com.castlefrog.agl;

import java.util.List;

public interface SimulatorProvider {
    Simulator<?, ?> newSimulator(List<String> params) throws IllegalArgumentException;
}
