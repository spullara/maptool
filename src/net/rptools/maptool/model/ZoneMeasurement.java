/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.model;

import net.rptools.maptool.client.CellPoint;

/**
 * @author trevor
 */
public class ZoneMeasurement {

	private int feetPerCell;
	private boolean roundDistance;
	
	public ZoneMeasurement(int feetPerCell, boolean roundDistance) {
		this.feetPerCell = feetPerCell;
		this.roundDistance = roundDistance;
	}
	
	public double distanceBetween(CellPoint startCell, CellPoint endCell) {
		
		// Calculate Distance
		int distX = Math.abs(startCell.x - endCell.x);
		int distY = Math.abs(startCell.y - endCell.y);
		
		double dist = Math.sqrt(distX*distX + distY*distY);
        
        if (roundDistance) {
            dist = Math.round(dist);
        }
        dist *= feetPerCell;
		
        return dist;
	}

	public String formatDistanceBetween(CellPoint startCell, CellPoint endCell) {
		return distanceStringEnglish(distanceBetween(startCell, endCell));
	}
	
    private static String distanceStringEnglish(double distance) {
        StringBuilder sb = new StringBuilder();
        long totalInches = Math.round(distance * 12);
        
        long feet = totalInches / 12;
        long inches = totalInches - (feet * 12);
        
        
        sb.append(feet).append("'");
        
        if (inches != 0) {
            sb.append(" ").append(inches).append("\"");
        }

        return sb.toString();
    }

}
