package com.castlefrog.agl.agents;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.castlefrog.agl.Action;
import com.castlefrog.agl.Agent;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.State;

/**
 * Enhanced UCT agent that can be run as normal UCT or a modified version of UCT
 * based on input parameters.
 */
public final class UctAgent implements Agent {
    /** number of simulations to run */
    private int nSimulations_;

    /** UCT constant */
    private double uctConstant_;

    /**
     * Maximum number of state samples to inspect from each action.
     * -1 indicates infinite sample size.
     */
    private int sparseSampleSize_;

    /** Number of distinct UCT trees to build and evaluate. */
    private int nEnsembles_;

    /** method for quickly simulating entire game */
    private SimulationMethod simulationMethod_;

    public enum SimulationMethod {
        RANDOM
    }

    /**
     * Defines a general node in a UCT tree.
     * The rewards_ field is lazily initialized because
     * its size isn't known until the first rewards vector
     * is passed to update.
     */
    private abstract class Node {
        /** rewards for each agent */
        protected int[] rewards_;

        /** number of visits to this node */
        protected int visits_;

        /**
         * Adds rewards and increments visits.
         * @param rewards for each agent
         */
        public void update(int[] rewards) {
            if (rewards_ == null) {
                rewards_ = new int[rewards.length];
            }
            for (int i = 0; i < rewards.length; i++) {
                rewards_[i] += rewards[i];
            }
            visits_ += 1;
        }

        public int getVisits() {
            return visits_;
        }

        /**
         * Get the reward for the agent.
         * @param agentId
         *      agent identifier
         * @return
         *      accumulated reward for agent
         */
        public int getReward(int agentId) {
            return rewards_[agentId];
        }
    }

    /**
     * Holds a state and a list of pointers to action nodes. The action nodes
     * represent all legal moves from the contained state.
     */
    private class StateNode<S extends State<S>, A extends Action> extends Node {
        private final S state_;
        private final List<ActionNode<S, A>> children_;

        /**
         * When a new state node is created it immediately
         * populates its children.
         * @param state should be immutable or deep copy of original object
         */
        public StateNode(S state, List<List<A>> legalActions) {
            state_ = state;

            List<List<A>> actionGroups = new ArrayList<>();
            for (List<A> actions : legalActions) {
                actionGroups = combineActions(actionGroups, actions);
            }
            children_ = new ArrayList<>(actionGroups.size());
            for (List<A> actions: actionGroups) {
                children_.add(new ActionNode<S, A>(actions));
            }
        }

        public List<List<A>> combineActions(List<List<A>> actionGroups,
                                            List<A> actions) {
            if (actions.size() == 0) {
                actions.add(null);
            }
            List<List<A>> newActionGroups = new ArrayList<>();
            if (actionGroups.size() == 0) {
                for (A action: actions) {
                    List<A> temp = new ArrayList<>();
                    temp.add(action);
                    newActionGroups.add(temp);
                }
            } else {
                for (A action: actions) {
                    for (List<A> actionGroup: actionGroups) {
                        List<A> temp = new ArrayList<>();
                        for (A tempAction: actionGroup) {
                            temp.add(tempAction);
                        }
                        temp.add(action);
                        newActionGroups.add(temp);
                    }
                }
            }
            return newActionGroups;
        }

        /**
         * Select child node with best UCT value. Always play a random
         * unexplored action first.
         * @return an action child node.
         */
        public ActionNode<S, A> uctSelect() {
            assert children_.size() > 0;
            if (visits_ <= children_.size()) {
                List<ActionNode<S, A>> unvisited = new ArrayList<>();
                for (ActionNode<S, A> child: children_) {
                    if (child.getVisits() == 0) {
                        unvisited.add(child);
                    }
                }
                return unvisited.get((int) (Math.random() * unvisited.size()));
            } else {
                ActionNode<S, A> result = null;
                double bestUct = 0;
                for (ActionNode<S, A> child: children_) {
                    double uctValue = 0;
                    for (int i = 0; i < rewards_.length; i += 1) {
                        //All agents with non null actions are trying to maximize their reward
                        if (child.getActions().get(i) != null) {
                            uctValue += ((double) child.getReward(i)) / child.getVisits() +
                                        uctConstant_ * Math.sqrt(Math.log(getVisits()) / child.getVisits()) +
                                        getRandomEpsilon();
                        }
                    }
                    if (result == null || uctValue > bestUct) {
                        bestUct = uctValue;
                        result = child;
                    }
                }
                return result;
            }
        }

        public S getState() {
            return state_.copy();
        }

        public List<ActionNode<S, A>> getChildren() {
            return children_;
        }

        /*public List<List<A>> getLegalActions() {
            List<List<A>> legalActions = new ArrayList<List<A>>();
            for (ActionNode<S, A> child: children_)
                legalActions.add(child.getActions());
            return legalActions;
        }*/
    }

    private class ActionNode<S extends State<S>, A extends Action> extends Node {
        private final List<A> actions_;

        private List<StateNode<S, A>> frequencyTable_;

