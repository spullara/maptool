/* The MIT License
 * 
 * Copyright (c) 2008 Jay Gorrell
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
