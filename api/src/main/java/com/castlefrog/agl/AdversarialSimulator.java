package com.castlefrog.agl;

public abstract class AdversarialSimulator<S extends State<S>, A extends Action>
        extends AbstractSimulator<S, A> {
    protected static final int N_AGENTS = 2;
    protected static final int[] REWARDS_NEUTRAL = new int[] { 0, 0 };
    protected static final int[] REWARDS_BLACK_WINS = new int[] { 1, -1 };
    protected static final int[] REWARDS_WHITE_WINS = new int[] { -1, 1 };

    protected AdversarialSimulator() {}

    protected AdversarialSimulator(AdversarialSimulator<S, A> simulator) {
        super(simulator);
    }

    public int getNAgents() {
        return N_AGENTS;
    }
}
