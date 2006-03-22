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

import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.LineSegment;
import net.rptools.maptool.model.drawing.Pen;


/**
 * Tool for drawing freehand lines.
 */
public class FreehandExposeTool extends FreehandTool implements MouseMotionListener {
    private static final long serialVersionUID = 3258132466219627316L;

    public FreehandExposeTool() {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/tool/FOGFree16.png"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    public String getInstructions() {
    	return "tool.freehandexpose.instructions";
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
    protected boolean isFill(MouseEvent e) {
    	// Expose tools are implied to be filled
    	return false;
    }
    
    @Override
    protected void completeDrawable(GUID zoneId, Pen pen, Drawable drawable) {

        if (!MapTool.getPlayer().isGM()) {
            MapTool.showError("Must be a GM to change the fog of war.");
            MapTool.getFrame().getCurrentZoneRenderer().repaint();
            return;
        }
        
        Zone zone = MapTool.getCampaign().getZone(zoneId);

        Polygon polygon = getPolygon((LineSegment) drawable);
        Area area = new Area(polygon);
        if (pen.isEraser()) {
            zone.hideArea(area);
            MapTool.serverCommand().hideFoW(zone.getId(), area);
        } else {
            zone.exposeArea(area);
            MapTool.serverCommand().exposeFoW(zone.getId(), area);
        }
        
        
        MapTool.getFrame().getCurrentZoneRenderer().updateFog();
    }
    
    @Override
    public String getTooltip() {
        return "Expose/Hide a freehand shape on the Fog of War";
    }
}
