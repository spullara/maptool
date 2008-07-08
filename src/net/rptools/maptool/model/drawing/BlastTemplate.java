/* The MIT License
 * 
 * Copyright (c) 2008 David Rice, Trevor Croft, Jay Gorrell
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
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.ZonePoint;

/**
 * The blast template draws a square for D&D 4e
 * 
 * @author jgorrell
 * @version $Revision: $ $Date: $ $Author: $
 */
public class BlastTemplate extends ConeTemplate {

    /*---------------------------------------------------------------------------------------------
     * Instance Variables
     *-------------------------------------------------------------------------------------------*/

    /**
     * Renderer for the blast. The {@link Shape} is just a rectangle. 
     */
    private ShapeDrawable renderer = new ShapeDrawable(new Rectangle());
    
    /*---------------------------------------------------------------------------------------------
     * Instance Methods
     *-------------------------------------------------------------------------------------------*/

    /**
     * This methods adjusts the rectangle in the renderer to match the new radius, vertex, or direction.
     * Due to the fact that it is impossible to draw to the cardinal directions evenly when the radius
     * is an even number and still stay in the squares, that case isn't allowed. 
     */
    private void adjustRectangle() {
        if (getZoneId() == null) return;
        int gridSize = MapTool.getCampaign().getZone(getZoneId()).getGrid().getSize();
        int half = (getRadius() / 2) * gridSize;
        int size = getRadius() * gridSize;
        Rectangle r = (Rectangle)renderer.getShape();
        r.setBounds(getVertex().x, getVertex().y, size, size);
        switch (getDirection()) {
        case WEST:
            r.x -= size;
            r.y -= half;
            break;
        case NORTH_WEST:
            r.x -= size;
            r.y -= size;
            break;
        case NORTH:
            r.x -= half;
            r.y -= size;
            break;
        case NORTH_EAST:
            r.y -= size;
            r.x += gridSize;
            break;
        case EAST:
            r.y -= half;
            r.x += gridSize;
            break;
        case SOUTH_EAST:
            r.x += gridSize;
            r.y += gridSize;
            break;
        case SOUTH:
            r.x -= half;
            r.y += gridSize;
            break;
        case SOUTH_WEST:
            r.x -= size;
            r.y += gridSize;
            break;
        } // endswitch
    }

    /*---------------------------------------------------------------------------------------------
     * Overridden *Template Methods
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * @see net.rptools.maptool.model.drawing.ConeTemplate#setDirection(net.rptools.maptool.model.drawing.AbstractTemplate.Direction)
     */
    @Override
    public void setDirection(Direction direction) {
        super.setDirection(direction);
        adjustRectangle();
    }

    /**
     * @see net.rptools.maptool.model.drawing.AbstractTemplate#setRadius(int)
     */
    @Override
    public void setRadius(int squares) {
        super.setRadius(squares);
        adjustRectangle();
    }

    /**
     * @see net.rptools.maptool.model.drawing.AbstractTemplate#setVertex(net.rptools.maptool.model.ZonePoint)
     */
    @Override
    public void setVertex(ZonePoint vertex) {
        super.setVertex(vertex);
        adjustRectangle();
    }

    /**
     * @see net.rptools.maptool.model.drawing.AbstractTemplate#getDistance(int, int)
     */
    @Override
    public int getDistance(int x, int y) {
        return Math.max(x, y);
    }

    /*---------------------------------------------------------------------------------------------
     * Overridden AbstractDrawing Methods
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * @see net.rptools.maptool.model.drawing.AbstractDrawing#draw(java.awt.Graphics2D)
     */
    @Override
    protected void draw(Graphics2D g) {
      renderer.draw(g);
    }

    /**
     * @see net.rptools.maptool.model.drawing.AbstractDrawing#drawBackground(java.awt.Graphics2D)
     */
    @Override
    protected void drawBackground(Graphics2D g) {    
      Composite old = g.getComposite();
      if (old != AlphaComposite.Clear)
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, DEFAULT_BG_ALPHA));
      renderer.drawBackground(g);
      g.setComposite(old);
    }
}
