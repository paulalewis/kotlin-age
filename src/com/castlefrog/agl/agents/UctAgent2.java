package com.castlefrog.agl.agents;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;
import com.castlefrog.agl.Agent;
import com.castlefrog.agl.Simulator;

/**
 * Enhanced UCT agent that can be run as normal UCT or a modified version of UCT
 * based on input parameters.
 */
public final class UctAgent2 implements Agent {
    /** number of simulations to run */
    private int nSimulations_;
    /** UCT Constant. */
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
    /**
     * if true then all actions from a simulation update
     * all root aciton values
     */
    private boolean allActionsAsFirst_;

    public enum SimulationMethod {
        RANDOM
    }

    /**
     * Defines a general node in a UCT tree.
     * The rewards_ field is lazily initialized because
     * its size isn't known until the first rewards vector
     * is passed to addRewards.
     */
    private abstract class Node {
        /** rewards for each agent */
        protected int[] rewards_;
        /** number of visits to this node */
        protected int nVisits_;

        public void addRewards(int[] rewards) {
            if (rewards_ == null)
                rewards_ = new int[rewards.length];
            for (int i = 0; i < rewards.length; i += 1)
                rewards_[i] += rewards[i];
        }

        public void incVisits() {
            nVisits_ += 1;
        }

        public int getVisits() {
            return nVisits_;
        }

        /**
         * Get the reward for the agent.
         * Reward is only valid if nVisits_ > 0
         * 
         * @param agentId
         *      agent identifier
         * @return
         *      accumulated reward for agent
         */
        public int getReward(int agentId) {
            assert nVisits_ > 0;
            return rewards_[agentId];
        }
    }

    /**
     * Holds a state and a list of pointers to action nodes. The action nodes
     * represent all legal moves from the contained state.
     */
    private class StateNode<S, A> extends Node {
        /** state this node represents */
        private S state_;
        /** list of children nodes */
        private List<ActionNode<S, A>> children_;
        /** parent of this node */
        private ActionNode<S, A> parent_;

        /**
         * When a new state node is created it immediately
         * populates its children.
         */
        public StateNode(S state,
                         List<List<A>> legalActions,
                         ActionNode<S, A> parent) {
            //state should be immutable or a deep copy of original object
            state_ = state;

            List<List<A>> actionGroups = new ArrayList<List<A>>();
            for (int i = 0; i < legalActions.size(); i++)
                actionGroups = combineActions(actionGroups, legalActions.get(i));
            
            children_ = new ArrayList<ActionNode<S, A>>(actionGroups.size());
            for (List<A> actions: actionGroups)
                children_.add(new ActionNode<S, A>(actions, this));
            parent_ = parent;
        }

