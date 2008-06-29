package net.rptools.maptool.model;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.walker.ZoneWalker;
import net.rptools.maptool.client.walker.astar.AStarHorizHexEuclideanWalker;
import net.rptools.maptool.model.TokenFootprint.OffsetTranslator;

public class HexGridHorizontal extends HexGrid {

	private static final int[] FACING_ANGLES =  new int[] {-150, -120, -90, -60, -30, 0, 30, 60, 90, 120, 150, 180};
	
	private static final OffsetTranslator OFFSET_TRANSLATOR = new OffsetTranslator() {
		public void translate(CellPoint originPoint, CellPoint offsetPoint) {
			if (originPoint.y%2==1 && offsetPoint.y%2==0) {
				offsetPoint.x++;
			}
		}
	};
	
	public HexGridHorizontal() {
		super();
	}

	@Override
	public int[] getFacingAngles() {
		return FACING_ANGLES;
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
		return getURadius()*2;
	}
	
	@Override
	public double getCellWidth() {
		return getVRadius()*2; 
	}
	
	@Override
	public ZoneWalker createZoneWalker() {
		return new AStarHorizHexEuclideanWalker(getZone());
	}
	
	@Override
	protected Dimension setCellOffset() {
		return new Dimension((int)getCellOffsetV(), (int)getCellOffsetU());
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

	@Override
	protected OffsetTranslator getOffsetTranslator() {
		return OFFSET_TRANSLATOR;
	}
}

