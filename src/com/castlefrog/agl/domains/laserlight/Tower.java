package com.castlefrog.agl.domains.laserlight;

public abstract class Tower {

    public class Cost {
        public int energy_;
        public int time_;
    }

    private Cost cost_;
    /** Damage a tower can take before being destroyed. */
    private int hitpoints_;
    /** Amount of energy a tower can store. */
    private int energyStorage_;

    public Cost getCost() {
        return cost_;
    }

    public int getHitpoints() {
        return hitpoints_;
    }

    public int getEnergyStorage() {
        return energyStorage_;
    }
}