        private Hashtable<Integer, StateNode<S, A>> children_;

        public ActionNode(List<A> actions) {
            actions_ = actions;
            frequencyTable_ = null;
            if (sparseSampleSize_ != -1) {
                children_ = new Hashtable<>(sparseSampleSize_);
            } else {
                children_ = new Hashtable<>();
            }
        }

        /**
         * Will take an action from the current simulator's state, create a new
         * state node at the next state and return that state node. If sparse
         * sampling limit has been reached then a random node is returned from the
         * current list of children.
         * @param simulator used to simulate actions.
         * @return selected child state node.
         */
        public StateNode<S, A> selectChild(Simulator<S, A> simulator) {
            if (sparseSampleSize_ == -1 || visits_ < sparseSampleSize_) {
                Simulator<S, A> simulatorCopy = simulator.copy();
                simulatorCopy.stateTransition(actions_);
                S state = simulatorCopy.getState();
                StateNode<S, A> stateNode = children_.get(state.hashCode());
                if (stateNode == null) {
                    stateNode = new StateNode<>(state, simulatorCopy.getLegalActions());
                    children_.put(state.hashCode(), stateNode);
                }
                return stateNode;
            } else {
                if (frequencyTable_ == null) {
                    frequencyTable_ = new ArrayList<>();
                    for (StateNode<S, A> stateNode : children_.values()) {
                        for (int i = 0; i < stateNode.visits_; i++) {
                            frequencyTable_.add(stateNode);
                        }
                    }
                    children_ = null; // Release hash table from memory
                }
                return frequencyTable_.get((int) (Math.random() * frequencyTable_.size()));
            }
        }

        public List<A> getActions() {
            return actions_;
        }
    }

    /**
     * Create a traditional UCT agent.
     * @param nSimulations
     *            the number of complete games to simulate.
     * @param uctConstant
     *            controls balance between exploration and exploitation.
     */
    public UctAgent(int nSimulations,
                    double uctConstant) {
        this(nSimulations, uctConstant, -1, 1);
    }

    /**
     * UCT algorithm with sparse sampling of large stochastic state spaces.
     * @param nSimulations
     *            the number of complete games played.
     * @param uctConstant
     *            controls balance between exploration and exploitation.
     * @param sparseSampleSize
     *            max number of sample states from any action node or infinite
     *            if equal to -1.
     */
    public UctAgent(int nSimulations,
                    double uctConstant,
                    int sparseSampleSize) {
        this(nSimulations, uctConstant, sparseSampleSize, 1);
    }

    /**
     * UCT algorithm with sparse sampling and ensemble methods.
     * @param nSimulations
     *            the number of complete games played.
     * @param uctConstant
     *            The higher the value of the UCT Constant the more
     *            exploration is done while building the tree.
     * @param sparseSampleSize
     *            max number of sample states from any action node or infinite
     *            if equal to -1.
     * @param nEnsembles
     *            number of trees built.
     */
    public UctAgent(int nSimulations,
                    double uctConstant,
                    int sparseSampleSize,
                    int nEnsembles) {
        if (nSimulations < 1) {
            throw new IllegalArgumentException("Number of Simulations < 1");
        }
        if (uctConstant < 0) {
            throw new IllegalArgumentException("UCT Constant > 0");
        }
        if (sparseSampleSize < 1 && sparseSampleSize != -1) {
            throw new IllegalArgumentException("Sparse Sample Size > 0 or = -1");
        }
        if (nEnsembles < 1) {
            throw new IllegalArgumentException("Ensemble trials must be > 0");
        }
        nSimulations_ = nSimulations;
        uctConstant_ = uctConstant;
        sparseSampleSize_ = sparseSampleSize;
        nEnsembles_ = nEnsembles;
        simulationMethod_ = SimulationMethod.RANDOM;
    }

    /**
     * Builds UCT trees and then selects the best action
     * in simultaneous or turn based domains.
     * If the number of trajectories is less than the number of actions at the
     * root state then not all actions are explored at least one time. In this
     * situation the best action is selected from only those that have been
     * explored.
     *
     */
    public <S extends State<S>, A extends Action> A selectAction(int agentId, S state, Simulator<S, A> simulator) {
        simulator.setState(state);
        List<List<A>> legalActions = simulator.getLegalActions();
        //if only one of action is possible, skip action selection algorithms
        if (legalActions.get(agentId).size() == 1) {
            return legalActions.get(agentId).get(0);
        }

        StateNode<S, A> temp = new StateNode<>(simulator.getState(), legalActions);
        double[] rootActionRewards = new double[temp.getChildren().size()];
        int[] rootActionVisits = new int[temp.getChildren().size()];
        // Generate UCT trees equal to the number of ensembles
        for (int i = 0; i < nEnsembles_; i += 1) {
            StateNode<S, A> root = new StateNode<>(simulator.getState(), legalActions);
            for (int j = 0; j < nSimulations_; j += 1) {
                playSimulation(root, simulator.copy());
            }

            // Save visits and current agent's rewards at all root action nodes
            List<ActionNode<S, A>> children = root.getChildren();
            for (int j = 0; j < children.size(); j += 1) {
                if (children.get(j).getVisits() > 0) {
                    rootActionRewards[j] += children.get(j).getReward(agentId);
                    rootActionVisits[j] += children.get(j).getVisits();
                }
            }
        }
        A selectedAction = null;
        double bestValue = 0;
        double[] values = new double[rootActionRewards.length];
        for (int i = 0; i < values.length; i += 1) {
            if (rootActionVisits[i] > 0) {
                values[i] = rootActionRewards[i] / rootActionVisits[i] + getRandomEpsilon();
            }
        }
        for (int i = 0; i < values.length; i += 1) {
            if (rootActionVisits[i] > 0 &&
                    (selectedAction == null || values[i] > bestValue)) {
                selectedAction = legalActions.get(agentId).get(i);
                bestValue = values[i];
            }
        }
        return selectedAction;
    }

