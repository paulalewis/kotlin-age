package com.castlefrog.agl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Keeps track of state transition history as well
 * as the decision times for each selected action.
 * A history may be saved or loaded to a file.
 */
public final class History<S, A> implements Serializable {
    private List<Node> nodes_;

    private final class Node implements Serializable {
        private S state_;
        private List<A> actions_;

        public Node(S state, List<A> actions) {
            state_ = state;
            actions_ = new ArrayList<A>();
            for (A action: actions)
                actions_.add(action);
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
    }

    /**
     * Construct a History object starting from
     * the given state.
     * 
     * @param state
     *      initial state of history
     */
    public History(S initialState) {
        nodes_ = new ArrayList<Node>();
        add(initialState, new ArrayList<A>());
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
        nodes_.add(new Node(state, actions));
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

    public S getState(int index) {
        return nodes_.get(index).getState();
    }

    public List<A> getActions(int index) {
        return nodes_.get(index).getActions();
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
        }
        return output.toString();
    }
}
