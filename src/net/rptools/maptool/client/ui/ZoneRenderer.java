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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

import net.rptools.common.swing.SwingUtil;
import net.rptools.common.util.ImageUtil;
import net.rptools.maptool.client.ClientStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.TransferableHelper;
import net.rptools.maptool.client.tool.ToolHelper;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.TokenSize;
import net.rptools.maptool.model.Zone;
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

    protected BufferedImage     backgroundImage;

    protected int               offsetX;
    protected int               offsetY;

    protected boolean           showGrid;
    protected Color             gridColor = new Color (150, 150, 150);

    protected int               scaleIndex;
    protected static float[]    scaleArray  = new float[] { .25F, .30F, .40F, .50F, .60F, .75F, 1F, 1.25F, 1.5F, 1.75F, 2F, 3F, 4F};
    protected static int SCALE_1TO1_INDEX; // Automatically scanned for

    private DrawableRenderer drawableRenderer = new DrawableRenderer();
    
    private Set<Token> zoomedTokenSet = new HashSet<Token>();
    private List<ZoneOverlay> overlayList = new ArrayList<ZoneOverlay>();
    private Map<Rectangle, Token> tokenBoundsMap = new HashMap<Rectangle, Token>();
    private Set<GUID> selectedTokenSet = new HashSet<GUID>();

	private Map<GUID, SelectionSet> selectionSetMap = new HashMap<GUID, SelectionSet>();
	private Map<Token, BufferedImage> replacementImageMap = new HashMap<Token, BufferedImage>();
	
    static {
		// Create scale array
    	for (int i = 0; i < scaleArray.length; i++) {
    		if (scaleArray[i] == 1) {
    			SCALE_1TO1_INDEX = i;
    			break;
    		}
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

	public void updateMoveSelectionSet (GUID keyToken, int x, int y) {
		
		SelectionSet set = selectionSetMap.get(keyToken);
		if (set == null) {
			return;
		}
		
		Token token = zone.getToken(keyToken);
		set.setOffset(x - token.getX(), y - token.getY());
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
		// TODO: clear out all caches
    }
    
    public Zone getZone() {
    	return zone;
    }
    
    public void zoomToken(Token token) {
    	if (!zoomedTokenSet.contains(token)) {
    		zoomedTokenSet.add(token);
    		repaint();
    	}
    }
    
    public void unzoomToken(Token token) {
    	if (zoomedTokenSet.contains(token)) {
    		zoomedTokenSet.remove(token);
    		repaint();
    	}
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

        offsetX += dx;
        offsetY += dy;

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
        x -= offsetX;
        y -= offsetY;

        int newX = (int) ((x * newScale) / oldScale);
        int newY = (int) ((y * newScale) / oldScale);

        offsetX -= newX - x;
        offsetY -= newY - y;

        repaint();
    }
    
    public abstract BufferedImage getBackgroundImage();    

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
        
    	drawableRenderer.renderDrawables(g, zone.getDrawnElements(), offsetX, offsetY, getScale());
    }
    
	protected void renderBorder(Graphics2D g) { /* no op */ }
	
    protected abstract void renderBoard(Graphics2D g);
    
    protected abstract void renderGrid(Graphics2D g);
    
	protected void renderMoveSelectionSets(Graphics2D g) {
	
        int gridSize = zone.getGridSize();
        int gridOffsetX = zone.getGridOffsetX();
        int gridOffsetY = zone.getGridOffsetY();
        float scale = scaleArray[scaleIndex];

		for (SelectionSet set : selectionSetMap.values()) {
			
			Token keyToken = zone.getToken(set.getKeyToken());

			int setOffsetX = set.getOffsetX();
			int setOffsetY = set.getOffsetY();
			
			int tokenOffsetX = keyToken.getX();
			int tokenOffsetY = keyToken.getY();
			
			for (GUID tokenGUID : set.getTokens()) {
				
				Token token = zone.getToken(tokenGUID);
				
				BufferedImage image = null;
	            Asset asset = AssetManager.getAsset(token.getAssetID());
	            if (asset == null) {
	                continue;
	            } else {
	            
					image = ImageManager.getImage(asset);
	            }

				int x = token.getX() + setOffsetX;
				int y = token.getY() + setOffsetY;
				
				// OPTIMIZE: combine this with the code in renderTokens()
	            int width = TokenSize.getWidth(token, gridSize);
	            int height = TokenSize.getHeight(token, gridSize);
				
	            x = (int)(x * scale) + (int) (gridOffsetX * scaleArray[scaleIndex]) + 1 + offsetX;
	            y = (int)(y * scale) + (int) (gridOffsetY * scaleArray[scaleIndex]) + 1 + offsetY;

            	Dimension dim = new Dimension(width, height);
            	SwingUtil.constrainTo(dim, HOVER_SIZE_THRESHOLD);

            	width *= scale;
            	height *= scale;
            	
				// Show distance only on the key token
				if (token == keyToken) {
					int halfWidth = width/2;
					int halfHeight = height/2;
					Point src = convertZoneToScreen(token.getX()+halfWidth, token.getY()+halfHeight);
					Point dst = new Point(x+halfWidth, y+halfHeight);
					
					g.setColor(Color.darkGray);
					g.drawLine(src.x-1, src.y-1, dst.x-1, dst.y-1);
					
					g.setColor(Color.lightGray);
					g.drawLine(src.x, src.y, dst.x, dst.y);
					
					g.setColor(Color.black);
					g.drawLine(src.x+1, src.y+1, dst.x+1, dst.y+1);

                    convertScreenToZone(src);
                    constrainToCell(src);
                    
                    convertScreenToZone(dst);
                    constrainToCell(dst);
                    
					ToolHelper.drawMeasurement(set.getPlayerId(), this, g, src, dst, true);
				}
				
				g.drawImage(image, x, y, width, height, this);			
				
			}

		}
	}
	
    protected void renderTokens(Graphics2D g) {

        int gridSize = zone.getGridSize();
        int gridOffsetX = zone.getGridOffsetX();
        int gridOffsetY = zone.getGridOffsetY();

        Rectangle clipBounds = g.getClipBounds();
        
        tokenBoundsMap.clear();
        for (Token token : zone.getTokens()) {

            // OPTIMIZE:
			BufferedImage image = null;
            Asset asset = AssetManager.getAsset(token.getAssetID());
            if (asset == null) {
                MapTool.serverCommand().getAsset(token.getAssetID());

                // In the mean time, show a placeholder
                image = ImageManager.UNKNOWN_IMAGE;
            } else {
            
				image = ImageManager.getImage(asset);
            }
			
			// Moving ?
			if (isTokenMoving(token)) {
				BufferedImage replacementImage = replacementImageMap.get(token);
				if (replacementImage == null) {
					replacementImage = ImageUtil.rgbToGrayscale(image);
					
					// TODO: fix this memory leak -> when to clean up the image (when selection set is removed)
					replacementImageMap.put(token, replacementImage);
				}
				
				image = replacementImage;
			}
			
            float scale = scaleArray[scaleIndex];

            int x = 0;
            int y = 0;

            int width = TokenSize.getWidth(token, gridSize);
            int height = TokenSize.getHeight(token, gridSize);
            
            // OPTIMIZE:
            x = (int)(token.getX() * scale + offsetX) + (int) (gridOffsetX * scaleArray[scaleIndex]) + 1;
            y = (int)(token.getY() * scale + offsetY) + (int) (gridOffsetY * scaleArray[scaleIndex]) + 1;

            if (scale >= 1.0 || !zoomedTokenSet.contains(token)) {
            	
            	width *= scale;
            	height *= scale;
            	
            } else {

            	Dimension dim = new Dimension(width, height);
            	SwingUtil.constrainTo(dim, HOVER_SIZE_THRESHOLD);

            	width = dim.width;
            	height = dim.height;
            	
            	x -= (width - (width*scale))/2;
            	y -= (height - (height*scale))/2;
            }
            
            // OPTIMIZE: don't create new Rectangles each time
            Rectangle bounds = new Rectangle();
            bounds.setBounds(x, y, width, height);
            tokenBoundsMap.put(bounds, token);

            if (bounds.intersects(clipBounds)) {
	            g.drawImage(image, x, y, width, height, this);
            }

            // Selected ?
            if (selectedTokenSet.contains(token.getId())) {
            	ClientStyle.selectedBorder.paintAround((Graphics2D) g, x, y, width, height);
            }
        }
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

    public Point constrainToCell(int x, int y) {
        return constrainToCell(new Point(x, y));
    }
    
    /**
     * Translate the zone coordinates in p and change them to cell coordinates.
     * Note that the result is not the cell x,y, but rather the zone x,y of the
     * cell the point is contained by
     * @param p
     * @return
     */
    public Point constrainToCell(Point p) {
        
        int gridSize = zone.getGridSize();
        
        int scalex = (p.x / gridSize);
        int scaley = (p.y / gridSize);
        
        p.x = scalex * gridSize;
        p.y = scaley * gridSize;

        return p;
    }
    
    public Point convertZoneToScreen(int x, int y) {
        return convertZoneToScreen(new Point(x,y));
    }
    
    /**
     * Translate the point from zone x,y to screen x,y
     * @param p
     * @return the same point instance (useful for function chaining)
     */
	public Point convertZoneToScreen(Point p) {
		
        double scale = scaleArray[scaleIndex];
		
        p.x = (int)(p.x * scale);
        p.y = (int)(p.y * scale);
        
        // Translate
        p.x += offsetX;
        p.y += offsetY;
        
        return p;
	}
	
	public Point convertScreenToZone(int x, int y) {

        return convertScreenToZone(new Point(x,y));
	}

    /**
     * Translate the point from screen x,y to zone x,y
     * @param p
     * @return the same point instance (useful for function chaining)
     */
    public Point convertScreenToZone(Point p) {
        
        double scale = scaleArray[scaleIndex];
        
        // Translate
        p.x -= offsetX;
        p.y -= offsetY;
        
        // Scale
        p.x = (int)(p.x / scale);
        p.y = (int)(p.y / scale);
        
        return p;
    }
    
  /**
   * Since the map can be scaled, this is a convenience method to find out
   * what cell is at this location. This version allows a point to be 
   * passed in to reduce the number of new <code>Point</code> objects 
   * created in the mouse move & mouse drag events.
   * 
   * @param x X location in screen coordinates.
   * @param y Y location in screen coordinates.
   * @param cell The point used to contain the cell coordinates. If <code>null</code>
   * a new point will be created. 
   * @return The cell coordinates in the passed point or in a new point.
   */
  public Point getCellAt (int x, int y, Point cell) {
    
    double scale = scaleArray[scaleIndex];
    
    // Translate
    x -= offsetX + (int) (zone.getGridOffsetX() * scale);
    y -= offsetY + (int) (zone.getGridOffsetY() * scale);
    
    // Scale
    if (cell == null) cell = new Point();
    cell.x = (int)Math.floor(x / (zone.getGridSize() * scale));
    cell.y = (int)Math.floor(y / (zone.getGridSize() * scale));
    
    return cell;
  }
  
  /**
   * Since the map can be scaled, this is a convenience method to find out
   * what cell is at this location. 
   * @param x screen x
   * @param y screen y
   * @return The cell coordinates in the passed point or in a new point.
   */
  public Point getCellAt (int x, int y) {
    return getCellAt(x, y, null);
  }
  
  /**
   * Find the screen cooridnates of the upper left hand corner of a cell taking
   * into acount scaling and translation. This version allows a point to be 
   * passed in to reduce the number of new <code>Point</code> objects 
   * created in the mouse move & mouse drag events.
   * 
   * @param cell Get the coordinates of this cell.
   * @param screen The point used to contains the screen coordinates. It may
   * be <code>null</code>.
   * @return The screen coordinates of the upper left hand corner in the passed
   * point or in a new point.
   */
  public Point getCellCooridnates(Point cell, Point screen) {
    double scale = scaleArray[scaleIndex]; 
    if (screen == null) screen = new Point();
    screen.x = offsetX + (int)(zone.getGridOffsetX() * scale + cell.x * zone.getGridSize() * scale);
    screen.y = offsetY + (int)(zone.getGridOffsetY() * scale + cell.y * zone.getGridSize() * scale);
    return screen;
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
		
		// Pixel distance from keyToken's origin
		private int offsetX = 0;
		private int offsetY = 0;
		
		public SelectionSet(String playerId, GUID tokenGUID, Set<GUID> selectionList) {

			selectionSet.addAll(selectionList);
			keyToken = tokenGUID;
			this.playerId = playerId;
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
	        Point p = dtde.getLocation();
	        p = getCellAt((int)p.getX(), (int)p.getY());
	        int x = (int)p.getX();
	        int y = (int)p.getY();
	
	        Token token = new Token(asset.getId());
	        token.setX(x * zone.getGridSize());
	        token.setY(y * zone.getGridSize());
	
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
