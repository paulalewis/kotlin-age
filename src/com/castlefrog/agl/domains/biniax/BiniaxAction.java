package com.castlefrog.agl.domains.biniax;

import com.castlefrog.agl.Action;

public enum BiniaxAction implements Action {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public BiniaxAction copy() {
        return this;
    }
}
