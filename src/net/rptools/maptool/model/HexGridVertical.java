/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.model;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.walker.ZoneWalker;
import net.rptools.maptool.client.walker.astar.AStarVertHexEuclideanWalker;
import net.rptools.maptool.model.TokenFootprint.OffsetTranslator;

public class HexGridVertical extends HexGrid {

	private static final int[] ALL_ANGLES = new int[] { -150, -120, -90, -60, -30, 0, 30, 60, 90, 120, 150, 180 };
	private static int[] FACING_ANGLES; // = new int[] {-150, -120, -90, -60, -30, 0, 30, 60, 90, 120, 150, 180};
	private static List<TokenFootprint> footprintList;

	private static final OffsetTranslator OFFSET_TRANSLATOR = new OffsetTranslator() {
		public void translate(CellPoint originPoint, CellPoint offsetPoint) {
			if (Math.abs(originPoint.x) % 2 == 1 && Math.abs(offsetPoint.x) % 2 == 0) {
				offsetPoint.y++;
			}
		}
	};

	public HexGridVertical() {
		super();
		if (FACING_ANGLES == null) {
			boolean faceEdges = AppPreferences.getFaceEdge();
			boolean faceVertices = AppPreferences.getFaceVertex();
			setFacings(faceEdges, faceVertices);
		}
	}

	public HexGridVertical(boolean faceEdges, boolean faceVertices) {
		super();
		setFacings(faceEdges, faceVertices);
	}

	@Override
	public void setFacings(boolean faceEdges, boolean faceVertices) {
		if (faceEdges && faceVertices) {
			FACING_ANGLES = ALL_ANGLES;
		} else if (!faceEdges && faceVertices) {
			FACING_ANGLES = new int[] { -120, -60, 0, 60, 120, 180 };
		} else if (faceEdges && !faceVertices) {
			FACING_ANGLES = new int[] { -150, -90, -30, 30, 90, 150 };
		} else {
			FACING_ANGLES = new int[] { 90 };
		}
	}

	@Override
	public List<TokenFootprint> getFootprints() {
		if (footprintList == null) {
			try {
				footprintList = loadFootprints("net/rptools/maptool/model/hexGridVertFootprints.xml", getOffsetTranslator());
			} catch (IOException ioe) {
				MapTool.showError("Could not load Hex Grid footprints", ioe);
			}
		}
		return footprintList;
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
		return getVRadius() * 2;
	}

	@Override
	public double getCellWidth() {
		return getURadius() * 2;
	}

	@Override
	protected Dimension setCellOffset() {
		return new Dimension((int) getCellOffsetU(), (int) getCellOffsetV());
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
		return (int) (renderer.getViewOffsetY() + getOffsetY() * renderer.getScale());
	}

	@Override
	protected int getOffU(ZoneRenderer renderer) {
		return (int) (renderer.getViewOffsetX() + getOffsetX() * renderer.getScale());
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
