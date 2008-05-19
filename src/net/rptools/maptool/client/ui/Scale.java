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

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scale implements Serializable {

    private int              scaleIndex;
    private static double     startScale = .01f;
    private static double     endScale = 20;
    private static double[]   scaleArray;
    
    public static int SCALE_1TO1_INDEX; // Automatically scanned for

    public static String PROPERTY_SCALE = "scale";
    public static String PROPERTY_OFFSET = "offset";
    
    private transient PropertyChangeSupport propertyChangeSupport;
    
    private int offsetX;
    private int offsetY;
    
    private int width;
    private int height;
    
    private boolean initialized;
    
    static {

    	// LATER: This whole process needs to be rewritten to be more 
    	// configurable
    	boolean lessThanOne = true;
    	List<Double> scaleList = new ArrayList<Double>();
    	double scale = startScale;
    	while (scale <= endScale) {
    		
    		if (scale < .1) {
    			scale += .01;
    		} else if (scale < 1) {
    			scale += .05;
    		} else {
    			scale += .15;
    		}
    		
    		scale = Math.round(scale*1000)/1000.0;

    		scaleList.add(scale);
    		
    		if (scale > 1 && lessThanOne) {
    			SCALE_1TO1_INDEX = scaleList.size()-1; 
    			lessThanOne = false;
    		}
    	}
    	
    	scaleArray = new double[scaleList.size()];
    	for (int i = 0; i < scaleArray.length; i++) {
    		scaleArray[i] = scaleList.get(i);
    	}
    }

    public static int getScaleCount() {
    	return scaleArray.length;
    }
    
    public Scale() {
    	this(0, 0);
    }
    
    public Scale(int width, int height) {
        scaleIndex = SCALE_1TO1_INDEX;
        this.width = width;
        this.height = height;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    	getPropertyChangeSupport().addPropertyChangeListener(listener);
    }
    
    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
    	getPropertyChangeSupport().addPropertyChangeListener(property, listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    	getPropertyChangeSupport().removePropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(String property, PropertyChangeListener listener) {
    	getPropertyChangeSupport().removePropertyChangeListener(property, listener);
    }
    
    public int getOffsetX() {
    	return offsetX;
    }
    
    public int getOffsetY() {
    	return offsetY;
    }
    
    public void setOffset(int x, int y) {
    	
    	int oldX = offsetX;
    	int oldY = offsetY;
    	
    	offsetX = x;
    	offsetY = y;
    	
    	getPropertyChangeSupport().firePropertyChange(PROPERTY_OFFSET, new Point(oldX, oldY), new Point(offsetX, offsetY));
    }
    
    public int getIndex() {
        return scaleIndex;
    }
    
    public void setIndex(int index) {
        index = Math.max(index, 0);
        index = Math.min(index, scaleArray.length - 1);

        int oldIndex = scaleIndex;
        
        scaleIndex = index;
        
        getPropertyChangeSupport().firePropertyChange(PROPERTY_SCALE, oldIndex, scaleIndex);
    }
    
    public double reset() {
    	double oldScale = scaleArray[scaleIndex];
        scaleIndex = SCALE_1TO1_INDEX;

        getPropertyChangeSupport().firePropertyChange(PROPERTY_SCALE, oldScale, scaleIndex);
        return oldScale;
    }
    
    public int getOneToOneScaleIndex() {
    	return SCALE_1TO1_INDEX;
    }
    
    public double getScale() {
        return scaleArray[scaleIndex];
    }
    
    public double scaleUp() {
    	double oldScale = getScale();
        setIndex(scaleIndex+1);
        return oldScale;
    }
    
    public double scaleDown() {
    	double oldScale = getScale();
        setIndex(scaleIndex - 1);
        return oldScale;
    }
    
    public void zoomReset() {
    	zoomTo(width/2, height/2, reset());
    }

    public void zoomIn(int x, int y) {
        zoomTo(x, y, scaleUp());
    }

    public void zoomOut(int x, int y) {
        zoomTo(x, y, scaleDown());
    }
    
    public boolean isInitialized() {
    	return initialized;
    }
    
    private PropertyChangeSupport getPropertyChangeSupport() {
    	if (propertyChangeSupport == null) {
    		 propertyChangeSupport = new PropertyChangeSupport(this);
    	}
    	return propertyChangeSupport;
    }
    
    /**
     * Fit the image into the given space by finding the zoom level
     * that allows the image to fit.  Then center the image
     * @param width
     * @param height
     * @return true if this call did something, false if the init has already been called
     */
    public boolean initialize(int width, int height) {
    	
    	if (initialized) {
    		return false;
    	}
    	
    	findScaleToFit(width-20, height-20);
    	centerIn(width, height);
    	
    	initialized = true;
    	return true;
    }
    
    public void centerIn(int width, int height) {
    	
    	int currWidth = (int)(this.width * getScale());
    	int currHeight = (int)(this.height * getScale());
    	
    	int x = (width - currWidth) / 2;
    	int y = (height - currHeight) / 2;
    	
    	setOffset(x, y);
    }
    
    public void findScaleToFit(int width, int height) {
    	
    	if (this.width == 0 || this.height == 0) {
    		return;
    	}
    	
    	// Find the scale that makes the size exceed the given dimensions
    	for (int i = 0; i < scaleArray.length; i ++) {
    		double scale = scaleArray[i];
    		if (this.width * scale > width || this.height * scale > height) {
    			setIndex(i-1);
    			return;
    		}
    	}
    	
    	// No scale was too big
    	setIndex(scaleArray.length-1);
    }
    
    private void zoomTo(int x, int y, double oldScale) {

        double newScale = getScale();

        // Keep the current pixel centered
        x -= offsetX;
        y -= offsetY;

        int newX = (int) ((x * newScale) / oldScale);
        int newY = (int) ((y * newScale) / oldScale);

        offsetX = offsetX-(newX - x);
        offsetY = offsetY-(newY - y);
    }
    
}
