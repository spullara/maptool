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
package net.rptools.maptool.client;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

public class AppState {

    public static final String USE_DOUBLE_WIDE_PROP_NAME = "useDoubleWide";
    
    private static boolean dropTokenAsInvisible = false;
    private static boolean showGrid = false;
    private static boolean showTokenNames = false;
    private static boolean newZonesVisible = true;
    private static boolean linkPlayerViews = false;
    private static boolean useDoubleWideLine = true;
    private static boolean newMapsHaveFoW = false;
    private static File campaignFile;
    private static int gridSize = 1;
    private static boolean tokensStartSnapToGrid = true;
    private static boolean useAlphaFog = false;
    
    private static PropertyChangeSupport changeSupport = new PropertyChangeSupport(AppState.class);
    
    public static void addPropertyChangeListener(PropertyChangeListener listener) {
      changeSupport.addPropertyChangeListener(listener);
    }
    
    public static void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
      changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public static boolean isTokensStartSnapToGrid() {
		return tokensStartSnapToGrid;
	}

	public static void setTokensStartSnapToGrid(boolean tokensStartSnapToGrid) {
		AppState.tokensStartSnapToGrid = tokensStartSnapToGrid;
	}

	public static int getGridSize() {
    	return gridSize;
    }
    
    public static void setGridSize(int size) {
    	gridSize = size;
    }
    
    public static boolean getNewMapsHaveFoW() {
		return newMapsHaveFoW;
	}

	public static void setNewMapsHaveFoW(boolean newMapsHaveFoW) {
		AppState.newMapsHaveFoW = newMapsHaveFoW;
	}

	public static boolean useDoubleWideLine() {
		return useDoubleWideLine;
	}

	public static void setUseDoubleWideLine(boolean useDoubleWideLine) {
    boolean old = AppState.useDoubleWideLine;
		AppState.useDoubleWideLine = useDoubleWideLine;
    changeSupport.firePropertyChange(USE_DOUBLE_WIDE_PROP_NAME, old, useDoubleWideLine);
	}

	public static boolean isDropTokenAsInvisible() {
        return dropTokenAsInvisible;
    }

    public static void setDropTokenAsInvisible(boolean dropTokenAsInvisible) {
        AppState.dropTokenAsInvisible = dropTokenAsInvisible;
    }

    public static boolean isShowGrid() {
    	return showGrid;
    }
    
    public static void setShowGrid(boolean flag) {
    	showGrid = flag;
    }

    public static void setShowTokenNames(boolean flag) {
    	showTokenNames = flag;
    }
    
    public static boolean isShowTokenNames() {
    	return showTokenNames;
    }
    
    public static void setNewZonesVisible(boolean flag) {
    	newZonesVisible = flag;
    }
    
    public static boolean isNewZonesVisible() {
    	return newZonesVisible;
    }
    
    public static boolean isPlayerViewLinked() {
    	return linkPlayerViews;
    }
    
    public static void setPlayerViewLinked(boolean flag) {
    	linkPlayerViews = flag;
    }

	public static File getCampaignFile() {
		return campaignFile;
	}

	public static void setCampaignFile(File campaignFile) {
		AppState.campaignFile = campaignFile;
	}

	public static boolean isUseAlphaFog() {
		return useAlphaFog;
	}

	public static void setUseAlphaFog(boolean useAlphaFog) {
		AppState.useAlphaFog = useAlphaFog;
	}
    
    
}
