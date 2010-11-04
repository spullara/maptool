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

import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.LineSegment;
import net.rptools.maptool.model.drawing.Pen;


/**
 * Tool for drawing freehand lines.
 */
public class PolygonExposeTool extends PolygonTool implements MouseMotionListener {
    private static final long serialVersionUID = 3258132466219627316L;

    public PolygonExposeTool() {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/tool/fog-blue-poly.png"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

	@Override
	public boolean isAvailable() {
		return MapTool.getPlayer().isGM();
	}

    @Override
    public String getTooltip() {
        return "tool.polyexpose.tooltip";
    }

    @Override
    public String getInstructions() {
    	return "tool.polyexpose.instructions";
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
    protected boolean isBackgroundFill(MouseEvent e) {
    	// Expose tools are implied to be filled
    	return false;
    }
    
    @Override
    protected Pen getPen() {
    	Pen pen = super.getPen();
    	pen.setBackgroundMode(Pen.MODE_TRANSPARENT);
    	pen.setThickness(1);
    	return pen;
    }

    @Override
    protected void stopLine(MouseEvent e) {
    	LineSegment line = getLine();
    	
        if (line == null) return; // Escape has been pressed
        addPoint(e);
        
        completeDrawable(renderer.getZone().getId(), getPen(), line);
        
        resetTool();
    }

    @Override
    protected void completeDrawable(GUID zoneId, Pen pen, Drawable drawable) {

        if (!MapTool.getPlayer().isGM()) {
            MapTool.showError("msg.error.fogexpose");
            MapTool.getFrame().refresh();
            return;
        }
        
        Zone zone = MapTool.getCampaign().getZone(zoneId);

        Polygon polygon = getPolygon((LineSegment) drawable);
        Area area = new Area(polygon);
        Set<GUID> selectedToks = getSelectedTokens();
        if (pen.isEraser()) {
            zone.hideArea(area, selectedToks);
            MapTool.serverCommand().hideFoW(zone.getId(), area, selectedToks);
        } else {
            zone.exposeArea(area, selectedToks);
            MapTool.serverCommand().exposeFoW(zone.getId(), area, selectedToks);
        }
        
        
        MapTool.getFrame().refresh();
    }
}
