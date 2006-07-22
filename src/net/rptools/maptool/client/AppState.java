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
    
    private static boolean showGrid = false;
    private static boolean showTokenNames = false;
    private static boolean linkPlayerViews = false;
    private static boolean useDoubleWideLine = true;
    private static boolean showMovementMeasurements = true;
    private static File campaignFile;
    private static int gridSize = 1;
    
    private static PropertyChangeSupport changeSupport = new PropertyChangeSupport(AppState.class);
    
    public static void addPropertyChangeListener(PropertyChangeListener listener) {
      changeSupport.addPropertyChangeListener(listener);
    }
    
    public static void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
      changeSupport.addPropertyChangeListener(propertyName, listener);
    }

	public static int getGridSize() {
    	return gridSize;
    }
    
    public static void setGridSize(int size) {
    	gridSize = size;
    }
    
	public static boolean useDoubleWideLine() {
		return useDoubleWideLine;
	}

	public static void setUseDoubleWideLine(boolean useDoubleWideLine) {
    boolean old = AppState.useDoubleWideLine;
		AppState.useDoubleWideLine = useDoubleWideLine;
    changeSupport.firePropertyChange(USE_DOUBLE_WIDE_PROP_NAME, old, useDoubleWideLine);
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
	
	public static void setShowMovementMeasurements(boolean show) {
		showMovementMeasurements = show;
	}
	
	public static boolean getShowMovementMeasurements() {
		return showMovementMeasurements;
	}
}
