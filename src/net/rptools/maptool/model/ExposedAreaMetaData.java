/**
 * 
 */
package net.rptools.maptool.model;

import java.awt.geom.Area;

public class ExposedAreaMetaData {
	private Area exposedAreaHistory;

	public ExposedAreaMetaData() {
		exposedAreaHistory = new Area();
	}

	public ExposedAreaMetaData(Area area) {
		exposedAreaHistory = new Area(area);
	}

	public Area getExposedAreaHistory() {
		if (exposedAreaHistory == null) {
			exposedAreaHistory = new Area();
		}
		return exposedAreaHistory;
	}

	public void addToExposedAreaHistory(Area newArea) {
		if (newArea != null && !newArea.isEmpty()) {
			exposedAreaHistory.add(newArea);
		}
	}

	public void removeExposedAreaHistory(Area newArea) {
		if (newArea != null && !newArea.isEmpty()) {
			exposedAreaHistory.subtract(newArea);
		}
	}

	public void clearExposedAreaHistory() {
		exposedAreaHistory = new Area();
	}
}