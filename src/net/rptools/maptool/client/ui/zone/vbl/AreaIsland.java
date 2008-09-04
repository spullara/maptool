package net.rptools.maptool.client.ui.zone.vbl;

import java.awt.Point;
import java.awt.geom.Area;
import java.util.HashSet;
import java.util.Set;

public class AreaIsland implements AreaContainer {

	private Area bounds;
	private Set<AreaOcean> oceanSet = new HashSet<AreaOcean>();
	
	private AreaMeta meta;
	
	public AreaIsland(Area bounds) {
		this.bounds = bounds;
	}
	
	public Set<AreaOcean> getOceans() {
		return new HashSet<AreaOcean>(oceanSet);
	}
	
	public void addOcean(AreaOcean ocean) {
		oceanSet.add(ocean);
	}
	
	////
	// AREA CONTAINER
	public Area getBounds() {
		return bounds;
	}
}
