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

import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.rptools.lib.util.ImageUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ZonePoint;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Label;

/**
 */
public class TextTool extends DefaultTool {

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
    
    ////
    // MOUSE
    @Override
    public void mousePressed(MouseEvent e) {

    	ZoneRenderer renderer = (ZoneRenderer) e.getSource();
    	if (SwingUtilities.isLeftMouseButton(e)) {
    		
    		String text = JOptionPane.showInputDialog(MapTool.getFrame(), "Label Text");
    		if (text == null) {
    			return;
    		}
    		
    		ZonePoint zp = ZonePoint.fromScreenPoint(renderer, e.getX(), e.getY());
    		Label label = new Label(text, zp.x, zp.y);
    		renderer.getZone().putLabel(label);
    		MapTool.serverCommand().putLabel(renderer.getZone().getId(), label);
    	}
    	
    	super.mousePressed(e);
    }
}
