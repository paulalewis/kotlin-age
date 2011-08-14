package com.castlefrog.agl.domains.laserlight;

import java.util.List;

public class EnemyType extends MapObject {
    private MovementType movementType_;
    private int velocity_;
    private int maxHitpoints_;
    private List<Weapon> weapons_;
    private int size_;

    /**
     * each enemy has a behavior method that
     * determines how that enemy makes actions.
     */
    //public EnemyAction getEnemyAction(LaserLightState state);
}
