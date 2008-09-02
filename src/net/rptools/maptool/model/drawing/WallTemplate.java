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
package net.rptools.maptool.model.drawing;

import java.util.List;

import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.ZonePoint;

/**
 * A template that draws consecutive blocks
 * 
 * @author Jay
 */
public class WallTemplate extends LineTemplate {

    /**
     * Set the path vertex, it isn't needed by the wall template but the superclass needs it to paint.
     */
    public WallTemplate() {
        setPathVertex(new ZonePoint(0, 0));
    }
    
    /**
     * @see net.rptools.maptool.model.drawing.AbstractTemplate#getRadius()
     */
    @Override
    public int getRadius() {
        return getPath() == null ? 0 : getPath().size();
    }
    
    /**
     * @see net.rptools.maptool.model.drawing.LineTemplate#setRadius(int)
     */
    @Override
    public void setRadius(int squares) {
        // Do nothing, calculated from path length
    }
    
    /**
     * @see net.rptools.maptool.model.drawing.LineTemplate#setVertex(net.rptools.maptool.model.ZonePoint)
     */
    @Override
    public void setVertex(ZonePoint vertex) {
        ZonePoint v = getVertex();
        v.x = vertex.x;
        v.y = vertex.y;
    }
    
    /**
     * @see net.rptools.maptool.model.drawing.LineTemplate#calcPath()
     */
    @Override
    protected List<CellPoint> calcPath() {
        return getPath(); // Do nothing, path is set by tool.
    }
}
