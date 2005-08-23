/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.client.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

import net.rptools.common.swing.SwingUtil;
import net.rptools.common.util.ImageUtil;
import net.rptools.maptool.client.CellPoint;
import net.rptools.maptool.client.ClientStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.TransferableHelper;
import net.rptools.maptool.client.ZonePoint;
import net.rptools.maptool.client.tool.ToolHelper;
import net.rptools.maptool.client.walker.NaiveWalker;
import net.rptools.maptool.client.walker.ZoneWalker;
import net.rptools.maptool.client.walker.astar.AStarEuclideanWalker;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.TokenSize;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.GraphicsUtil;
import net.rptools.maptool.util.ImageManager;


/**
 */
public abstract class ZoneRenderer extends JComponent implements DropTargetListener {
    private static final long serialVersionUID = 3832897780066104884L;

    // TODO: Perhaps make this a user defined limit
    public static final int HOVER_SIZE_THRESHOLD = 40;
    public static final int EDGE_LIMIT = 25; // can't move board past this edge
	
    public static final int MIN_GRID_SIZE = 10;
    
    protected Zone              zone;

    protected Point             viewOffset = new Point();

    protected boolean           showGrid;
    protected Color             gridColor = new Color (150, 150, 150);

    protected int               scaleIndex;
    protected static float[]    scaleArray  = new float[] { .25F, .30F, .40F, .50F, .60F, .75F, 1F, 1.25F, 1.5F, 1.75F, 2F, 3F, 4F};
    protected static int SCALE_1TO1_INDEX; // Automatically scanned for

    private static BufferedImage cellHighlightImage;
    
    private DrawableRenderer drawableRenderer = new DrawableRenderer();
    
    private List<ZoneOverlay> overlayList = new ArrayList<ZoneOverlay>();
    private Map<Rectangle, Token> tokenBoundsMap = new HashMap<Rectangle, Token>();
    private Set<GUID> selectedTokenSet = new HashSet<GUID>();

	private Map<GUID, SelectionSet> selectionSetMap = new HashMap<GUID, SelectionSet>();

    // Optimizations
    private Map<Token, BufferedImage> replacementImageMap = new HashMap<Token, BufferedImage>();
	private Map<Token, BufferedImage> resizedImageMap = new HashMap<Token, BufferedImage>();
    
    static {
		// Create scale array
    	for (int i = 0; i < scaleArray.length; i++) {
    		if (scaleArray[i] == 1) {
    			SCALE_1TO1_INDEX = i;
    			break;
    		}
    	}
    	
    	try {
    		cellHighlightImage  = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/blueDot.png");
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
    	}
    }
    
