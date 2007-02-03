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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;

import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;

/**
 * Abstract drawing.  This class takes care of setting up the Pen since that
 * will be the same for all implementing classes.
 */
public abstract class AbstractDrawing implements Drawable {

	/**
	 * The unique identifier for this drawable. It is immutable.
	 */
	private GUID id = new GUID();

	private String layer;
	
    /* (non-Javadoc)
     * @see maptool.model.drawing.Drawable#draw(java.awt.Graphics2D, maptool.model.drawing.Pen)
     */
    public void draw(Graphics2D g, Pen pen) {
        if (pen == null) {
            pen = Pen.DEFAULT;
        } 
        
        Stroke oldStroke = g.getStroke(); 
        g.setStroke(new BasicStroke(pen.getThickness(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Composite oldComposite = g.getComposite();
        if (pen.isEraser()) {
        	g.setComposite(AlphaComposite.Clear);
        }

        if (pen.getBackgroundMode() == Pen.MODE_SOLID && pen.getBackgroundPaint() != null) {
            Paint bgColor = pen.getBackgroundPaint().getPaint();
            
            g.setPaint(bgColor);
            
            
            drawBackground(g);
        }
        
        if (pen.getForegroundMode() == Pen.MODE_SOLID && pen.getPaint() != null) {
            Paint paint = pen.getPaint().getPaint();
        	g.setPaint(paint);
            draw(g);
        }

        g.setComposite(oldComposite);
        g.setStroke(oldStroke);
    }
    
    protected abstract void draw(Graphics2D g);
    
    protected abstract void drawBackground(Graphics2D g);

    /**
     * Get the id for this AbstractDrawing.
     *
     * @return Returns the current value of id.
     */
    public GUID getId() {
      return id;
    }

	public void setLayer(Zone.Layer layer) {
		this.layer = layer != null ? layer.name() : null;
	}
	
	public Zone.Layer getLayer() {
		return layer != null ? Zone.Layer.valueOf(layer) : Zone.Layer.TOKEN;
	}

	/**
     * Use the id for equals.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof AbstractDrawing)) return false;
      return id.equals(obj);
    }

    /**
     * Use the id for hash code.
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      return id.hashCode();
    }
}
