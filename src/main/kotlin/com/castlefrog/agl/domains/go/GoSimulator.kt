package com.castlefrog.agl.domains.go

import com.castlefrog.agl.Simulator
import com.castlefrog.agl.domains.AdversarialRewards
import com.castlefrog.agl.domains.nextPlayerTurnSequential
import com.castlefrog.agl.requireLegalAction

/**
 * Go with area scoring after two consecutive passes.
 *
 * Rules modeled: alternate turns, capture by removing groups with no liberties,
 * simple ko (no immediate recapture of a single-stone ko), no suicide unless the
 * move captures, pass allowed. Scoring is Chinese-style area score (stones +
 * surrounded empty points); higher score wins. Komi is applied for white.
 */
class GoSimulator(
    val boardSize: Int = GoState.DEFAULT_BOARD_SIZE,
    val komi: Double = DEFAULT_KOMI
) : Simulator<GoState, GoAction> {

    init {
        require(boardSize in MIN_BOARD_SIZE..MAX_BOARD_SIZE) {
            "Invalid board size: $boardSize"
        }
    }

    override val initialState: GoState
        get() = GoState(boardSize = boardSize)

    override fun numberOfPlayers(): Int = NUMBER_OF_PLAYERS

    override fun calculateRewards(state: GoState): IntArray {
        if (state.consecutivePasses < 2) {
            return AdversarialRewards.neutral()
        }
        val black = areaScore(state, GoState.LOCATION_BLACK)
        val white = areaScore(state, GoState.LOCATION_WHITE) + komi
        return when {
            black > white -> AdversarialRewards.firstPlayerWins()
            white > black -> AdversarialRewards.secondPlayerWins()
            else -> AdversarialRewards.neutral()
        }
    }

    override fun calculateLegalActions(state: GoState): List<Set<GoAction>> {
        val legal = listOf(mutableSetOf<GoAction>(), mutableSetOf())
        if (state.consecutivePasses >= 2) {
            return legal
        }
        val turn = state.agentTurn.toInt()
        legal[turn].add(GoAction.pass())
        val stone = if (turn == 0) GoState.LOCATION_BLACK else GoState.LOCATION_WHITE
        for (x in 0 until state.boardSize) {
            for (y in 0 until state.boardSize) {
                if (state.get(x, y) != GoState.LOCATION_EMPTY) continue
                if (x == state.koX && y == state.koY) continue
                if (isLegalPlacement(state, x, y, stone)) {
                    legal[turn].add(GoAction.place(x, y))
                }
            }
        }
        return legal
    }

    override fun stateTransition(state: GoState, actions: List<GoAction?>): GoState {
        val agentTurn = state.agentTurn.toInt()
        val legalActions = calculateLegalActions(state)
        val action = requireLegalAction(actions, agentTurn, legalActions[agentTurn], state)
        val next = state.copy()
        if (action.isPass) {
            next.consecutivePasses += 1
            next.koX = -1
            next.koY = -1
            next.agentTurn = nextPlayerTurnSequential(agentTurn, NUMBER_OF_PLAYERS).toByte()
            return next
        }
        next.consecutivePasses = 0
        val stone = if (agentTurn == 0) GoState.LOCATION_BLACK else GoState.LOCATION_WHITE
        val opponent = if (agentTurn == 0) GoState.LOCATION_WHITE else GoState.LOCATION_BLACK
        next.set(action.x, action.y, stone)

        var captured = 0
        var lastCapturedX = -1
        var lastCapturedY = -1
        for ((dx, dy) in ORTHO) {
            val nx = action.x + dx
            val ny = action.y + dy
            if (!next.isOnBoard(nx, ny)) continue
            if (next.get(nx, ny) != opponent) continue
            val group = collectGroup(next, nx, ny)
            if (liberties(next, group).isEmpty()) {
                for ((gx, gy) in group) {
                    next.set(gx, gy, GoState.LOCATION_EMPTY)
                    captured += 1
                    lastCapturedX = gx
                    lastCapturedY = gy
                }
            }
        }
        if (agentTurn == 0) {
            next.capturedByBlack += captured
        } else {
            next.capturedByWhite += captured
        }

        // Simple ko: single stone captured and the played stone has exactly one liberty.
        if (captured == 1) {
            val playedGroup = collectGroup(next, action.x, action.y)
            if (playedGroup.size == 1 && liberties(next, playedGroup).size == 1) {
                next.koX = lastCapturedX
                next.koY = lastCapturedY
            } else {
                next.koX = -1
                next.koY = -1
            }
        } else {
            next.koX = -1
            next.koY = -1
        }

        next.agentTurn = nextPlayerTurnSequential(agentTurn, NUMBER_OF_PLAYERS).toByte()
        return next
    }

    private fun isLegalPlacement(state: GoState, x: Int, y: Int, stone: Byte): Boolean {
        val trial = state.copy()
        trial.set(x, y, stone)
        val opponent = if (stone == GoState.LOCATION_BLACK) GoState.LOCATION_WHITE else GoState.LOCATION_BLACK
        var captures = false
        for ((dx, dy) in ORTHO) {
            val nx = x + dx
            val ny = y + dy
            if (!trial.isOnBoard(nx, ny)) continue
            if (trial.get(nx, ny) != opponent) continue
            val group = collectGroup(trial, nx, ny)
            if (liberties(trial, group).isEmpty()) {
                captures = true
                for ((gx, gy) in group) {
                    trial.set(gx, gy, GoState.LOCATION_EMPTY)
                }
            }
        }
        val ownGroup = collectGroup(trial, x, y)
        val ownLibs = liberties(trial, ownGroup)
        return captures || ownLibs.isNotEmpty()
    }

    private fun areaScore(state: GoState, color: Byte): Double {
        var stones = 0
        val visited = Array(state.boardSize) { BooleanArray(state.boardSize) }
        var territory = 0
        for (x in 0 until state.boardSize) {
            for (y in 0 until state.boardSize) {
                when (state.get(x, y)) {
                    color -> stones += 1
                    GoState.LOCATION_EMPTY -> {
                        if (!visited[x][y]) {
                            val region = ArrayList<Pair<Int, Int>>()
                            val borders = HashSet<Byte>()
                            floodEmpty(state, x, y, visited, region, borders)
                            if (borders.size == 1 && borders.contains(color)) {
                                territory += region.size
                            }
                        }
                    }
                }
            }
        }
        return (stones + territory).toDouble()
    }

    private fun floodEmpty(
        state: GoState,
        x: Int,
        y: Int,
        visited: Array<BooleanArray>,
        region: MutableList<Pair<Int, Int>>,
        borders: MutableSet<Byte>
    ) {
        val stack = ArrayDeque<Pair<Int, Int>>()
        stack.add(x to y)
        visited[x][y] = true
        while (stack.isNotEmpty()) {
            val (cx, cy) = stack.removeFirst()
            region.add(cx to cy)
            for ((dx, dy) in ORTHO) {
                val nx = cx + dx
                val ny = cy + dy
                if (!state.isOnBoard(nx, ny)) continue
                val v = state.get(nx, ny)
                if (v == GoState.LOCATION_EMPTY) {
                    if (!visited[nx][ny]) {
                        visited[nx][ny] = true
                        stack.add(nx to ny)
                    }
                } else {
                    borders.add(v)
                }
            }
        }
    }

    companion object {
        private const val NUMBER_OF_PLAYERS = 2
        private const val MIN_BOARD_SIZE = 2
        private const val MAX_BOARD_SIZE = 19
        private const val DEFAULT_KOMI = 6.5
        private val ORTHO = listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)

        internal fun collectGroup(state: GoState, x: Int, y: Int): List<Pair<Int, Int>> {
            val color = state.get(x, y)
            if (color == GoState.LOCATION_EMPTY) return emptyList()
            val group = ArrayList<Pair<Int, Int>>()
            val visited = Array(state.boardSize) { BooleanArray(state.boardSize) }
            val stack = ArrayDeque<Pair<Int, Int>>()
            stack.add(x to y)
            visited[x][y] = true
            while (stack.isNotEmpty()) {
                val (cx, cy) = stack.removeFirst()
                group.add(cx to cy)
                for ((dx, dy) in ORTHO) {
                    val nx = cx + dx
                    val ny = cy + dy
                    if (!state.isOnBoard(nx, ny) || visited[nx][ny]) continue
                    if (state.get(nx, ny) == color) {
                        visited[nx][ny] = true
                        stack.add(nx to ny)
                    }
                }
            }
            return group
        }

        internal fun liberties(state: GoState, group: List<Pair<Int, Int>>): Set<Pair<Int, Int>> {
            val libs = HashSet<Pair<Int, Int>>()
            for ((x, y) in group) {
                for ((dx, dy) in ORTHO) {
                    val nx = x + dx
                    val ny = y + dy
                    if (state.isOnBoard(nx, ny) && state.get(nx, ny) == GoState.LOCATION_EMPTY) {
                        libs.add(nx to ny)
                    }
                }
            }
            return libs
        }
    }
}
