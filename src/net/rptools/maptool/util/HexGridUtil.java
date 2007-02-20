package net.rptools.maptool.util;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.HexGrid;
import net.rptools.maptool.model.HexGridHorizontal;
import net.rptools.maptool.model.HexGridVertical;
import net.rptools.maptool.model.TokenSize;


/**
 * Provides methods to handle hexgrid issues that don't exist with a square grid.
 * @author Tylere
 */
public class HexGridUtil {
	

	/** 
	 * Convert to u-v coordinates where the v-axis points
	 * along the direction of edge to edge hexes
	 */ 
	private static int[] toUVCoords(CellPoint cp, HexGrid grid) {
		int cpU, cpV;
		if (grid instanceof HexGridHorizontal) {
			cpU = cp.y;
			cpV = cp.x;
		}
		else {
			cpU = cp.x;
			cpV = cp.y;
		}
		return new int[] {cpU, cpV};
	}
	
	// TODO: figure out the best return type.
	/**
	 * Convert from u-v coords to grid coords
	 * @param pUV point in u-v space
	 * @param grid
	 * @return the point in grid-space
	 */
	private static CellPoint fromUVCoords(Point pUV, HexGrid grid) {
		CellPoint cp = new CellPoint(pUV.x, pUV.y);
		if (grid instanceof HexGridHorizontal) {
			cp.x = pUV.y;
			cp.y = pUV.x;
		}
		
		return cp;
	}
	
	/**
	 * Gets the cells in a creature's area according to the hex-based creature sizes here:
	 * http://www.d20srd.org/srd/variant/adventuring/hexGrid.htm
	 * 
	 * @param sizeFactor The token size (token diameter in cells)
	 * @param baseCellPoint The token's base CellPoint coordinate
	 * @return All CellPoints included in the token's space
	 */
	public static Set<CellPoint> getIncludedCells(int sizeFactor, CellPoint baseCellPoint, HexGrid grid) {
		Set<CellPoint> includedCellsSet = new HashSet<CellPoint>();
		Set<Point> UVPoints = new HashSet<Point>();

		includedCellsSet.add(baseCellPoint);
		
		int[] cpUV = toUVCoords(baseCellPoint, grid); 
		int cpU = cpUV[0];
		int cpV = cpUV[1];
		
		boolean isCellInOddSection = Math.abs(cpU) % 2 == 0 ? false : true;
		
		// Add the cells covered by the size (base cell is already included)
		switch (sizeFactor) {
		
			case 1: // single hex
				break;
				
			case 2: // Large = T shape of 3 hexes
				if (isCellInOddSection) {
					UVPoints.add(new Point(cpU, cpV+1));
					UVPoints.add(new Point(cpU+1, cpV+1));
					break;
				}
				else {
					UVPoints.add(new Point(cpU+1, cpV));
					UVPoints.add(new Point(cpU, cpV+1));
					break;
				}
				
			case 3: // Huge = Hex of 7 hexes.
				if (isCellInOddSection) {
					for (int v = 1; v <= 2; v++){
						for( int u = -1; u <= 1; u++) {
							UVPoints.add(new Point(cpU+u, cpV+v));
						}
					}
					break;
				}
				else {
					for (int u = -1; u <= 1; u++){
						for( int v = 0; v <= 1; v++) {
							UVPoints.add(new Point(cpU+u, cpV+v));
						}
					}
					UVPoints.add(new Point(cpU, cpV+2));
					break;
				}
				
			case 4: // Gargantuan = some funky shape =)
				if (isCellInOddSection) {
					for (int v = 1; v <= 3; v++){
						for( int u = -1; u <= 1; u++) {
							UVPoints.add(new Point(cpU+u, cpV+v));
						}
					}
					UVPoints.add(new Point(cpU+2, cpV+1));
					UVPoints.add(new Point(cpU+2, cpV+2));
					break;
				}
				else{
					for (int v = 1; v <= 2; v++){
						for( int u = -1; u <= 2; u++) {
							UVPoints.add(new Point(cpU+u, cpV+v));
						}
					}
					UVPoints.add(new Point(cpU-1, cpV));
					UVPoints.add(new Point(cpU+1, cpV));
					UVPoints.add(new Point(cpU, cpV+3));
					break;	
				}
				
			case 6: // Colossal = some bigger funky shape =)
				if (isCellInOddSection) {
					for (int v = 1; v <= 5; v++){
						for( int u = -2; u <= 3; u++) {
							// exclude the 4 cells in the loop that we don't want 
							if ( !( (u==3 && v==1) || (v==5 &&( u ==-2 || u > 1)) ) ) {
								UVPoints.add(new Point(cpU+u, cpV+v));
							}	
						}
					}
					break;
				}
				else {
					for (int v = 1; v <= 4; v++){
						for( int u = -2; u <= 3; u++) {
							if ( !(u ==3 && v == 4))
							UVPoints.add(new Point(cpU+u, cpV+v));
						}
					}
					UVPoints.add(new Point(cpU-1, cpV));
					UVPoints.add(new Point(cpU+1, cpV));
					UVPoints.add(new Point(cpU, cpV+5));
					break;	
				}
				
			default:
				break;			
		}
		
		// Convert back to grid coordinates and add the cells
		for (Point p : UVPoints) {
			includedCellsSet.add(fromUVCoords(p, grid));
		}
		
		return includedCellsSet;
		
	}
	
