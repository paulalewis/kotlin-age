package com.castlefrog.agl.domains.hex

import com.castlefrog.agl.State

data class HexState(val boardSize: Int,
               val bitBoards: Array<ByteArray>,
               var agentTurn: Byte) : State<HexState> {

    companion object {
        val LOCATION_EMPTY = 0
        val LOCATION_BLACK = 1
        val LOCATION_WHITE = 2

        val TURN_BLACK: Byte = 0
        val TURN_WHITE: Byte = 1
    }

    override fun copy(): HexState {
        return HexState(boardSize, bitBoards, agentTurn)
    }

    val locations: Array<ByteArray>
        get() {
            val locations = Array(boardSize) { ByteArray(boardSize) }
            var i = 0
            while (i < boardSize) {
                var j = 0
                while (j < boardSize) {
                    locations[i][j] = getLocation(i, j).toByte()
                    j += 1
                }
                i += 1
            }
            return locations
        }

    fun getLocation(x: Int, y: Int): Int {
        checkLocationArgs(x, y)
        val bitLocation = y * boardSize + x
        val byteLocation = bitLocation / java.lang.Byte.SIZE
        if (bitBoards[0][byteLocation].toInt() and (1 shl bitLocation % java.lang.Byte.SIZE) != 0) {
            return LOCATION_BLACK
        } else if (bitBoards[1][byteLocation].toInt() and (1 shl bitLocation % java.lang.Byte.SIZE) != 0) {
            return LOCATION_WHITE
        } else {
            return LOCATION_EMPTY
        }
    }

    fun isLocationEmpty(x: Int, y: Int): Boolean {
        checkLocationArgs(x, y)
        val bitLocation = y * boardSize + x
        val byteLocation = bitLocation / java.lang.Byte.SIZE
        return bitBoards[0][byteLocation].toInt() or bitBoards[1][byteLocation].toInt() and (1 shl bitLocation % java.lang.Byte.SIZE) == LOCATION_EMPTY
    }

    fun isLocationOnBoard(x: Int, y: Int): Boolean {
        return x >= 0 && x < boardSize && y >= 0 && y < boardSize
    }

    val nPieces: Int
        get() {
            return (0..bitBoards[0].size - 1).sumBy {
                Integer.bitCount((bitBoards[0][it].toInt() or bitBoards[1][it].toInt()) and 0xff)
            }
        }

    fun setLocation(x: Int, y: Int, value: Int) {
        checkLocationArgs(x, y)
        val bitLocation = y * boardSize + x
        val byteLocation = bitLocation / java.lang.Byte.SIZE
        val byteShift = bitLocation % java.lang.Byte.SIZE
        if (value == LOCATION_EMPTY) {
            bitBoards[0][byteLocation] = (bitBoards[0][byteLocation].toInt() and (1 shl byteShift xor 0xff)).toByte()
            bitBoards[1][byteLocation] = (bitBoards[1][byteLocation].toInt() and (1 shl byteShift xor 0xff)).toByte()
        } else if (value == LOCATION_BLACK) {
            bitBoards[0][byteLocation] = (bitBoards[0][byteLocation].toInt() or (1 shl byteShift)).toByte()
            bitBoards[1][byteLocation] = (bitBoards[1][byteLocation].toInt() and (1 shl byteShift xor 0xff)).toByte()
        } else if (value == LOCATION_WHITE) {
            bitBoards[0][byteLocation] = (bitBoards[0][byteLocation].toInt() and (1 shl byteShift xor 0xff)).toByte()
            bitBoards[1][byteLocation] = (bitBoards[1][byteLocation].toInt() or (1 shl byteShift)).toByte()
        }
    }

    private fun checkLocationArgs(x: Int, y: Int) {
        if (!isLocationOnBoard(x, y))
            throw IllegalArgumentException("(x=$x,y=$y) out of bounds")
    }

    override fun hashCode(): Int {
        var hashCode = 17 + boardSize
        hashCode = hashCode * 19 + bitBoards[0].hashCode()
        hashCode = hashCode * 29 + bitBoards[1].hashCode()
        hashCode = hashCode * 31 + agentTurn
        return hashCode
    }

    override fun equals(other: Any?): Boolean {
        if (other !is HexState) {
            return false
        }
        if (bitBoards.size != other.bitBoards.size) {
            return false
        }
        for (i in bitBoards.indices) {
            if (bitBoards[i].size != other.bitBoards[i].size) {
                return false
            }
            bitBoards[i].indices
                    .filter { other.bitBoards[i][it] != bitBoards[i][it] }
                    .forEach { return false }
        }
        return other.boardSize == boardSize && other.agentTurn == agentTurn
    }

    override fun toString(): String {
        val output = StringBuilder()
        for (i in boardSize - 1 downTo 0) {
            for (j in i..boardSize - 2) {
                output.append(" ")
            }
            for (j in 0..boardSize - 1) {
                val location = getLocation(j, i)
                if (location == LOCATION_BLACK) {
                    output.append("X")
                } else if (location == LOCATION_WHITE) {
                    output.append("O")
                } else {
                    output.append("-")
                }
                if (j != boardSize - 1) {
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
