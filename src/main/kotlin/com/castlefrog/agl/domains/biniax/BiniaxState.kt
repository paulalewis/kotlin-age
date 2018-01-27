package com.castlefrog.agl.domains.biniax

import com.castlefrog.agl.State
import java.util.Arrays

/**
 * Defines a Biniax state.
 * Location value representation:
 * 0: empty
 * x: single element 0 < x <= MAX_ELEMENTS
 * xy: element pair 0 < x <= MAX_ELEMENTS and 0 < y <= MAX_ELEMENTS and x < y
 */
data class BiniaxState(
        val locations: ByteArray = ByteArray(BiniaxSimulator.WIDTH * BiniaxSimulator.HEIGHT),
        val maxElements: Byte = 10,
        var freeMoves: Byte = 2,
        var nTurns: Int = 0) : State<BiniaxState> {

    init {
        if (locations.size != BiniaxSimulator.WIDTH * BiniaxSimulator.HEIGHT) {
            throw IllegalArgumentException("locations size must be = " + BiniaxSimulator.WIDTH * BiniaxSimulator.HEIGHT)
        }
    }

    override fun copy(): BiniaxState {
        val copyLocations = ByteArray(locations.size)
        System.arraycopy(locations, 0, copyLocations, 0, locations.size)
        return copy(locations = copyLocations, maxElements = maxElements, freeMoves = freeMoves, nTurns = nTurns)
    }

    override fun hashCode(): Int {
        return 11 * (17 * freeMoves + Arrays.hashCode(locations)) + nTurns
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        return other is BiniaxState
                && Arrays.equals(locations, other.locations)
                && freeMoves == other.freeMoves
                && nTurns == other.nTurns
    }

    override fun toString(): String {
        val output = StringBuilder()
        output.append("Turns: ").append(nTurns).append("\n")
        output.append("Free Moves: ").append(freeMoves.toInt()).append("\n")
        for (i in 0 until BiniaxSimulator.WIDTH) {
            output.append("----")
        }
        output.append("-\n")
        for (j in 0 until BiniaxSimulator.HEIGHT) {
            output.append(":")
            for (i in 0 until BiniaxSimulator.WIDTH) {
                val location = locations[i + j * BiniaxSimulator.WIDTH].toInt()
                if (i != 0) {
                    output.append(" ")
                }
                when (location) {
                    0 -> output.append("   ")
                    in 1..(maxElements - 1) -> {
                        output.append("[")
                        output.append((0x40 + location).toChar())
                        output.append("]")
                    }
                    else -> {
                        output.append((0x40 + location / (maxElements)).toChar())
                        output.append("-")
                        output.append((0x40 + location % (maxElements)).toChar())
                    }
                }
            }
            output.append(":\n")
        }
        output.append("-")
        for (i in 0 until BiniaxSimulator.WIDTH) {
            output.append("----")
        }
        return output.toString()
    }

}
