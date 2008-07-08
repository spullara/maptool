package net.rptools.maptool.client.ui.zone;

import java.awt.geom.Area;

import net.rptools.maptool.model.drawing.DrawablePaint;

public class DrawableLight {

	private DrawablePaint paint;
	private Area area;
	
	public DrawableLight(DrawablePaint paint, Area area) {
		super();
		this.paint = paint;
		this.area = area;
	}
	
	public DrawablePaint getPaint() {
		return paint;
	}
	public Area getArea() {
		return area;
	}
	
	
}