	// TODO: somehow combine with getPositionYOffset, they are almsot identical.
	/**
	 * @return The x-value required to translate from the top-left of a token's base cell
	 * to the top left of a token's bounding rectange
	 */
	public static int getPositionXOffset(int tokenSize, int scaledCellHeight, HexGrid grid) {
		int xOffset = 0;
		int sizeFactor = (int)TokenSize.getSizeInstance(tokenSize).sizeFactor();
		
		if( grid instanceof HexGridVertical) {
			switch (sizeFactor) {
			case 3:
			case 4:
				xOffset = -(int)(scaledCellHeight*0.75);
				break;
			case 6:
				xOffset = -(int)(scaledCellHeight*1.5);
				break;
			default:
				break;
			}
		}
		//No x-offset required for horizontal hex grids
		
		return xOffset;
	}
	
	/**
	 * @return The y-value required to translate from the top-left of a token's base cell
	 * to the top left of a token's bounding rectange
	 */
	public static int getPositionYOffset(int tokenSize, int scaledCellHeight, HexGrid grid) {
		int yOffset = 0;
		int sizeFactor = (int)TokenSize.getSizeInstance(tokenSize).sizeFactor();
		
		if( grid instanceof HexGridHorizontal) {

			switch (sizeFactor) {
			case 3:
			case 4:
				yOffset = -(int)(scaledCellHeight*0.75);
				break;
			case 6:
				yOffset = -(int)(scaledCellHeight*1.5);
			default:
				break;
			}
		}
		// No y-offset required for vertical hex grids
		
		return yOffset;
	}
	
	public static Dimension getTokenAdjust(HexGrid grid, int width, int height, int tokenSize) {
		
		int sizeFactor = (int)TokenSize.getSizeInstance(tokenSize).sizeFactor();
		int dU = 0;
		int dV = 0;

		// V component will always be larger, except for a single cell
		if (sizeFactor > 1) {
			dV = Math.abs(width-height)/2;
		}
		else {
			dU = Math.abs(width-height)/2;
		}
		Point pUV = new Point (dU, dV);
		CellPoint cp = fromUVCoords(pUV, grid);
		
		return new Dimension(cp.x, cp.y);
	}
	
	public static CellPoint getWaypoint(HexGrid grid, CellPoint cp, int width, int height) {
	
		if( width == height ) {
			int[] cpUV = toUVCoords(cp, grid); 
			Point pUV = new Point(cpUV[0], cpUV[1] + (int)((width-1)/2) );
			return fromUVCoords(pUV, grid);
		}
		
		return cp;
	}
	
	/**
	 * 
	 * @param width Token width in Cells
	 * @return
	 */
	public static Point getCellGroupCenterOffset(HexGrid grid, int tokenSize, float scale) {
		int sizeFactor = (int)TokenSize.getSizeInstance(tokenSize).sizeFactor();
		int pU = 0;
		int pV = 0;

			pU = (int)(grid.getCellGroupCenterUComponent(sizeFactor)*scale);
			pV = (int)(grid.getCellGroupCenterVComponent(sizeFactor)*scale);
			
			Point pUV = new Point (pU, pV);
			CellPoint cp = fromUVCoords(pUV, grid);
			
			return new Point(cp.x,cp.y);

	}
	
	
	// TODO: Clean this up.  Merge with above method.
	public static Point getCellGroupCenterOffset(HexGrid grid, int width, int height, float scale) {
		
		int pU = 0;
		int pV = 0;
		
		if (width == height) {
			
			// This doesn't need to be this complicated...look at again with fresh eyes later.
			// Why doesn't just getCellGroupCenterUComponent(width) work for all? maybe it does =P
			switch (width) {
			case 2:
				pU = (int)((grid.getCellGroupCenterUComponent(width)+grid.getCellOffsetU())*scale);
				break;
			case 3:
				pU = (int)((grid.getCellGroupCenterUComponent(width-1)+1.5*grid.getCellOffsetU())*scale);
				break;
			case 4:
				pU = (int)((grid.getCellGroupCenterUComponent(width-1)+2*grid.getCellOffsetU())*scale);
				break;
			case 6:
				pU = (int)((grid.getCellGroupCenterUComponent(width-2)+2.5*grid.getCellOffsetU())*scale);
				break;
			default:
				break;
			}
			pV = (int)((grid.getCellGroupCenterVComponent(width)+grid.getCellOffsetV())*scale);
			
			Point pUV = new Point (pU, pV);
			CellPoint cp = fromUVCoords(pUV, grid);
			
			return new Point(cp.x,cp.y);
		}
		
		return new Point(0,0);

	}
	
	/**
	 * @return The hex grid aligning dimensions of a token
	 */
	public static Dimension getTokenDimensions(int tokenSize, HexGrid grid, float scale) {
		
		float sizeFactor = TokenSize.getSizeInstance(tokenSize).sizeFactor();
		int dimU = 0;
		int dimV = 0;

		dimU = (int)((sizeFactor*grid.getURadius()*1.5 + grid.getURadius()*0.5) *scale);
		dimU -= 2; // pull in by a couple pixels.  Not sure why this is required
		dimV = (int)(sizeFactor*grid.getVRadius()*2*scale);

		Point pUV = new Point (dimU, dimV);
		CellPoint cp = fromUVCoords(pUV, grid);
		
		return new Dimension(cp.x,cp.y);

	}

}
