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
package net.rptools.maptool.client.tool;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import net.rptools.maptool.client.MapToolClient;
import net.rptools.maptool.client.Tool;
import net.rptools.maptool.client.ZoneRenderer;
import net.rptools.maptool.client.swing.SwingUtil;
import net.rptools.maptool.model.Zone;


/**
 */
public class GridTool extends Tool implements MouseListener, MouseMotionListener, MouseWheelListener {
    private static final long serialVersionUID = 3760846783148208951L;

    private int dragStartX;
	private int dragStartY;

	public GridTool () {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/grid.gif"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /* (non-Javadoc)
	 * @see maptool.client.Tool#attachTo(maptool.client.ZoneRenderer)
	 */
	protected void attachTo(ZoneRenderer renderer) {
		renderer.setGridVisible(true);
		renderer.setMouseWheelEnabled(false);
	}
	
	/* (non-Javadoc)
	 * @see maptool.client.Tool#detachFrom(maptool.client.ZoneRenderer)
	 */
	protected void detachFrom(ZoneRenderer renderer) {
		renderer.setGridVisible(false);
		
		// Commit the grid size change
		if (MapToolClient.isConnected()) {
			Zone zone = renderer.getZone();
	        MapToolClient.getInstance().getConnection().callMethod(MapToolClient.COMMANDS.setZoneGridSize.name(), zone.getId(), zone.getGridOffsetX(), zone.getGridOffsetY(), zone.getGridSize());
		}
	}

    ////
    // MOUSE LISTENER
	public void mouseClicked(java.awt.event.MouseEvent e) {}
	
	public void mouseEntered(java.awt.event.MouseEvent e){}
	
	public void mouseExited(java.awt.event.MouseEvent e){}
	
	public void mousePressed(java.awt.event.MouseEvent e){
		
		dragStartX = e.getX();
		dragStartY = e.getY();
	}
	
	public void mouseReleased(java.awt.event.MouseEvent e){}
	
    ////
    // MOUSE MOTION LISTENER
    public void mouseDragged(java.awt.event.MouseEvent e){
        
        int dx = e.getX() - dragStartX;
        int dy = e.getY() - dragStartY;

        dragStartX = e.getX();
        dragStartY = e.getY();

        ZoneRenderer renderer = (ZoneRenderer) e.getSource();
        
        if (SwingUtil.isControlDown(e)) {
            renderer.moveViewBy(dx, dy);
        } else {
            renderer.moveGridBy(dx, dy);
        }
    }
    
    public void mouseMoved(java.awt.event.MouseEvent e){}
    
    ////
    // MOUSE WHEEL LISTENER
    /* (non-Javadoc)
     * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
     */
    public void mouseWheelMoved(MouseWheelEvent e) {

        ZoneRenderer renderer = (ZoneRenderer) e.getSource();
        
        if (SwingUtil.isControlDown(e)) {
        
            if (e.getWheelRotation() > 0) {
                
                renderer.zoomOut(e.getX(), e.getY());
            } else {
                
                renderer.zoomIn(e.getX(), e.getY());
            }
        } else {

            
            if (e.getWheelRotation() > 0) {
                
                renderer.adjustGridSize(1);
            } else {
                
                renderer.adjustGridSize(-1);
            }
        }
    }
    
    private static final Map<KeyStroke, Action> KEYSTROKES = new HashMap<KeyStroke, Action>();
    static {
        KEYSTROKES.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.SHIFT_DOWN_MASK), new GridSizeAction(GridSizeAction.Size.Decrease));
        KEYSTROKES.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.SHIFT_DOWN_MASK), new GridSizeAction(GridSizeAction.Size.Decrease));
        KEYSTROKES.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.SHIFT_DOWN_MASK), new GridSizeAction(GridSizeAction.Size.Increase));
        KEYSTROKES.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.SHIFT_DOWN_MASK), new GridSizeAction(GridSizeAction.Size.Increase));
        KEYSTROKES.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), new GridOffsetAction(GridOffsetAction.Direction.Up));
        KEYSTROKES.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), new GridOffsetAction(GridOffsetAction.Direction.Left));
        KEYSTROKES.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), new GridOffsetAction(GridOffsetAction.Direction.Down));
        KEYSTROKES.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), new GridOffsetAction(GridOffsetAction.Direction.Right));
    }
    protected Map<KeyStroke, Action> getKeyActionMap() {
        return KEYSTROKES;
    }
    
    private static final class GridSizeAction extends AbstractAction {
        private static enum Size { Increase, Decrease };
        private final Size size;
        public GridSizeAction(Size size) {
            this.size = size;
        }

        public void actionPerformed(ActionEvent e) {
            ZoneRenderer renderer = (ZoneRenderer) e.getSource();
            switch (size) {
            case Increase:
                renderer.adjustGridSize(1);
                break;
            case Decrease:
                renderer.adjustGridSize(-1);
                break;
            }
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
}
