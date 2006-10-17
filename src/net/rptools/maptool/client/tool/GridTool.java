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
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppState;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ui.Scale;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.Grid;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.ZonePoint;

import com.jeta.forms.components.colors.JETAColorWell;
import com.jeta.forms.components.panel.FormPanel;


/**
 */
public class GridTool extends DefaultTool {
    private static final long serialVersionUID = 3760846783148208951L;

    private static enum Size { Increase, Decrease };

    private JSpinner gridSizeSpinner;
    private JTextField gridOffsetXTextField;
    private JTextField gridOffsetYTextField;
    private JETAColorWell colorWell;
    private JSlider zoomSlider;
    
    private FormPanel controlPanel;
    
	private int dragOffsetX;
	private int dragOffsetY;

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

        gridSizeSpinner = controlPanel.getSpinner("gridSize");
        gridSizeSpinner.setModel(new SpinnerNumberModel());
        gridSizeSpinner.addChangeListener(new UpdateGridListener());
        
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
        		ZonePoint zp = new ScreenPoint(renderer.getSize().width/2, renderer.getSize().height/2).convertToZone(renderer);

        		renderer.setScaleIndex(zoomSlider.getValue());
        		renderer.centerOn(zp);
        		copyControlPanelToGrid();
        	}
        });
        
    }
	
	@Override
	protected void installKeystrokes(Map<KeyStroke, Action> actionMap) {
		super.installKeystrokes(actionMap);
		
        actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.SHIFT_DOWN_MASK), new GridSizeAction(Size.Decrease));
        actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.SHIFT_DOWN_MASK), new GridSizeAction(Size.Decrease));
        actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.SHIFT_DOWN_MASK), new GridSizeAction(Size.Increase));
        actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.SHIFT_DOWN_MASK), new GridSizeAction(Size.Increase));
        actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), new GridOffsetAction(Direction.Up));
        actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), new GridOffsetAction(Direction.Left));
        actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), new GridOffsetAction(Direction.Down));
        actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), new GridOffsetAction(Direction.Right));
	}

	private void copyGridToControlPanel() {
		Zone zone = renderer.getZone();

		Grid grid = zone.getGrid();
		
		gridSizeSpinner.setValue(grid.getSize());
		gridOffsetXTextField.setText(Integer.toString(grid.getOffsetX()));
		gridOffsetYTextField.setText(Integer.toString(grid.getOffsetY()));
		colorWell.setColor(new Color(zone.getGridColor()));
		zoomSlider.setValue(renderer.getScaleIndex());
	}
	
	private void copyControlPanelToGrid() {
		
		Zone zone = renderer.getZone();

		Grid grid = zone.getGrid();
		grid.setSize(Math.max((Integer)gridSizeSpinner.getValue(), Grid.MIN_GRID_SIZE));
		grid.setOffset(getInt(gridOffsetXTextField, 0), getInt(gridOffsetYTextField, 0));
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
    	return getInt(component.getText(), defaultValue);
    }
    private int getInt(String value, int defaultValue) {
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
        MapTool.serverCommand().setZoneGridSize(zone.getId(), zone.getGrid().getOffsetX(), zone.getGrid().getOffsetY(), zone.getGrid().getSize(), zone.getGridColor());
        
        super.detachFrom(renderer);
	}

    ////
    // MOUSE LISTENER
	
	public void mousePressed(java.awt.event.MouseEvent e){

		if (SwingUtilities.isLeftMouseButton(e)) {
			
    		ZonePoint zp = new ScreenPoint(e.getX(), e.getY()).convertToZone(renderer);
	        int x = zp.x-renderer.getZone().getGrid().getOffsetX();
	        int y = zp.y-renderer.getZone().getGrid().getOffsetY();
			
	        dragOffsetX = x % renderer.getZone().getGrid().getSize();
	        dragOffsetY = y % renderer.getZone().getGrid().getSize();
		} else {
			super.mousePressed(e);
		}
	}
	
    ////
    // MOUSE MOTION LISTENER
    public void mouseDragged(java.awt.event.MouseEvent e){

    	if (SwingUtilities.isLeftMouseButton(e)) {

    		ZonePoint zp = new ScreenPoint(e.getX(), e.getY()).convertToZone(renderer);
            int x = zp.x - dragOffsetX;
            int y = zp.y - dragOffsetY;

            int gridSize = renderer.getZone().getGrid().getSize();
            
            x %= gridSize;
            y %= gridSize; 

            if (x > 0) {
            	x -= gridSize;
            }
            if (y > 0) {
            	y -= gridSize;
            }
            
            renderer.getZone().getGrid().setOffset(x, y);
            
            renderer.repaint();
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
    	
    	int oldGridSize = renderer.getZone().getGrid().getSize();
    	
        switch (direction) {
        case Increase:
            renderer.adjustGridSize(1);
            
            if (renderer.getZone().getGrid().getSize() != oldGridSize) {
            	renderer.moveGridBy(-cell.x, -cell.y);
            }
            break;
        case Decrease:
            renderer.adjustGridSize(-1);

            if (renderer.getZone().getGrid().getSize() != oldGridSize) {
            	renderer.moveGridBy(cell.x, cell.y);
            }
            break;
        }
        
        copyGridToControlPanel();
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
    private class UpdateGridListener implements KeyListener, ChangeListener {
    	public void keyPressed(KeyEvent e) {
    	}
    	public void keyReleased(KeyEvent e) {
    		copyControlPanelToGrid();
    	}
    	public void keyTyped(KeyEvent e) {
    	}
    	
    	public void stateChanged(ChangeEvent e) {
    		copyControlPanelToGrid();
    	}
    }
}
