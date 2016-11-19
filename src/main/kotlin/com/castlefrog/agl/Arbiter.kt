package com.castlefrog.agl

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.schedulers.Schedulers
import java.util.Vector

/**
 * Arbiter is used to regulate agents with the simulator.
 * It allows each agent to run in its own thread for
 * simultaneous domains and records history and other game data.
 */
class Arbiter<S : State<S>, A : Action<A>>(val history: History<S, A>,
                                        val world: Simulator<S, A>,
                                        val agents: List<Agent>) {

    val decisionTimes: LongArray
    val stateChange: ObservableSource<S>

    init {
        if (world.nAgents != agents.size) {
            throw IllegalArgumentException("Expects " + world.nAgents +
                    " agents but " + agents.size + " provided.")
        }
        world.state = history.state
        decisionTimes = LongArray(world.nAgents)

        val selectActionObservables = (0..world.nAgents - 1)
                .map {
                    Observable.defer {
                        Observable.just(agents[it].selectAction(it, world.state, world.copy()).get())
                    }
                }
        stateChange = Observable.create<S> {
            while (!world.isTerminalState) {
                Observable.zip(selectActionObservables) { args ->
                    val actions = Vector<A>()
                    args.mapTo(actions) { it as A }
                    actions
                }
                        .subscribeOn(Schedulers.io())
                        .subscribe { actions ->
                            world.stateTransition(actions)
                            history.add(world.state, actions)
                            it.onNext(world.state)
                        }
            }
            it.onComplete()
        }.publish()
    }

}
