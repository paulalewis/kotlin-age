package com.castlefrog.agl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Keeps track of state transition history as well
 * as the decision times for each selected action.
 * A history may be saved or loaded to a file.
 */
public final class History<S, A> {
    /** list of nodes where each node is a state and list of actions to that state */
    private List<Node> nodes_;
    /** simulator used to determine state transitions */
    private Simulator<S, A> simulator_;

    private class Node {
        private S state_;

        private List<A> actions_;

        private long[] decisionTimes_;
        
        public Node(S state, List<A> actions, long[] decisionTimes) {
            state_ = state;
            actions_ = new ArrayList<A>();
            for (A action: actions)
                actions_.add(action);
            decisionTimes_ = new long[decisionTimes.length];
            for (int i = 0; i < decisionTimes.length; i += 1)
                decisionTimes_[i] = decisionTimes[i];
        }

        public S getState() {
            return state_;
        }

        public List<A> getActions() {
            List<A> actions = new ArrayList<A>();
            for (A action: actions_)
                actions.add(action);
            return actions;
        }

        public long[] getDecisionTimes() {
            long[] decisionTimes = new long[decisionTimes_.length];
            for (int i = 0; i < decisionTimes_.length; i += 1)
                decisionTimes[i] = decisionTimes_[i];
            return decisionTimes;
        }
    }

    /**
     * Construct a History object starting from
     * the given state and using the given simulator.
     * 
     * @param state
     *      initial state of history
     * @param simulator
     *      the simulator used to determine state transitions
     */
    public History(S initialState, Simulator<S, A> simulator) {
        nodes_ = new ArrayList<Node>();
        add(initialState, new ArrayList<A>(), new long[simulator.getNAgents()]);
        simulator_ = simulator.clone();
    }

    public History(String filepath) throws IOException {
        load(filepath);
    }
    
    /**
     * Add the next state and the actions taken by each agent
     * to arrive at that state.
     * 
     * @param state
     *      the current state
     * @param actions
     *      the actions taken by each agent to end up in the current state
     */
    public void add(S state, List<A> actions) {
        add(state, actions, new long[actions.size()]);
    }

    /**
     * Add the next state and the actions taken by each agent
     * to arrive at that state.
     * 
     * @param state
     *      the current state
     * @param actions
     *      the actions taken by each agent to end up in the current state
     * @param decisionTimes
     *      the time taken for each agent to select an action
     */
    public void add(S state,
                    List<A> actions,
                    long[] decisionTimes) {
        //if (state instanceof Cloneable)
        //    nodes_.add(new Node(state.clone(), actions, decisionTimes));
        //else
            nodes_.add(new Node(state, actions, decisionTimes));
    }

    /**
     * removes the last history node
     * but does not remove initial state
     * node.
     */
    public void removeLast() {
        if (nodes_.size() > 1)
            nodes_.remove(nodes_.size() - 1);
    }

    /**
     * Clears history data and loads state transition history from a file.
     * TODO - allow to read strings and interpret them as data.
     * 
     * @param filepath
     *      location of history file
     */
    public void load(String filepath) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(filepath));
        nodes_ = new ArrayList<Node>();
        input.close();
    }

    /**
     * Saves string representation of the History class to a file.
     * TODO - save a history object as XML data
     * 
     * @param filepath
     *            location to save file.
     */
    public void save(String filepath) throws IOException {
        BufferedWriter output = new BufferedWriter(new FileWriter(filepath));
        output.write(toString());
        output.close();
    }

    public S getState(int index) {
        simulator_.setState(nodes_.get(index).getState());
        return simulator_.getState();
    }

    public List<A> getActions(int index) {
        return nodes_.get(index).getActions();
    }

    /**
     * Returns the time taken to select an action.
     * If the return value is 0 then there was
     * no decision to be made by agent (it had
     * no legal actions from the given state).
     *
     * @return
     *      time taken by each agent to select an action
     */
    public long[] getDecisionTimes(int index) {
        return nodes_.get(index).getDecisionTimes();
    }

    public int[] getRewards(int index) {
        simulator_.setState(nodes_.get(index).getState());
        return simulator_.getRewards();
    }
    
    public int size() {
        return nodes_.size();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Node node: nodes_) {
            output.append(node.getActions() + "\n\n");
            output.append(node.getState() + "\n\n");
            long[] times = node.getDecisionTimes();
            for (int i = 0; i < simulator_.getNAgents(); i += 1)
                output.append(times[i] / 1000000000. + "\n");
            output.append("\n");
        }
        return output.toString();
    }
}
