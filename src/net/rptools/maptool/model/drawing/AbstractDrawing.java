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
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * Abstract drawing.  This class takes care of setting up the Pen since that
 * will be the same for all implementing classes.
 */
public abstract class AbstractDrawing implements Drawable {

    /* (non-Javadoc)
     * @see maptool.model.drawing.Drawable#draw(java.awt.Graphics2D, maptool.model.drawing.Pen)
     */
    public void draw(Graphics2D g, Pen pen, int translateX, int translateY) {
        if (pen == null) {
            pen = Pen.DEFAULT;
        } 
        
        Stroke oldStroke = g.getStroke(); 
        g.setStroke(new BasicStroke(pen.getThickness(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Composite oldComposite = g.getComposite();
        if (pen.isEraser()) {
        	g.setComposite(AlphaComposite.Clear);
        }

        if (pen.getBackgroundMode() == Pen.MODE_SOLID) {
            Color bgColor = new Color(pen.getBackgroundColor());
            g.setColor(bgColor);
            drawBackground(g, translateX, translateY);
        }
        
        if (pen.getForegroundMode() == Pen.MODE_SOLID) {
            Color color = new Color(pen.getColor());
        	g.setColor(color);
            draw(g, translateX, translateY);
        }

        g.setComposite(oldComposite);
        g.setStroke(oldStroke);
    }
    
    protected abstract void draw(Graphics2D g, int translateX, int translateY);
    
    protected abstract void drawBackground(Graphics2D g, int translateX, int translateY);

}
