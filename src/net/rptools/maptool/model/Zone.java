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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawnElement;

/**
 * This object represents the maps that will appear for placement of {@link Token}s.  This
 * object extends Token because the background image is a scaled asset, which
 * is exactly the definition of a Token.
 */
public class Zone extends Token {
    private String name;
    
    private int gridSize = 40;
    private int gridOffsetX = 0;
    private int gridOffsetY = 0;
    
    private int feetPerCell = 5;
    
    private List<DrawnElement> drawables = new LinkedList<DrawnElement>();

    private LinkedHashMap<GUID, Token> tokens = new LinkedHashMap<GUID, Token>();

    public Zone() {

    }

    public Zone(GUID backgroundAsset) {
        super(backgroundAsset);
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
    }

    public int getGridOffsetY() {
        return gridOffsetY;
    }

    public void setGridOffsetY(int gridOffsetY) {
        this.gridOffsetY = gridOffsetY;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }
    
    public int getFeetPerCell() {
    	return feetPerCell;
    }
    
    public void setFeetPerCell(int feetPerCell) {
    	this.feetPerCell = feetPerCell;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // drawables
    ///////////////////////////////////////////////////////////////////////////

    public void addDrawable(DrawnElement drawnElement) {
    	drawables.add(drawnElement);
    }
    
    public List<DrawnElement> getDrawnElements() {
    	return drawables;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // tokens
    ///////////////////////////////////////////////////////////////////////////
    public void putToken(Token token) {
        // removed and then added to protect Z order
        this.tokens.remove(token.getId()); 
        this.tokens.put(token.getId(), token);
    }
    
    public void removeToken(GUID id) {
        this.tokens.remove(id);
    }

    public List<Token> getTokens() {
        return new ArrayList<Token>(this.tokens.values());
    }
}