    public ZoneRenderer(Zone zone) {
        if (zone == null) { throw new IllegalArgumentException("Zone cannot be null"); }

        this.zone = zone;
        scaleIndex = SCALE_1TO1_INDEX;
        
        // DnD
        new DropTarget(this, this);

        // Get focus when clicked in
        addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				requestFocus();
			}
        });
        
    }

	public void addMoveSelectionSet (String playerId, GUID keyToken, Set<GUID> tokenList, boolean clearLocalSelected) {
		
		// I'm not supposed to be moving a token when someone else is already moving it
		if (clearLocalSelected) {
			for (GUID guid : tokenList) {
				
				selectedTokenSet.remove (guid);
			}
		}
		
		selectionSetMap.put(keyToken, new SelectionSet(playerId, keyToken, tokenList));
		repaint();
	}

	public void updateMoveSelectionSet (GUID keyToken, ZonePoint offset) {
		
		SelectionSet set = selectionSetMap.get(keyToken);
		if (set == null) {
			return;
		}
		
		Token token = zone.getToken(keyToken);
		set.setOffset(offset.x - token.getX(), offset.y - token.getY());
		repaint();
	}
	
	public void removeMoveSelectionSet (GUID keyToken) {
		
		SelectionSet set = selectionSetMap.remove(keyToken);
		if (set == null) {
			return;
		}
		
		for (GUID tokenGUID : set.getTokens()) {
			
			Token token = zone.getToken(tokenGUID);
			token.setX(set.getOffsetX() + token.getX());
			token.setY(set.getOffsetY() + token.getY());
		}

		repaint();
	}

	public boolean isTokenMoving(Token token) {
		
		for (SelectionSet set : selectionSetMap.values()) {
			
			if (set.contains(token)) {
				return true;
			}
		}
		
		return false;
	}
	
    /**
     * Clear internal caches and backbuffers
     */
    public void flush() {
        replacementImageMap.clear();
        resizedImageMap.clear();
    }
    
    public Zone getZone() {
    	return zone;
    }
    
    public void addOverlay(ZoneOverlay overlay) {
        overlayList.add(overlay);
    }

    public void removeOverlay(ZoneOverlay overlay) {
        overlayList.remove(overlay);
    }

    public void toggleGrid() {
        showGrid = !showGrid;
        
        repaint();
    }
    
    public void setGridVisible(boolean visible) {
        showGrid = visible;

        repaint();
    }

    /* (non-Javadoc)
	 * @see javax.swing.JComponent#isRequestFocusEnabled()
	 */
	public boolean isRequestFocusEnabled() {
		return true;
	}
    
    public void moveGridBy(int dx, int dy) {

    	int gridOffsetX = zone.getGridOffsetX();
    	int gridOffsetY = zone.getGridOffsetY();
    	int gridSize = zone.getGridSize();
    	
        gridOffsetX += dx;
        gridOffsetY += dy;

        gridOffsetX %= gridSize;
        gridOffsetY %= gridSize;

        if (gridOffsetY > 0) {
            gridOffsetY = gridOffsetY - gridSize;
        }
        
        if (gridOffsetX > 0) {
            gridOffsetX = gridOffsetX - gridSize;
        }

        zone.setGridOffsetX(gridOffsetX);
        zone.setGridOffsetY(gridOffsetY);
        
        repaint();
    }

    public void adjustGridSize(int delta) {
        zone.setGridSize(Math.max(0, zone.getGridSize() + delta));

        repaint();
    }

    public void moveViewBy(int dx, int dy) {

        viewOffset.x += dx;
        viewOffset.y += dy;

        repaint();
    }

    public void zoomReset() {
    	zoomTo(getSize().width/2, getSize().height/2, SCALE_1TO1_INDEX);
    }

    public void zoomIn(int x, int y) {
        zoomTo(x, y, scaleIndex + 1);
    }

    public void zoomOut(int x, int y) {
        zoomTo(x, y, scaleIndex - 1);
    }

    private void zoomTo(int x, int y, int index) {

        index = Math.max(index, 0);
        index = Math.min(index, scaleArray.length - 1);

        double oldScale = scaleArray[scaleIndex];
        double newScale = scaleArray[index];

        scaleIndex = index;

        // Keep the current pixel centered
        x -= viewOffset.x;
        y -= viewOffset.y;

        int newX = (int) ((x * newScale) / oldScale);
        int newY = (int) ((y * newScale) / oldScale);

        viewOffset.x -= newX - x;
        viewOffset.y -= newY - y;

        repaint();
    }
    
    public abstract BufferedImage getMiniImage();
    
    public void paintComponent(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;
		
        if (zone == null) { return; }
        int gridSize = (int) (zone.getGridSize() * getScale());

    	renderBoard(g2d);
        if (showGrid && gridSize >= MIN_GRID_SIZE) {renderGrid(g2d);}
        renderDrawableOverlay(g2d);
        renderTokens(g2d);
		renderMoveSelectionSets(g2d);
		renderBorder(g2d);
		
        for (int i = 0; i < overlayList.size(); i++) {
            ZoneOverlay overlay = overlayList.get(i);
            overlay.paintOverlay(this, (Graphics2D) g);
        }
    }
    
    protected void renderDrawableOverlay(Graphics g) {
        
    	drawableRenderer.renderDrawables(g, zone.getDrawnElements(), viewOffset.x, viewOffset.y, getScale());
    }
    
	protected void renderBorder(Graphics2D g) { /* no op */ }
	
    protected abstract void renderBoard(Graphics2D g);
    
    protected abstract void renderGrid(Graphics2D g);
    
	protected void renderMoveSelectionSets(Graphics2D g) {
	
        int gridSize = zone.getGridSize();
        float scale = scaleArray[scaleIndex];

		for (SelectionSet set : selectionSetMap.values()) {
			
			Token keyToken = zone.getToken(set.getKeyToken());
			ZoneWalker walker = set.getWalker();

			int setOffsetX = set.getOffsetX();
			int setOffsetY = set.getOffsetY();
			
			for (GUID tokenGUID : set.getTokens()) {
				
				Token token = zone.getToken(tokenGUID);
                
                // Perhaps deleted ?
                if (token == null) {
                    continue;
                }
				
	            Asset asset = AssetManager.getAsset(token.getAssetID());
	            if (asset == null) {
	                continue;
	            }
	            
                ScreenPoint newScreenPoint = ScreenPoint.fromZonePoint(this, token.getX() + setOffsetX, token.getY() + setOffsetY);
				
				// OPTIMIZE: combine this with the code in renderTokens()
	            int width = TokenSize.getWidth(token, gridSize);
	            int height = TokenSize.getHeight(token, gridSize);
				
            	int scaledWidth = (int)(width * scale);
            	int scaledHeight = (int)(height * scale);
            	
				// Show distance only on the key token
				if (token == keyToken) {

					// Render the path
					List<CellPoint> path = walker.getPath();
					for (CellPoint p : path) {
						highlightCell(g, p);
					}
				}

				g.drawImage(getScaledToken(token, scaledWidth, scaledHeight), newScreenPoint.x+1, newScreenPoint.y+1, this);			

				// Other details
				if (token == keyToken) {

					int y = newScreenPoint.y + scaledHeight + 10;
					int x = newScreenPoint.x + scaledWidth/2;
					GraphicsUtil.drawBoxedString(g, Integer.toString(walker.getDistance()), x, y);
					if (set.getPlayerId() != null && set.getPlayerId().length() > 0) {
						GraphicsUtil.drawBoxedString(g, set.getPlayerId(), x, y + 20);
					}
				}
				
			}

		}
	}
	
	protected void highlightCell(Graphics2D g, CellPoint point) {
		
		int gridSize = (int) getScaledGridSize();
		
		// Top left of cell
		ScreenPoint p = ScreenPoint.fromZonePoint(this, point.x*zone.getGridSize(), point.y*zone.getGridSize());

		g.drawImage(cellHighlightImage, p.x+gridSize/3, p.y+gridSize/3, gridSize/3, gridSize/3, this);
	}
	
    protected void renderTokens(Graphics2D g) {

        int gridSize = zone.getGridSize();
        int gridOffsetX = zone.getGridOffsetX();
        int gridOffsetY = zone.getGridOffsetY();

        Rectangle clipBounds = g.getClipBounds();
        float scale = scaleArray[scaleIndex];
        
        tokenBoundsMap.clear();
        for (Token token : zone.getTokens()) {

            int width = (int)(TokenSize.getWidth(token, gridSize) * scale);
            int height = (int)(TokenSize.getHeight(token, gridSize) * scale);
            
            // OPTIMIZE:
            int x = (int)(token.getX() * scale + viewOffset.x) + (int) (gridOffsetX * scaleArray[scaleIndex]) + 1;
            int y = (int)(token.getY() * scale + viewOffset.y) + (int) (gridOffsetY * scaleArray[scaleIndex]) + 1;

            Rectangle tokenBounds = new Rectangle(x, y, width, height);
            tokenBoundsMap.put(tokenBounds, token);

            // OPTIMIZE:
			BufferedImage image = null;
            Asset asset = AssetManager.getAsset(token.getAssetID());
            if (asset == null) {
                MapTool.serverCommand().getAsset(token.getAssetID());

                // In the mean time, show a placeholder
                image = ImageManager.UNKNOWN_IMAGE;
            } else {
            
				image = getScaledToken(token, width, height);
            }

            // Only draw if we're visible
            // NOTE: this takes place AFTER resizing the image, that's so that the user
            // sufferes a pause only once while scaling, and not as new tokens are
            // scrolled onto the screen
            if (!tokenBounds.intersects(clipBounds)) {
                continue;
            }

			// Moving ?
			if (isTokenMoving(token)) {
				BufferedImage replacementImage = replacementImageMap.get(token);
				if (replacementImage == null || replacementImage.getWidth() != width || replacementImage.getHeight() != height) {
					replacementImage = ImageUtil.rgbToGrayscale(image);
					
					// TODO: fix this memory leak -> when to clean up the image (when selection set is removed)
					replacementImageMap.put(token, replacementImage);
				}
				
				image = replacementImage;
			}

            // Draw
            g.drawImage(image, x, y, this);

            // Selected ?
            if (selectedTokenSet.contains(token.getId())) {
            	ClientStyle.selectedBorder.paintAround(g, x, y, width, height);
            }
        }
    }

    // TODO: This will create redundant copies of assets, but needs to do this
    // in order to account for tokens with the same asset of different sizes.
    // At some point, figure out a more intelligent way of doing this
    public BufferedImage getScaledToken(Token token, int width, int height) {

        // Cached value ?
        BufferedImage image = resizedImageMap.get(token);
        if (image != null) {
            if (image.getWidth() == width && image.getHeight() == height) {
                return image;
            }
            
            resizedImageMap.remove(token);
        }

        // Don't scale if we're already at 1:1
        image = ImageManager.getImage(AssetManager.getAsset(token.getAssetID()));
        if (image.getWidth() == width && image.getHeight() == height) {
            return image;
        }
        
        // Scale and save
        BufferedImage scaledImage = new BufferedImage(width, height, Transparency.BITMASK);
        Graphics2D g = (Graphics2D)scaledImage.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(image, 0, 0, width, height, this);
        g.dispose();

        // Don't store the image if it's a placeholder
        // TODO: Optimize this, we don't want to rebuild resized temp images every refresh
        if (image != ImageManager.UNKNOWN_IMAGE) {
            resizedImageMap.put(token, scaledImage);
        }
        
        return scaledImage;
    }
    
    public Set<GUID> getSelectedTokenSet() {
    	return selectedTokenSet;
    }
    
    public void selectToken(GUID token) {
        if (token == null) {
            return;
        }
        
    	selectedTokenSet.add(token);
    	
    	repaint();
    }
    
    public void clearSelectedTokens() {
    	selectedTokenSet.clear();
    	
    	repaint();
    }
    
    public Rectangle getTokenBounds(Token token) {
    	
    	for (Rectangle rect : tokenBoundsMap.keySet()) {
    		if (tokenBoundsMap.get(rect) == token) {
    			return rect;
    		}
    	}
    	
    	return null;
    }
    
	/**
	 * Returns the token at screen location x, y (not cell location). To get
	 * the token at a cell location, use getGameMap() and use that.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Token getTokenAt (int x, int y) {
		
		for (Rectangle rect : tokenBoundsMap.keySet()) {
			if (rect.contains(x, y)) {
				return tokenBoundsMap.get(rect);
			}
		}
		
		return null;
	}

    public ZonePoint constrainToCell(int x, int y) {
        return constrainToCell(new ZonePoint(x, y));
    }
    
    /**
     * Translate the zone coordinates in p and change them to cell coordinates.
     * Note that the result is not the cell x,y, but rather the zone x,y of the
     * cell the point is contained by
     * @param p
     * @return original point
     */
    public ZonePoint constrainToCell(ZonePoint p) {
        
        int gridSize = zone.getGridSize();
        
        int scalex = (p.x / gridSize);
        int scaley = (p.y / gridSize);
        
        // Handle +/- transition
        if (p.x < 0) {scalex --;}
        if (p.y < 0) {scaley --;}
        
        p.x = scalex * gridSize;
        p.y = scaley * gridSize;

        return p;
    }
    
    public int getOffsetX() {
        return viewOffset.x;
    }
    
    public int getOffsetY() {
        return viewOffset.y;
    }
    
  /**
   * Since the map can be scaled, this is a convenience method to find out
   * what cell is at this location. 
   * 
   * @param x X location in screen coordinates.
   * @param y Y location in screen coordinates.
   * @param cell The point used to contain the cell coordinates. If <code>null</code>
   * a new point will be created. 
   * @return The cell coordinates in the passed point or in a new point.
   */
  public CellPoint getCellAt (int x, int y) {
    
    double scale = scaleArray[scaleIndex];
    
    // Translate
    x -= viewOffset.x + (int) (zone.getGridOffsetX() * scale);
    y -= viewOffset.y + (int) (zone.getGridOffsetY() * scale);
    
    // Scale
    x = (int)Math.floor(x / (zone.getGridSize() * scale));
    y = (int)Math.floor(y / (zone.getGridSize() * scale));
    
    return new CellPoint(x, y);
  }
  
  /**
   * Find the screen cooridnates of the upper left hand corner of a cell taking
   * into acount scaling and translation. 
   * 
   * @param cell Get the coordinates of this cell.
   * @param screen The point used to contains the screen coordinates. It may
   * be <code>null</code>.
   * @return The screen coordinates of the upper left hand corner in the passed
   * point or in a new point.
   */
  public ScreenPoint getCellCooridnates(CellPoint cell) {
    double scale = scaleArray[scaleIndex]; 

    int x = viewOffset.x + (int)(zone.getGridOffsetX() * scale + cell.x * zone.getGridSize() * scale);
    int y = viewOffset.y + (int)(zone.getGridOffsetY() * scale + cell.y * zone.getGridSize() * scale);
    
    return new ScreenPoint(x, y);
  }
  
    public double getScale() {
    	return scaleArray[scaleIndex];
    }
    
    public double getScaledGridSize() {
    	// Optimize: only need to calc this when grid size or scale changes
    	return getScale() * zone.getGridSize();
    }
	
	/**
	 * Represents a movement set
	 */
	private class SelectionSet {
		
		private HashSet<GUID> selectionSet = new HashSet<GUID>();
		private GUID keyToken;
		private String playerId;
		private ZoneWalker walker;
		private Token token;
		
		// Pixel distance from keyToken's origin
        private int offsetX;
        private int offsetY;
		
		public SelectionSet(String playerId, GUID tokenGUID, Set<GUID> selectionList) {

			selectionSet.addAll(selectionList);
			keyToken = tokenGUID;
			this.playerId = playerId;
			
			token = zone.getToken(tokenGUID);
			CellPoint tokenPoint = new CellPoint(token.getX()/zone.getGridSize(), token.getY()/zone.getGridSize());
			walker = new AStarEuclideanWalker(zone);
			walker.setWaypoints(tokenPoint, tokenPoint);
		}
		
		public ZoneWalker getWalker() {
			return walker;
		}
		
		public GUID getKeyToken() {
			return keyToken;
		}

		public Set<GUID> getTokens() {
			return selectionSet;
		}
		
		public boolean contains(Token token) {
			return selectionSet.contains(token.getId());
		}
		
		public void setOffset(int x, int y) {

            offsetX = x;
            offsetY = y;
            
            int cellX = (token.getX()+offsetX)/zone.getGridSize();
            int cellY = (token.getY()+offsetY)/zone.getGridSize();
			CellPoint point = new CellPoint(cellX, cellY);
			walker.replaceLastWaypoint(point);
            
		}
		
		public int getOffsetX() {
			return offsetX;
		}
		
		public int getOffsetY() {
			return offsetY;
		}
		
		public String getPlayerId() {
			return playerId;
		}
	}

	////
    // DROP TARGET LISTENER
    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
     */
    public void dragEnter(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
     */
    public void dragExit(DropTargetEvent dte) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
     */
    public void dragOver(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
     */
    public void drop(DropTargetDropEvent dtde) {

    	// TODO: This section needs to be consolidated with ZoneSelectionPanel.drop()
    	Asset asset = TransferableHelper.getAsset(dtde);

    	if (asset != null) {
	        CellPoint p = getCellAt((int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
	
	        Token token = new Token(asset.getId());
	        token.setX(p.x * zone.getGridSize());
	        token.setY(p.y * zone.getGridSize());
	
	        zone.putToken(token);

            MapTool.serverCommand().putToken(zone.getId(), token);
	        
	        repaint();
    	}
    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
     */
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }
    
}
