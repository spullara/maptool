package net.rptools.maptool.model;

import java.awt.geom.Area;

import net.rptools.maptool.model.drawing.DrawablePaint;

public class Light {

	private DrawablePaint paint;
	private double facingOffset;
	private double radius;
	private double arcAngle;
	
	public Light() {
		// For serialization
	}
	
	public Light(double facingOffset, double radius, double arcAngle, DrawablePaint paint) {
		this.facingOffset = facingOffset;
		this.radius = radius;
		this.arcAngle = arcAngle;
		this.paint = paint;
	}
	
	public DrawablePaint getPaint() {
		return paint;
	}
	public void setPaint(DrawablePaint paint) {
		this.paint = paint;
	}
	public double getFacingOffset() {
		return facingOffset;
	}
	public void setFacingOffset(double facingOffset) {
		this.facingOffset = facingOffset;
	}
	public double getRadius() {
		return radius;
	}
	public void setRadius(double radius) {
		this.radius = radius;
	}
	public double getArcAngle() {
		return arcAngle;
	}
	public void setArcAngle(double arcAngle) {
		this.arcAngle = arcAngle;
	}
	
	public Area getArea(Token token, Grid grid) {
		return null;
	}
}
