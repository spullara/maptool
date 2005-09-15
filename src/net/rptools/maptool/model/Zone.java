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

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import net.rptools.maptool.model.drawing.DrawnElement;
import net.rptools.maptool.util.MD5Key;

/**
 * This object represents the maps that will appear for placement of {@link Token}s.  This
 * object extends Token because the background image is a scaled asset, which
 * is exactly the definition of a Token.
 */
public class Zone extends Token {
    private String name;
    
    public enum Event {
        TOKEN_ADDED,
        TOKEN_REMOVED,
        TOKEN_CHANGED,
        GRID_CHANGED,
        DRAWABLE_ADDED,
        DRAWABLE_REMOVED
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

    private LinkedHashMap<GUID, Token> tokens = new LinkedHashMap<GUID, Token>();

    private Area exposedArea = new Area();
    private boolean hasFog;
    
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
    }
    
    public void clearExposedArea() {
    	exposedArea = new Area();
    }
    
    public void exposeArea(Area area) {
    	exposedArea.add(area);
    }
    
    public void hideArea(Area area) {
    	exposedArea.subtract(area);
    }
    
    public Area getExposedArea() {
    	return exposedArea;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
    
    /**
     * Delete the drawable so it is no longer painted.
     * 
     * @param drawableId The id of the drawable being deleted.
     */
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
        boolean newToken = !tokens.containsKey(token.getId());

        // removed and then added to protect Z order
        this.tokens.remove(token.getId()); 
        this.tokens.put(token.getId(), token);
        
        if (newToken) {
            fireModelChangeEvent(new ModelChangeEvent(this, Event.TOKEN_ADDED, token));
        } else {
            fireModelChangeEvent(new ModelChangeEvent(this, Event.TOKEN_CHANGED, token));
        }
    }
    
    public void removeToken(GUID id) {
        Token token = this.tokens.remove(id);
        if (token != null) {
            fireModelChangeEvent(new ModelChangeEvent(this, Event.DRAWABLE_REMOVED, token));
        }
    }
	
	public Token getToken(GUID id) {
		return tokens.get(id);
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
        return new ArrayList<Token>(this.tokens.values());
    }

}
