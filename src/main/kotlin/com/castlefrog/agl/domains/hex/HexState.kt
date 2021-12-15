package com.castlefrog.agl.domains.hex

import com.castlefrog.agl.State

data class HexState(
    val boardSize: Int,
    val bitBoards: Array<ByteArray> = Array(2) {
        ByteArray((boardSize * boardSize + java.lang.Byte.SIZE - 1) / java.lang.Byte.SIZE)
    },
    var agentTurn: Byte = TURN_BLACK
) : State<HexState> {

    companion object {
        const val N_PLAYERS = 2

        const val LOCATION_EMPTY = 0
        const val LOCATION_BLACK = 1
        const val LOCATION_WHITE = 2

        const val TURN_BLACK: Byte = 0
        const val TURN_WHITE: Byte = 1
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
        return when {
            bitBoards[0][byteLocation].toInt() and (1 shl bitLocation % java.lang.Byte.SIZE) != 0 -> LOCATION_BLACK
            bitBoards[1][byteLocation].toInt() and (1 shl bitLocation % java.lang.Byte.SIZE) != 0 -> LOCATION_WHITE
            else -> LOCATION_EMPTY
        }
    }

    fun isLocationEmpty(x: Int, y: Int): Boolean {
        checkLocationArgs(x, y)
        val bitLocation = y * boardSize + x
        val byteLocation = bitLocation / java.lang.Byte.SIZE
        return bitBoards[0][byteLocation].toInt() or bitBoards[1][byteLocation].toInt() and (1 shl bitLocation % java.lang.Byte.SIZE) == LOCATION_EMPTY
    }

    fun isLocationOnBoard(x: Int, y: Int): Boolean {
        return x in 0 until boardSize && y in 0 until boardSize
    }

    val nPieces: Int
        get() {
            return (bitBoards[0].indices).sumBy {
                Integer.bitCount((bitBoards[0][it].toInt() or bitBoards[1][it].toInt()) and 0xff)
            }
        }

    fun setLocation(x: Int, y: Int, value: Int) {
        checkLocationArgs(x, y)
        val bitLocation = y * boardSize + x
        val byteLocation = bitLocation / java.lang.Byte.SIZE
        val byteShift = bitLocation % java.lang.Byte.SIZE
        when (value) {
            LOCATION_EMPTY -> {
                bitBoards[0][byteLocation] =
                    (bitBoards[0][byteLocation].toInt() and (1 shl byteShift xor 0xff)).toByte()
                bitBoards[1][byteLocation] =
                    (bitBoards[1][byteLocation].toInt() and (1 shl byteShift xor 0xff)).toByte()
            }
            LOCATION_BLACK -> {
                bitBoards[0][byteLocation] = (bitBoards[0][byteLocation].toInt() or (1 shl byteShift)).toByte()
                bitBoards[1][byteLocation] =
                    (bitBoards[1][byteLocation].toInt() and (1 shl byteShift xor 0xff)).toByte()
            }
            LOCATION_WHITE -> {
                bitBoards[0][byteLocation] =
                    (bitBoards[0][byteLocation].toInt() and (1 shl byteShift xor 0xff)).toByte()
                bitBoards[1][byteLocation] = (bitBoards[1][byteLocation].toInt() or (1 shl byteShift)).toByte()
            }
        }
    }

    private fun checkLocationArgs(x: Int, y: Int) {
        if (!isLocationOnBoard(x, y))
            throw IllegalArgumentException("(x=$x,y=$y) out of bounds")
    }

    override fun hashCode(): Int {
        var hashCode = 17 + boardSize
        hashCode = hashCode * 19 + bitBoards.contentHashCode()
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
                .forEach { _ -> return false }
        }
        return other.boardSize == boardSize && other.agentTurn == agentTurn
    }

    override fun toString(): String {
        val output = StringBuilder()
        for (i in boardSize - 1 downTo 0) {
            for (j in i..boardSize - 2) {
                output.append(" ")
            }
            for (j in 0 until boardSize) {
                when (getLocation(j, i)) {
                    LOCATION_BLACK -> output.append("X")
                    LOCATION_WHITE -> output.append("O")
                    else -> output.append("-")
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
