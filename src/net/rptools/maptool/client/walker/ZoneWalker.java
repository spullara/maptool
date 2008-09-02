/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.client.walker;

import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.Path;


public interface ZoneWalker {
  
  public void setWaypoints(CellPoint... points);
  public void addWaypoints(CellPoint... point);
  
  public CellPoint replaceLastWaypoint(CellPoint point);
  
  public boolean isWaypoint(CellPoint point);
  
  public int getDistance();
  public Path getPath();
  public CellPoint getLastPoint();
  
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
