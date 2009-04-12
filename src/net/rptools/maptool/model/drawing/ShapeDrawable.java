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
package net.rptools.maptool.model.drawing;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;

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
    
	public Area getArea() {
		return new Area(shape);
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
