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
package net.rptools.maptool.client.ui.adjustgrid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import net.rptools.lib.swing.SwingUtil;

public class AdjustGridPanel extends JComponent implements MouseListener, MouseMotionListener {

    private static final int PADDING = 10;
    private static final int MINIMUM_GRID_SIZE = 5;
    
    private int gridCountX = 10;
    private int gridCountY = 10;
    private boolean showGrid = true;
    
    private BufferedImage image;
    
    private enum Handle {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }
    
    private int topGrid;
    private int bottomGrid;
    private int leftGrid;
    private int rightGrid;
    
    private boolean showRows = true;
    private boolean showCols = true;
    
    private Handle draggingHandle;

    public AdjustGridPanel() {
        setOpaque(false);
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    public Rectangle getGridBounds() {
    	return new Rectangle(topGrid, leftGrid, rightGrid - leftGrid, bottomGrid - topGrid);
    }
    
    public void setZoneImage(BufferedImage image) {
        this.image = image;
        
        topGrid = 0;
        bottomGrid = image.getHeight();
        leftGrid = 0;
        rightGrid = image.getWidth();
    }
    
    public void setShowRows(boolean show) {
    	showRows = show;
    }
    
    public void setShowCols(boolean show) {
    	showCols = show;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        if (image == null) {
            return;
        }

        // CALCULATIONS
        Dimension size = getSize();
        Dimension imageSize = getScaledImageSize();
        Point imagePosition = getScaledImagePosition();
        
        double imgRatio = getScaledImageRatio();

        // handles
        int top = (int)(topGrid * imgRatio);
        int bottom = (int)(bottomGrid * imgRatio);
        int left = (int)(leftGrid * imgRatio);
        int right = (int)(rightGrid * imgRatio);
        
        // SETUP
        Graphics2D g2d = (Graphics2D) g;

        // BG FILL
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, size.width, size.height);
        
        // IMAGE
        g2d.drawImage(image, imagePosition.x, imagePosition.y, imageSize.width, imageSize.height, null);
        
        // GRID
        g2d.setColor(Color.blue);
        double dx = ((rightGrid - leftGrid) / (float)gridCountX) * imgRatio;
        double dy = ((bottomGrid - topGrid) / (float)gridCountY) * imgRatio;
        
        // across
        if (showCols) {
	        int x = imagePosition.x + left;
	        for (int i = 0; i < gridCountX; i++) {
	            g2d.drawLine(x + (int)(i*dx), imagePosition.y + top, x + (int)(i*dx), imagePosition.y + bottom);
	        }
        }
        
        // down
        if (showRows) {
	        int y = imagePosition.y + top;
	        for (int i = 0; i < gridCountY; i++) {
	            g2d.drawLine(imagePosition.x + left, y+(int)(i*dy), imagePosition.x+right, y+(int)(i*dy));
	        }
        }
        
        // HANDLES
        g2d.setColor(Color.red);
        g2d.drawLine(imagePosition.x, top+imagePosition.y, imagePosition.x+imageSize.width, top+imagePosition.y);
        g2d.drawLine(imagePosition.x, bottom+imagePosition.y, imagePosition.x +imageSize.width, bottom+imagePosition.y);
        g2d.drawLine(left+imagePosition.x, imagePosition.y, left+imagePosition.x, imagePosition.y+imageSize.height);
        g2d.drawLine(right+imagePosition.x, imagePosition.y, right+imagePosition.x, imagePosition.y+imageSize.height);
        
    }
    
    public void setGridCountX(int count) {
        gridCountX = count;
        repaint();
    }
    
    public void setGridCountY(int count) {
        gridCountY = count;
        repaint();
    }
    
    private float getScaledImageRatio() {
        return getScaledImageSize().width / (float)image.getWidth();
    }
    
    private Dimension getScaledImageSize() {
        Dimension size = getSize();
        Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());
        SwingUtil.constrainTo(imageSize, size.width-PADDING*2, size.height-PADDING*2);

        return imageSize;
    }
    
    private Point getScaledImagePosition() {
        Dimension size = getSize();
        Dimension imageSize = getScaledImageSize();
        
        int imgX = (size.width - imageSize.width)/2;
        int imgY = (size.height - imageSize.height)/2;
        
        return new Point(imgX, imgY);
    }
    
    private void updateHandles(MouseEvent e) {
        // Convert
        float imgRatio = getScaledImageRatio();
        Point imgPosition = getScaledImagePosition();
        
        Point location = e.getPoint();
        location.translate(-imgPosition.x, -imgPosition.y);

        location = new Point((int)(location.x / imgRatio), (int)(location.y / imgRatio));

        switch (draggingHandle) {
        case TOP: {
            if (location.y < 0) {location.y = 0;}
            if (location.y > bottomGrid - MINIMUM_GRID_SIZE) {location.y = bottomGrid-MINIMUM_GRID_SIZE;}
            
            topGrid = location.y;
            break;
        }
        case BOTTOM: {
            if (location.y < topGrid + MINIMUM_GRID_SIZE) {location.y = topGrid+MINIMUM_GRID_SIZE;}
            if (location.y > image.getHeight()) {location.y = image.getHeight();}
            
            bottomGrid = location.y;
            break;
        }
        case LEFT: {
            if (location.x < 0) {location.x = 0;}
            if (location.x > rightGrid - MINIMUM_GRID_SIZE) {location.x = rightGrid-MINIMUM_GRID_SIZE;}
            
            leftGrid = location.x;
            break;
        }
        case RIGHT: {
            if (location.x < leftGrid+MINIMUM_GRID_SIZE) {location.x = leftGrid+MINIMUM_GRID_SIZE;}
            if (location.x > image.getWidth()) {location.x = image.getWidth();}
            
            rightGrid = location.x;
            break;
        }
        }
        repaint();
    }
    
    //// 
    // MOUSE LISTENER
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        
        float imgRatio = getScaledImageRatio();
        Point imgPos = getScaledImagePosition();
        
        int top = (int)(topGrid * imgRatio) + imgPos.y;
        int bottom = (int)(bottomGrid * imgRatio) + imgPos.y;
        int left = (int)(leftGrid * imgRatio)+imgPos.x;
        int right = (int)(rightGrid * imgRatio)+imgPos.x;

        int distTop = Math.abs(e.getY() - top);
        int distBottom = Math.abs(e.getY() - bottom);
        int distLeft = Math.abs(e.getX() - left);
        int distRight = Math.abs(e.getX() - right);
        
        int dist = distTop;
        draggingHandle = Handle.TOP;
        
        if (distBottom < dist) {dist = distBottom; draggingHandle = Handle.BOTTOM;}
        if (distLeft < dist) {dist = distLeft; draggingHandle = Handle.LEFT;}
        if (distRight < dist) {dist = distRight; draggingHandle = Handle.RIGHT;}
        
        updateHandles(e);
    }
    
    public void mouseReleased(MouseEvent e) {
        draggingHandle = null;
        
        repaint();
    }

    ////
    // MOUSE MOTION LISTENER
    public void mouseDragged(MouseEvent e) {
        
        updateHandles(e);
        
        repaint();
    }
    public void mouseMoved(MouseEvent e) {
        
        Dimension imgSize = getScaledImageSize();
        Point imgPos = getScaledImagePosition();
        
        boolean insideMap = e.getX() > imgPos.x && e.getX() < imgPos.x+imgSize.width && e.getY() > imgPos.y && e.getY() < imgPos.y + imgSize.height;
        if ((insideMap && showGrid) || (!insideMap && !showGrid)) {
            showGrid = !insideMap;
            repaint();
        }
    }
    
}