        public List<List<A>> combineActions(List<List<A>> actionGroups,
                                            List<A> actions) {
            if (actions.size() == 0)
                actions.add(null);
            List<List<A>> newActionGroups = new ArrayList<List<A>>();
            if (actionGroups.size() == 0) {
                for (A action: actions) {
                    List<A> temp = new ArrayList<A>();
                    temp.add(action);
                    newActionGroups.add(temp);
                }
            } else {
                for (A action: actions) {
                    for (List<A> actionGroup: actionGroups) {
                        List<A> temp = new ArrayList<A>();
                        for (A tempAction: actionGroup)
                            temp.add(tempAction);
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
         * 
         * @return an action child node.
         */
        public ActionNode<S, A> uctSelect() {
            assert children_.size() > 0;
            if (nVisits_ <= children_.size()) {
                List<ActionNode<S, A>> unvisited = new ArrayList<ActionNode<S, A>>();
                for (ActionNode<S, A> child: children_)
                    if (child.getVisits() == 0)
                        unvisited.add(child);
                return unvisited.get((int) (Math.random() * unvisited.size()));
            } else {
                ActionNode<S, A> result = null;
                double bestUct = 0;
                for (ActionNode<S, A> child: children_) {
                    double uctValue = 0;
                    for (int i = 0; i < rewards_.length; i++) {
                        //All agents with non null actions are trying to maximize their reward
                        if (child.getActions().get(i) != null) {
                            uctValue += ((double) child.getReward(i)) / child.getVisits()
                                     + uctConstant_ * Math.sqrt(Math.log(getVisits()) / child.getVisits())
                                     + getRandomEpsilon();
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

        /**
         * the returned state should not
         * be modified
         */
        public S getState() {
            //if (state_ instanceof Cloneable)
            //    return state_.clone();
            //else
            return state_;
        }

        public List<ActionNode<S, A>> getChildren() {
            return children_;
        }

        public List<List<A>> getLegalActions() {
            List<List<A>> legalActions = new ArrayList<List<A>>();
            for (ActionNode<S, A> child: children_)
                legalActions.add(child.getActions());
            return legalActions;
        }

        public ActionNode<S, A> getParent() {
            return parent_;
        }

        @Override
        public int hashCode() {
            return state_.hashCode();
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof StateNode))
                return false;
            StateNode<S, A> node = (StateNode<S, A>) object;
            return (state_.equals(node.getState()));
        }
    }

    private class ActionNode<S, A> extends Node {
        private List<A> actions_;

        private List<StateNode<S, A>> frequencyTable_;

        private Hashtable<Integer, StateNode<S, A>> children_;

        private StateNode<S, A> parent_;

        public ActionNode(List<A> actions,
                           StateNode<S, A> parent) {
            actions_ = actions;
            frequencyTable_ = null;
            if (sparseSampleSize_ != -1)
                children_ = new Hashtable<Integer, StateNode<S, A>>(sparseSampleSize_);
            else
                children_ = new Hashtable<Integer, StateNode<S, A>>();
            parent_ = parent;
        }

        /**
         * Will take an action from the current simulator's state, create a new
         * state node at the next state and return that state node. If sparse
         * sampling limit has been reached then a random node is returned from the
         * current list of children.
         * 
         * @param simulator used to simulate actions.
         * @return selected child state node.
         */
        public StateNode<S, A> selectChild(Simulator<S, A> simulator) {
            if (sparseSampleSize_ == -1 || nVisits_ < sparseSampleSize_) {
                Simulator<S, A> clone = simulator.clone();
                clone.stateTransition(actions_);
                S state = clone.getState();
                StateNode<S, A> stateNode = children_.get(state.hashCode());
                if (stateNode == null) {
                    stateNode = new StateNode<S, A>(state, clone.getLegalActions(), this);
                    children_.put(state.hashCode(), stateNode);
                }
                return stateNode;
            } else {
                if (frequencyTable_ == null) {
                    frequencyTable_ = new ArrayList<StateNode<S, A>>();
                    for (StateNode<S, A> stateNode : children_.values())
                        for (int i = 0; i < stateNode.nVisits_; i += 1)
                            frequencyTable_.add(stateNode);
                    children_ = null; // Release hash table from memory
                }
                return frequencyTable_.get((int) (Math.random() * frequencyTable_.size()));
            }
        }

        public List<A> getActions() {
            return actions_;
        }

        public StateNode<S, A> getParent() {
            return parent_;
        }

        public boolean equals(List<A> actions) {
            for (int i = 0; i < actions_.size(); i += 1) {
                if (actions_.get(i) == null) {
                    if (actions.get(i) != null)
                        return false;
                } else if (!actions_.get(i).equals(actions.get(i)))
                    return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int code = 7;
            for (A action: actions_) {
                if (action != null)
                    code = 11 * code + action.hashCode();
                else
                    code = 7 * code;
            }
            return code;
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof ActionNode))
                return false;
            ActionNode<S, A> node = (ActionNode<S, A>) object;
            List<A> actions = node.getActions();
            for (int i = 0; i < actions_.size(); i += 1) {
                if (actions_.get(i) == null) {
                    if (actions.get(i) != null)
                        return false;
                } else if (!actions_.get(i).equals(actions.get(i)))
                    return false;
            }
            return true;
        }
    }

    /**
     * Create a traditional UCT agent.
     * 
     * @param nSimulations
     *            the number of complete games to simulate.
     * @param uctConstant
     *            controls balance between exploration and exploitation.
     */
    public UctAgent2(int nSimulations,
                    double uctConstant) {
        this(nSimulations, uctConstant, -1, 1, false);
    }

    /**
     * UCT algorithm with sparse sampling of large stochastic state spaces.
     * 
     * @param nSimulations
     *            the number of complete games played.
     * @param uctConstant
     *            controls balance between exploration and exploitation.
     * @param sparseSampleSize
     *            max number of sample states from any action node or infinite
     *            if equal to -1.
     */
    public UctAgent2(int nSimulations,
                    double uctConstant,
                    int sparseSampleSize) {
        this(nSimulations, uctConstant, sparseSampleSize, 1, false);
    }
    
    public UctAgent2(int nSimulations,
                    double uctConstant,
                    int sparseSampleSize,
                    int nEnsembles) {
        this(nSimulations, uctConstant, sparseSampleSize, nEnsembles, false);
    }

    /**
     * UCT algorithm with sparse sampling and ensemble methods.
     * 
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
    public UctAgent2(int nSimulations,
                    double uctConstant,
                    int sparseSampleSize,
                    int nEnsembles,
                    boolean allActionsAsFirst) {
        if (nSimulations < 1)
            throw new IllegalArgumentException("Number of Simulations < 1");
        if (uctConstant < 0)
            throw new IllegalArgumentException("UCT Constant > 0");
        if (sparseSampleSize < 1 && sparseSampleSize != -1)
            throw new IllegalArgumentException("Sparse Sample Size > 0 or = -1");
        if (nEnsembles < 1)
            throw new IllegalArgumentException("Ensemble trials must be > 0");
        nSimulations_ = nSimulations;
        uctConstant_ = uctConstant;
        sparseSampleSize_ = sparseSampleSize;
        nEnsembles_ = nEnsembles;
        allActionsAsFirst_ = allActionsAsFirst;
        simulationMethod_ = SimulationMethod.RANDOM;
    }

    /**
     * Builds a UCT tree for each ensemble and then
     * selects the best action.
     * If the number of trajectories is less than the number of actions at the
     * root state then not all actions are explored at least one time. In this
     * situation the best action is selected from only those that have been
     * explored.
     */
    public <S, A> A selectAction(int agentId,
                                 S state,
                                 Simulator<S, A> simulator) {
        simulator.setState(state);
        List<List<A>> legalActions = simulator.getLegalActions();
        double[] rootActionRewards = null;
        int[] rootActionVisits = null;
        
        for (int i = 0; i < nEnsembles_; i += 1) {
            StateNode<S, A> root = new StateNode<S, A>(simulator.getState(),legalActions, null);
            if (allActionsAsFirst_) {
                for (int j = 0; j < nSimulations_; j += 1) {
                    HashSet<ActionNode<S, A>> visited = new HashSet<ActionNode<S, A>>();
                    int[] rewards = playSimulation(root, simulator.clone(), visited);
                }
            } else {
                for (int j = 0; j < nSimulations_; j += 1)
                    playSimulation(root, simulator.clone());
            }

            // Save visits and current agent's rewards at all root action nodes
            List<ActionNode<S, A>> children = root.getChildren();
            if (rootActionRewards == null) {
                rootActionRewards = new double[children.size()];
                rootActionVisits = new int[children.size()];
            }
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
        for (int i = 0; i < values.length; i += 1)
            if (rootActionVisits[i] > 0)
                values[i] = rootActionRewards[i] / rootActionVisits[i] + getRandomEpsilon();
        for (int i = 0; i < values.length; i += 1) {
            if (rootActionVisits[i] > 0 &&
                    (selectedAction == null || values[i] > bestValue)) {
                selectedAction = legalActions.get(agentId).get(i);
                bestValue = values[i];
            }
        }
        return selectedAction;
    }

    private double getRandomEpsilon() {
        final double EPSILON = 0.0000000005;
        return EPSILON * (Math.random() - 0.5);
    }

    /**
     * This method walks down the tree making decisions of the best nodes as it
     * goes. When it reaches an unexplored leaf node it plays a random game to
     * initialize that nodes value.
     * TODO - fix cumulative rewards
     * 
     * @param node
     *            current state node being traversed in tree.
     * @param simulator
     *            contains current state of game being played.
     * @return rewards of simulated game are passed up the tree.
     */
    private <S, A> int[] playSimulation(StateNode<S, A> root,
                                        Simulator<S, A> simulator) {
        StateNode<S, A> current = root;
        simulator.setState(current.getState());
        int[] rewards; //= new int[simulator.getNumberOfAgents()];
        //traverse tree to a leaf node
        while (current.getVisits() != 0 &&
                !simulator.isTerminalState()/*current.getChildren().size() != 0*/) {
            ActionNode<S, A> node = current.uctSelect();
            current = node.selectChild(simulator);
            simulator.setState(current.getState());
        }
        //play simulation
        rewards = simulateGame(simulator);
        //back propogation
        while (current.getParent() != null) {
            current.addRewards(rewards);
            current.incVisits();
            current.getParent().addRewards(rewards);
            current.getParent().incVisits();
            current = current.getParent().getParent();
        }
        current.addRewards(rewards);
        current.incVisits();
        return rewards;
    }

    private <S, A> int[] playSimulation(StateNode<S, A> root,
                                        Simulator<S, A> simulator,
                                        HashSet<ActionNode<S, A>> visited) {
        StateNode<S, A> current = root;
        List<ActionNode<S, A>> children = root.getChildren();
        simulator.setState(current.getState());
        int[] rewards; //= new int[simulator.getNumberOfAgents()];
        //traverse tree to a leaf node
        while (current.getVisits() != 0 &&
                !simulator.isTerminalState()/*current.getChildren().size() != 0*/) {
            ActionNode<S, A> node = current.uctSelect();
            current = node.selectChild(simulator);
            simulator.setState(current.getState());
        }
        //play simulation
        rewards = simulateGame(simulator, children);
        //back propogation
        while (current.getParent() != null) {
            current.addRewards(rewards);
            current.incVisits();
            ActionNode<S, A> parent = current.getParent();
            parent.addRewards(rewards);
            parent.incVisits();
            current = parent.getParent();
            //if (allActionsAsFirst_) {
                for (int i = 0; i < children.size(); i += 1)
                    if (parent.equals(children.get(i)))
                        children.get(i).addRewards(rewards);
            //}
        }
        current.addRewards(rewards);
        current.incVisits();
        return rewards;
    }
    
    /**
     * Quickly simulate a game from the current state and return accumulated
     * reward.
     * 
     * @param simulator
     *            a copy of the simulator you want to use to simulate game.
     * @return accumulated reward vector from the game.
     */
    private <S, A>
        int[] simulateGame(Simulator<S, A> simulator) {
        List<List<A>> legalActions = simulator.getLegalActions();
        int[] totalRewards = simulator.getRewards();
        while (!simulator.isTerminalState()) {
            switch (simulationMethod_) {
            case RANDOM:
                List<A> selectedActions = new ArrayList<A>();
                for (List<A> actions: legalActions) {
                    if (actions.size() != 0)
                        selectedActions.add(actions.get((int) (Math.random() * actions.size())));
                    else
                        selectedActions.add(null);
                }
                simulator.stateTransition(selectedActions);
                break;
            }
            for (int i = 0; i < totalRewards.length; i += 1)
                totalRewards[i] += simulator.getRewards()[i];
            legalActions = simulator.getLegalActions();
        }
        return totalRewards;
    }

    private <S, A>
        int[] simulateGame(Simulator<S, A> simulator,
                           List<ActionNode<S, A>> rootChildren) {
        List<List<A>> actionsList = new ArrayList<List<A>>();
        List<List<A>> legalActions = simulator.getLegalActions();
        int[] totalRewards = simulator.getRewards();
        while (!simulator.isTerminalState()) {
            switch (simulationMethod_) {
            case RANDOM:
                List<A> selectedActions = new ArrayList<A>();
                for (List<A> actions: legalActions) {
                    if (actions.size() != 0)
                        selectedActions.add(actions.get((int) (Math.random() * actions.size())));
                    else
                        selectedActions.add(null);
                }
                simulator.stateTransition(selectedActions);
                actionsList.add(selectedActions);
                break;
            }
            for (int i = 0; i < totalRewards.length; i += 1)
                totalRewards[i] += simulator.getRewards()[i];
            legalActions = simulator.getLegalActions();
        }
        //allFirstActions
        for (List<A> actions: actionsList) {
            for (ActionNode<S, A> child: rootChildren) {
                if (child.equals(actions))
                    child.addRewards(totalRewards);
            }
        }
        return totalRewards;
    }

    public String getName() {
        return "uct";
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(getName() + " agent");
        output.append("\n  Number of simulations =     " + nSimulations_);
        output.append("\n  UCT constant =              " + uctConstant_);
        output.append("\n  Sparse sample size =        ");
        if (sparseSampleSize_ > 0)
            output.append(sparseSampleSize_);
        else
            output.append("\u221e");
        output.append("\n  Number of ensembles =       " + nEnsembles_);
        output.append("\n  All actions as first =       " + allActionsAsFirst_);
        return output.toString();
    }
}
