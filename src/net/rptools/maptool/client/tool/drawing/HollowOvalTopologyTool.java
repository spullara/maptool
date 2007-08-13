package net.rptools.maptool.client.tool.drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.tool.ToolHelper;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.model.drawing.DrawableColorPaint;
import net.rptools.maptool.model.drawing.Oval;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.util.GraphicsUtil;

public class HollowOvalTopologyTool extends AbstractDrawingTool implements MouseMotionListener {

    private static final long serialVersionUID = 3258413928311830325L;

    protected Oval oval;
    private ZonePoint originPoint;
	
    public HollowOvalTopologyTool() {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/tool/drawopencirc.png"))));
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
    	return "tool.ovaltpology.instructions";
    }
    
    @Override
    public String getTooltip() {
        return "Draw a hollow oval topology";
    }

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
    	
        if (oval != null) {
        	
        	Pen pen = new Pen();
        	pen.setEraser(getPen().isEraser());
            pen.setOpacity(AppStyle.topologyRemoveColor.getAlpha());
            pen.setForegroundMode(Pen.MODE_TRANSPARENT);
        	pen.setThickness(1.0f);        	
        	        	
            if (pen.isEraser()) {
                pen.setEraser(false);
                pen.setBackgroundPaint(new DrawableColorPaint(AppStyle.topologyRemoveColor));
            } else {
            	pen.setBackgroundPaint(new DrawableColorPaint(AppStyle.topologyAddColor));
            }

            paintTransformed(g, renderer, oval, pen);
            
            Point start = oval.getStartPoint();
            Point end = oval.getEndPoint();
            
            ToolHelper.drawBoxedMeasurement(renderer, g, new ScreenPoint(start.x, start.y), new ScreenPoint(end.x, end.y));
        }
    }    
    
    public void mousePressed(MouseEvent e) {

    	if (SwingUtilities.isLeftMouseButton(e)) {
	    	ZonePoint zp = getPoint(e);
	        
	        if (oval == null) {
	            oval = new Oval(zp.x, zp.y, zp.x, zp.y);
	            originPoint = zp;
	        } else {
	            oval.getEndPoint().x = zp.x;
	            oval.getEndPoint().y = zp.y;
	            
	            Area area = GraphicsUtil.createLineSegmentEllipse(oval.getStartPoint().x, oval.getStartPoint().y, oval.getEndPoint().x, oval.getEndPoint().y);
	            
	            // Still use the whole area if it's an erase action
	            if (!isEraser(e)) {
	            	int x1 = Math.min(oval.getStartPoint().x, oval.getEndPoint().x) + 2;
	            	int y1 = Math.min(oval.getStartPoint().y, oval.getEndPoint().y) + 2;
	            	
	            	int x2 = Math.max(oval.getStartPoint().x, oval.getEndPoint().x) - 2;
	            	int y2 = Math.max(oval.getStartPoint().y, oval.getEndPoint().y) - 2;
	            	
	            	Area innerArea = GraphicsUtil.createLineSegmentEllipse(x1, y1, x2, y2); 
		            area.subtract(innerArea);
	            }	            
	            
	            if (isEraser(e)) {
		            renderer.getZone().removeTopology(area);
		            MapTool.serverCommand().removeTopology(renderer.getZone().getId(), area);
	            } else {
		            renderer.getZone().addTopology(area);
		            MapTool.serverCommand().addTopology(renderer.getZone().getId(), area);
	            }
	            renderer.repaint();	            
	            
	            oval = null;	            
	        }
	
	    	setIsEraser(isEraser(e));
    	}
    	
    	super.mousePressed(e);    	
    } 
    
    @Override
    public void mouseDragged(MouseEvent e) {
    	
    	if (oval == null) {
    		super.mouseDragged(e);
    	}
    }
    
    public void mouseMoved(MouseEvent e) {
    	if (oval != null) {
    		ZonePoint sp = getPoint(e);
    		
            oval.getEndPoint().x = sp.x;
            oval.getEndPoint().y = sp.y;
            oval.getStartPoint().x = originPoint.x - (sp.x - originPoint.x);
            oval.getStartPoint().y = originPoint.y - (sp.y - originPoint.y);
	        
	        renderer.repaint();
    	}    	
    }

  /**
   * Stop drawing a rectangle and repaint the zone.
   */
  public void resetTool() {
	  if (oval != null) {
	    oval = null;
	    renderer.repaint();
	  } else {
		  super.resetTool();
	  }
  }    
}
