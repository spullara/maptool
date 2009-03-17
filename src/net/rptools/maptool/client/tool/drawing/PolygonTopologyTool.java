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
import net.rptools.maptool.language.I18N;
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
    public String getTooltip() {
        return "tool.polytopo.tooltip";
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
    		area = ((LineSegment)drawable).createLineArea();
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
