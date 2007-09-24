package net.rptools.maptool.model;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.walker.ZoneWalker;
import net.rptools.maptool.client.walker.astar.AStarVertHexEuclideanWalker;
import net.rptools.maptool.model.TokenFootprint.OffsetTranslator;

public class HexGridVertical extends HexGrid {
	
	private static final int[] FACING_ANGLES = new int[] {-150, -90, -30, 30, 90, 150};
	
	private static final OffsetTranslator OFFSET_TRANSLATOR = new OffsetTranslator() {
		public void translate(CellPoint originPoint, CellPoint offsetPoint) {
			if (originPoint.x%2==1 && offsetPoint.x%2==0) {
				offsetPoint.y++;
			}
		}
	};
	
	public HexGridVertical() {
		super();
	}

	@Override
	public int[] getFacingAngles() {
		return FACING_ANGLES;
	}
	
	@Override
	public BufferedImage getCellHighlight() {
		return pathHighlight;
	}
	
	@Override
	public double getCellHeight() {
		return getVRadius()*2;
	}
	
	@Override
	public double getCellWidth() {
		return getURadius()*2;
	}
	
	@Override
	protected Dimension setCellOffset() {
		return new Dimension((int)getCellOffsetU(), (int)getCellOffsetV());
	}
	
	
	@Override
	public ZoneWalker createZoneWalker() {
		return new AStarVertHexEuclideanWalker(getZone());
	}
	
	@Override
	protected void setGridDrawTranslation(Graphics2D g, double U, double V) {
		g.translate(U, V);
	}
	
	@Override
	protected double getRendererSizeV(ZoneRenderer renderer) {
		return renderer.getSize().getHeight();
	}
	
	@Override
	protected double getRendererSizeU(ZoneRenderer renderer) {
		return renderer.getSize().getWidth();
	}
	
	@Override
	protected int getOffV(ZoneRenderer renderer) {
		return (int)(renderer.getViewOffsetY() + getOffsetY()*renderer.getScale());
	}
	
	@Override
	protected int getOffU(ZoneRenderer renderer) {
		return (int)(renderer.getViewOffsetX() + getOffsetX()*renderer.getScale());
	}
	
	@Override
	public CellPoint convert(ZonePoint zp) {
		return convertZP(zp.x, zp.y);
	}
	
	@Override
	protected int getOffsetU() {
		return getOffsetX();
	}
	
	@Override
	protected int getOffsetV() {
		return getOffsetY();
	}
	
	@Override
	public ZonePoint convert(CellPoint cp) {
		return convertCP(cp.x, cp.y);
	}

	@Override
	protected OffsetTranslator getOffsetTranslator() {
		return OFFSET_TRANSLATOR;
	}
}

