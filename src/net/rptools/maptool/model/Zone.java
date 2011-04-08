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

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.PlayerView;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.InitiativeList.TokenInitiative;
import net.rptools.maptool.model.drawing.DrawableColorPaint;
import net.rptools.maptool.model.drawing.DrawablePaint;
import net.rptools.maptool.model.drawing.DrawableTexturePaint;
import net.rptools.maptool.model.drawing.DrawnElement;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.util.StringUtil;

import org.apache.log4j.Logger;

/**
 * This object represents the maps that will appear for placement of {@link Token}s.
 * <p>
 * Note: When adding new fields to this class, make sure to add functionality to the constructor, {@link #imported()},
 * {@link #optimize()}, and {@link #readResolve()} to ensure they are properly initialized for maximum compatibility.
 */
public class Zone extends BaseModel {
	private static final Logger log = Logger.getLogger(Zone.class);

	public enum VisionType {
		OFF, DAY, NIGHT
	}

	public enum Event {
		TOKEN_ADDED, TOKEN_REMOVED, TOKEN_CHANGED, GRID_CHANGED, DRAWABLE_ADDED, DRAWABLE_REMOVED, FOG_CHANGED, LABEL_ADDED, LABEL_REMOVED, LABEL_CHANGED, TOPOLOGY_CHANGED, INITIATIVE_LIST_CHANGED, BOARD_CHANGED
	}

	public enum Layer {
		TOKEN("Token"), GM("Hidden"), OBJECT("Object"), BACKGROUND("Background");

		private String displayName;

		private Layer(String displayName) {
			this.displayName = displayName;
		}

		@Override
		public String toString() {
			return displayName;
		}

		// A simple interface to allow layers to be turned on/off
		private boolean drawEnabled = true;

		public boolean isEnabled() {
			return drawEnabled;
		}

		public void setEnabled(boolean enabled) {
			drawEnabled = enabled;
		}
	}

	public static final int DEFAULT_TOKEN_VISION_DISTANCE = 250; // In units
	public static final int DEFAULT_PIXELS_CELL = 50;
	public static final int DEFAULT_UNITS_PER_CELL = 5;

	public static final DrawablePaint DEFAULT_FOG = new DrawableColorPaint(Color.black);

	// The zones should be ordered.  We could have the server assign each zone
	// an incrementing number as new zones are created, but that would take a lot
	// more elegance than we really need.  Instead, let's just keep track of the
	// time when it was created.  This should give us sufficient granularity, because
	// seriously -- what's the likelihood of two GMs separately creating a new zone at exactly
	// the same millisecond since the epoch?
	private long creationTime = System.currentTimeMillis();

	private GUID id = new GUID(); // Ideally would be 'final', but that complicates imported()

	private Grid grid;
	private int gridColor = Color.black.getRGB();
	private float imageScaleX = 1;
	private float imageScaleY = 1;

	private int tokenVisionDistance = DEFAULT_TOKEN_VISION_DISTANCE;

	private int unitsPerCell = DEFAULT_UNITS_PER_CELL;

	private List<DrawnElement> drawables = new LinkedList<DrawnElement>();
	private List<DrawnElement> gmDrawables = new LinkedList<DrawnElement>();
	private List<DrawnElement> objectDrawables = new LinkedList<DrawnElement>();
	private List<DrawnElement> backgroundDrawables = new LinkedList<DrawnElement>();

	private final Map<GUID, Label> labels = new LinkedHashMap<GUID, Label>();
	private final Map<GUID, Token> tokenMap = new HashMap<GUID, Token>();
	private Map<GUID, ExposedAreaMetaData> exposedAreaMeta = new HashMap<GUID, ExposedAreaMetaData>();
	private final List<Token> tokenOrderedList = new LinkedList<Token>();

	private InitiativeList initiativeList = new InitiativeList(this);

	private Area exposedArea = new Area();
	private boolean hasFog;
	private DrawablePaint fogPaint;

	private Area topology = new Area();

	// The 'board' layer, at the very bottom of the layer stack.
	// Itself has two sub-layers:
	//   The top one is an optional texture, typically a pre-drawn map.
	//   The bottom one is either an infinitely tiling texture or a color.
	private DrawablePaint backgroundPaint;
	private MD5Key mapAsset;
	private Point boardPosition = new Point(0, 0);
	private boolean drawBoard = true;
	private boolean boardChanged = false;

	private String name;
	private boolean isVisible;

	private VisionType visionType = VisionType.OFF;

	// These are transitionary properties, very soon the width and height won't matter
	private int height;
	private int width;

	private transient HashMap<String, Integer> tokenNumberCache;

	/**
	 * Note: When adding new fields to this class, make sure to update all constructors, {@link #imported()},
	 * {@link #readResolve()}, and potentially {@link #optimize()}.
	 */
	public Zone() {
		// TODO: Was this needed?
		//setGrid(new SquareGrid());
	}

	public void setBackgroundPaint(DrawablePaint paint) {
		backgroundPaint = paint;
	}

	public void setMapAsset(MD5Key id) {
		mapAsset = id;
		boardChanged = true;
	}

	public void setTokenVisionDistance(int units) {
		tokenVisionDistance = units;
	}

	public int getTokenVisionDistance() {
		return tokenVisionDistance;
	}

	public VisionType getVisionType() {
		return visionType;
	}

