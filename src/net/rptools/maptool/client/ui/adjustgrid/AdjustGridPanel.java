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
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.ui.Scale;

public class AdjustGridPanel extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener {

    private static final int MINIMUM_GRID_SIZE = 5;
    
    private static enum Direction { Increase, Decrease };

    private int gridOffsetX = 0;
    private int gridOffsetY = 0;
    private int gridSize = 40;
    private boolean showGrid = true;

    private int mouseX;
    private int mouseY;
    
    private int dragStartX;
	private int dragStartY;
	private int dragOffsetX;
	private int dragOffsetY;
	
	private Color gridColor = Color.red;

	private BufferedImage image;
    
    private Scale scale;
    
    public AdjustGridPanel() {
        setOpaque(false);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("-"), "zoomOut");
        getActionMap().put("zoomOut", new AbstractAction() {
        	public void actionPerformed(ActionEvent e) {
        		zoomOut();
        	}
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("="), "zoomIn");
        getActionMap().put("zoomIn", new AbstractAction() {
        	public void actionPerformed(ActionEvent e) {
        		zoomIn();
        	}
        });
        
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("+"), "zoomRest");
        getActionMap().put("zoomReset", new AbstractAction() {
        	public void actionPerformed(ActionEvent e) {
        		zoomReset();
        	}
        });
    }
    
    public void setZoneImage(BufferedImage image) {
        this.image = image;

        scale = new Scale(image.getWidth(), image.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        if (image == null) {
            return;
        }

        Dimension size = getSize();
        
        scale.initialize(size.width, size.height);

        // CALCULATIONS
        Dimension imageSize = getScaledImageSize();
        Point imagePosition = getScaledImagePosition();
        
        double imgRatio = scale.getScale();

        // SETUP
        Graphics2D g2d = (Graphics2D) g;

        // BG FILL
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, size.width, size.height);
        
        // IMAGE
        g2d.drawImage(image, imagePosition.x, imagePosition.y, imageSize.width, imageSize.height, null);
        g2d.setColor(Color.black);
        g2d.drawRect(imagePosition.x, imagePosition.y, imageSize.width, imageSize.height);
        
        // GRID
        g2d.setColor(gridColor);
        double gridSize = this.gridSize * imgRatio;
        
        // across
        int x = imagePosition.x + (int)(gridOffsetX*imgRatio);
        for (int i = (int)gridSize; i <= imageSize.width; i += gridSize) {
            g2d.drawLine(x + i, imagePosition.y, x + i, imagePosition.y + imageSize.height-1);
        }
        
        // down
        int y = imagePosition.y + (int)(gridOffsetY*imgRatio);
        for (int i = (int)gridSize; i <= imageSize.height; i += gridSize) {
            g2d.drawLine(imagePosition.x, y+i, imagePosition.x+imageSize.width-1, y+i);
        }
    }
    
    public void setGridColor(Color color) {
    	gridColor = color;
    }
    
    @Override
    public boolean isFocusable() {
    	return true;
    }
    public void setGridOffsetX(int offsetX, int offsetY) {
    	gridOffsetX = offsetX;
    	gridOffsetY = offsetY;
    	
        repaint();
    }
    
    private Dimension getScaledImageSize() {

        Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());
        imageSize.width *= scale.getScale();
        imageSize.height *= scale.getScale();
        
        return imageSize;
    }
    
    private Point getScaledImagePosition() {
        
        int imgX = scale.getOffsetX();
        int imgY = scale.getOffsetY();
        
        return new Point(imgX, imgY);
    }
    
    public void zoomIn() {
    	scale.zoomIn(mouseX, mouseY);
    	repaint();
    }
    
    public void zoomOut() {
    	scale.zoomOut(mouseX, mouseY);
    	repaint();
    }
    
    public void zoomReset() {
    	scale.reset();
    	repaint();
    }
    
    public void moveGridBy(int dx, int dy) {

        gridOffsetX += dx;
        gridOffsetY += dy;

        gridOffsetX %= gridSize;
        gridOffsetY %= gridSize;

        if (gridOffsetY > 0) {
            gridOffsetY = gridOffsetY - gridSize;
        }
        
        if (gridOffsetX > 0) {
            gridOffsetX = gridOffsetX - gridSize;
        }

        repaint();
    }

    public void adjustGridSize(int delta) {
        gridSize = Math.max(MINIMUM_GRID_SIZE, gridSize + delta);

        repaint();
    }

    private void adjustGridSize(Direction direction) {

        Dimension imageSize = getScaledImageSize();
        Point imagePosition = getScaledImagePosition();

        double gridSize = this.gridSize * scale.getScale();
        
        int cellX = (int)((mouseX - imagePosition.x) / gridSize);
        int cellY = (int)((mouseY - imagePosition.y) / gridSize);

        switch (direction) {
        case Increase:
            adjustGridSize(1);
            
            if (this.gridSize != gridSize) {
            	moveGridBy(-cellX, -cellY);
            }
            break;
        case Decrease:
            adjustGridSize(-1);

            if (this.gridSize != gridSize) {
            	moveGridBy(cellX, cellY);
            }
            break;
        }
    }
    
    //// 
    // MOUSE LISTENER
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        
		mouseX = e.getX();
		mouseY = e.getY();

		dragStartX = e.getX();
		dragStartY = e.getY();
		
        Point imagePosition = getScaledImagePosition();

        int x = (int)((e.getX() - imagePosition.x)/scale.getScale());
        int y = (int)((e.getY() - imagePosition.y)/scale.getScale());
		
        dragOffsetX = x % gridSize;
        dragOffsetY = y % gridSize;
    }
    
    public void mouseReleased(MouseEvent e) {
    }

    ////
    // MOUSE MOTION LISTENER
    public void mouseDragged(MouseEvent e) {
        
    	if (SwingUtilities.isLeftMouseButton(e)) {

            Point imagePosition = getScaledImagePosition();

            int x = (int)((e.getX() - imagePosition.x)/scale.getScale());
            int y = (int)((e.getY() - imagePosition.y)/scale.getScale());
    		
            gridOffsetX = x % gridSize;
            gridOffsetY = y % gridSize;
            
            if (gridOffsetY > 0) {
                gridOffsetY = gridOffsetY - gridSize;
            }
            
            if (gridOffsetX > 0) {
                gridOffsetX = gridOffsetX - gridSize;
            }

            repaint();
            System.out.format("%d %d, %d %d\n", x, y, gridOffsetX, gridOffsetY);
//	        
//            moveGridBy(dx, dy);
    	} else {
    		int offsetX = scale.getOffsetX() + e.getX() - dragStartX;
    		int offsetY = scale.getOffsetY() + e.getY() - dragStartY;
    		
    		scale.setOffset(offsetX, offsetY);
    		
    		dragStartX = e.getX();
    		dragStartY = e.getY();

    		repaint();
    	}
    }
    public void mouseMoved(MouseEvent e) {
        
        Dimension imgSize = getScaledImageSize();
        Point imgPos = getScaledImagePosition();
        
        boolean insideMap = e.getX() > imgPos.x && e.getX() < imgPos.x+imgSize.width && e.getY() > imgPos.y && e.getY() < imgPos.y + imgSize.height;
        if ((insideMap && showGrid) || (!insideMap && !showGrid)) {
            showGrid = !insideMap;
            repaint();
        }
        
		mouseX = e.getX();
		mouseY = e.getY();
    }
    
    ////
    // MOUSE WHEEL LISTENER
    public void mouseWheelMoved(MouseWheelEvent e) {

        if (SwingUtil.isControlDown(e)) {
	    	if (e.getWheelRotation() > 0) {
	    		scale.zoomOut(e.getX(), e.getY());
	    	} else {
	    		scale.zoomIn(e.getX(), e.getY());
	    	}
        } else {

            if (e.getWheelRotation() > 0) {
                
                adjustGridSize(Direction.Increase);
            } else {
                
            	adjustGridSize(Direction.Decrease);
            }
        }
    	repaint();
    }

