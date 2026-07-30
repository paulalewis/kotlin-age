package com.castlefrog.agl.domains.go

import com.castlefrog.agl.Action

/**
 * A stone placement on the board, or a pass.
 *
 * @param x column in `0 until boardSize` (ignored when [isPass] is true)
 * @param y row in `0 until boardSize` (ignored when [isPass] is true)
 * @param isPass when true, the player passes
 */
data class GoAction(
    val x: Int = -1,
    val y: Int = -1,
    val isPass: Boolean = false
) : Action {

    override fun toString(): String {
        return if (isPass) "pass" else "${('A' + x)}$y"
    }

    companion object {
        private val PASS = GoAction(isPass = true)

        fun pass(): GoAction = PASS

        fun place(x: Int, y: Int): GoAction = GoAction(x, y, isPass = false)
    }
}
