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
package net.rptools.maptool.model;

import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;

public class CellPoint extends AbstractPoint {

    public CellPoint(int x, int y) {
        super(x, y);
    }
    
    public String toString() {
        return "CellPoint" + super.toString();
    }
    
    /**
     * Find the screen cooridnates of the upper left hand corner of a cell taking
     * into acount scaling and translation. 
     * 
     * @param renderer This renderer provides scaling
     * @return The screen coordinates of the upper left hand corner in the passed
     * point or in a new point.
     */
    public ScreenPoint convertToScreen(ZoneRenderer renderer) {
      double scale = renderer.getScale(); 
      Zone zone = renderer.getZone();

      Grid grid = zone.getGrid();
      ZonePoint zp = grid.convert(this);
      
      int sx = renderer.getViewOffsetX() + (int)(zp.x * scale);
      int sy = renderer.getViewOffsetY() + (int)(zp.y * scale);
      
      return new ScreenPoint(sx, sy);
    }
    
}
