package net.rptools.maptool.model;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Zone.Event;

/**
 * Base class for grids.
 * @author trevor
 */
public abstract class Grid {

    public static final int MIN_GRID_SIZE = 5;
    public static final int MAX_GRID_SIZE = 250;

	private int offsetX = 0;
	private int offsetY = 0;
	private int size = AppConstants.DEFAULT_GRID_SIZE;
	
	private Zone zone;

	public Grid(Zone zone) {
		this.zone = zone;
	}
	
	public abstract CellPoint convert(ZonePoint zp);
	public abstract ZonePoint convert(CellPoint cp);
	public abstract GridCapabilities getCapabilities();
	public abstract int getTokenSpace();
	
	public Zone getZone() {
		return zone;
	}
	
	public void setOffset(int offsetX, int offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		
        fireGridChanged();
	}
	
	public int getOffsetX() {
		return offsetX;
	}
	
	public int getOffsetY() {
		return offsetY;
	}
	
	public void setSize(int size) {
    	if (size < MIN_GRID_SIZE) {
    		size = MIN_GRID_SIZE;
    	}
    	
    	if (size > MAX_GRID_SIZE) {
    		size = MAX_GRID_SIZE;
    	}

    	this.size = size;

		fireGridChanged();
	}

	public int getSize() {
		return size;
	}
	
	protected void fireGridChanged() {
        zone.fireModelChangeEvent(new ModelChangeEvent(this, Event.GRID_CHANGED));
	}
	
	public abstract void draw(ZoneRenderer renderer, Graphics2D g, Rectangle bounds);
}
