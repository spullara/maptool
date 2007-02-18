package net.rptools.maptool.model;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.walker.ZoneWalker;
import net.rptools.maptool.client.walker.astar.AStarVertHexEuclideanWalker;

public class HexGridVertical extends HexGrid {
	
	public HexGridVertical() {
		super();
	}
	
	@Override
	protected void initFacingAngles() {
		facingAngles = new int[] {-150, -90, -30, 30, 90, 150};
	}
	
	@Override
	public BufferedImage getCellHighlight() {
		return pathHighlight;
	}
	
	@Override
	public double getCellHeight() {
		return minorRadius*2;
	}
	
	@Override
	public double getCellWidth() {
		return edgeLength*2; // edgeLength is edgeProjection*2;
	}
	
	@Override
	protected Dimension setCellOffset() {
		return new Dimension((int)-edgeLength, -minorRadius);
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

}

