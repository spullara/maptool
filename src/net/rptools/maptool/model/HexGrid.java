package net.rptools.maptool.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;

public class HexGrid extends Grid {

	private static final GridCapabilities GRID_CAPABILITIES= new GridCapabilities() {
		public boolean isPathingSupported() {return false;}
		public boolean isSnapToGridSupported() {return false;}
	};
	
	private static GeneralPath hex;
	private static int sideSize = 4;
	private static int height = 20;
	private static int topWidth = 28;
	static {
		hex = new GeneralPath();
		hex.moveTo(0, height);
		hex.lineTo(sideSize, 0);
		hex.lineTo(sideSize + topWidth, 0);
		hex.lineTo(sideSize + topWidth + sideSize, height);
	}

	public HexGrid(Zone zone) {
		super(zone);
	}
	
	@Override
	public CellPoint convert(ZonePoint zp) {
		return new CellPoint(0, 0);
	}

	@Override
	public ZonePoint convert(CellPoint cp) {
		return new ZonePoint(0, 0);
	}

	@Override
	public GridCapabilities getCapabilities() {
		return GRID_CAPABILITIES;
	}

	@Override
	public void draw(ZoneRenderer renderer, Graphics2D g, Rectangle bounds) {

		double scale = renderer.getScale();
        double gridSize = getSize() * scale;

        
        int offX = (int)(renderer.getViewOffsetX() % gridSize + getOffsetX()*scale);
        int offY = (int)(renderer.getViewOffsetY() % gridSize + getOffsetY()*scale);

        int startCol = (int)((int)(bounds.x / gridSize) * gridSize);
        int startRow = (int)((int)(bounds.y / gridSize) * gridSize);

        int count = 0;
        
        g.setColor(Color.red);
        ScreenPoint sp = ScreenPoint.fromZonePoint(renderer, 40, 0);
        g.drawLine(sp.x, 0, sp.x, renderer.getSize().height);
        
        g.setColor(Color.red);
        sp = ScreenPoint.fromZonePoint(renderer, 0, 0);
        g.drawLine(sp.x, 0, sp.x, renderer.getSize().height);
        
        g.setColor(new Color(getZone().getGridColor()));
        g.translate(offX-getSize(), offY-getSize());
		for (int y = 0; y < renderer.getSize().height + getSize(); y += height) {

			int offsetX = (count % 2 == 0 ? 0 : sideSize + topWidth);
			count ++;
			for (int x = 0; x < renderer.getSize().width + getSize(); x += topWidth + sideSize + sideSize + topWidth) {

				g.translate(x + offsetX, y);
				g.draw(hex);
				g.translate(-(x + offsetX), -y);
			}
		}
		g.translate(-offX+getSize(), -offY+getSize());
	}

}
