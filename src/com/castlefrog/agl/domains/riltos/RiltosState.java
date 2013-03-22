package com.castlefrog.agl.domains.riltos;

import java.util.List;

public class RiltosState implements Cloneable {
	private Location[][] map_;
    private List<Region> regions_;
	
	public RiltosState(Location[][] map,
                       List<Region> regions) {
        map_ = new Location[map.length][map[0].length];
        for (int i = 0; i < map.length; i += 1)
            for (int j = 0; j < map[0].length; j += 1)
                map_[i][j] = map[i][j];
        for (Region region: regions)
            regions_.add(region);
	}

    @Override
    public RiltosState clone() {
        return this;
    }
	
	public Location[][] getMap() {
		Location[][] map = new Location[map_.length][map_.length];
		for (int i = 0; i < map_.length; i += 1)
			map[i] = map_[i];
		return map;
	}
	
	public Location getLocation(int x, int y) {
		return map_[x][y];
	}

    public Region getRegion(int index) {
        return regions_.get(index);
    }

    public int getNumberOfRegions() {
        return regions_.size();
    }
}
