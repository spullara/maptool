package net.rptools.maptool.model;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the set of cells a token occupies based on its size.
 * Each token is assumed to take up at least one cell, additional cells are indicated
 * by cell offsets assuming the occupied cell is at 0, 0 
 */
public class TokenFootprint {

	private Set<Point> cellSet = new HashSet<Point>();
	
	private String name;
	private GUID id;
	private boolean isDefault;
	
	public TokenFootprint() {
		// for serialization
	}
	
	public TokenFootprint(String name, boolean isDefault, Point... points) {
		this.name = name;
		id = new GUID();
		this.isDefault = isDefault;
		
		for (Point p : points) {
			cellSet.add(p);
		}
		
	}
	
	public TokenFootprint(String name, Point... points) {
		this(name, false, points);
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	public boolean isDefault() {
		return isDefault;
	}
	
	public GUID getId() {
		return id;
	}
	
	public String getName() { 
		return name;
	}

	public Rectangle getBounds(Grid grid) {
		return getBounds(grid, null);
	}
	
	/**
	 * Return a rectangle that exactly bounds the footprint, values are in ZonePoint space
	 */
	public Rectangle getBounds(Grid grid, CellPoint cell) {

		cell = cell != null ? cell : new CellPoint(0, 0);
		Rectangle cellBounds = grid.getBounds(cell) ;
		Rectangle bounds = new Rectangle(cellBounds);
		
		for (Point p : cellSet) {
			
			CellPoint cp = new CellPoint(cell.x + p.x, cell.y + p.y);
			bounds.add(grid.getBounds(cp));
		}
		
		return bounds;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TokenFootprint)) {
			return false;
		}
		
		return ((TokenFootprint)obj).id.equals(id);
	}
}
