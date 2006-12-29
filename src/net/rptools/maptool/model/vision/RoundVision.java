package net.rptools.maptool.model.vision;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import net.rptools.maptool.model.Vision;
import net.rptools.maptool.model.Zone;

public class RoundVision extends Vision {

	public RoundVision() {
	}
	
	public RoundVision(int distance) {
		setDistance(distance);
	}
	
	@Override
	protected Area createArea(Zone zone) {

		int size = getDistance() * getZonePointsPerCell(zone)*2;
		int half = size/2;
		Area area = new Area(new Ellipse2D.Float(-half, -half, size, size));
		
		return area;
	}
	
	@Override
	public String toString() {
		return "Round";
	}
}
