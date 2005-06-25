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

import net.rptools.maptool.util.MD5Key;

/**
 * This object represents the placeable objects on a map.  For example an icon that represents a character
 * would exist as an {@link Asset} (the image itself) and a location and scale.  
 */
public class Token {
    private GUID id = new GUID();
    private MD5Key assetID;

    private int x;
    private int y;
    
    private boolean snapToScale = true; // Whether the scaleX and scaleY represent snap-to-grid measurements
    private int width = 1; // Default to using exactly 1x1 grid cell
    private int height = 1;
    private int size = TokenSize.Size.Medium.value(); // Abstract size
    
	private boolean snapToGrid = true; // Whether the token snaps to the current grid or is free floating
	
    public Token() {
        
    }
    
    public Token(MD5Key assetID) {
        this.assetID = assetID;
    }

    public MD5Key getAssetID() {
        return assetID;
    }

    public void setAsset(MD5Key assetID) {
        this.assetID = assetID;
    }

    public GUID getId() {
        return id;
    }

    public void setId(GUID id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    
    /**
     * @return Returns the scaleX.
     */
    public int getWidth() {
        return width;
    }
    /**
     * @param scaleX The scaleX to set.
     */
    public void setWidth(int width) {
        this.width = width;
    }
    /**
     * @return Returns the sizeY.
     */
    public int getHeight() {
        return height;
    }
    /**
     * @param height The sizeY to set.
     */
    public void setHeight(int height) {
        this.height = height;
    }
    /**
     * @return Returns the snapScale.
     */
    public boolean isSnapToScale() {
        return snapToScale;
    }
    /**
     * @param snapScale The snapScale to set.
     */
    public void setSnapToScale(boolean snapScale) {
        this.snapToScale = snapScale;
    }
    
	/**
	 * @return Returns the size.
	 */
	public int getSize() {
		return size;
	}
	/**
	 * @param size The size to set.
	 */
	public void setSize(int size) {
		this.size = size;
	}

	public boolean isSnapToGrid() {
		return snapToGrid;
	}

	public void setSnapToGrid(boolean snapToGrid) {
		this.snapToGrid = snapToGrid;
	}
	
	
}
