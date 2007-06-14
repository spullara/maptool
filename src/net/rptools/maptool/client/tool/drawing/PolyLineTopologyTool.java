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
package net.rptools.maptool.client.tool.drawing;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.drawing.DrawableColorPaint;
import net.rptools.maptool.model.drawing.DrawablePaint;
import net.rptools.maptool.model.drawing.LineSegment;
import net.rptools.maptool.model.drawing.Pen;


/**
 * Tool for drawing freehand lines.
 */
public class PolyLineTopologyTool extends PolygonTopologyTool implements MouseMotionListener {
    private static final long serialVersionUID = 3258132466219627316L;

    public PolyLineTopologyTool() {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/tool/draws.png"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
	@Override
    public String getInstructions() {
    	return "tool.poly.instructions";
    }

    protected boolean isBackgroundFill(MouseEvent e) {
    	return false;
    }
    
    protected Pen getPen() {
    	
    	Pen pen = new Pen(MapTool.getFrame().getPen());
		pen.setEraser(isEraser());
		pen.setForegroundMode(Pen.MODE_SOLID);
        pen.setBackgroundMode(Pen.MODE_TRANSPARENT);
        pen.setThickness(1.0f);
        pen.setPaint(new DrawableColorPaint(isEraser() ? AppStyle.topologyRemoveColor : AppStyle.topologyAddColor));

		return pen;
    }


    protected Polygon getPolygon(LineSegment line) {
        Polygon polygon = new Polygon();
        for (Point point : line.getPoints()) {
            polygon.addPoint(point.x, point.y);
        }
        
        return polygon;
    }
    
    @Override
    public String getTooltip() {
        return "Draw poly lines";
    }
    
}
