package com.castlefrog.agl.domains.connect4

import com.castlefrog.agl.AdversarialSimulator
import com.castlefrog.agl.TurnType
import sun.reflect.generics.reflectiveObjects.NotImplementedException

import java.util.ArrayList

class Connect4Simulator : AdversarialSimulator<Connect4State, Connect4Action> {

    private val columnHeights_: IntArray
    val turnType: TurnType

    private constructor(state: Connect4State, turnType: TurnType) {
        legalActions_ = ArrayList<List<Connect4Action>>()
        legalActions_.add(ArrayList<Connect4Action>())
        legalActions_.add(ArrayList<Connect4Action>())
        columnHeights_ = IntArray(Connect4State.WIDTH)
        this.turnType = turnType
        setState(state)
    }

    private constructor(simulator: Connect4Simulator, columnHeights: IntArray) : super(simulator) {
        turnType = simulator.turnType
        columnHeights_ = IntArray(columnHeights.size)
        System.arraycopy(columnHeights, 0, columnHeights_, 0, columnHeights.size)
    }

    override fun copy(): Connect4Simulator {
        return Connect4Simulator(this, columnHeights_)
    }

    override fun setState(state: Connect4State) {
        state_ = state
        rewards_ = computeRewards(state_)
        computeLegalActions(state_)
    }

    private fun computeHeights(state: Connect4State) {
        val bitBoard = state.bitBoardBlack or state.bitBoardWhite
        for (i in 0..Connect4State.WIDTH - 1) {
            columnHeights_[i] = (Connect4State.HEIGHT + 1) * i
            while (bitBoard and (1L shl columnHeights_[i]) != 0L) {
                columnHeights_[i] += 1
            }
        }
    }

    override fun stateTransition(actions: List<Connect4Action?>) {
        assert(actions.size == N_AGENTS)
        val action = actions[state_.agentTurn]
        if (action == null || !legalActions_[state_.agentTurn].contains(action)) {
            throw IllegalArgumentException("Illegal action, $action, from state, $state_")
        }
        if (state_.agentTurn == 0) {
            state_.bitBoardBlack = state_.bitBoardBlack xor (1L shl columnHeights_[action.location]++)
        } else {
            state_.bitBoardWhite = state_.bitBoardWhite xor (1L shl columnHeights_[action.location]++)
        }
        state_.agentTurn = getNextAgentTurn(state_.agentTurn)
        rewards_ = computeRewards(state_)
        computeLegalActions(action)
    }

    private fun computeLegalActions(action: Connect4Action) {
        if (legalActions_[state_.agentTurn].isEmpty()) {
            val temp = legalActions_[0]
            legalActions_[0] = legalActions_[1]
            legalActions_[1] = temp
        }
        if (rewards_ == AdversarialSimulator.REWARDS_NEUTRAL) {
            if (1L shl columnHeights_[action.location] and ABOVE_TOP_ROW != 0L) {
                legalActions_[state_.agentTurn].remove(action)
            }
        } else {
            clearLegalActions()
        }
    }

    private fun computeLegalActions(state: Connect4State) {
        clearLegalActions()
        computeHeights(state)
        if (rewards_ == AdversarialSimulator.REWARDS_NEUTRAL) {
            for (i in 0..Connect4State.WIDTH - 1) {
                if (1L shl columnHeights_[i] and ABOVE_TOP_ROW == 0L) {
                    legalActions_[state.agentTurn].add(Connect4Action.valueOf(i))
                }
            }
        }
    }

    private fun computeRewards(state: Connect4State): IntArray {
        val height = Connect4State.HEIGHT
        for (i in 0..N_AGENTS - 1) {
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

    private fun getNextAgentTurn(agentTurn: Int): Int {
        when (turnType) {
            TurnType.RANDOM -> return (Math.random() * AdversarialSimulator.N_AGENTS).toInt()
            TurnType.SEQUENTIAL -> return (agentTurn + 1) % AdversarialSimulator.N_AGENTS
            else -> throw NotImplementedException()
        }
    }

    companion object {
        private val ALL_LOCATIONS = (1L shl (Connect4State.HEIGHT + 1) * Connect4State.WIDTH) - 1
        private val FIRST_COLUMN = (1L shl Connect4State.HEIGHT + 1) - 1
        private val BOTTOM_ROW = ALL_LOCATIONS / FIRST_COLUMN
        private val ABOVE_TOP_ROW = BOTTOM_ROW shl Connect4State.HEIGHT

        fun create(turnType: TurnType): Connect4Simulator {
            return Connect4Simulator(getInitialState(turnType), turnType)
        }

        fun create(state: Connect4State,
                   turnType: TurnType): Connect4Simulator {
            return Connect4Simulator(state, turnType)
        }

        fun getInitialState(turnType: TurnType): Connect4State {
            when (turnType) {
                TurnType.RANDOM -> return Connect4State(agentTurn = (Math.random() * AdversarialSimulator.N_AGENTS).toInt())
                TurnType.SEQUENTIAL -> return Connect4State()
                else -> throw NotImplementedException()
            }
        }
    }
}
