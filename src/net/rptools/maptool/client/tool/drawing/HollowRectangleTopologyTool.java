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

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.model.drawing.Rectangle;


/**
 * @author drice
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HollowRectangleTopologyTool extends RectangleTopologyTool implements MouseMotionListener {
    private static final long serialVersionUID = 3258413928311830323L;

    protected Rectangle rectangle;
    
    public HollowRectangleTopologyTool() {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/tool/RectangleBlue16.png"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
	@Override
    public String getInstructions() {
    	return "tool.recttpology.instructions";
    }
    
    @Override
    public String getTooltip() {
        return "Draw a hollow rectanglar topology";
    }

    public void mousePressed(MouseEvent e) {

    	ScreenPoint sp = getPoint(e);
    	
    	if (SwingUtilities.isLeftMouseButton(e)) {
	        if (rectangle == null) {
	            rectangle = new Rectangle(sp.x, sp.y, sp.x, sp.y);
	        } else {
	            rectangle.getEndPoint().x = sp.x;
	            rectangle.getEndPoint().y = sp.y;
	            
	            ZonePoint startPoint = new ScreenPoint((int) rectangle.getStartPoint().getX(), (int) rectangle.getStartPoint().getY()).convertToZone(renderer); 
	            ZonePoint endPoint = new ScreenPoint((int) rectangle.getEndPoint().getX(), (int) rectangle.getEndPoint().getY()).convertToZone(renderer);
	
	            int x1 = Math.min(startPoint.x, endPoint.x);
	            int x2 = Math.max(startPoint.x, endPoint.x);
	            int y1 = Math.min(startPoint.y, endPoint.y);
	            int y2 = Math.max(startPoint.y, endPoint.y);
	            
	            Area area = new Area(new java.awt.Rectangle(x1-1, y1-1, x2 - x1+3, y2 - y1+3));
	            Area innerArea = new Area(new java.awt.Rectangle(x1+1, y1+1, x2 - x1 - 1, y2 - y1 - 1));
	            area.subtract(innerArea);
	            System.out.println("AREA: " + area);
	            
	            if (isEraser(e)) {
		            renderer.getZone().removeTopology(area);
		            MapTool.serverCommand().removeTopology(renderer.getZone().getId(), area);
	            } else {
		            renderer.getZone().addTopology(area);
		            MapTool.serverCommand().addTopology(renderer.getZone().getId(), area);
	            }
	            renderer.repaint();
	            // TODO: send this to the server
	            
	            rectangle = null;
	        }
        
	        setIsEraser(isEraser(e));
    	}
    	
    	super.mousePressed(e);
    }
}
