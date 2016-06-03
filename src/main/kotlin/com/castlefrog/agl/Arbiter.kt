package com.castlefrog.agl

import rx.Observable
import rx.Scheduler
import rx.functions.Action1
import rx.functions.Func0
import rx.functions.FuncN
import rx.schedulers.Schedulers

import java.util.ArrayList
import java.util.Vector
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Arbiter is used to regulate agents with the simulator.
 * It allows each agent to run in its own thread for
 * simultaneous domains and records history and other game data.
 */
class Arbiter<S : State<S>, A : Action<A>>(val history: History<S, A>,
                                        val world: Simulator<S, A>,
                                        val agents: List<Agent>) {

    val decisionTimes: LongArray
    var listener = {}

    init {
        if (world.nAgents != agents.size) {
            throw IllegalArgumentException("Expects " + world.nAgents +
                    " agents but " + agents.size + " provided.")
        }
        world.state = history.state
        decisionTimes = LongArray(world.nAgents)
    }

    /**
     * Reset the arbiter to the initial
     * state so that another game may be played.
     */
    @Synchronized fun reset() {
        history.clear()
        world.state = history.state
    }

    /**
     * Take a single state transition in domain.
     * If the agent has no legal actions to take
     * from a given state then that agent is only
     * allowed to return a null action. This method
     * skips an agents select action method if the
     * only action available is null.
     */
    @Synchronized fun step() {
        if (!world.isTerminalState) {
            val selectActionObservables = ArrayList<Observable<A>>()
            for (i in 0..world.nAgents - 1) {
                val agentId = i
                selectActionObservables.add(
                        Observable.defer {
                            Observable.just(agents[agentId].selectAction(agentId, world.state, world.copy()))
                        })
            }

            Observable.zip(selectActionObservables) { args ->
                val actions = Vector<A>()
                for (action in args) {
                    actions.add(action as A)
                }
                actions
            }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.immediate())
                    .subscribe { actions ->
                        world.stateTransition(actions)
                        history.add(world.state, actions)
                        listener
                    }
        }
    }

    /**
     * Move back one state in history.
     */
    @Synchronized fun prevHistory() {
        if (history.hasPrevState()) {
            history.prevState()
            world.state = history.state
        }
    }

    /**
     * Move forward one state in history.
     */
    @Synchronized fun nextHistory() {
        if (history.hasNextState()) {
            history.nextState()
            world.state = history.state
        }
    }

    fun getRewards(): IntArray {
        return world.rewards
    }

    fun isTerminalState(): Boolean {
        return world.isTerminalState
    }

}
