package com.castlefrog.agl.domains.laserlight;

public class Weapon {
    /** damage dealt. */
    private int damage_;
    /** time taken before next attack */
    private int coolDown_;
    /** total energy taken over the period of cooldown */
    private int energy_;
    private int chargeRate_;
    /** number of guns. */
    private int numberOfBarrels_;
    private EnergyColor color_;
}
