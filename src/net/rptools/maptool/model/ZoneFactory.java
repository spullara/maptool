package net.rptools.maptool.model;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.AppState;

public class ZoneFactory {

	public static Zone createZone(String name, int feetPerCell, MD5Key backgroundAsset) {
		
		Zone zone = new Zone(backgroundAsset);
		zone.setVisible(AppPreferences.getNewMapsVisible());
		zone.setHasFog(AppPreferences.getNewMapsHaveFOW());
		zone.setName(name);
		zone.setFeetPerCell(feetPerCell);

	    return zone;
	}
	public static Zone createZone(MD5Key backgroundAsset) {
		
		return createZone("", Zone.DEFAULT_FEET_PER_CELL, backgroundAsset);
	}
}
