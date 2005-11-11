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
package net.rptools.maptool.model;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.rptools.maptool.client.ZonePoint;
import net.rptools.maptool.model.drawing.DrawnElement;
import net.rptools.maptool.util.MD5Key;

/**
 * This object represents the maps that will appear for placement of {@link Token}s.  This
 * object extends Token because the background image is a scaled asset, which
 * is exactly the definition of a Token.
 */
public class Zone extends Token {
    
    public enum Event {
        TOKEN_ADDED,
        TOKEN_REMOVED,
        TOKEN_CHANGED,
        GRID_CHANGED,
        DRAWABLE_ADDED,
        DRAWABLE_REMOVED,
        FOG_CHANGED,
        LABEL_ADDED,
        LABEL_REMOVED,
        LABEL_CHANGED
    }
    
    private static final int MIN_GRID_SIZE = 10;
    private static final int MAX_GRID_SIZE = 250;

    public interface Type {
        public static final int MAP = 0;
        public static final int INFINITE = 1;
    }
    
    private int gridSize = 40;
    private int gridOffsetX = 0;
    private int gridOffsetY = 0;
    private int type = Type.MAP;
    
    private int feetPerCell = 5;
    
    private List<DrawnElement> drawables = new LinkedList<DrawnElement>();

    private Map<GUID, Label> labels = new LinkedHashMap<GUID, Label>();
    private Map<GUID, Token> tokenMap = new HashMap<GUID, Token>();
    private List<Token> tokenOrderedList = new LinkedList<Token>();

    private Area exposedArea = new Area();
    private boolean hasFog;
    
    public static final Comparator<Token> TOKEN_Z_ORDER_COMPARATOR = new Comparator<Token>() {
    	public int compare(Token o1, Token o2) {
    		int lval = o1.getZOrder();
    		int rval = o2.getZOrder();
    		
    		return lval < rval ? -1 : lval == rval ? 0 : 1;
    	}
    };
    
    public Zone() {
    }

    public Zone(MD5Key backgroundAsset) {
        super(backgroundAsset);
    }

    public boolean hasFog() {
    	return hasFog;
    }
    
    public void setHasFog(boolean flag) {
    	hasFog = flag;
        fireModelChangeEvent(new ModelChangeEvent(this, Event.FOG_CHANGED));
    }

    public boolean isTokenVisible(Token token) {

        // Base case, nothing is visible
        if (!token.isVisible()) {
            return false;
        }
        
        // Base case, everything is visible
        if (!hasFog()) {
            return true;
        }
        
        // Token is visible, and there is fog
        int x = token.getX();
        int y = token.getY();
        int w = TokenSize.getWidth(token, gridSize);
        int h = TokenSize.getHeight(token, gridSize);

        return getExposedArea().intersects(x, y, w, h);
    }
    
    public void clearExposedArea() {
    	exposedArea = new Area();
        fireModelChangeEvent(new ModelChangeEvent(this, Event.FOG_CHANGED));
    }
    
    public void exposeArea(Area area) {
    	exposedArea.add(area);
        fireModelChangeEvent(new ModelChangeEvent(this, Event.FOG_CHANGED));
    }
    
    public void hideArea(Area area) {
    	exposedArea.subtract(area);
        fireModelChangeEvent(new ModelChangeEvent(this, Event.FOG_CHANGED));
    }
    
    public ZonePoint getNearestVertex(ZonePoint point) {
    	
    	int gridx = (int)Math.round(point.x / (double)gridSize);
    	int gridy = (int)Math.round(point.y / (double)gridSize);
    	
    	return new ZonePoint(gridx * gridSize, gridy * gridSize);
    }
    
    public Area getExposedArea() {
    	return exposedArea;
    }
    
    public int getGridOffsetX() {
        return gridOffsetX;
    }

    public void setGridOffsetX(int gridOffsetX) {
        this.gridOffsetX = gridOffsetX;
        fireModelChangeEvent(new ModelChangeEvent(this, Event.GRID_CHANGED));
    }

    public int getGridOffsetY() {
        return gridOffsetY;
    }

    public void setGridOffsetY(int gridOffsetY) {
        this.gridOffsetY = gridOffsetY;
        fireModelChangeEvent(new ModelChangeEvent(this, Event.GRID_CHANGED));
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
    	
    	if (gridSize < MIN_GRID_SIZE) {
    		gridSize = MIN_GRID_SIZE;
    	}
    	
    	if (gridSize > MAX_GRID_SIZE) {
    		gridSize = MAX_GRID_SIZE;
    	}
    	
        this.gridSize = gridSize;
        fireModelChangeEvent(new ModelChangeEvent(this, Event.GRID_CHANGED));
    }
    
    public int getFeetPerCell() {
    	return feetPerCell;
    }
    
    public void setFeetPerCell(int feetPerCell) {
    	this.feetPerCell = feetPerCell;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public int getType() {
        return type;
    }
    
    public int getLargestZOrder() {
        return tokenOrderedList.size() > 0 ? tokenOrderedList.get(tokenOrderedList.size()-1).getZOrder() : 0;
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
    	drawables.add(drawnElement);
        fireModelChangeEvent(new ModelChangeEvent(this, Event.DRAWABLE_ADDED, drawnElement));
    }
    
    public List<DrawnElement> getDrawnElements() {
    	return drawables;
    }
    
    public void removeDrawable(GUID drawableId) {
      ListIterator<DrawnElement> i = drawables.listIterator();
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
    public void putToken(Token token) {
        boolean newToken = !tokenMap.containsKey(token.getId());

        this.tokenMap.put(token.getId(), token);
        
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
    
    public void removeToken(GUID id) {
        Token token = this.tokenMap.remove(id);
        if (token != null) {
        	tokenOrderedList.remove(token);
            fireModelChangeEvent(new ModelChangeEvent(this, Event.DRAWABLE_REMOVED, token));
        }
    }
	
	public Token getToken(GUID id) {
		return tokenMap.get(id);
	}
	
	/**
	 * Returns the first token with a given name.  The name is matched case-insensitively.
	 */
	public Token getTokenByName(String name) {
		for (Token token : getTokens()) {
			if (token.getName().equalsIgnoreCase(name)) {
				return token;
			}
		}
		
		return null;
	}

    public List<Token> getTokens() {
        return Collections.unmodifiableList(tokenOrderedList);
    }

}
