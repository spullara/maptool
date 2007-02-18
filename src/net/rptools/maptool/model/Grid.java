package net.rptools.maptool.model;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.walker.ZoneWalker;
import net.rptools.maptool.model.Zone.Event;

/**
 * Base class for grids.
 * @author trevor
 */
public abstract class Grid {

    public static final int MIN_GRID_SIZE = 5;
    public static final int MAX_GRID_SIZE = 350;

	private int offsetX = 0;
	private int offsetY = 0;
	private int size;
	
	private Zone zone;

	private Area cellShape;
	
	public Grid() {
		setSize(AppPreferences.getDefaultGridSize());
	}
	
	/**
	 * @return Coordinates in Cell-space of the ZonePoint
	 */
	public abstract CellPoint convert(ZonePoint zp);
	
	/**
	 * @return A ZonePoint whose position within the cell depends on the grid type:<br>
	 * <i>SquareGrid</i> - top right of cell (x_min, y_min)<br>
	 * <i>HexGrid</i> - center of cell<br>
	 * For HexGrids Use getCellOffset() to move ZonePoint from center to top right
	 */
	public abstract ZonePoint convert(CellPoint cp);
	
	public abstract GridCapabilities getCapabilities();
	public abstract int getTokenSpace();
	public abstract double getCellWidth();
	public abstract double getCellHeight();
	
	/**
	 * @return The offset required to translate from the center of a cell
	 * to the top right (x_min, y_min) of the cell's bounding rectangle.
	 * Used for non-square grids only.<br>
	 * <br>
	 * Why?  Because mySquareGrid.convert(CellPoint cp) returns a ZonePoint
	 * in the top right corner(x_min, y_min) of the square-cell, whereas
	 * myHexGrid.convert(CellPoint cp) returns a ZonePoint in the center of
	 * the hex-cell.  Thus adding the CellOffset allows us to position the 
	 * ZonePoint returned by myHexGrid.convert(CellPoint cp) in an equivalent
	 * position to that returned by myHexGrid.convert(CellPoint cp)....I think ;)
	 */
	public abstract Dimension getCellOffset();
	
	public Zone getZone() {
		return zone;
	}
	
	public void setZone(Zone zone) {
		this.zone = zone;
	}
	
	public Area getCellShape() {
		return cellShape;
	}
	
	public abstract BufferedImage getCellHighlight();
	

	protected void setCellShape(Area cellShape) {
		this.cellShape = cellShape;
	}

	protected abstract Area createCellShape(int size);
	
	/**
	 * @param Both The grid's x and y offset components
	 */
	public void setOffset(int offsetX, int offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		
        fireGridChanged();
	}
	
	/**
	 * @return The x component of the grid's offset.
	 */
	public int getOffsetX() {
		return offsetX;
	}
	
	/**
	 * @return The y component of the grid's offset
	 */
	public int getOffsetY() {
		return offsetY;
	}

	public abstract ZoneWalker createZoneWalker();
	
	
	/**
	 * Sets the grid size and creates the grid cell shape
	 * @param size The size of the grid<br>
	 *  <i>SquareGrid</i> - edge length<br>
	 *  <i>HexGrid</i> - edge to edge diameter
	 */
	public void setSize(int size) {
		
    	this.size = constrainSize(size);
    	cellShape = createCellShape(size);
		fireGridChanged();
	}
	
	/**
	 * Constrains size to MIN_GRID_SIZE <= size <= MIN_GRID_SIZE
	 * @return The size after it has been constrained
	 */
	protected final int constrainSize (int size) {
				
    	if (size < MIN_GRID_SIZE) {
    		size = MIN_GRID_SIZE;
    	}
    	else if (size > MAX_GRID_SIZE) {
    		size = MAX_GRID_SIZE;
    	}
    	return size;
	}

	/**
	 * @return The size of the grid
	 * <i>SquareGrid</i> - edge length<br>
	 * <i>HexGrid</i> - edge to edge diameter
	 */
	public int getSize() {
		return size;
	}
	
	public abstract int[] getFacingAngles();
	
	private void fireGridChanged() {
		if (zone != null) {
			zone.fireModelChangeEvent(new ModelChangeEvent(this, Event.GRID_CHANGED));
		}
	}
	
	/**
	 * Draws the grid scaled to the renderer's scale and within the renderer's boundaries
	 */
	public abstract void draw(ZoneRenderer renderer, Graphics2D g, Rectangle bounds);
}
