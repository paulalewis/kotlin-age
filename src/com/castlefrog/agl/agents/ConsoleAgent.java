package com.castlefrog.agl.agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.castlefrog.agl.Action;
import com.castlefrog.agl.Agent;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.State;

public final class ConsoleAgent implements Agent {
    public ConsoleAgent() {}

    public <S extends State, A extends Action> A selectAction(int agentId, S state, Simulator<S, A> simulator) {
        simulator.setState(state);
        A action = null;
        List<A> legalActions = simulator.getLegalActions(agentId);
        if (legalActions.get(0) != null) {
            System.out.println(state);
            System.out.print("Input Move (" + legalActions.size() + ") (");
            for (int i = 0; i < legalActions.size(); i++) {
                if (i == legalActions.size() - 1)
                    System.out.print(legalActions.get(i).toString() + ")\n");
                else
                    System.out.print(legalActions.get(i).toString() + ",");
            }
            do {
                String input = getInput();
                action = matchToAction(input, legalActions);
            } while (action == null);
        }
        return action;
    }

    private <A extends Action> A matchToAction(String input, List<A> actions) {
        for (int i = 0; i < actions.size(); i++) {
            if (actions.get(i).toString().toLowerCase().equals(
                    input.toLowerCase()))
                return actions.get(i);
        }
        return null;
    }

    private String getInput() {
        String input = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            input = in.readLine();
        } catch (IOException e) {
        }
        return input;
    }

    public String getName() {
        return "console";
    }

    @Override
    public String toString() {
        return getName() + " agent";
    }
}
