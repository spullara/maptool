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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawableColorPaint;
import net.rptools.maptool.model.drawing.LineSegment;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.model.drawing.ShapeDrawable;


/**
 * Tool for drawing freehand lines.
 */
public class PolygonTopologyTool extends LineTool implements MouseMotionListener {
    private static final long serialVersionUID = 3258132466219627316L;

    public PolygonTopologyTool() {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/tool/top-blue-poly.png"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    @Override
    // Override abstracttool to prevent color palette from
    // showing up
	protected void attachTo(ZoneRenderer renderer) {
    	super.attachTo(renderer);
    	// Hide the drawable color palette
		MapTool.getFrame().hideControlPanel();
	}
    
	@Override
	public boolean isAvailable() {
		return MapTool.getPlayer().isGM();
	}
	
	@Override
	protected boolean drawMeasurement() {
		return false;
	}

	@Override
    public String getInstructions() {
    	return "tool.poly.instructions";
    }

    protected boolean isBackgroundFill(MouseEvent e) {
    	return true;
    }

    protected void completeDrawable(GUID zoneGUID, Pen pen, Drawable drawable) {

    	Area area = null;
    	if (drawable instanceof LineSegment) {
//    		area = new Area(getPolygon((LineSegment) drawable));
    		area = createLineArea((LineSegment) drawable);
    	} else {
    		area = new Area(((ShapeDrawable)drawable).getShape());
    	}

        if (pen.isEraser()) {
            renderer.getZone().removeTopology(area);
            MapTool.serverCommand().removeTopology(renderer.getZone().getId(), area);
        } else {
            renderer.getZone().addTopology(area);
            MapTool.serverCommand().addTopology(renderer.getZone().getId(), area);
        }
        renderer.repaint();
    }
    
    private Area createLineArea(LineSegment line) {
    	
    	GeneralPath gp = null;
    	for (Point point : line.getPoints()) {

    		if (gp == null) {
    			gp = new GeneralPath();
    			gp.moveTo(point.x, point.y);
    			continue;
    		}
    		
    		gp.lineTo(point.x, point.y);
    	}

    	BasicStroke stroke = new BasicStroke(2);
    	
    	return new Area(stroke.createStrokedShape(gp));
    }

    protected Pen getPen() {
    	
    	Pen pen = new Pen(MapTool.getFrame().getPen());
		pen.setEraser(isEraser());
		pen.setForegroundMode(Pen.MODE_TRANSPARENT);
        pen.setBackgroundMode(Pen.MODE_SOLID);
        pen.setThickness(1.0f);
        pen.setPaint(new DrawableColorPaint(isEraser() ? AppStyle.topologyRemoveColor : AppStyle.topologyAddColor));
        
		return pen;
    }

    protected Polygon getPolygon(LineSegment line) {
        Polygon polygon = new Polygon();
        for (Point point : line.getPoints()) {
            polygon.addPoint(point.x, point.y);
        }
        
        return polygon;
    }
    
    @Override
    public String getTooltip() {
        return "tool.polytopo.tooltip";
    }
    
    @Override
    public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
    	
    	
    	if (MapTool.getPlayer().isGM()) {

	    	Zone zone = renderer.getZone();
	    	Area topology = zone.getTopology();
	
    		Graphics2D g2 = (Graphics2D) g.create();
	    	g2.translate(renderer.getViewOffsetX(), renderer.getViewOffsetY());
	    	g2.scale(renderer.getScale(), renderer.getScale());
	
	    	g2.setColor(AppStyle.topologyColor);
	    	g2.fill(topology);
	
	    	g2.dispose();
    	}

    	super.paintOverlay(renderer, g);
    }
}
