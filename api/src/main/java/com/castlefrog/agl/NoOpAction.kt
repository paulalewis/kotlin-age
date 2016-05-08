package com.castlefrog.agl

class NoOpAction private constructor() : Action {
    override fun copy(): Action {
        return this
    }

    companion object {
        val instance: NoOpAction = NoOpAction()
    }
}
