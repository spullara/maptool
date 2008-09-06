package net.rptools.maptool.client.ui.zone.vbl;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

public class AreaOcean implements AreaContainer {

	private AreaMeta meta;
	private Set<AreaIsland> islandSet = new HashSet<AreaIsland>();
	
	public AreaOcean(AreaMeta meta) {
		this.meta = meta;
	}
	
	public Set<VisibleAreaSegment> getVisibleAreaSegments(Point2D origin) {
		
		Set<VisibleAreaSegment> segSet = new HashSet<VisibleAreaSegment>();

		// If an island contains the point, then we're 
		// not in this ocean, short circuit out
		for (AreaIsland island : islandSet) {
			if (island.getBounds().contains(origin)) {
				return segSet;
			}
		}
		
		// Inside boundaries
		for (AreaIsland island : islandSet) {
			segSet.addAll(island.getVisibleAreaSegments(origin));
		}
		
		// Outside boundary
		if (meta != null) {
			segSet.addAll(meta.getVisibleAreas(origin));
		}

		return segSet;
	}
	
	public AreaOcean getDeepestOceanAt(Point2D point) {

		if (meta != null && !meta.area.contains(point)) {
			return null;
		}

		// If the point is in an island, then let the island figure it out
		for (AreaIsland island : islandSet) {
			AreaOcean ocean = island.getDeepestOceanAt(point);
			if (ocean != null) {
				return ocean;
			}
		}
		
		return this;
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
		return meta != null ? meta.area : null;
	}
}
