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
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.tool.DefaultTool;
import net.rptools.maptool.client.ui.zone.ZoneOverlay;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Zone;


/**
 * Tool for filling in enclosed areas of topology
 */
public class FillTopologyTool extends DefaultTool implements ZoneOverlay {

	private static final long serialVersionUID = -2125841145363502135L;

	public FillTopologyTool() {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/tool/top-blue-free.png"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
	@Override
    public String getInstructions() {
    	return "tool.filltopology.instructions";
    }

	@Override
	public void mouseClicked(MouseEvent e) {

		if (!SwingUtilities.isLeftMouseButton(e)) {
			return;
		}
		
//		ZonePoint zp = new ScreenPoint(e.getX(), e.getY()).convertToZone(renderer);
		
//		Area holeArea = renderer.getTopologyAreaData().getHoleAt(zp.x, zp.y); 
//		if (holeArea == null) {
//			MapTool.showError("Must click in an enclosed area");
//			return;
//		}
//		
//        renderer.getZone().addTopology(holeArea);
//        MapTool.serverCommand().addTopology(renderer.getZone().getId(), holeArea);
        
        renderer.repaint();
	}
	
    public void paintOverlay(ZoneRenderer renderer, Graphics2D g2) {
    	Graphics2D g = (Graphics2D)g2.create();
    	
    	Color oldColor = g.getColor();

    	if (MapTool.getPlayer().isGM()) {
	    	Zone zone = renderer.getZone();
	    	Area topology = zone.getTopology();
	
	    	double scale = renderer.getScale();
	    	g.translate(renderer.getViewOffsetX(), renderer.getViewOffsetY());
	    	g.scale(scale, scale);

	    	g.setColor(AppStyle.topologyColor);

	    	g.fill(topology);
	
	    	g.setColor(oldColor);
    	}
    }
    
    @Override
    public String getTooltip() {
        return "tool.filltopology.tooltip";
    }
    
}