    /**
     * Selects an action by summing the rewards and visits
     * of the root actions for each tree and selecting the
     * one with the best average reward.
     */
    /*private int selectActionIndex(double[][] rootActionRewards,
                                  int[][] rootActionVisits) {
        int actionIndex = 0;
        double[] values = new double[rootActionRewards[0].length];
        int[] visits = new int[values.length];
        for (int i = 0; i < nEnsembles_; i += 1)
            for (int j = 0; j < values.length; j += 1) {
                values[j] += rootActionRewards[i][j];
                visits[j] += rootActionVisits[i][j];
            }
        while (visits[actionIndex] == 0)
            actionIndex += 1;
        for (int i = actionIndex + 1; i < values.length; i += 1)
            if (visits[i] > 0
                    && values[i] / visits[i] + getRandomEpsilon()
                        > values[actionIndex] / visits[actionIndex])
                actionIndex = i;
        return actionIndex;
    }*/

    private double getRandomEpsilon() {
        final double epsilon = 0.0000000005;
        return epsilon * (Math.random() - 0.5);
    }

    /**
     * This method walks down the tree making decisions of the best nodes as it
     * goes. When it reaches an unexplored leaf node it plays a random game to
     * initialize that nodes value.
     * @param node
     *            current state node being traversed in tree.
     * @param simulator
     *            contains current state of game being played.
     * @return rewards of simulated game are passed up the tree.
     */
    private <S extends State<S>, A extends Action> int[] playSimulation(StateNode<S, A> node, Simulator<S, A> simulator) {
        int[] rewards;
        if (simulator.isTerminalState() || node.getVisits() == 0) {
            rewards = simulateGame(simulator);
        } else {
            rewards = playSimulation(node.uctSelect(), simulator);
        }
        node.update(rewards);
        return rewards;
    }

    /**
     * This method walks down the tree making decisions of the best nodes as it
     * goes. When it reaches an unexplored leaf node it plays a random game to
     * initialize that nodes value.
     * @param node
     *            current action node being traversed in tree.
     * @param simulator
     *            contains current state of game being played.
     * @return rewards of simulated game are passed up the tree.
     */
    private <S extends State<S>, A extends Action> int[] playSimulation(ActionNode<S, A> node, Simulator<S, A> simulator) {
        StateNode<S, A> child = node.selectChild(simulator);
        //TODO - decide if needed or can separate setState and setActions
        //This only improves performance - is there another way?
        //simulator.setState(child.getState(), child.getLegalActions());
        simulator.setState(child.getState());
        int[] rewards = playSimulation(child, simulator);
        node.update(rewards);
        return rewards;
    }

    /**
     * Quickly simulate a game from the current state and return accumulated
     * reward.
     * @param simulator
     *            a copy of the simulator you want to use to simulate game.
     * @return accumulated reward vector from the game.
     */
    private <S extends State<S>, A extends Action> int[] simulateGame(Simulator<S, A> simulator) {
        List<List<A>> legalActions = simulator.getLegalActions();
        int[] totalRewards = simulator.getRewards();
        while (!simulator.isTerminalState()) {
            switch (simulationMethod_) {
            default:
            case RANDOM:
                List<A> selectedActions = new ArrayList<>();
                for (List<A> actions: legalActions) {
                    if (actions.size() != 0) {
                        selectedActions.add(actions.get((int) (Math.random() * actions.size())));
                    } else {
                        selectedActions.add(null);
                    }
                }
                simulator.stateTransition(selectedActions);
                break;
            }
            for (int i = 0; i < totalRewards.length; i++) {
                totalRewards[i] += simulator.getRewards()[i];
            }
            legalActions = simulator.getLegalActions();
        }
        return totalRewards;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(UctAgent.class.getSimpleName());
        output.append("\n  number of simulations:     ").append(nSimulations_);
        output.append("\n  UCT constant:              ").append(uctConstant_);
        if (sparseSampleSize_ > 0) {
            output.append("\n  sparse sample size:        ").append(sparseSampleSize_);
        }
        if (nEnsembles_ > 1) {
            output.append("\n  number of ensembles:       ").append(nEnsembles_);
        }
        return output.toString();
    }
}
