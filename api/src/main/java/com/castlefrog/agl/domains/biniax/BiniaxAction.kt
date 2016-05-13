package com.castlefrog.agl.domains.biniax

import com.castlefrog.agl.Action

enum class BiniaxAction : Action {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    override fun copy(): BiniaxAction {
        return this
    }
}