	public void setVisionType(VisionType visionType) {
		this.visionType = visionType;
	}

	/**
	 * Returns the distance in map pixels at a 1:1 zoom
	 */
	public int getTokenVisionInPixels() {
		if (tokenVisionDistance == 0) {
			// TODO: This is here to provide transition between pre 1.3b19 an 1.3b19.  Remove later
			tokenVisionDistance = DEFAULT_TOKEN_VISION_DISTANCE;
		}
		return (tokenVisionDistance * grid.getSize() / getUnitsPerCell());
	}

	public void setFogPaint(DrawablePaint paint) {
		fogPaint = paint;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MD5Key getMapAssetId() {
		return mapAsset;
	}

	public DrawablePaint getBackgroundPaint() {
		return backgroundPaint;
	}

	public DrawablePaint getFogPaint() {
		return fogPaint != null ? fogPaint : DEFAULT_FOG;
	}

	/**
	 * Note: When adding new fields to this class, make sure to update all constructors, {@link #imported()},
	 * {@link #readResolve()}, and potentially {@link #optimize()}.
	 */
	public Zone(Zone zone) {
		/*
		 * JFJ 2010-10-27 Don't forget that since there are new zones AND new tokens created here from the old one being
		 * passed in, if you have any data that needs to transfer over, you will need to manually copy it as is done
		 * below for various items.
		 */
		backgroundPaint = zone.backgroundPaint;
		mapAsset = zone.mapAsset;
		fogPaint = zone.fogPaint;
		visionType = zone.visionType;

		setName(zone.getName());

		try {
			grid = (Grid) zone.grid.clone();
			grid.setZone(this);
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace();
		}

		unitsPerCell = zone.unitsPerCell;
		tokenVisionDistance = zone.tokenVisionDistance;

		imageScaleX = zone.imageScaleX;
		imageScaleY = zone.imageScaleY;

		if (zone.drawables != null) {
			drawables = new LinkedList<DrawnElement>();
			drawables.addAll(zone.drawables);
		}

		if (zone.objectDrawables != null) {
			objectDrawables = new LinkedList<DrawnElement>();
			objectDrawables.addAll(zone.objectDrawables);
		}

		if (zone.backgroundDrawables != null) {
			backgroundDrawables = new LinkedList<DrawnElement>();
			backgroundDrawables.addAll(zone.backgroundDrawables);
		}

		if (zone.gmDrawables != null) {
			gmDrawables = new LinkedList<DrawnElement>();
			gmDrawables.addAll(zone.gmDrawables);
		}

		if (zone.labels != null) {
			Iterator<GUID> i = zone.labels.keySet().iterator();
			while (i.hasNext()) {
				this.putLabel(new Label(zone.labels.get(i.next())));
			}
		}

		// Copy the tokens, save a map between old and new for the initiative list.
		if (zone.initiativeList == null)
			zone.initiativeList = new InitiativeList(zone);
		Object[][] saveInitiative = new Object[zone.initiativeList.getSize()][2];
		initiativeList.setZone(null);

		if (zone.tokenMap != null) {
			Iterator<GUID> i = zone.tokenMap.keySet().iterator();
			while (i.hasNext()) {
				Token old = zone.tokenMap.get(i.next());
				Token token = new Token(old);
//				token.setZoneId(getId());
//				ExposedAreaMetaData oldMeta = zone.getExposedAreaMetaData(old.getExposedAreaGUID());
//				if (oldMeta != null) {
//					exposedAreaMeta.put(old.getExposedAreaGUID(), new ExposedAreaMetaData(oldMeta.getExposedAreaHistory()));
//				}

				this.putToken(token);
				List<Integer> list = zone.initiativeList.indexOf(old);
				for (Integer integer : list) {
					int index = integer.intValue();
					saveInitiative[index][0] = token;
					saveInitiative[index][1] = zone.initiativeList.getTokenInitiative(index);
				}
			}
		}

		// Set the initiative list using the newly create tokens.
		if (saveInitiative.length > 0) {
			for (int i = 0; i < saveInitiative.length; i++) {
				initiativeList.insertToken(i, (Token) saveInitiative[i][0]);
				TokenInitiative ti = initiativeList.getTokenInitiative(i);
				TokenInitiative oldti = (TokenInitiative) saveInitiative[i][1];
				ti.setHolding(oldti.isHolding());
				ti.setState(oldti.getState());
			}
		}
		initiativeList.setZone(this);
		initiativeList.setCurrent(zone.initiativeList.getCurrent());
		initiativeList.setRound(zone.initiativeList.getRound());
		initiativeList.setHideNPC(zone.initiativeList.isHideNPC());

		boardPosition = (Point) zone.boardPosition.clone();
		exposedArea = (Area) zone.exposedArea.clone();
		exposedAreaMeta = zone.exposedAreaMeta;
		topology = (Area) zone.topology.clone();
		isVisible = zone.isVisible;
		hasFog = zone.hasFog;
	}

	public GUID getId() {
		return id;
	}

	/**
	 * Should be invoked only when a Zone has been imported from an external source and needs to be cleaned up before
	 * being used. Currently this cleanup consists of allocating a new GUID, setting the creation time to `now', and
	 * resetting the initiative list (setting the related zone and clearing the model).
	 */
	public void imported() {
		id = new GUID();
		creationTime = System.currentTimeMillis();
		initiativeList.setZone(this);
		initiativeList.clearModel();
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
		grid.setZone(this);
//		tokenVisionDistance = DEFAULT_TOKEN_VISION_DISTANCE * grid.getSize() / unitsPerCell;
		fireModelChangeEvent(new ModelChangeEvent(this, Event.GRID_CHANGED));
	}

	public Grid getGrid() {
		return grid;
	}

	public int getGridColor() {
		return gridColor;
	}

	public void setGridColor(int color) {
		gridColor = color;
	}

	/**
	 * Board pseudo-object. Not making full object since this will change when new layer model is created
	 */
	public boolean isBoardChanged() {
		return boardChanged;
	}

	public void setBoardChanged(boolean set) {
		boardChanged = set;
	}

	public void setBoard(Point position) {
		boardPosition.x = position.x;
		boardPosition.y = position.y;
		setBoardChanged(true);
		fireModelChangeEvent(new ModelChangeEvent(mapAsset, Event.BOARD_CHANGED));
	}

	public void setBoard(int newX, int newY) {
		boardPosition.x = newX;
		boardPosition.y = newY;
		setBoardChanged(true);
		fireModelChangeEvent(new ModelChangeEvent(mapAsset, Event.BOARD_CHANGED));
	}

	public void setBoard(Point position, MD5Key asset) {
		this.setMapAsset(asset);
		this.setBoard(position);
	}

	public int getBoardX() {
		return boardPosition.x;
	}

	public int getBoardY() {
		return boardPosition.y;
	}

	public boolean drawBoard() {
		return drawBoard;
	}

	public void setDrawBoard(boolean draw) {
		drawBoard = draw;
	}

	//
	// Misc Scale methods
	//

	public float getImageScaleX() {
		return imageScaleX;
	}

	public void setImageScaleX(float imageScaleX) {
		this.imageScaleX = imageScaleX;
	}

	public float getImageScaleY() {
		return imageScaleY;
	}

	public void setImageScaleY(float imageScaleY) {
		this.imageScaleY = imageScaleY;
	}

	//
	// Fog
	//
	public boolean hasFog() {
		return hasFog;
	}

	public void setHasFog(boolean flag) {
		hasFog = flag;
		fireModelChangeEvent(new ModelChangeEvent(this, Event.FOG_CHANGED));
	}

	/**
	 * Determines whether the given ZonePoint is visible when using the specified PlayerView. This currently includes
	 * checking the following criteria:
	 * <ol>
	 * <li>If fog is turned off, return true.
	 * <li>If the view is a GM view, return true.
	 * <li>If Vision is <b>Day</b> or <b>Night</b> and we're not using IndividualFOW, return intersection of point with
	 * exposedArea.
	 * <li>If Vision is off or we ARE using IndividualFOW, combine exposed areas of all owned tokens and return
	 * intersection of point with the combined area.
	 * </ol>
	 * 
	 * @param point
	 * @param view
	 * @return
	 */
	public boolean isPointVisible(ZonePoint point, PlayerView view) {
		if (!hasFog() || view.isGMView()) {
			return true;
		}
		if (MapTool.getServerPolicy().isUseIndividualFOW() && getVisionType() != VisionType.OFF) {
			Area combined = new Area();
			List<Token> toks = (view.getTokens() != null && !view.getTokens().isEmpty()) ? view.getTokens() : this.getTokens();
			// Should this use FindTokenFunctions.OwnedFilter and zone.getTokenList()?
			for (Token tok : toks) {
				if (!AppUtil.playerOwns(tok)) {
					continue;
				}
				if (exposedAreaMeta.containsKey(tok.getExposedAreaGUID())) {
					combined.add(exposedAreaMeta.get(tok.getExposedAreaGUID()).getExposedAreaHistory());
				}
			}
			combined.add(exposedArea);
			return combined.contains(point.x, point.y);
		} else {
			return exposedArea.contains(point.x, point.y);
		}
	}

	public boolean isEmpty() {
		// @formatter:off
		return	(drawables == null || drawables.isEmpty()) &&
					(gmDrawables == null || gmDrawables.isEmpty()) &&
					(objectDrawables == null || objectDrawables.isEmpty()) &&
					(backgroundDrawables == null || backgroundDrawables.isEmpty()) &&
					(tokenOrderedList == null || tokenOrderedList.isEmpty()) &&
					(labels == null || labels.isEmpty());
		// @formatter:on
	}

	/**
	 * Determines if the passed non-<code>null</code> parameter represents a visible token. The current criteria works
	 * like this:
	 * <ol>
	 * <li>If the <i>Visible to Players</i> flag is off, return <code>false</code>.
	 * <li>If the fog-of-war for the map is off, return <code>true</code>.
	 * <li>If the player does not own the token and it's <i>Visible to Owner Only</i>, return <code>false</code>.
	 * <li>If the token's bounds intersect the exposed area for this map, return <code>true</code>.
	 * </ol>
	 * 
	 * @param token
	 * @return
	 */
	public boolean isTokenVisible(Token token) {
		if (token == null) {
			return false;
		}
		// Base case, nothing is visible
		if (!token.isVisible()) {
			return false;
		}
		// Base case, everything is visible
		if (!hasFog()) {
			return true;
		}
		if (token.isVisibleOnlyToOwner() && !AppUtil.playerOwns(token)) {
			return false;
		}
		// Token is visible, and there is fog
		int x = token.getX();
		int y = token.getY();
		Rectangle tokenSize = token.getBounds(this);
		Area combined = new Area(exposedArea);
		PlayerView view = MapTool.getFrame().getZoneRenderer(this).getPlayerView();
		if (MapTool.getServerPolicy().isUseIndividualFOW() && getVisionType() != VisionType.OFF) {
			List<Token> toks = (view.getTokens() != null && !view.getTokens().isEmpty()) ? view.getTokens() : this.getTokens();
			// Should this use FindTokenFunctions.OwnedFilter and zone.getTokenList()?
			for (Token tok : toks) {
				if (!AppUtil.playerOwns(tok)) {
					continue;
				}
				if (exposedAreaMeta.containsKey(tok.getExposedAreaGUID())) {
					combined.add(exposedAreaMeta.get(tok.getExposedAreaGUID()).getExposedAreaHistory());
				}
			}
		}
		return combined.intersects(x, y, tokenSize.width, tokenSize.height);
	}

	public void clearTopology() {
		topology = new Area();
		fireModelChangeEvent(new ModelChangeEvent(this, Event.TOPOLOGY_CHANGED));
	}

	public void addTopology(Area area) {
		topology.add(area);
		fireModelChangeEvent(new ModelChangeEvent(this, Event.TOPOLOGY_CHANGED));
	}

	public void removeTopology(Area area) {
		topology.subtract(area);
		fireModelChangeEvent(new ModelChangeEvent(this, Event.TOPOLOGY_CHANGED));
	}

	public Area getTopology() {
		return topology;
	}

	public void clearExposedArea() {
		exposedArea = new Area();
		// There used to be a foreach loop here that iterated over getTokens() and called .clear() -- why?!
		exposedAreaMeta.clear();
		fireModelChangeEvent(new ModelChangeEvent(this, Event.FOG_CHANGED));
	}

	public void exposeArea(Area area, Token token) {
		if (area == null) {
			return;
		}
		if (token != null) {
			if ((MapTool.getServerPolicy().isUseIndividualFOW() && AppUtil.playerOwns(token)) || MapTool.isPersonalServer()) {
				ExposedAreaMetaData meta = exposedAreaMeta.get(token.getExposedAreaGUID());
				if (meta == null)
					meta = new ExposedAreaMetaData();
				meta.addToExposedAreaHistory(area);
//				meta.addToExposedAreaHistory(MapTool.getFrame().getZoneRenderer(getId()).getZoneView().getVisibleArea(token));
				exposedAreaMeta.put(token.getExposedAreaGUID(), meta);
				MapTool.getFrame().getZoneRenderer(this.getId()).getZoneView().flush();
				putToken(token);
			}
		}
		exposedArea.add(area);
		fireModelChangeEvent(new ModelChangeEvent(this, Event.FOG_CHANGED));
	}

	/**
	 * Retrieves the selected tokens and adds the passed in area to their exposed area. (Why are we passing in a
	 * <code>Set&lt;GUID></code> when <code>Set&lt;Token></code> would be much more efficient?)
	 * 
	 * @param area
	 * @param selectedToks
	 */
	public void exposeArea(Area area, Set<GUID> selectedToks) {
		if (area == null) {
			return;
		}
		if (getVisionType() == VisionType.OFF) {
			exposedArea.add(area);
		}
		if (selectedToks != null && !selectedToks.isEmpty() && MapTool.getServerPolicy().isUseIndividualFOW()) {
			List<Token> allToks = new ArrayList<Token>();

			for (GUID guid : selectedToks) {
				allToks.add(getToken(guid));
			}
			for (Token tok : allToks) {
				if (!AppUtil.playerOwns(tok)) {
					continue;
				}
				if (!tok.getHasSight()) {
					continue;
				}

				ExposedAreaMetaData meta = exposedAreaMeta.get(tok.getExposedAreaGUID());
				if (meta == null)
					meta = new ExposedAreaMetaData();
				meta.addToExposedAreaHistory(area);
				exposedAreaMeta.put(tok.getExposedAreaGUID(), meta);
				MapTool.getFrame().getZoneRenderer(this.getId()).getZoneView().flush();
			}
		} else {
			exposedArea.add(area);
		}
		fireModelChangeEvent(new ModelChangeEvent(this, Event.FOG_CHANGED));
	}

	/**
	 * Modifies the global exposed area (GEA) or token exposed by resetting it and then setting it to the contents of
	 * the passed in Area and firing a ModelChangeEvent.
	 * 
	 * @param area
	 * @param selectedToks
	 */

	public void setFogArea(Area area, Set<GUID> selectedToks) {
		if (area == null) {
			return;
		}
		if (selectedToks != null && !selectedToks.isEmpty()) {
			List<Token> allToks = new ArrayList<Token>();

			for (GUID guid : selectedToks) {
				allToks.add(getToken(guid));
			}
			for (Token tok : allToks) {
				if (!tok.getHasSight()) {
					continue;
				}
				ExposedAreaMetaData meta = exposedAreaMeta.get(tok.getExposedAreaGUID());
				if (meta == null)
					meta = new ExposedAreaMetaData();
				meta.clearExposedAreaHistory();
				meta.addToExposedAreaHistory(area);
				exposedAreaMeta.put(tok.getExposedAreaGUID(), meta);
				MapTool.getFrame().getZoneRenderer(this.getId()).getZoneView().flush(tok);
				putToken(tok);
			}
		} else {
			exposedArea.reset();
			exposedArea.add(area);
		}
		fireModelChangeEvent(new ModelChangeEvent(this, Event.FOG_CHANGED));
	}

	public void hideArea(Area area, Set<GUID> selectedToks) {
		if (area == null) {
			return;
		}
		if (getVisionType() == VisionType.OFF) {
			exposedArea.subtract(area);
		}
		if (selectedToks != null && !selectedToks.isEmpty() && MapTool.getServerPolicy().isUseIndividualFOW()) {
			List<Token> allToks = new ArrayList<Token>();

			for (GUID guid : selectedToks) {
				allToks.add(getToken(guid));
			}
			for (Token tok : allToks) {
				if (!AppUtil.playerOwns(tok)) {
					continue;
				}
				if (!tok.getHasSight()) {
					continue;
				}
				ExposedAreaMetaData meta = exposedAreaMeta.get(tok.getExposedAreaGUID());
				if (meta == null)
					meta = new ExposedAreaMetaData();
				meta.removeExposedAreaHistory(area);
				exposedAreaMeta.put(tok.getExposedAreaGUID(), meta);
				MapTool.getFrame().getZoneRenderer(this.getId()).getZoneView().flush(tok);
				putToken(tok);
			}
		} else {
			exposedArea.subtract(area);
		}
		fireModelChangeEvent(new ModelChangeEvent(this, Event.FOG_CHANGED));
	}

	public long getCreationTime() {
		return creationTime;
	}

	// FIXME This needs to take the current grid type into account, such as square or hex
	public ZonePoint getNearestVertex(ZonePoint point) {
		int gridx = (int) Math.round((point.x - grid.getOffsetX()) / grid.getCellWidth());
		int gridy = (int) Math.round((point.y - grid.getOffsetY()) / grid.getCellHeight());

//    	System.out.println("gx:" + gridx + " zx:" + (gridx * grid.getCellWidth() + grid.getOffsetX()));
		return new ZonePoint((int) (gridx * grid.getCellWidth() + grid.getOffsetX()), (int) (gridy * grid.getCellHeight() + grid.getOffsetY()));
	}

	public Area getExposedArea(PlayerView view) {
		Area combined = new Area(getExposedArea());

		//if ((MapTool.getServerPolicy().isUseIndividualFOW() && view.isGMView())
		//		|| !MapTool.getServerPolicy().isUseIndividualFOW()) {
		//	combined = getExposedArea();
		//	return combined;
		//}
		List<Token> toks = view.getTokens();
		if (toks == null || toks.isEmpty()) {
			return combined;
//			toks = getTokens();
		}
		for (Token tok : toks) {
			if (!tok.getHasSight() || !AppUtil.playerOwns(tok)) {
				continue;
			}
			ExposedAreaMetaData meta = exposedAreaMeta.get(tok.getExposedAreaGUID());
			if (meta != null) {
				combined.add(meta.getExposedAreaHistory());
			}
		}
		return combined;
	}

	/**
	 * This is the Global Exposed Area (GEA) discussed so much on the dev-team mailing list. :)
	 * 
	 * @return Area object representing exposed fog area visible to all tokens
	 */
	public Area getExposedArea() {
		return exposedArea;
	}

	public int getUnitsPerCell() {
		return Math.max(unitsPerCell, 1);
	}

	public void setUnitsPerCell(int unitsPerCell) {
		this.unitsPerCell = unitsPerCell;
	}

	public int getLargestZOrder() {
		return tokenOrderedList.size() > 0 ? tokenOrderedList.get(tokenOrderedList.size() - 1).getZOrder() : 0;
	}

	public int getSmallestZOrder() {
		return tokenOrderedList.size() > 0 ? tokenOrderedList.get(0).getZOrder() : 0;
	}

	///////////////////////////////////////////////////////////////////////////
	// labels
	///////////////////////////////////////////////////////////////////////////
	public void putLabel(Label label) {
		boolean newLabel = labels.containsKey(label.getId());
		labels.put(label.getId(), label);

		if (newLabel) {
			fireModelChangeEvent(new ModelChangeEvent(this, Event.LABEL_ADDED, label));
		} else {
			fireModelChangeEvent(new ModelChangeEvent(this, Event.LABEL_CHANGED, label));
		}
	}

	public List<Label> getLabels() {
		return new ArrayList<Label>(this.labels.values());
	}

	public void removeLabel(GUID labelId) {
		Label label = labels.remove(labelId);
		if (label != null) {
			fireModelChangeEvent(new ModelChangeEvent(this, Event.LABEL_REMOVED, label));
		}
	}

	///////////////////////////////////////////////////////////////////////////
	// drawables
	///////////////////////////////////////////////////////////////////////////

	public void addDrawable(DrawnElement drawnElement) {
		switch (drawnElement.getDrawable().getLayer()) {
		case OBJECT:
			objectDrawables.add(drawnElement);
			break;
		case BACKGROUND:
			backgroundDrawables.add(drawnElement);
			break;
		case GM:
			gmDrawables.add(drawnElement);
			break;
		default:
			drawables.add(drawnElement);
		}
		fireModelChangeEvent(new ModelChangeEvent(this, Event.DRAWABLE_ADDED, drawnElement));
	}

	public List<DrawnElement> getDrawnElements() {
		return getDrawnElements(Zone.Layer.TOKEN);
	}

	public List<DrawnElement> getObjectDrawnElements() {
		return getDrawnElements(Zone.Layer.OBJECT);
	}

	public List<DrawnElement> getGMDrawnElements() {
		return getDrawnElements(Zone.Layer.GM);
	}

	public List<DrawnElement> getBackgroundDrawnElements() {
		return getDrawnElements(Zone.Layer.BACKGROUND);
	}

	public List<DrawnElement> getDrawnElements(Zone.Layer layer) {
		switch (layer) {
		case OBJECT:
			return objectDrawables;
		case GM:
			return gmDrawables;
		case BACKGROUND:
			return backgroundDrawables;
		default:
			return drawables;
		}
	}

	public void removeDrawable(GUID drawableId) {
		// Since we don't know anything about the drawable, look through all the layers
		removeDrawable(drawables, drawableId);
		removeDrawable(backgroundDrawables, drawableId);
		removeDrawable(objectDrawables, drawableId);
		removeDrawable(gmDrawables, drawableId);
	}

	private void removeDrawable(List<DrawnElement> drawableList, GUID drawableId) {
		ListIterator<DrawnElement> i = drawableList.listIterator();
		while (i.hasNext()) {
			DrawnElement drawable = i.next();
			if (drawable.getDrawable().getId().equals(drawableId)) {
				i.remove();

				fireModelChangeEvent(new ModelChangeEvent(this, Event.DRAWABLE_REMOVED, drawable));
				return;
			}
		}
	}

	///////////////////////////////////////////////////////////////////////////
	// tokens
	///////////////////////////////////////////////////////////////////////////
	/**
	 * Adds the specified Token to this zone, accounting for updating the ordered list of tokens as well as firing the
	 * appropriate <code>ModelChangeEvent</code> (either <code>Event.TOKEN_ADDED</code> or
	 * <code>Event.TOKEN_CHANGED</code>).
	 * 
	 * @param token
	 *            the Token to be added to this zone
	 */
	public void putToken(Token token) {
		boolean newToken = !tokenMap.containsKey(token.getId());

		tokenMap.put(token.getId(), token);

		// LATER: optimize this
		tokenOrderedList.remove(token);
		tokenOrderedList.add(token);

		Collections.sort(tokenOrderedList, TOKEN_Z_ORDER_COMPARATOR);

		if (newToken) {
			fireModelChangeEvent(new ModelChangeEvent(this, Event.TOKEN_ADDED, token));
		} else {
			fireModelChangeEvent(new ModelChangeEvent(this, Event.TOKEN_CHANGED, token));
		}
	}

	/**
	 * Same as {@link #putToken(List)} but optimizes map updates by accepting a list of Tokens. Note that this method
	 * fires a single <code>ModelChangeEvent</code> using <code>Event.TOKEN_ADDED</code> and passes the list of added
	 * tokens as a parameter. Ditto for <code>Event.TOKEN_CHANGED</code>.
	 * <p>
	 * <b>Note that this code is NOT ready for prime time</b> and shouldn't be used until all references to code that
	 * handle token {add,change} events have been updated to accept an array as a parameter instead of a single token
	 * reference.
	 * <p>
	 * Marked as "deprecated" to ensure no one calls it accidentally. At least until the updates are done.
	 * 
	 * @param tokens
	 *            List of Tokens to be added to this zone
	 */
	@Deprecated
	public void putTokens(List<Token> tokens) {
		// Create a couple of booleans to represent whether tokens were added and/or changed
		List<Token> addedTokens = new LinkedList<Token>(tokens);
		addedTokens.removeAll(tokenMap.values());

		List<Token> changedTokens = new LinkedList<Token>(tokens);
		changedTokens.retainAll(tokenMap.values());

		for (Token t : tokens) {
			tokenMap.put(t.getId(), t);
		}
		tokenOrderedList.removeAll(tokens);
		tokenOrderedList.addAll(tokens);

		Collections.sort(tokenOrderedList, TOKEN_Z_ORDER_COMPARATOR);

		if (!addedTokens.isEmpty())
			fireModelChangeEvent(new ModelChangeEvent(this, Event.TOKEN_ADDED, addedTokens));
		if (!changedTokens.isEmpty())
			fireModelChangeEvent(new ModelChangeEvent(this, Event.TOKEN_CHANGED, changedTokens));
	}

	public void removeToken(GUID id) {
		Token token = tokenMap.remove(id);
		if (token != null) {
			tokenOrderedList.remove(token);
			fireModelChangeEvent(new ModelChangeEvent(this, Event.TOKEN_REMOVED, token));
		}
	}

	public Token getToken(GUID id) {
		return tokenMap.get(id);
	}

	/**
	 * Returns the first token with a given name. The name is matched case-insensitively.
	 */
	public Token getTokenByName(String name) {
		for (Token token : getAllTokens()) {
			if (StringUtil.isEmpty(token.getName())) {
				continue;
			}
			if (token.getName().equalsIgnoreCase(name)) {
				return token;
			}
		}
		return null;
	}

	/**
	 * Looks for the given identifier as a token name, token GM name, or GUID, in that order.
	 * 
	 * @param identifier
	 * @return token that matches the identifier or <code>null</code>
	 */
	public Token resolveToken(String identifier) {
		Token token = getTokenByName(identifier);

		if (token == null)
			token = getTokenByGMName(identifier);

		if (token == null) {
			try {
				token = getToken(GUID.valueOf(identifier));
			} catch (Exception e) {
				// indication of not a GUID, OK to ignore
			}
		}
		return token;
	}

	/**
	 * Returns the first token with a given GM name. The name is matched case-insensitively.
	 */
	public Token getTokenByGMName(String name) {
		for (Token token : getAllTokens()) {
			if (StringUtil.isEmpty(token.getGMName())) {
				continue;
			}
			if (token.getGMName().equalsIgnoreCase(name)) {
				return token;
			}
		}
		return null;
	}

	public List<DrawnElement> getAllDrawnElements() {
		List<DrawnElement> list = new ArrayList<DrawnElement>();

		list.addAll(getDrawnElements());
		list.addAll(getObjectDrawnElements());
		list.addAll(getBackgroundDrawnElements());
		list.addAll(getGMDrawnElements());

		return list;
	}

	public int getTokenCount() {
		return tokenOrderedList.size();
	}

	public List<Token> getAllTokens() {
		return Collections.unmodifiableList(new ArrayList<Token>(tokenOrderedList));
	}

	public Set<MD5Key> getAllAssetIds() {
		Set<MD5Key> idSet = new HashSet<MD5Key>();

		// Zone
		if (getBackgroundPaint() instanceof DrawableTexturePaint) {
			idSet.add(((DrawableTexturePaint) getBackgroundPaint()).getAssetId());
		}
		idSet.add(getMapAssetId());
		if (getFogPaint() instanceof DrawableTexturePaint) {
			idSet.add(((DrawableTexturePaint) getFogPaint()).getAssetId());
		}

		// Tokens
		for (Token token : getAllTokens()) {
			idSet.addAll(token.getAllImageAssets());
		}

		// Painted textures
		for (DrawnElement drawn : getAllDrawnElements()) {
			DrawablePaint paint = drawn.getPen().getPaint();
			if (paint instanceof DrawableTexturePaint) {
				idSet.add(((DrawableTexturePaint) paint).getAssetId());
			}
			paint = drawn.getPen().getBackgroundPaint();
			if (paint instanceof DrawableTexturePaint) {
				idSet.add(((DrawableTexturePaint) paint).getAssetId());
			}
		}
		// It's easier to just remove null at the end than to do a is-null check on each asset
		idSet.remove(null);

		return idSet;
	}

	public List<Token> getTokensFiltered(Filter filter) {
		ArrayList<Token> copy = new ArrayList<Token>(getTokenCount());

		for (Token token : tokenOrderedList) {
			if (filter.matchToken(token)) {
				copy.add(token);
			}
		}
		return Collections.unmodifiableList(copy);
	}

	/**
	 * This is the list of non-stamp tokens, both pc and npc
	 */
	public List<Token> getTokens() {
		return getTokensFiltered(new Filter() {
			public boolean matchToken(Token t) {
				return !t.isStamp();
			}
		});
	}

	public List<Token> getStampTokens() {
		return getTokensFiltered(new Filter() {
			public boolean matchToken(Token t) {
				return t.isObjectStamp();
			}
		});
	}

	public List<Token> getPlayerTokens() {
		return getTokensFiltered(new Filter() {
			public boolean matchToken(Token t) {
				return t.getType() == Token.Type.PC;
			}
		});
	}

	public List<Token> getBackgroundStamps() {
		return getTokensFiltered(new Filter() {
			public boolean matchToken(Token t) {
				return t.isBackgroundStamp();
			}
		});
	}

	public List<Token> getGMStamps() {
		return getTokensFiltered(new Filter() {
			public boolean matchToken(Token t) {
				return t.isGMStamp();
			}
		});
	}

	public int findFreeNumber(String tokenBaseName, boolean checkDm) {
		if (tokenNumberCache == null) {
			tokenNumberCache = new HashMap<String, Integer>();
		}
		Integer _lastUsed = tokenNumberCache.get(tokenBaseName);

		int lastUsed;

		if (_lastUsed == null) {
			lastUsed = 0;
		} else {
			lastUsed = _lastUsed;
		}
		boolean repeat = true;
		while (repeat) {
			lastUsed++;
			repeat = false;
			if (checkDm) {
				Token token = getTokenByGMName(Integer.toString(lastUsed));
				if (token != null) {
					repeat = true;
				}
			}
			if (!repeat && tokenBaseName != null) {
				String name = tokenBaseName + " " + lastUsed;
				Token token = getTokenByName(name);
				if (token != null) {
					repeat = true;
				}
			}
		}
		tokenNumberCache.put(tokenBaseName, lastUsed);
		return lastUsed;
	}

	public static interface Filter {
		public boolean matchToken(Token t);
	}

	public static final Comparator<Token> TOKEN_Z_ORDER_COMPARATOR = new TokenZOrderComparator();

	public static class TokenZOrderComparator implements Comparator<Token> {
		public int compare(Token o1, Token o2) {
			int lval = o1.getZOrder();
			int rval = o2.getZOrder();

			if (lval == rval) {
				return o1.getId().compareTo(o2.getId());
			} else {
				return lval - rval;
			}
		}
	}

	/** @return Getter for initiativeList */
	public InitiativeList getInitiativeList() {
		return initiativeList;
	}

	/**
	 * @param initiativeList
	 *            Setter for the initiativeList
	 */
	public void setInitiativeList(InitiativeList initiativeList) {
		this.initiativeList = initiativeList;
		fireModelChangeEvent(new ModelChangeEvent(this, Event.INITIATIVE_LIST_CHANGED));
	}

	public void optimize() {
		log.debug("Optimizing Map " + getName());
		MapTool.getFrame().setStatusMessage(I18N.getText("Zone.status.optimizing", getName()));
		collapseDrawables();
	}

	/**
	 * Clear out any drawables that are hidden/erased. This is an optimization step that should only happen when you
	 * can't undo your changes and re-expose a drawable, typically at load.
	 */
	private void collapseDrawables() {
		collapseDrawableLayer(drawables);
		collapseDrawableLayer(gmDrawables);
		collapseDrawableLayer(objectDrawables);
		collapseDrawableLayer(backgroundDrawables);
	}

	private void collapseDrawableLayer(List<DrawnElement> layer) {
		if (layer.isEmpty()) {
			return;
		}
		Area area = new Area();
		List<DrawnElement> list = new ArrayList<DrawnElement>(layer);
		Collections.reverse(list);
		int eraserCount = 0;
		for (ListIterator<DrawnElement> drawnIter = list.listIterator(); drawnIter.hasNext();) {
			DrawnElement drawn = drawnIter.next();
			// Are we covered ourselves ?
			Area drawnArea = drawn.getDrawable().getArea();
			if (drawnArea == null) {
				continue;
			}
			// Does drawable cover area?  If not, get rid of it.
			if (drawnArea.isEmpty()) {
				drawnIter.remove();
				continue;
			}
			//    		if (GraphicsUtil.contains(area, drawnArea)) {  // Too expensive
			if (area.contains(drawnArea.getBounds())) { // Not as accurate, but faster
				drawnIter.remove();
				continue;
			}
			// Are we possibly covering something up?
			if (drawn.getPen().isEraser() && (drawn.getPen().getBackgroundMode() == Pen.MODE_SOLID)) {
				area.add(drawnArea);
				eraserCount++;
				continue;
			}
		}
		// Now use the new list
		layer.clear();
		// If the number of elements is greater than the number of erasables, keep them all.
		if (list.size() > eraserCount) {
			layer.addAll(list);
			Collections.reverse(layer);
		}
	}

	////
	// Backward compatibility
	@Override
	protected Object readResolve() {
		super.readResolve();

		// 1.3b76 -> 1.3b77
		// adding the exposed area for Individual FOW
		if (exposedAreaMeta == null) {
			exposedAreaMeta = new HashMap<GUID, ExposedAreaMetaData>();
		}
		// 1.3b70 -> 1.3b71
		// These two variables were added
		if (drawBoard == false) {
			// this should check the file version, not the value
			drawBoard = true;
		}
		if (boardPosition == null) {
			boardPosition = new Point(0, 0);
		}

		// 1.3b47 -> 1.3b48
		if (visionType == null) {
			if (getTokensFiltered(new Filter() {
				public boolean matchToken(Token token) {
					return token.hasLightSources();
				}
			}).size() > 0) {
				visionType = VisionType.NIGHT;
			} else if (topology != null && !topology.isEmpty()) {
				visionType = VisionType.DAY;

			} else {
				visionType = VisionType.OFF;
			}
		}
		// Look for the bizarre z-ordering disappearing trick
		boolean foundZero = false;
		boolean fixZOrder = false;
		for (Token token : tokenOrderedList) {
			if (token.getZOrder() == 0) {
				if (foundZero) {
					fixZOrder = true;
					break;
				}
				foundZero = true;
			}
		}
		if (fixZOrder) {
			int z = 0;
			for (Token token : tokenOrderedList) {
				token.setZOrder(z++);
			}
		}
		return this;
	}

	public Map<GUID, ExposedAreaMetaData> getExposedAreaMetaData() {
		if (exposedAreaMeta == null) {
			exposedAreaMeta = new HashMap<GUID, ExposedAreaMetaData>();
		}
		return exposedAreaMeta;
	}

	/**
	 * Find the area of this map which has been exposed to the given token GUID.
	 * 
	 * @param tokenExposedAreaGUID
	 *            token whose exposed area should be returned
	 * @return area of fog cleared away for/by this token
	 */
	public ExposedAreaMetaData getExposedAreaMetaData(GUID tokenExposedAreaGUID) {
		ExposedAreaMetaData meta = exposedAreaMeta.get(tokenExposedAreaGUID);
		if (meta != null) {
			return meta;
		}
		meta = new ExposedAreaMetaData();
		exposedAreaMeta.put(tokenExposedAreaGUID, meta);
		return meta;
	}

	public void setExposedAreaMetaData(GUID tokenExposedAreaGUID, ExposedAreaMetaData meta) {
		if (exposedAreaMeta == null) {
			exposedAreaMeta = new HashMap<GUID, ExposedAreaMetaData>();
		}
		exposedAreaMeta.put(tokenExposedAreaGUID, meta);
		fireModelChangeEvent(new ModelChangeEvent(this, Event.FOG_CHANGED));
	}
}
