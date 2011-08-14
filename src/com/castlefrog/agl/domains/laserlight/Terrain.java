package com.castlefrog.agl.domains.laserlight;

/**
 * map terrain.
 * void
 *      empty pit where flying units can move across.
 * energy
 *      location where enemies spawn and power
 *      cores may be built.
 * flat
 *      a normal tile that can be moved or built on.
 * obstacle
 *      a tile that cannon be built upon or moved across
 *      except by flying units. it obstrcuts ranged attacks
 *      and line of sight
 */
public enum Terrain {
    VOID,
    ENERGY_RED,
    ENERGY_BLUE,
    ENERGY_GREEN,
    ENERGY_WHITE,
    FLAT,
    IMPASSIBLE,
    SLOW_TILE,
    SPEED_TILE,
    WAY_POINT
}
