package com.castlefrog.agl

import java.util.Optional

/**
 * An agent interacts in a domain by selecting
 * an action from a list of legal actions for
 * a player from the current state.
 */
interface Agent {

    /**
     * An agent selects an action given a state, simulator and player ID.
     * @param playerId player ID that this agent is selecting the action for
     * @param state current domain state.
     * @param simulator simulator that determines action outcomes in domain.
     * @return selected action from the current state or no action
     *         if the agent had no legal actions to choose
     */
    fun <S : State<S>, A : Action<A>> selectAction(playerId: Int, state: S, simulator: Simulator<S, A>): Optional<A>

    companion object {
        /**
         * Helper method for checking if a player has at least 1 legal action
         * @param playerId id of the player to select
         * @param legalActions list of legal actions from a given state in the simulator
         * @return true if the given player has at least 1 legal action
         */
        fun <A : Action<A>> playerHasLegalActions(playerId: Int, legalActions: List<List<A>>): Boolean {
            if (playerId < 0 || playerId >= legalActions.size) {
                return false
            }
            val actions = legalActions[playerId]
            if (actions.isEmpty()) {
                return false
            }
            return true
        }
    }
}
