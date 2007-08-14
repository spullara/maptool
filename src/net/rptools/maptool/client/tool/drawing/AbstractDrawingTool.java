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

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;

import net.rptools.lib.swing.ColorPicker;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolUtil;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.tool.DefaultTool;
import net.rptools.maptool.client.tool.LayerSelectionDialog;
import net.rptools.maptool.client.tool.LayerSelectionDialog.LayerSelectionListener;
import net.rptools.maptool.client.ui.zone.ZoneOverlay;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.model.Zone.Layer;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.Pen;


/**
 * Tool for drawing freehand lines.
 */
public abstract class AbstractDrawingTool extends DefaultTool implements MouseListener, ZoneOverlay {
    
    private boolean isEraser;
    
    private boolean isSnapToGridSelected;
    private boolean isEraseSelected;
    private static LayerSelectionDialog layerSelectionDialog;

    private static Zone.Layer selectedLayer = Zone.Layer.TOKEN;

    static {
		layerSelectionDialog = new LayerSelectionDialog(new Zone.Layer[]{Zone.Layer.TOKEN, Zone.Layer.GM, Zone.Layer.OBJECT, Zone.Layer.BACKGROUND}, new LayerSelectionListener() {
			public void layerSelected(Layer layer) {
				selectedLayer = layer;
			}
		});
    	
    }
    
    protected Rectangle createRect(ZonePoint originPoint, ZonePoint newPoint) {
    	
    	int x = Math.min(originPoint.x, newPoint.x);
    	int y = Math.min(originPoint.y, newPoint.y);
    	
    	int w = Math.max(originPoint.x, newPoint.x) - x;
    	int h = Math.max(originPoint.y, newPoint.y) - y;
    	
    	return new Rectangle(x, y, w, h);
    }

    protected void paintTransformed(Graphics2D g, ZoneRenderer renderer, Drawable drawing, Pen pen) {

    	AffineTransform transform = new AffineTransform();
    	transform.translate(renderer.getViewOffsetX(), renderer.getViewOffsetY());
    	transform.scale(renderer.getScale(), renderer.getScale());
    	
    	AffineTransform oldTransform = g.getTransform();
    	g.transform(transform);
    	drawing.draw(g, pen);
    	g.setTransform(oldTransform);
    }

	protected void attachTo(ZoneRenderer renderer) {
		if (MapTool.getPlayer().isGM()) {
			MapTool.getFrame().showControlPanel(MapTool.getFrame().getColorPicker(), layerSelectionDialog);
		} else {
			MapTool.getFrame().showControlPanel(MapTool.getFrame().getColorPicker());
		}
		renderer.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		MapTool.getFrame().getColorPicker().setSnapSelected(isSnapToGridSelected);
		MapTool.getFrame().getColorPicker().setEraseSelected(isEraseSelected);
		super.attachTo(renderer);
	}

	protected void detachFrom(ZoneRenderer renderer) {
		MapTool.getFrame().hideControlPanel();
		renderer.setCursor(Cursor.getDefaultCursor());
		
		isSnapToGridSelected = MapTool.getFrame().getColorPicker().isSnapSelected();
		isEraseSelected = MapTool.getFrame().getColorPicker().isEraseSelected();
		
		super.detachFrom(renderer);
	}
    
    protected void setIsEraser(boolean eraser) {
    	isEraser = eraser;
    }
    protected boolean isEraser() {
    	return isEraser;
    }
    
    protected boolean isBackgroundFill(MouseEvent e) {
    	
    	boolean defaultValue = MapTool.getFrame().getColorPicker().isFillBackgroundSelected();
    	return defaultValue;
    }
    
    protected boolean isEraser(MouseEvent e) {
    	
    	boolean defaultValue = MapTool.getFrame().getColorPicker().isEraseSelected();
    	if (SwingUtil.isShiftDown(e)) {
    		// Invert from the color panel
    		defaultValue = !defaultValue;
    	}
    	return defaultValue;
    }
    
    protected boolean isSnapToGrid(MouseEvent e) {
    	
    	boolean defaultValue = MapTool.getFrame().getColorPicker().isSnapSelected();
    	if (SwingUtil.isControlDown(e)) {
    		// Invert from the color panel
    		defaultValue = !defaultValue;
    	}
    	return defaultValue;
    }
    
    protected Pen getPen() {
    	
    	Pen pen = new Pen(MapTool.getFrame().getPen());
		pen.setEraser(isEraser);
		
		ColorPicker picker = MapTool.getFrame().getColorPicker();
		if (picker.isFillForegroundSelected()) {
	        pen.setForegroundMode(Pen.MODE_SOLID);
		} else {
			pen.setForegroundMode(Pen.MODE_TRANSPARENT);
		}
		if (picker.isFillBackgroundSelected()) {
	        pen.setBackgroundMode(Pen.MODE_SOLID);
		} else {
			pen.setBackgroundMode(Pen.MODE_TRANSPARENT);
		}

		return pen;
    }
    
    protected ZonePoint getPoint(MouseEvent e) {
    	
    	ScreenPoint sp = new ScreenPoint(e.getX(), e.getY());
    	ZonePoint zp = sp.convertToZone(renderer);
    	if (isSnapToGrid(e)) {
	    	zp = renderer.getZone().getNearestVertex(zp);
	    	sp = ScreenPoint.fromZonePoint(renderer, zp);
    	} 

    	return zp;
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

    	if (!hasPaint(pen)) {
    		return;
    	}
    	drawable.setLayer(selectedLayer);
    	
    	// Send new textures
    	MapToolUtil.uploadTexture(pen.getPaint());
    	MapToolUtil.uploadTexture(pen.getBackgroundPaint());
    	
		// Tell the local/server to render the drawable.
        MapTool.serverCommand().draw(zoneId, pen, drawable);
      
        // Allow it to be undone
        DrawableUndoManager.getInstance().addDrawable(zoneId, pen, drawable);
    }
    
    private boolean hasPaint(Pen pen) {
    	return pen.getForegroundMode() != Pen.MODE_TRANSPARENT || pen.getBackgroundMode() != Pen.MODE_TRANSPARENT;
    }
    
}
