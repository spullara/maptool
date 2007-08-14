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
package net.rptools.maptool.model.drawing;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;

/**
 * An rectangle
 */
public class ShapeDrawable extends AbstractDrawing {

    private Shape shape;
    private boolean useAntiAliasing;
    
    public ShapeDrawable(Shape shape, boolean useAntiAliasing) {
    	this.shape = shape;
    	this.useAntiAliasing = useAntiAliasing;
    }
    public ShapeDrawable(Shape shape) {
        this(shape, true);
    }
    
    /* (non-Javadoc)
	 * @see net.rptools.maptool.model.drawing.Drawable#getBounds()
	 */
	public java.awt.Rectangle getBounds() {

        return shape.getBounds();
	}
    
    protected void draw(Graphics2D g) {

    	Object oldAA = applyAA(g);
        g.draw(shape);
        restoreAA(g, oldAA);
    }

    protected void drawBackground(Graphics2D g) {

    	Object oldAA = applyAA(g);
        g.fill(shape);
        restoreAA(g, oldAA);
    }
    
    public Shape getShape() {
    	return shape;
    }
    
    private Object applyAA(Graphics2D g) {
    	Object oldAA = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
    	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, useAntiAliasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
    	return oldAA;
    }
    
    private void restoreAA(Graphics2D g, Object oldAA) {
    	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAA);
    }
}
