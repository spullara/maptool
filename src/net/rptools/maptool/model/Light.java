package net.rptools.maptool.model;

import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import net.rptools.maptool.model.drawing.DrawableColorPaint;
import net.rptools.maptool.model.drawing.DrawablePaint;
import net.rptools.maptool.util.StringUtil;

public class Light {

	private DrawablePaint paint;
	private double facingOffset;
	private double radius;
	private double arcAngle;
	private ShapeType shape;
	
	public Light() {
		// For serialization
	}
	
	public Light(ShapeType shape, double facingOffset, double radius, double arcAngle, DrawablePaint paint) {
		this.facingOffset = facingOffset;
		this.shape = shape;
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
	public ShapeType getShape() {
		return shape;
	}
	public void setShape(ShapeType shape) {
		this.shape = shape;
	}
	public Area getArea(Token token, Zone zone) {
		double size = radius / zone.getUnitsPerCell() * zone.getGrid().getSize();
		
		if (shape == null) {
			shape = ShapeType.CIRCLE;
		}
		switch (shape) {

		case SQUARE:
			return new Area(new Rectangle2D.Double(-size, -size, size*2, size*2));
		
		default:
		case CIRCLE:
			return new Area(new Ellipse2D.Double(-size, -size, size*2, size*2));
		}
		
	}

}
