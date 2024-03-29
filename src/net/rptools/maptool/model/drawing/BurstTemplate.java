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

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.ZonePoint;

/**
 * Create and paint a donut burst
 * 
 * @author Jay
 */
public class BurstTemplate extends RadiusTemplate {

    /*---------------------------------------------------------------------------------------------
     * Instance Variables
     *-------------------------------------------------------------------------------------------*/

    /**
     * Renderer for the blast. The {@link Shape} is just a rectangle. 
     */
    private ShapeDrawable renderer = new ShapeDrawable(new Rectangle());
    
    /**
     * Renderer for the blast. The {@link Shape} is just a rectangle. 
     */
    private ShapeDrawable vertexRenderer = new ShapeDrawable(new Rectangle());
    
    /*---------------------------------------------------------------------------------------------
     * Instance Methods
     *-------------------------------------------------------------------------------------------*/

    /**
     * This methods adjusts the rectangle in the renderer to match the new radius, vertex, or direction.
     * Due to the fact that it is impossible to draw to the cardinal directions evenly when the radius
     * is an even number and still stay in the squares, that case isn't allowed. 
     */
    private void adjustShape() {
        if (getZoneId() == null) return;
        int gridSize = MapTool.getCampaign().getZone(getZoneId()).getGrid().getSize();
        Rectangle r = (Rectangle)vertexRenderer.getShape();
        r.setBounds(getVertex().x, getVertex().y, gridSize, gridSize);
        r = (Rectangle)renderer.getShape();
        r.setBounds(getVertex().x, getVertex().y, gridSize, gridSize);
        r.x -= getRadius() * gridSize;
        r.y -= getRadius() * gridSize;
        r.width = r.height = (getRadius() * 2 + 1) * gridSize;
    }

    /*---------------------------------------------------------------------------------------------
     * Overridden *Template Methods
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * @see net.rptools.maptool.model.drawing.AbstractTemplate#setRadius(int)
     */
    @Override
    public void setRadius(int squares) {
        super.setRadius(squares);
        adjustShape();
    }

    /**
     * @see net.rptools.maptool.model.drawing.AbstractTemplate#setVertex(net.rptools.maptool.model.ZonePoint)
     */
    @Override
    public void setVertex(ZonePoint vertex) {
        super.setVertex(vertex);
        adjustShape();
    }

    /**
     * @see net.rptools.maptool.model.drawing.AbstractTemplate#getDistance(int, int)
     */
    @Override
    public int getDistance(int x, int y) {
        return Math.max(x, y);
    }
    
    @Override
    public Rectangle getBounds() {
    	Rectangle r = new Rectangle(renderer.getShape().getBounds());
    	// We don't know pen width, so add some padding to account for it
    	r.x -= 5;
    	r.y -= 5;
    	r.width += 10;
    	r.height += 10;
    	
    	return r;
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
      vertexRenderer.draw(g);
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
