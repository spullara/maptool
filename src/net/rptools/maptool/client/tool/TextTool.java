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

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ui.zone.ZoneOverlay;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Label;
import net.rptools.maptool.model.ZonePoint;

/**
 */
public class TextTool extends DefaultTool implements ZoneOverlay {

	private Label selectedLabel;
	
	private int dragStartX;
	private int dragStartY;
	private boolean isDragging;
	
	public TextTool () {
        try {
            setIcon(new ImageIcon(ImageUtil.getImage("net/rptools/maptool/client/image/tool/text.png")));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
	@Override
	protected void attachTo(ZoneRenderer renderer) {
		renderer.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		super.attachTo(renderer);
	}

	@Override
	protected void detachFrom(ZoneRenderer renderer) {
		renderer.setCursor(Cursor.getDefaultCursor());
		super.detachFrom(renderer);
	}
	
    @Override
    public String getTooltip() {
        return "Put text onto the zone";
    }
    
    @Override
    public String getInstructions() {
    	return "tool.label.instructions";
    }
    
    public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
    	
    	if (selectedLabel != null) {
    		AppStyle.selectedBorder.paintWithin(g, renderer.getLabelBounds(selectedLabel));
    	}
    }

    @Override
    protected void installKeystrokes(Map<KeyStroke, Action> actionMap) {
    	super.installKeystrokes(actionMap);
    	
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {

				if (selectedLabel != null) {
					renderer.getZone().removeLabel(selectedLabel.getId());
		    		MapTool.serverCommand().removeLabel(renderer.getZone().getId(), selectedLabel.getId());
					selectedLabel = null;
		    		repaint();
				}
			}
		});
    }
    
    ////
    // MOUSE
    @Override
    public void mousePressed(MouseEvent e) {
		dragStartX = e.getX();
		dragStartY = e.getY();
		
		super.mousePressed(e);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {

    	if (isDragging) {
    		isDragging = false;
    		return;
    	}
    	
    	if (SwingUtilities.isLeftMouseButton(e)) {

    		Label label = renderer.getLabelAt(e.getX(), e.getY());
    		
    		if (label == null && selectedLabel != null) {
    			selectedLabel = null;
    			renderer.repaint();
    			return;
    		}
    		
    		if (label != selectedLabel) {
    			selectedLabel = null;
    		}
    		
    		if (label == null) {
    			
        		ZonePoint zp = new ScreenPoint(e.getX(), e.getY()).convertToZone(renderer);
    			label = new Label("", zp.x, zp.y);
    		} else {
    			
    			if (selectedLabel == null) {
    				selectedLabel = label;
    				renderer.repaint();
    				return;
    			}
    		}
    		
    		String text = JOptionPane.showInputDialog(MapTool.getFrame(), "Label Text", label.getLabel());
    		if (text == null) {
    			return;
    		}
    		
    		label.setLabel(text);
    		
    		renderer.getZone().putLabel(label);
    		MapTool.serverCommand().putLabel(renderer.getZone().getId(), label);
    		
    		selectedLabel = null;
    		renderer.repaint();
    	}
    	
    	super.mouseReleased(e);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
    	
    	if (selectedLabel == null) {
    		return;
    	}

    	isDragging = true;
    	
    	int dx = e.getX() - dragStartX;
    	int dy = e.getY() - dragStartY;
    	
    	ScreenPoint sp = ScreenPoint.fromZonePoint(renderer, new ZonePoint(selectedLabel.getX(), selectedLabel.getY()));
    	sp.x += dx;
    	sp.y += dy;
    	ZonePoint zp = sp.convertToZone(renderer);
    	
    	selectedLabel.setX(zp.x);
    	selectedLabel.setY(zp.y);
    	
    	dragStartX = e.getX();
    	dragStartY = e.getY();
    	
    	renderer.repaint();
    }
}
