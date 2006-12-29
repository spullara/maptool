package net.rptools.maptool.model;

import java.awt.geom.Area;

public abstract class Vision {

	private int distance;
	private int angle; // degrees
	private boolean enabled = true;
	private int lastGridSize;

	private transient Area area;
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Angle in degrees
	 */
	public int getAngle() {
		return angle;
	}

	/**
	 * Angle in degrees
	 */
	public void setAngle(int angle) {
		this.angle = angle;
		flush();
	}
	
	/**
	 * Distance in zone points
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * Distance in zone points
	 * @param distance
	 */
	public void setDistance(int distance) {
		this.distance = distance;
		flush();
	}

	/**
	 * Get the area shape that this vision represents, in zone points, centered on the origin x,y
	 */
	public Area getArea(Zone zone) {
		if (area == null || lastGridSize != zone.getGrid().getSize()) {
			area = createArea(zone);
		}
		lastGridSize = zone.getGrid().getSize();
		return area;
	}

	/**
	 * Specific vision types must be able to create the shape they represent
	 */
	protected abstract Area createArea(Zone zone);

	/**
	 * Flush the cached vision information
	 */
	protected void flush() {
		area = null;
	}
	
	protected int getZonePointsPerCell(Zone zone) {
		return (int)(zone.getGrid().getSize() / zone.getFeetPerCell());
	}
}
