package net.rptools.maptool.client.tool.drawing;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.Pen;

public class RectangleExposeTool extends RectangleTool {

	@Override
	protected void completeDrawable(GUID zoneId, Pen pen, Drawable drawable) {

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
}
