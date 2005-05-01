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
package net.rptools.maptool.client.tool.drawing;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseListener;

import net.rptools.maptool.client.MapToolClient;
import net.rptools.maptool.client.Tool;
import net.rptools.maptool.client.ZoneOverlay;
import net.rptools.maptool.client.ZoneRenderer;
import net.rptools.maptool.model.drawing.Pen;


/**
 * Tool for drawing freehand lines.
 */
public abstract class AbstractDrawingTool extends Tool implements MouseListener, ZoneOverlay {
    protected ZoneRenderer zoneRenderer;
    
    private boolean isEraser;

	protected void attachTo(ZoneRenderer renderer) {
		this.zoneRenderer = renderer;
	}

	protected void detachFrom(ZoneRenderer renderer) {
		zoneRenderer = null;
	}
    
    protected void convertScreenToZone(Point p) {
        Point point = zoneRenderer.convertScreenToZone(p.x, p.y);
        
        p.x = point.x;
        p.y = point.y;
    }
 
    protected void setIsEraser(boolean eraser) {
    	isEraser = eraser;
    }
    
    protected Pen getPen() {
    	
    	Pen pen = new Pen(MapToolClient.getInstance().getPen());
		pen.setEraser(isEraser);
		
		return pen;
    }
    
    public abstract void paintOverlay(ZoneRenderer renderer, Graphics2D g);
}
