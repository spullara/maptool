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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.LineSegment;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.model.drawing.ShapeDrawable;
import net.rptools.maptool.util.GraphicsUtil;


/**
 * Tool for drawing freehand lines.
 */
public class PolygonTopologyTool extends LineTool implements MouseMotionListener {
    private static final long serialVersionUID = 3258132466219627316L;

    public PolygonTopologyTool() {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/tool/poly.png"))));
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
    	
    	Area lineArea = new Area();
    	Point lastPoint = null;
    	for (Point point : line.getPoints()) {
    		
    		if (lastPoint == null) {
    			lastPoint = point;
    			continue;
    		}
    		
    		Area segmentArea = GraphicsUtil.createAreaBetween(lastPoint, point, 2);
    		lineArea.add(segmentArea);
    		
    		lastPoint = point;
    	}
    	
    	return lineArea;
    }

    protected Pen getPen() {
    	
    	Pen pen = new Pen(MapTool.getFrame().getPen());
		pen.setEraser(isEraser());
		pen.setForegroundMode(Pen.MODE_TRANSPARENT);
        pen.setBackgroundMode(Pen.MODE_SOLID);

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
        return "Draw closed polygons lines";
    }
    
    @Override
    public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
    	Color oldColor = g.getColor();
    	
    	if (MapTool.getPlayer().isGM()) {
	    	Zone zone = renderer.getZone();
	    	Area topology = zone.getTopology();
	
	    	double scale = renderer.getScale();
	    	AffineTransform transform = new AffineTransform();
	    	transform.scale(scale, scale);
	    	transform.translate(renderer.getViewOffsetX()/scale, renderer.getViewOffsetY()/scale);
	    	topology = topology.createTransformedArea(transform);
	
	    	g.setColor(AppStyle.topologyColor);
	    	g.fill(topology);
	
	    	g.setColor(oldColor);
    	}

    	super.paintOverlay(renderer, g);
    }
}
