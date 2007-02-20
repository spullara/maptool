package net.rptools.maptool.model;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.walker.ZoneWalker;
import net.rptools.maptool.client.walker.astar.AStarHorizHexEuclideanWalker;

public class HexGridHorizontal extends HexGrid {

	public HexGridHorizontal() {
		super();
	}
	
	@Override
	protected void initFacingAngles() {
		facingAngles = new int[] {-120, -60, 0, 60, 120, 180};
	}
	
	@Override
	public BufferedImage getCellHighlight() {
		// rotate the default path highlight 90 degrees
		AffineTransform at = new AffineTransform();
		at.rotate(Math.toRadians(90.0),pathHighlight.getHeight()/2,pathHighlight.getHeight()/2);
		
		AffineTransformOp atOp = new AffineTransformOp(at,AffineTransformOp.TYPE_BILINEAR );
				
		return atOp.filter(pathHighlight, null);
	}
	
	@Override
	public double getCellHeight() {
		return edgeLength + edgeProjection*2; // edgeLength is edgeProjection*2;
	}
	
	@Override
	public double getCellWidth() {
		return minorRadius*2; 
	}
	
	@Override
	public ZoneWalker createZoneWalker() {
		return new AStarHorizHexEuclideanWalker(getZone());
	}
	
	@Override
	protected Dimension setCellOffset() {
		return new Dimension(-minorRadius, (int)-edgeLength);
	}
	
	@Override
	protected void orientHex(GeneralPath hex) {
		// flip the half-hex over y = x
		AffineTransform at = new AffineTransform();
		at.rotate(Math.toRadians(90.0));
		at.scale(1, -1);
		hex.transform(at);
	}
	
	@Override
	protected void setGridDrawTranslation(Graphics2D g, double U, double V) {
		g.translate(V, U);
	}

	@Override
	protected double getRendererSizeV(ZoneRenderer renderer) {
		return renderer.getSize().getWidth();
	}
	
	@Override
	protected double getRendererSizeU(ZoneRenderer renderer) {
		return renderer.getSize().getHeight();
	}
	
	@Override
	protected int getOffV(ZoneRenderer renderer) {
		return (int)(renderer.getViewOffsetX() + getOffsetX()*renderer.getScale());
	}
	
	@Override
	protected int getOffU(ZoneRenderer renderer) {
		return (int)(renderer.getViewOffsetY() + getOffsetY()*renderer.getScale());
	}
	
	@Override
	public CellPoint convert(ZonePoint zp) {
		CellPoint cp = convertZP(zp.y, zp.x);
		return new CellPoint(cp.y, cp.x);
	}
	
	@Override
	protected int getOffsetU() {
		return getOffsetY();
	}
	
	@Override
	protected int getOffsetV() {
		return getOffsetX();
	}
	
	@Override
	public ZonePoint convert(CellPoint cp) {
		ZonePoint zp = convertCP(cp.y, cp.x);
		return new ZonePoint(zp.y, zp.x);
	}
	
}

