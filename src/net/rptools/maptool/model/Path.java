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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Path {

	private List<CellPoint> cellList = new LinkedList<CellPoint>();
	private List<CellPoint> waypointList = new LinkedList<CellPoint>();
	
	public void addPathCell(CellPoint point) {
		cellList.add(point);
	}
	
	public void addAllPathCells(List<CellPoint> cells) {
		cellList.addAll(cells);
	}
	
	public List<CellPoint> getCellPath() {
		return Collections.unmodifiableList(cellList);
	}
	
	public void addWayPoint(CellPoint point) {
		waypointList.add(point);
	}
	
	public boolean isWaypoint(CellPoint point) {
		return waypointList.contains(point);
	}
	
	public Path derive(int cellOffsetX, int cellOffsetY) {
		
		Path path = new Path();
		for (CellPoint cp : cellList) {
			path.addPathCell(new CellPoint(cp.x - cellOffsetX, cp.y - cellOffsetY));
		}
		for (CellPoint cp : waypointList) {
			path.addWayPoint(new CellPoint(cp.x - cellOffsetX, cp.y - cellOffsetY));
		}
		
		return path;
	}
}
