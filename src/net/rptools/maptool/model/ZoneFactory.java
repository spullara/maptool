package net.rptools.maptool.model;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.AppState;

public class ZoneFactory {

	public static Zone createZone(int type, String name, int feetPerCell, MD5Key backgroundAsset) {
		
		Zone zone = new Zone(type, backgroundAsset);
		zone.setVisible(AppState.isNewZonesVisible());
		zone.setHasFog(AppState.getNewMapsHaveFoW());
		zone.setName(name);
		zone.setFeetPerCell(feetPerCell);

	    return zone;
	}
	public static Zone createZone(int type, MD5Key backgroundAsset) {
		
		return createZone(type, "", Zone.DEFAULT_FEET_PER_CELL, backgroundAsset);
	}
}
