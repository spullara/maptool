/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
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
