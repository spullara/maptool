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
package net.rptools.maptool.client.walker;

import java.util.List;

import net.rptools.maptool.client.CellPoint;


public interface ZoneWalker {
  
  public void setWaypoints(CellPoint... points);
  public void addWaypoints(CellPoint... point);
  
  public CellPoint replaceLastWaypoint(CellPoint point);
  
  public boolean isWaypoint(CellPoint point);
  
  public int getDistance();
  public List<CellPoint> getPath();
  
  /**
   * Remove an existing waypoint. Nothing is removed if the passed point is not a waypoint.
   * 
   * @param point The point to be removed
   * @return The value <code>true</code> is returned if the point is removed.
   */
  boolean removeWaypoint(CellPoint point);
  
  /**
   * Toggle the existance of a way point. A waypoint is added if the passed point
   * is not on an existing waypoint or a waypoint is removed if it is on an 
   * existing point.
   * 
   * @param point Point being toggled
   * @return The value <code>true</code> if a waypoint was added, <code>false</code>
   * if one was removed.
   */
  boolean toggleWaypoint(CellPoint point);
}
