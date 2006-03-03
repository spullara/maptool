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

public class Scale {

    private int               scaleIndex;
    private static float[]    scaleArray  = new float[] { .25F, .30F, .40F, .50F, .60F, .75F, 1F, 1.25F, 1.5F, 1.75F, 2F, 3F, 4F};
    private static int SCALE_1TO1_INDEX; // Automatically scanned for
    
    private int offsetX;
    private int offsetY;
    
    private int width;
    private int height;
    
    static {
        // Create scale array
        for (int i = 0; i < scaleArray.length; i++) {
            if (scaleArray[i] == 1) {
                SCALE_1TO1_INDEX = i;
                break;
            }
        }
    }

    public Scale() {
    	this(0, 0);
    }
    
    public Scale(int width, int height) {
        scaleIndex = SCALE_1TO1_INDEX;
        this.width = width;
        this.height = height;
    }
    
    public int getOffsetX() {
    	return offsetX;
    }
    
    public int getOffsetY() {
    	return offsetY;
    }
    
    public void setOffset(int x, int y) {
    	offsetX = x;
    	offsetY = y;
    }
    
    public int getIndex() {
        return scaleIndex;
    }
    
    public void setIndex(int index) {
        index = Math.max(index, 0);
        index = Math.min(index, scaleArray.length - 1);

        scaleIndex = index;
    }
    
    public float reset() {
        float oldScale = scaleArray[scaleIndex];
        scaleIndex = SCALE_1TO1_INDEX;
        return oldScale;
    }
    
    public float getScale() {
        return scaleArray[scaleIndex];
    }
    
    public float scaleUp() {
        float oldScale = scaleArray[scaleIndex];
        scaleIndex = Math.min(scaleIndex + 1, scaleArray.length -1);
        return oldScale;
    }
    
    public float scaleDown() {
        float oldScale = scaleArray[scaleIndex];
        scaleIndex = Math.max(scaleIndex - 1, 0);
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
