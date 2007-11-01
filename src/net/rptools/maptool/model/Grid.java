package net.rptools.maptool.model;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.rptools.lib.FileUtil;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.walker.ZoneWalker;
import net.rptools.maptool.model.TokenFootprint.OffsetTranslator;
import net.rptools.maptool.model.Zone.Event;

import com.thoughtworks.xstream.XStream;

/**
 * Base class for grids.
 * @author trevor
 */
public abstract class Grid implements Cloneable{

    public static final int MIN_GRID_SIZE = 5;
    public static final int MAX_GRID_SIZE = 350;

    private static final Dimension NO_DIM = new Dimension();
    private static final Point NO_POINT = new Point();
    
	private int offsetX = 0;
	private int offsetY = 0;
	private int size;
	
	private Zone zone;

	private Area cellShape;
	
	public Grid() {
		setSize(AppPreferences.getDefaultGridSize());
	}
	
	public Grid(Grid grid) {
		setSize(grid.getSize());
		setOffset(grid.offsetX, grid.offsetY);
	}
	
	public void drawCoordinatesOverlay(Graphics2D g, ZoneRenderer renderer) {
		// Do nothing my default
	}
	
	protected List<TokenFootprint> loadFootprints(String path, OffsetTranslator... translators) throws IOException {

		List<TokenFootprint> footprintList = (List<TokenFootprint>) new XStream().fromXML(new String(FileUtil.loadResource(path)));
		for (TokenFootprint footprint : footprintList) {
			for (OffsetTranslator ot : translators) {
				footprint.addOffsetTranslator(ot);
			}
		}
		
		return footprintList;
	}
	
	public TokenFootprint getDefaultFootprint() {
		for (TokenFootprint footprint : getFootprints()) {
			if (footprint.isDefault()) {
				return footprint;
			}
		}
		
		// None specified, use the first
		return getFootprints().get(0);
	}
	
	public TokenFootprint getFootprint(GUID guid) {
		if (guid == null) {
			return getDefaultFootprint();
		}
		
		for (TokenFootprint footprint : getFootprints()) {
			if (footprint.getId().equals(guid)) {
				return footprint;
			}
		}
		return getDefaultFootprint();
	}
	
	public abstract List<TokenFootprint> getFootprints();
	
	public Object clone () 
    	throws CloneNotSupportedException
    {
		return super.clone();

		/*Grid newGrid = (Grid)super.clone();
		
		return newGrid;*/
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
	
	public int[] getFacingAngles() {
		return null;
	}

	public abstract GridCapabilities getCapabilities();
	
	public int getTokenSpace() {
		return getSize();
	}
	public double getCellWidth() {
		return 0;
	}
	public double getCellHeight() {
		return 0;
	}
	
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
	public Dimension getCellOffset() {
		return NO_DIM;
	}
	
	public Zone getZone() {
		return zone;
	}
	
	public void setZone(Zone zone) {
		this.zone = zone;
	}
	
	public Area getCellShape() {
		return cellShape;
	}
	
	public BufferedImage getCellHighlight(){
		return null;
	}
	
	protected abstract Area createCellShape(int size);

	protected void setCellShape(Area cellShape) {
		this.cellShape = cellShape;
	}


	
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

	public ZoneWalker createZoneWalker(){
		return null;
	}
	
	
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
	 * @return The size of the grid<br><br>
	 * *<i>SquareGrid</i> - edge length<br>
	 * *<i>HexGrid</i> - edge to edge diameter
	 */
	public int getSize() {
		return size;
	}
	

	
	private void fireGridChanged() {
		if (zone != null) {
			zone.fireModelChangeEvent(new ModelChangeEvent(this, Event.GRID_CHANGED));
		}
	}
	
	/**
	 * Draws the grid scaled to the renderer's scale and within the renderer's boundaries
	 */
	public void draw(ZoneRenderer renderer, Graphics2D g, Rectangle bounds) {
		// Do nothing
	}
	
	public abstract Rectangle getBounds(CellPoint cp);
	
	/**
	 * Override if getCapabilities.isSecondDimensionAdjustmentSupported() returns true
	 * @param length the second settable dimension
	 * @return
	 */
	public void setSecondDimension(double length) {
	}
	
	/**
	 * Override if getCapabilities.isSecondDimensionAdjustmentSupported() returns true
	 *  @return length the curent value of the second settable dimension
	 */
	public double getSecondDimension() {
		return 0;
	}
	
}
