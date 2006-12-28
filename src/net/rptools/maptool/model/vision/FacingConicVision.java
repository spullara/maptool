package net.rptools.maptool.model.vision;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Vision;

public class FacingConicVision extends Vision {

	private Integer lastFacing;
	private GUID tokenGUID;
	
	public FacingConicVision(GUID tokenGUID, int distance) {
		setDistance(distance);
		this.tokenGUID = tokenGUID;
	}
	
	@Override
	public Area getArea() {
		Token token = getToken();
		if (token == null) {
			return null;
		}
		if (lastFacing != null && !lastFacing.equals(token.getFacing())) {
			flush();
		}
		return super.getArea();
	}
	
	@Override
	protected Area createArea() {

		Token token = getToken();
		if (token == null || token.getFacing() == null) {
			return null;
		}
		
		// Start round
		int size = getDistance();
		int half = size/2;
		Area area = new Area(new Ellipse2D.Float(-half, -half, size, size));

		// Cut off the part that isn't in the cone
		area.subtract(new Area(new Rectangle(-1000, 1, 2000, 2000)));
		area.subtract(new Area(new Rectangle(-1000, -1000, 999, 2000)));
		
		// Rotate
		int angle = (-token.getFacing() + 45);
		area.transform(AffineTransform.getRotateInstance(Math.toRadians(angle)));
		
		lastFacing = token.getFacing();
		
		return area;
	}
	
	private Token getToken() {
		return MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenGUID);
	}
}
