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
import net.rptools.maptool.client.ZonePoint;
import net.rptools.maptool.client.ui.zone.ZoneOverlay;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Label;

/**
 */
public class TextTool extends DefaultTool implements ZoneOverlay {

	private Label selectedLabel;
	
	public TextTool () {
        try {
            setIcon(new ImageIcon(ImageUtil.getImage("net/rptools/maptool/client/image/Tool_Draw_Write.gif")));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
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
    protected Map<KeyStroke, Action> getKeyActionMap() {
    	
		return new HashMap<KeyStroke, Action>() {
			{
				put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), new AbstractAction() {
					public void actionPerformed(ActionEvent e) {

						if (selectedLabel != null) {
							renderer.getZone().removeLabel(selectedLabel.getId());
				    		MapTool.serverCommand().removeLabel(renderer.getZone().getId(), selectedLabel.getId());
							selectedLabel = null;
				    		repaint();
						}
					}
				});
			}};
    }
    
    ////
    // MOUSE
    @Override
    public void mousePressed(MouseEvent e) {

    	ZoneRenderer renderer = (ZoneRenderer) e.getSource();
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
    			
        		ZonePoint zp = ZonePoint.fromScreenPoint(renderer, e.getX(), e.getY());
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
    	
    	super.mousePressed(e);
    }
}
