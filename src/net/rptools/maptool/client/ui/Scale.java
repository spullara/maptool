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

public class Scale implements Serializable {

	private double oneToOneScale = 1; // Let this be configurable at some point
	private double scale = oneToOneScale;
	private double scaleIncrement = .075;
    
	private int zoomLevel = 0;
	
    public static String PROPERTY_SCALE = "scale";
    public static String PROPERTY_OFFSET = "offset";
    
    private transient PropertyChangeSupport propertyChangeSupport;
    
    private int offsetX;
    private int offsetY;
    
    private int width;
    private int height;
    
    private boolean initialized;
    
    // LEGACY for 1.3b31 and earlier
    private int              scaleIndex;
    
    public Scale() {
    	this(0, 0);
    }
    
    public Scale(int width, int height) {
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
    
    public double getScale() {
        return scale;
    }
    
    public void setScale(double scale) {
    	double oldScale = this.scale;
        this.scale = scale;

        getPropertyChangeSupport().firePropertyChange(PROPERTY_SCALE, oldScale, scale);
    }
    
    public double reset() {
    	double oldScale = this.scale;
        scale = oneToOneScale;

        getPropertyChangeSupport().firePropertyChange(PROPERTY_SCALE, oldScale, scale);
        return oldScale;
    }
    
    public double scaleUp() {
    	zoomLevel++;
    	setScale(Math.pow(1+scaleIncrement, zoomLevel));
        return scale;
    }
    
    public double scaleDown() {
    	zoomLevel--;
    	setScale(Math.pow(1+scaleIncrement, zoomLevel));
        return scale;
    }
    
    public void zoomReset() {
    	zoomTo(width/2, height/2, reset());
    }

    public void zoomIn(int x, int y) {
    	double oldScale = scale;
    	scaleUp();
        zoomTo(x, y, oldScale);
    }

    public void zoomOut(int x, int y) {
    	double oldScale = scale;
    	scaleDown();
        zoomTo(x, y, oldScale);
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
    
    private void zoomTo(int x, int y, double oldScale) {

        // Keep the current pixel centered
        x -= offsetX;
        y -= offsetY;

        int newX = (int) ((x * scale) / oldScale);
        int newY = (int) ((y * scale) / oldScale);

        offsetX = offsetX-(newX - x);
        offsetY = offsetY-(newY - y);
    }
    
}
