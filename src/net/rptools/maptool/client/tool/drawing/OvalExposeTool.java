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

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.Pen;

public class OvalExposeTool extends OvalTool {
	private static final long serialVersionUID = -9023090752132286356L;

	public OvalExposeTool() {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/tool/fog-blue-oval.png"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

	@Override
	public boolean isAvailable() {
		return MapTool.getPlayer().isGM();
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
    public String getTooltip() {
        return "tool.ovalexpose.tooltip";
    }

    @Override
    public String getInstructions() {
    	return "tool.ovalexpose.instructions";
    }
    
    @Override
	protected void completeDrawable(GUID zoneId, Pen pen, Drawable drawable) {

		if (!MapTool.getPlayer().isGM()) {
			MapTool.showError("msg.error.fogexpose");
			MapTool.getFrame().refresh();
			return;
		}

		Zone zone = MapTool.getCampaign().getZone(zoneId);

		Rectangle bounds = drawable.getBounds();
		Area area = new Area(new Ellipse2D.Double(bounds.x, bounds.y, bounds.width, bounds.height));
		if (pen.isEraser()) {
			zone.hideArea(area);
			MapTool.serverCommand().hideFoW(zone.getId(), area);
		} else {
			zone.exposeArea(area);
			MapTool.serverCommand().exposeFoW(zone.getId(), area);
		}
		
		
		MapTool.getFrame().refresh();
	}
}
