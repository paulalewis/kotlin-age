package com.castlefrog.agl.domains.connect4

import com.castlefrog.agl.AdversarialSimulator
import com.castlefrog.agl.TurnType
import sun.reflect.generics.reflectiveObjects.NotImplementedException

import java.util.ArrayList
import java.util.Arrays

class Connect4Simulator(state: Connect4State,
                        legalActions: List<MutableList<Connect4Action>>? = null,
                        rewards: IntArray? = null,
                        val turnType: TurnType = TurnType.SEQUENTIAL,
                        private val columnHeights: IntArray = IntArray(Connect4State.WIDTH)) :
        AdversarialSimulator<Connect4State, Connect4Action>() {

    override var state: Connect4State = state
        set(value) {
            field = value
            _legalActions = null
            _rewards = null
        }

    private var _legalActions: List<MutableList<Connect4Action>>? = null
    override val legalActions: List<MutableList<Connect4Action>>
        get() {
            if (_legalActions == null) {
                _legalActions = computeLegalActions(state, rewards, columnHeights)
            }
            return _legalActions ?: computeLegalActions(state, rewards, columnHeights)
        }

    private var _rewards: IntArray? = null
    override val rewards: IntArray
        get() {
            if (_rewards == null) {
                _rewards = computeRewards(state)
            }
            return _rewards ?: computeRewards(state)
        }

    init {
        _legalActions = legalActions
        _rewards = rewards
    }

    override fun copy(): Connect4Simulator {
        return Connect4Simulator(state.copy(), _legalActions?.copy(), _rewards?.copyOf(), turnType, columnHeights.copyOf())
    }

    override fun stateTransition(actions: Map<Int, Connect4Action>) {
        val action = actions[state.agentTurn]
        if (action === null || !legalActions[state.agentTurn].contains(action)) {
            throw IllegalArgumentException("Illegal action, $action, from state, $state")
        }
        if (state.agentTurn == 0) {
            state.bitBoardBlack = state.bitBoardBlack xor (1L shl columnHeights[action.location]++)
        } else {
            state.bitBoardWhite = state.bitBoardWhite xor (1L shl columnHeights[action.location]++)
        }
        state.agentTurn = getNextAgentTurn(state.agentTurn, turnType)
        _rewards = computeRewards(state)
        computeLegalActions(action, state, rewards, legalActions, columnHeights)
    }

    companion object {
        private val ALL_LOCATIONS = (1L shl (Connect4State.HEIGHT + 1) * Connect4State.WIDTH) - 1
        private val FIRST_COLUMN = (1L shl Connect4State.HEIGHT + 1) - 1
        private val BOTTOM_ROW = ALL_LOCATIONS / FIRST_COLUMN
        private val ABOVE_TOP_ROW = BOTTOM_ROW shl Connect4State.HEIGHT

        fun create(turnType: TurnType): Connect4Simulator {
            return Connect4Simulator(state = getInitialState(turnType), turnType = turnType)
        }

        fun getInitialState(turnType: TurnType): Connect4State {
            when (turnType) {
                TurnType.RANDOM -> return Connect4State(agentTurn = getNextAgentTurn(0, turnType))
                TurnType.SEQUENTIAL -> return Connect4State()
                else -> throw NotImplementedException()
            }
        }

        private fun computeRewards(state: Connect4State): IntArray {
            val height = Connect4State.HEIGHT
            for (i in 0..N_PLAYERS - 1) {
                val bitBoard = if (i == 0) state.bitBoardBlack else state.bitBoardWhite
                val diagonal1 = bitBoard and (bitBoard shr height)
                val horizontal = bitBoard and (bitBoard shr height + 1)
                val diagonal2 = bitBoard and (bitBoard shr height + 2)
                val vertical = bitBoard and (bitBoard shr 1)
                if (diagonal1 and (diagonal1 shr 2 * height) or
                        (horizontal and (horizontal shr 2 * (height + 1))) or
                        (diagonal2 and (diagonal2 shr 2 * (height + 2))) or
                        (vertical and (vertical shr 2)) != 0L) {
                    if (i == 0) {
                        return AdversarialSimulator.REWARDS_BLACK_WINS
                    } else {
                        return AdversarialSimulator.REWARDS_WHITE_WINS
                    }
                }
            }
            return AdversarialSimulator.REWARDS_NEUTRAL
        }

        private fun computeLegalActions(action: Connect4Action,
                                        state: Connect4State,
                                        rewards: IntArray,
                                        legalActions: List<MutableList<Connect4Action>>,
                                        columnHeights: IntArray) {
            legalActions[state.agentTurn].addAll(legalActions[(state.agentTurn + 1) % 2])
            legalActions[(state.agentTurn + 1) % 2].clear()
            if (Arrays.equals(rewards, AdversarialSimulator.REWARDS_NEUTRAL)) {
                if (1L shl columnHeights[action.location] and ABOVE_TOP_ROW != 0L) {
                    legalActions[state.agentTurn].remove(action)
                }
            } else {
                legalActions[0].clear()
                legalActions[1].clear()
            }
        }

        private fun computeLegalActions(state: Connect4State,
                                        rewards: IntArray,
                                        columnHeights: IntArray): List<MutableList<Connect4Action>> {
            val legalActions = ArrayList<MutableList<Connect4Action>>()
            legalActions.add(ArrayList<Connect4Action>())
            legalActions.add(ArrayList<Connect4Action>())
            computeHeights(state, columnHeights)
            if (Arrays.equals(rewards, AdversarialSimulator.REWARDS_NEUTRAL)) {
                for (i in 0..Connect4State.WIDTH - 1) {
                    if (1L shl columnHeights[i] and ABOVE_TOP_ROW == 0L) {
                        legalActions[state.agentTurn].add(Connect4Action.valueOf(i))
                    }
                }
            }
            return legalActions
        }

        private fun computeHeights(state: Connect4State, columnHeights: IntArray) {
            val bitBoard = state.bitBoardBlack or state.bitBoardWhite
            for (i in 0..Connect4State.WIDTH - 1) {
                columnHeights[i] = (Connect4State.HEIGHT + 1) * i
                while (bitBoard and (1L shl columnHeights[i]) != 0L) {
                    columnHeights[i] += 1
                }
            }
        }

        private fun getNextAgentTurn(agentTurn: Int, turnType: TurnType): Int {
            when (turnType) {
                TurnType.RANDOM -> return (Math.random() * AdversarialSimulator.N_PLAYERS).toInt()
                TurnType.SEQUENTIAL -> return (agentTurn + 1) % AdversarialSimulator.N_PLAYERS
                else -> throw NotImplementedException()
            }
        }

    }
}