/*
    private final Map<KeyStroke, Action> KEYSTROKES = new HashMap<KeyStroke, Action>() {
	    {
	        put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.SHIFT_DOWN_MASK), new GridSizeAction(Size.Decrease));
	        put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.SHIFT_DOWN_MASK), new GridSizeAction(Size.Decrease));
	        put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.SHIFT_DOWN_MASK), new GridSizeAction(Size.Increase));
	        put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.SHIFT_DOWN_MASK), new GridSizeAction(Size.Increase));
	        put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), new GridOffsetAction(GridOffsetAction.Direction.Up));
	        put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), new GridOffsetAction(GridOffsetAction.Direction.Left));
	        put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), new GridOffsetAction(GridOffsetAction.Direction.Down));
	        put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), new GridOffsetAction(GridOffsetAction.Direction.Right));
	    }
    };
    protected Map<KeyStroke, Action> getKeyActionMap() {
        return KEYSTROKES;
    }
    
    private final class GridSizeAction extends AbstractAction {
        private final Size size;
        public GridSizeAction(Size size) {
            this.size = size;
        }

        public void actionPerformed(ActionEvent e) {
            ZoneRenderer renderer = (ZoneRenderer) e.getSource();
            adjustGridSize(renderer, size);
        }
    }
    
    private static final class GridOffsetAction extends AbstractAction {
        private static enum Direction { Left, Right, Up, Down };
        private final Direction direction;

        public GridOffsetAction(Direction direction) {
            this.direction = direction;
        }

        public void actionPerformed(ActionEvent e) {
            ZoneRenderer renderer = (ZoneRenderer) e.getSource();
            switch (direction) {
            case Left:
                renderer.moveGridBy(-1, 0);
                break;
            case Right:
                renderer.moveGridBy(1, 0);
                break;
            case Up:
                renderer.moveGridBy(0, -1);
                break;
            case Down:
                renderer.moveGridBy(0, 1);
                break;
            }
        }
    }
 */    
}
