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

import javax.swing.undo.UndoManager;

import net.rptools.maptool.client.MapToolClient;
import net.rptools.maptool.client.Tool;
import net.rptools.maptool.client.ZoneOverlay;
import net.rptools.maptool.client.ZoneRenderer;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawnElement;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.server.MapToolServer;


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

    /**
     * Render a drawable on a zone. This method consolidates all of the calls to the 
     * server inone place so that it is easier to keep them in sync.
     * 
     * @param zoneId Id of the zone where the <code>drawable</code> is being drawn.
     * @param pen The pen used to draw.
     * @param drawable What is being drawn.
     */
    protected void completeDrawable(GUID zoneId, Pen pen, Drawable drawable) {

		// Tell the local/server to render the drawable.
      if (MapToolClient.isConnected()) {
        MapToolClient.getInstance().getConnection().callMethod(MapToolServer.COMMANDS.draw.name(), zoneId, pen, drawable);
      } else {
        zoneRenderer.getZone().addDrawable(new DrawnElement(drawable, pen));
        zoneRenderer.repaint();
      } // endif
      
      // Allow it to be undone
      DrawableUndoManager.getInstance().addDrawable(zoneId, pen, drawable);
    }
}
