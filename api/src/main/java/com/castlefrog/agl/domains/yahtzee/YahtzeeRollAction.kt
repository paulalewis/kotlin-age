package com.castlefrog.agl.domains.yahtzee

/**
 * The roll action controls which dice get rolled and which are kept for next
 * state.
 * Indicated quantity of each die number to not roll again.
 */
data class YahtzeeRollAction(val selected: ByteArray = ByteArray(YahtzeeState.N_VALUES)) : YahtzeeAction {

    override fun copy(): YahtzeeRollAction {
        return copy(selected)
    }

    override fun toString(): String {
        val output = StringBuilder()
        output.append("[ ")
        for (value in selected) {
            output.append(value).append(" ")
        }
        output.append("]")
        return output.toString()
    }

}
