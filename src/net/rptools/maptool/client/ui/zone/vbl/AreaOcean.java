package net.rptools.maptool.client.ui.zone.vbl;

import java.awt.geom.Area;
import java.util.HashSet;
import java.util.Set;

public class AreaOcean implements AreaContainer {

	private Area bounds;
	private Set<AreaIsland> islandSet = new HashSet<AreaIsland>();
	
	public AreaOcean(Area bounds) {
		this.bounds = bounds;
	}
	
	public Set<AreaIsland> getIslands() {
		return new HashSet<AreaIsland>(islandSet);
	}
	
	public void addIsland(AreaIsland island) {
		islandSet.add(island);
	}
	
	////
	// AREA CONTAINER
	public Area getBounds() {
		return bounds;
	}
}
