package com.castlefrog.agl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Keeps track of state transition history.
 */
public final class History<S extends State, A extends Action> implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Node> nodes;

    private final class Node implements Serializable {
        private static final long serialVersionUID = 1L;

        private S state;
        private List<A> actions;

        public Node(S state, List<A> actions) {
            this.state = state;
            this.actions = actions;
        }

        public S getState() {
            return state;
        }

        public List<A> getActions() {
            return actions;
        }
    }

    /**
     * Construct a History object starting from
     * the given state.
     * @param state
     *      initial state of history
     */
    public History(S initialState) {
        nodes = new ArrayList<Node>();
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
        nodes.add(new Node(state, actions));
    }

    public void add(S state, List<A> actions, int index) {
        while (index < nodes.size() - 1) {
            removeLast();
        }
        nodes.add(new Node(state, actions));
    }

    public void removeLast() {
        if (nodes.size() > 0) {
            nodes.remove(nodes.size() - 1);
        }
    }

    public S getState(int index) {
        return nodes.get(index).getState();
    }

    public List<A> getActions(int index) {
        return nodes.get(index).getActions();
    }

    public int getSize() {
        return nodes.size();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Node node: nodes) {
            output.append(node.actions + "\n\n");
            output.append(node.state + "\n\n");
        }
        return output.toString();
    }
}
