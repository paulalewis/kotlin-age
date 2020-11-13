package com.castlefrog.agl.domains.havannah

import com.castlefrog.agl.State
import kotlin.math.max

data class HavannahState(
    val base: Int,
    val locations: Array<ByteArray>,
    var agentTurn: Byte
) : State<HavannahState> {

    companion object {
        const val LOCATION_EMPTY: Byte = 0
        const val LOCATION_BLACK: Byte = 1
        const val LOCATION_WHITE: Byte = 2

        const val TURN_BLACK: Byte = 0
        const val TURN_WHITE: Byte = 1
    }

    override fun copy(): HavannahState {
        val copyLocations = Array(locations.size) { ByteArray(locations.size) }
        for (i in locations.indices) {
            copyLocations[i] = locations[i].copyOf()
        }
        return HavannahState(base, copyLocations, agentTurn)
    }

    fun isLocationEmpty(x: Int, y: Int): Boolean {
        return locations[x][y] == LOCATION_EMPTY
    }

    val nPieces: Int
        get() {
            var nPieces = 0
            (locations.indices).forEach { i ->
                (locations[0].indices)
                    .asSequence()
                    .filter { j -> locations[i][j] != LOCATION_EMPTY }
                    .forEach { _ -> nPieces += 1 }
            }
            return nPieces
        }

    override fun hashCode(): Int {
        var hashCode = 17 + agentTurn
        hashCode = hashCode * 31 + base
        for (i in locations.indices) {
            for (j in locations[i].indices) {
                hashCode = hashCode * 31 + locations[i][j]
            }
        }
        return hashCode
    }

    override fun equals(other: Any?): Boolean {
        if (other !is HavannahState) {
            return false
        }
        for (i in locations.indices) {
            for (j in locations[i].indices) {
                if (locations[i][j] != other.locations[i][j]) {
                    return false
                }
            }
        }
        return base == other.base && agentTurn == other.agentTurn
    }

    override fun toString(): String {
        val output = StringBuilder()
        for (i in locations.size - 1 downTo 0) {
            for (j in 0..max(base - i - 2, i - base)) {
                output.append(" ")
            }
            var xMin = 0
            var xMax = locations.size
            if (i >= base) {
                xMin = i - base + 1
            } else {
                xMax = base + i
            }
            for (j in xMin until xMax) {
                when {
                    locations[j][i] == LOCATION_BLACK -> output.append("X")
                    locations[j][i] == LOCATION_WHITE -> output.append("O")
                    else -> output.append("-")
                }
                if (j != xMax - 1) {
                    output.append(" ")
                }
            }
            if (i != 0) {
                output.append("\n")
            }
        }
        return output.toString()
    }
}
