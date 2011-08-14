package com.castlefrog.agl.domains.laserlight;

import java.util.List;

/**
 * Hexagon grid.
 * New enemies spawn when a wave is destroyed.
 * Spawn locations that are lit up indicate next
 * wave of units will be spawing and of the lit up color.
 */
public class Map {
    private List<List<Terrain>> grid_;
    private List<Tower> towers_;
    private List<Enemy> enemies_;

    public Map(List<List<Terrain>> grid) {
        grid_ = grid;
    }

    public int getWidth() {
        return grid_.size();
    }

    public int getHeight() {
        return grid_.get(0).size();
    }

    public String toString() {
        return "";
    }
}
