package com.castlefrog.agl.domains.biniax

import com.castlefrog.agl.State

/**
 * Defines a Biniax state.
 * Location value representation:
 * 0: empty
 * x: single element 0 < x <= MAX_ELEMENTS
 * xy: element pair 0 < x <= MAX_ELEMENTS and 0 < y <= MAX_ELEMENTS and x < y
 */
data class BiniaxState(val locations: Array<ByteArray>,
                       val maxElements: Int,
                       var freeMoves: Byte,
                       var nTurns: Int = 0) : State<BiniaxState> {

    val width: Int
        get() = locations.size

    val height: Int
        get() = locations[0].size

    override fun copy(): BiniaxState {
        val copyLocations = Array(width) { ByteArray(height) }
        for (i in 0..width - 1) {
            System.arraycopy(locations[i], 0, copyLocations[i], 0, height)
        }
        return copy(copyLocations, maxElements, freeMoves, nTurns)
    }

    override fun hashCode(): Int {
        var code = 7 + freeMoves
        for (row in locations) {
            for (location in row) {
                code = 11 * code + location
            }
        }
        code = 11 * code + nTurns
        return code
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BiniaxState) {
            return false
        }
        if (freeMoves != other.freeMoves) {
            return false
        }
        for (i in 0..width - 1) {
            for (j in 0..height - 1) {
                if (locations[i][j] != other.locations[i][j]) {
                    return false
                }
            }
        }
        return nTurns == other.nTurns
    }

    override fun toString(): String {
        val output = StringBuilder()
        output.append("Turns: ").append(nTurns).append("\n")
        output.append("Free Moves: ").append(freeMoves.toInt()).append("\n")
        for (i in 0..width - 1) {
            output.append("----")
        }
        output.append("-\n")
        for (j in 0..height - 1) {
            output.append(":")
            for (i in 0..width - 1) {
                if (i != 0) {
                    output.append(" ")
                }
                if (locations[i][j].toInt() == 0) {
                    output.append("   ")
                } else if (locations[i][j] in 1..(maxElements - 1)) {
                    output.append("[")
                    output.append((0x40 + locations[i][j]).toChar())
                    output.append("]")
                } else {
                    output.append((0x40 + locations[i][j] / (maxElements)).toChar())
                    output.append("-")
                    output.append((0x40 + locations[i][j] % (maxElements)).toChar())
                }
            }
            output.append(":\n")
        }
        output.append("-")
        for (i in 0..width - 1) {
            output.append("----")
        }
        return output.toString()
    }

}
