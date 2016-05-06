package com.castlefrog.agl.domains.yahtzee

/**
 * The roll action controls which dice get rolled and which are kept for next
 * state.
 */
class YahtzeeRollAction private constructor(selected: ByteArray) : YahtzeeAction {
    /** Indicated quantity of each die number to not roll again.  */
    private val selected_: ByteArray

    init {
        selected_ = ByteArray(YahtzeeState.N_VALUES)
        System.arraycopy(selected, 0, selected_, 0, YahtzeeState.N_VALUES)
    }

    override fun copy(): YahtzeeRollAction {
        return this
    }

    val selected: ByteArray
        get() {
            val selected = ByteArray(selected_.size)
            System.arraycopy(selected_, 0, selected, 0, selected_.size)
            return selected
        }

    fun getSelected(index: Int): Byte {
        return selected_[index]
    }

    override fun hashCode(): Int {
        var code = 7
        for (value in selected_) {
            code = 11 * code + value
        }
        return code
    }

    override fun equals(other: Any?): Boolean {
        if (other !is YahtzeeRollAction) {
            return false
        }
        for (i in 0..YahtzeeState.N_VALUES - 1) {
            if (selected_[i] != other.getSelected(i)) {
                return false
            }
        }
        return true
    }

    override fun toString(): String {
        val output = StringBuilder()
        output.append("[ ")
        for (value in selected_) {
            output.append(value.toInt()).append(" ")
        }
        output.append("]")
        return output.toString()
    }

    companion object {

        fun valueOf(selected: ByteArray): YahtzeeRollAction {
            return YahtzeeRollAction(selected)
        }
    }
}
