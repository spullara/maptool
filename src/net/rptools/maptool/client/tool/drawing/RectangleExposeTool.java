package net.rptools.maptool.client.tool.drawing;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.Pen;

public class RectangleExposeTool extends RectangleTool {

    public RectangleExposeTool() {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/Tool_Draw_Rect_Fog.gif"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    // Override abstracttool to prevent color palette from
    // showing up
	protected void attachTo(ZoneRenderer renderer) {
		this.zoneRenderer = renderer;
	}

	@Override
	protected void completeDrawable(GUID zoneId, Pen pen, Drawable drawable) {

		if (!MapTool.getPlayer().isGM()) {
			MapTool.showError("Must be a GM to change the fog of war.");
			MapTool.getFrame().getCurrentZoneRenderer().repaint();
			return;
		}
		
		Zone zone = MapTool.getCampaign().getZone(zoneId);

		Rectangle bounds = drawable.getBounds();
		Area area = new Area(new RoundRectangle2D.Float(bounds.x, bounds.y, bounds.width, bounds.height, 20, 20));
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
        return "Expose/Hide a rectangle on the Fog of War";
    }
}
