package com.castlefrog.agl.domains.biniax

import com.castlefrog.agl.Action

enum class BiniaxAction : Action<BiniaxAction> {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    override fun copy(): BiniaxAction = this
}
