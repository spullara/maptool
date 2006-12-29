package net.rptools.maptool.model.vision;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import net.rptools.maptool.model.Vision;

public class RoundVision extends Vision {

	public RoundVision() {
	}
	
	public RoundVision(int distance) {
		setDistance(distance);
	}
	
	@Override
	protected Area createArea() {

		int size = getDistance();
		int half = size/2;
		Area area = new Area(new Ellipse2D.Float(-half, -half, size, size));
		
		return area;
	}
}
