package com.castlefrog.agl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Keeps track of state transition history.
 */
public final class History<S extends State<S>, A extends Action> implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Node> nodes_;

    private final class Node implements Serializable {
        private static final long serialVersionUID = 1L;

        private S state_;
        private List<A> actions_;

        public Node(S state, List<A> actions) {
            state_ = state;
            actions_ = actions;
        }

        public S getState() {
            return state_;
        }

        public List<A> getActions() {
            return actions_;
        }
    }

    /**
     * Construct a History object starting from
     * the given state.
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
     * @param state
     *      the current state
     * @param actions
     *      the actions taken by each agent to end up in the current state
     */
    public void add(S state, List<A> actions) {
        nodes_.add(new Node(state, actions));
    }

    public void add(S state, List<A> actions, int index) {
        while (index < nodes_.size() - 1) {
            removeLast();
        }
        nodes_.add(new Node(state, actions));
    }

    public void removeLast() {
        if (nodes_.size() > 0) {
            nodes_.remove(nodes_.size() - 1);
        }
    }

    public S getState(int index) {
        return nodes_.get(index).getState();
    }

    public List<A> getActions(int index) {
        return nodes_.get(index).getActions();
    }

    public int getSize() {
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
