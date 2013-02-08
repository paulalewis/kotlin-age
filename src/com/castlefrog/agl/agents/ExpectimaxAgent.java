package com.castlefrog.agl.agents;

import java.util.HashSet;
import java.util.List;

import com.castlefrog.agl.Agent;
import com.castlefrog.agl.Simulator;

/**
 * The Brute Force Agent explore the entire tree up to a certain depth.
 * TODO - this agent in not fully implemented yet
 */
public class ExpectimaxAgent implements Agent {
    /** Max depth of search tree. */
    private int maxDepth_;

    /** Sparse sample size of states. */
    private int sampleSize_;

    /** Number of Monte-Carlo simulations run at leaves of tree. */
    private int nSimulations_;

    public ExpectimaxAgent(int maxDepth, int sampleSize, int nSimulations) {
        if (maxDepth < 1 || sampleSize < 1 || nSimulations < 1)
            throw new IllegalArgumentException("Max depth > 0 : Sample Size > 0 : Number of Simulations > 0");
        maxDepth_ = maxDepth;
        sampleSize_ = sampleSize;
        nSimulations_ = nSimulations;
    }

    public <S, A> A selectAction(int agentId, S state, Simulator<S, A> simulator) {
        simulator.setState(state);
        List<A> actions = simulator.getLegalActions().get(agentId);
        double[][] qValues = new double[actions.size()][simulator.getNAgents()];
        for (int i = 0; i < actions.size(); i++) {
            for (int j = 0; j < sampleSize_; j++) {
                Simulator<S, A> clone = simulator.clone();
                //clone.stateTransition(actions.get(i));
                double[] values = sparseSampleTree(clone, maxDepth_ - 1);
                int[] rewards = clone.getRewards();
                for (int k = 0; k < qValues[i].length; k++)
                    qValues[i][k] = rewards[k] + values[k];
            }
            for (int j = 0; j < qValues[i].length; j++)
                qValues[i][j] /= sampleSize_;
        }
        // Find max qValue
        int best = 0;
        for (int i = 1; i < actions.size(); i++)
            if (qValues[i][agentId] > qValues[best][agentId])
                best = i;
        return actions.get(best);
    }

    private <S, A> double[] sparseSampleTree(Simulator<S, A> simulator, int horizon) {
        List<List<A>> actions = simulator.getLegalActions();
        if (actions.size() == 0) { // if terminal state return reward
            int[] rewards = simulator.getRewards();
            double[] values = new double[simulator.getNAgents()];
            for (int i = 0; i < values.length; i++)
                values[i] = rewards[i];
            return values;
        }
        double[][] qValues = new double[actions.size()][simulator.getNAgents()];
        for (int i = 0; i < actions.size(); i++) { // i = action index
            if (horizon > 0) {
                for (int j = 0; j < sampleSize_; j++) { // j = number of samples
                                                        // of taking action i
                    Simulator<S, A> clone = simulator.clone();
                    clone.stateTransition(actions.get(i));
                    double[] values = sparseSampleTree(clone, horizon - 1);
                    int[] rewards = clone.getRewards();
                    for (int k = 0; k < qValues[i].length; k++)
                        qValues[i][k] = rewards[k] + values[k];
                }
                for (int j = 0; j < qValues[i].length; j++)
                    qValues[i][j] /= sampleSize_;
            } else {
                double[] totalRewards = new double[simulator.getNAgents()];
                for (int k = 0; k < nSimulations_; k++) {
                    double[] rewards = simulateGame(simulator.clone());
                    for (int l = 0; l < rewards.length; l++)
                        totalRewards[l] += rewards[l];
                }
                for (int k = 0; k < totalRewards.length; k++)
                    totalRewards[k] /= nSimulations_;
                qValues[i] = totalRewards;
            }
        }
        // Find max qValue
        int best = 0;
        /*for (int i = 1; i < actions.size(); i++)
            if (qValues[i][agentId] > qValues[best][agentId])
                best = i;*/
        return qValues[best];
    }

    private <S, A> double[] simulateGame(Simulator<S, A> simulator) {
        List<List<A>> actions = simulator.getLegalActions();
        int[] rewards = simulator.getRewards();
        double[] totalRewards = new double[rewards.length];
        for (int i = 0; i < rewards.length; i += 1)
            totalRewards[i] += rewards[i];
        while (actions.size() > 0) {
            simulator.stateTransition(actions.get((int) (Math.random() * actions.size())));
            for (int i = 0; i < totalRewards.length; i += 1)
                totalRewards[i] += simulator.getRewards()[i];
            actions = simulator.getLegalActions();
        }
        return totalRewards;
    }

    public String getName() {
        return "expectimax";
    }

    @Override
    public String toString() {
        return getName() + " agent";
    }
}
