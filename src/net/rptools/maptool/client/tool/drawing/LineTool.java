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

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.rptools.maptool.model.drawing.Point;


/**
 * Tool for drawing freehand lines.
 */
public class LineTool extends AbstractLineTool implements MouseMotionListener {
    private static final long serialVersionUID = 3258132466219627316L;

    private Point tempPoint; 

    public LineTool() {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/Tool_Draw_Line.gif"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    ////
    // MOUSE LISTENER
    public void mouseClicked(java.awt.event.MouseEvent e) {}
    
    public void mouseEntered(java.awt.event.MouseEvent e){}
    
    public void mouseExited(java.awt.event.MouseEvent e){}
    
    public void mousePressed(java.awt.event.MouseEvent e){
        int x = e.getX();
        int y = e.getY();
        
        
        if (getLine() == null) {

        	startLine(x, y);
            setIsEraser(SwingUtilities.isRightMouseButton(e));
            zoneRenderer.setMouseWheelEnabled(false);

        } else {
        	
        	if (SwingUtilities.isLeftMouseButton(e)) {

        		stopLine(x, y);
            	zoneRenderer.setMouseWheelEnabled(true);
            	
        	} else if (SwingUtilities.isRightMouseButton(e)) {
        		
                tempPoint = null;
        	}
        }
        
    }
    
    public void mouseReleased(java.awt.event.MouseEvent e){ 
    	
    }

    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        if (getLine() != null) {
            if (tempPoint != null) removePoint(tempPoint);
            tempPoint = addPoint(x, y);
        }
   }

}
