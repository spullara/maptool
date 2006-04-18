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
package net.rptools.maptool.client.tool;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.client.CellPoint;
import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ZonePoint;
import net.rptools.maptool.client.ui.zone.ZoneOverlay;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.walker.ZoneWalker;
import net.rptools.maptool.client.walker.astar.AStarEuclideanWalker;
import net.rptools.maptool.model.Pointer;
import net.rptools.maptool.util.GraphicsUtil;


/**
 */
public class MeasureTool extends DefaultTool implements ZoneOverlay {

	private ZoneWalker walker;
	
	public MeasureTool () {
        try {
            setIcon(new ImageIcon(ImageUtil.getImage("net/rptools/maptool/client/image/Tool_Measure.gif")));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    @Override
    public String getTooltip() {
        return "Measure the distance along a path";
    }

    @Override
    public String getInstructions() {
    	return "tool.measure.instructions";
    }
    
    public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
        
        if (walker == null) {
            return;
        }

        renderer.renderPath(g, walker, 1, 1);

        ScreenPoint sp = walker.getLastPoint().convertToScreen(renderer);
        
        int y = sp.y - 10;
        int x = sp.x + (int)(renderer.getScaledGridSize()/2);
        GraphicsUtil.drawBoxedString(g, Integer.toString(walker.getDistance()), x, y);
    }
    
    @Override
    protected void installKeystrokes(Map<KeyStroke, Action> actionMap) {
    	super.installKeystrokes(actionMap);
    	
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				if (walker == null) {
					return;
				}
				
				// Waypoint
		        CellPoint cp = ZonePoint.fromScreenPoint(renderer, mouseX, mouseY).convertToCell(renderer);
		        walker.toggleWaypoint(cp);
			}
		});
    }
    
    ////
    // MOUSE LISTENER
	@Override
	public void mousePressed(java.awt.event.MouseEvent e){

		ZoneRenderer renderer = (ZoneRenderer) e.getSource();
        
        CellPoint cellPoint = renderer.getCellAt(new ScreenPoint(e.getX(), e.getY()));
        
		if (SwingUtilities.isLeftMouseButton(e)) {
			walker = new AStarEuclideanWalker(renderer.getZone());
            walker.addWaypoints(cellPoint, cellPoint);
            renderer.repaint();
            return;
		} 
        
        super.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {

        ZoneRenderer renderer = (ZoneRenderer) e.getSource();

		if (SwingUtilities.isLeftMouseButton(e)) {
		
			walker = null;
			renderer.repaint();
            return;
		}
        
        super.mouseReleased(e);
	}
	
    ////
    // MOUSE MOTION LISTENER
	@Override
    public void mouseDragged(MouseEvent e){

        if (SwingUtilities.isRightMouseButton(e)) {
            super.mouseDragged(e);
            return;
        }

        ZoneRenderer renderer = (ZoneRenderer) e.getSource();
        CellPoint cellPoint = renderer.getCellAt(new ScreenPoint(e.getX(), e.getY()));
        walker.replaceLastWaypoint(cellPoint);
        renderer.repaint();
    }
}
