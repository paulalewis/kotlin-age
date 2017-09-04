package com.castlefrog.agl.domains.havannah

import com.castlefrog.agl.State

data class HavannahState(val base: Int,
                    val locations: Array<ByteArray>,
                    var agentTurn: Byte) : State<HavannahState> {

    companion object {
        val LOCATION_EMPTY: Byte = 0
        val LOCATION_BLACK: Byte = 1
        val LOCATION_WHITE: Byte = 2

        val TURN_BLACK: Byte = 0
        val TURN_WHITE: Byte = 1
    }

    override fun copy(): HavannahState {
        val copyLocations = Array(locations.size) { ByteArray(locations.size) }
        for (i in 0 until locations.size) {
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
            for (i in 0 until locations.size) {
                for (j in 0 until locations[0].size) {
                    if (locations[i][j] != LOCATION_EMPTY) {
                        nPieces += 1
                    }
                }
            }
            return nPieces
        }

    override fun hashCode(): Int {
        var hashCode = 17 + agentTurn
        hashCode = hashCode * 31 + base
        for (i in 0 until locations.size) {
            for (j in 0 until locations.size) {
                hashCode = hashCode * 31 + locations[i][j]
            }
        }
        return hashCode
    }

    override fun equals(other: Any?): Boolean {
        if (other !is HavannahState) {
            return false
        }
        for (i in 0 until locations.size) {
            for (j in 0 until locations.size) {
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
            for (j in 0..Math.max(base - i - 2, i - base)) {
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
