package com.castlefrog.agl.domains.havannah

import com.castlefrog.agl.State

import java.io.Serializable

data class HavannahState(val base: Int,
                    val locations: Array<ByteArray>,
                    var agentTurn: Byte) : State<HavannahState>, Serializable {

    companion object {
        val LOCATION_EMPTY = 0
        val LOCATION_BLACK = 1
        val LOCATION_WHITE = 2
    }

    override fun copy(): HavannahState {
        return HavannahState(base, locations, agentTurn)
    }

    /**
     * Gets a location on board.
     */
    fun getLocation(x: Int, y: Int): Byte {
        return locations[x][y]
    }

    fun isLocationEmpty(x: Int, y: Int): Boolean {
        return locations[x][y].toInt() == LOCATION_EMPTY
    }

    val size: Int
        get() = locations.size

    val nLocations: Int
        get() = 3 * base.toInt() * base.toInt() - 3 * base + 1

    val corners: Array<IntArray>
        get() = arrayOf(intArrayOf(0, 0), intArrayOf(0, base - 1), intArrayOf(base - 1, 0), intArrayOf(base - 1, size - 1), intArrayOf(size - 1, base - 1), intArrayOf(size - 1, size - 1))

    val sides: Array<Array<IntArray>>
        get() {
            val sides = Array(6) { Array(base - 2) { IntArray(2) } }
            var i = 0
            while (i < base - 2) {
                sides[0][i][0] = 0
                sides[0][i][1] = i + 1
                sides[1][i][0] = i + 1
                sides[1][i][1] = 0
                sides[2][i][0] = i + 1
                sides[2][i][1] = base + i
                sides[3][i][0] = base + i
                sides[3][i][1] = size - 1
                sides[4][i][0] = size - 1
                sides[4][i][1] = base + i
                sides[5][i][0] = base + i
                sides[5][i][1] = i + 1
                i += 1
            }
            return sides
        }

    val nPieces: Int
        get() {
            var nPieces = 0
            for (i in 0..locations.size - 1) {
                for (j in 0..locations[0].size - 1) {
                    if (locations[i][j].toInt() != LOCATION_EMPTY) {
                        nPieces += 1
                    }
                }
            }
            return nPieces
        }

    fun setLocation(x: Int,
                    y: Int,
                    value: Int) {
        locations[x][y] = value.toByte()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is HavannahState) {
            return false
        }
        val locations = other.locations
        for (i in 0..locations.size - 1) {
            for (j in 0..locations.size - 1) {
                if (locations[i][j] != locations[i][j]) {
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
            for (j in xMin..xMax - 1) {
                if (locations[j][i].toInt() == LOCATION_BLACK) {
                    output.append("X ")
                } else if (locations[j][i].toInt() == LOCATION_WHITE) {
                    output.append("O ")
                } else {
                    output.append("- ")
                }
            }
            output.append("\n")
        }
        return output.toString()
    }
}
