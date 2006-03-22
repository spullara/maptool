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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppState;
import net.rptools.maptool.client.CellPoint;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ui.Scale;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Zone;

import com.jeta.forms.components.colors.JETAColorWell;
import com.jeta.forms.components.panel.FormPanel;


/**
 */
public class GridTool extends DefaultTool {
    private static final long serialVersionUID = 3760846783148208951L;

    private static enum Size { Increase, Decrease };

    private JTextField gridSizeTextField;
    private JTextField gridOffsetXTextField;
    private JTextField gridOffsetYTextField;
    private JETAColorWell colorWell;
    private JSlider zoomSlider;
    
    private FormPanel controlPanel;
    
    private int dragStartX;
	private int dragStartY;

	private int mouseX;
	private int mouseY;

	private boolean oldShowGrid;
	
	public GridTool () {
        try {
            setIcon(new ImageIcon(ImageUtil.getImage("net/rptools/maptool/client/image/grid.gif")));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
        // Create the control panel
        controlPanel = new FormPanel("net/rptools/maptool/client/ui/forms/adjustGridControlPanel.jfrm");
        controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        gridSizeTextField = controlPanel.getTextField("gridSize");
        gridSizeTextField.addKeyListener(new UpdateGridListener());
        
        gridOffsetXTextField = controlPanel.getTextField("offsetX");
        gridOffsetXTextField.addKeyListener(new UpdateGridListener());

        gridOffsetYTextField = controlPanel.getTextField("offsetY");
        gridOffsetYTextField.addKeyListener(new UpdateGridListener());
        
        colorWell = (JETAColorWell) controlPanel.getComponentByName("colorWell");
        colorWell.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		copyControlPanelToGrid();
        	}
        });

        zoomSlider = (JSlider) controlPanel.getComponentByName("zoomSlider");
        zoomSlider.setMinimum(0);
        zoomSlider.setMaximum(Scale.getScaleCount());
        zoomSlider.addChangeListener(new ChangeListener(){
        	public void stateChanged(ChangeEvent e) {
        		renderer.setScaleIndex(zoomSlider.getValue());
        		copyControlPanelToGrid();
        	}
        });
    }
	
	private void copyGridToControlPanel() {
		Zone zone = renderer.getZone();

		gridSizeTextField.setText(Integer.toString(zone.getGridSize()));
		gridOffsetXTextField.setText(Integer.toString(zone.getGridOffsetX()));
		gridOffsetYTextField.setText(Integer.toString(zone.getGridOffsetY()));
		colorWell.setColor(new Color(zone.getGridColor()));
		zoomSlider.setValue(renderer.getScaleIndex());
	}
	
	private void copyControlPanelToGrid() {
		
		Zone zone = renderer.getZone();

		zone.setGridSize(getInt(gridSizeTextField, Zone.MIN_GRID_SIZE));
		zone.setGridOffsetX(getInt(gridOffsetXTextField, 0));
		zone.setGridOffsetY(getInt(gridOffsetYTextField, 0));
		zone.setGridColor(colorWell.getColor().getRGB());
	
		renderer.repaint();
	}

    @Override
    public String getTooltip() {
        return "Show/Hide the map grid";
    }
    
    @Override
    public String getInstructions() {
    	return "Left Click: offset grid, Mouse Wheel: scale grid";
    }

    private int getInt(JTextComponent component, int defaultValue) {
    	String value = component.getText();
    	return value.length() > 0 ? Integer.parseInt(value.trim()) : defaultValue;
    }
    
    /* (non-Javadoc)
	 * @see maptool.client.Tool#attachTo(maptool.client.ZoneRenderer)
	 */
	protected void attachTo(ZoneRenderer renderer) {
		
		oldShowGrid = AppState.isShowGrid();
		AppState.setShowGrid(true);
		
		MapTool.getFrame().showControlPanel(controlPanel);
		renderer.repaint();
		
		super.attachTo(renderer);

		copyGridToControlPanel();
	}
	
	/* (non-Javadoc)
	 * @see maptool.client.Tool#detachFrom(maptool.client.ZoneRenderer)
	 */
	protected void detachFrom(ZoneRenderer renderer) {
		AppState.setShowGrid(oldShowGrid);
		MapTool.getFrame().hideControlPanel();
		renderer.repaint();
		
		// Commit the grid size change
        Zone zone = renderer.getZone();
        MapTool.serverCommand().setZoneGridSize(zone.getId(), zone.getGridOffsetX(), zone.getGridOffsetY(), zone.getGridSize(), zone.getGridColor());
        
        super.detachFrom(renderer);
	}

    ////
    // MOUSE LISTENER
	
	public void mousePressed(java.awt.event.MouseEvent e){

		if (SwingUtilities.isLeftMouseButton(e)) {
			dragStartX = e.getX();
			dragStartY = e.getY();
		} else {
			super.mousePressed(e);
		}
	}
	
    ////
    // MOUSE MOTION LISTENER
    public void mouseDragged(java.awt.event.MouseEvent e){

    	if (SwingUtilities.isLeftMouseButton(e)) {
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
	        
	        copyGridToControlPanel();
    	} else {
    		super.mouseDragged(e);
    	}
    }
    
    public void mouseMoved(java.awt.event.MouseEvent e){
    	mouseX = e.getX();
    	mouseY = e.getY();
    }
    
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
                
                adjustGridSize(renderer, Size.Increase);
            } else {
                
            	adjustGridSize(renderer, Size.Decrease);
            }
        }
    }
    
    private void adjustGridSize(ZoneRenderer renderer, Size direction) {
    	
    	CellPoint cell = renderer.getCellAt(new ScreenPoint(mouseX, mouseY));
        if (cell == null) { return; }
    	
    	int oldGridSize = renderer.getZone().getGridSize();
    	
        switch (direction) {
        case Increase:
            renderer.adjustGridSize(1);
            
            if (renderer.getZone().getGridSize() != oldGridSize) {
            	renderer.moveGridBy(-cell.x, -cell.y);
            }
            break;
        case Decrease:
            renderer.adjustGridSize(-1);

            if (renderer.getZone().getGridSize() != oldGridSize) {
            	renderer.moveGridBy(cell.x, cell.y);
            }
            break;
        }
        
        copyGridToControlPanel();
    }
    
    private final Map<KeyStroke, Action> KEYSTROKES = new HashMap<KeyStroke, Action>() {
	    {
	        put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.SHIFT_DOWN_MASK), new GridSizeAction(Size.Decrease));
	        put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.SHIFT_DOWN_MASK), new GridSizeAction(Size.Decrease));
	        put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.SHIFT_DOWN_MASK), new GridSizeAction(Size.Increase));
	        put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.SHIFT_DOWN_MASK), new GridSizeAction(Size.Increase));
	        put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), new GridOffsetAction(Direction.Up));
	        put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), new GridOffsetAction(Direction.Left));
	        put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), new GridOffsetAction(Direction.Down));
	        put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), new GridOffsetAction(Direction.Right));
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
            copyGridToControlPanel();
        }
    }
    
    private static enum Direction { Left, Right, Up, Down };
    private class GridOffsetAction extends AbstractAction {
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

            copyGridToControlPanel();
        }
    }
    
    ////
    // ACTIONS
    private class UpdateGridListener implements KeyListener {
    	public void keyPressed(KeyEvent e) {
    	}
    	public void keyReleased(KeyEvent e) {
    		copyControlPanelToGrid();
    	}
    	public void keyTyped(KeyEvent e) {
    	}
    }
}
