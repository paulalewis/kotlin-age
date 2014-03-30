package com.castlefrog.agl;

public abstract class Adversarial2AgentSimulator<S extends State<S>, A extends Action>
        extends AbstractSimulator<S, A> {
    protected static final int N_AGENTS = 2;
    protected static final int[] REWARDS_NEUTRAL = new int[] { 0, 0 };
    protected static final int[] REWARDS_AGENT1_WINS = new int[] { 1, -1 };
    protected static final int[] REWARDS_AGENT2_WINS = new int[] { -1, 1 };

    protected Adversarial2AgentSimulator() {}

    protected Adversarial2AgentSimulator(Adversarial2AgentSimulator<S, A> simulator) {
        super(simulator);
    }

    public int getNAgents() {
        return N_AGENTS;
    }
}