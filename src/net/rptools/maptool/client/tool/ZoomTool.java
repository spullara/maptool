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
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.rptools.maptool.client.ui.Tool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;


/**
 */
public class ZoomTool extends Tool implements MouseListener {

	public ZoomTool () {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/Tool_Draw_Circle_Zoom.gif"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /* (non-Javadoc)
	 * @see maptool.client.Tool#attachTo(maptool.client.ZoneRenderer)
	 */
	protected void attachTo(ZoneRenderer renderer) {
	}
	
	/* (non-Javadoc)
	 * @see maptool.client.Tool#detachFrom(maptool.client.ZoneRenderer)
	 */
	protected void detachFrom(ZoneRenderer renderer) {
	}

    ////
    // MOUSE LISTENER
	public void mouseClicked(MouseEvent e) {
		
		ZoneRenderer renderer = (ZoneRenderer) e.getSource();
		
		if (SwingUtilities.isRightMouseButton(e)) {
			renderer.zoomIn(e.getX(), e.getY());
		} else {
			renderer.zoomOut(e.getX(), e.getY());
		}
		
	}
	
	public void mouseEntered(MouseEvent e){}
	
	public void mouseExited(MouseEvent e){}
	
	public void mousePressed(MouseEvent e){}
	
	public void mouseReleased(MouseEvent e){}
	
    @Override
    protected void resetTool() {
      // Do nothing here for now
    }

}
