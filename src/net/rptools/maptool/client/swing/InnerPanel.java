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
package net.rptools.maptool.client.swing;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class InnerPanel extends JPanel {

    private int dragx, dragy;
   
    protected float alpha = 0.75f;
   
    Image backbuffer;
       
    public InnerPanel () {

        addMouseListener(new MouseAdapter(){
          
           
            public void mousePressed(MouseEvent e) {
                dragx = e.getX();
                dragy = e.getY();
               
               
                JLayeredPane layeredPane = JLayeredPane.getLayeredPaneAbove(InnerPanel.this);
                layeredPane.moveToFront(InnerPanel.this);
            }
           
        });
       
       
        addMouseMotionListener(new MouseMotionAdapter(){
          
            /* (non-Javadoc)
             * @see java.awt.event.MouseMotionAdapter#mouseDragged(java.awt.event.MouseEvent)
             */
            public void mouseDragged(MouseEvent e) {
               
                JLayeredPane layeredPane = JLayeredPane.getLayeredPaneAbove(InnerPanel.this);
                e = SwingUtilities.convertMouseEvent(InnerPanel.this, e, layeredPane);

                Dimension size = InnerPanel.this.getSize();
                Dimension psize = layeredPane.getSize();
               
                int x = Math.max(0, e.getX() - dragx);
                int y = Math.max(0, e.getY() - dragy);
               
                if (x > psize.width - size.width) x = psize.width - size.width;
                if (y > psize.height - size.height) y = psize.height - size.height;
               
                setLocation(x, y);
               
            }
        });
    }

//    /* (non-Javadoc)
//     * @see java.awt.Component#isOpaque()
//     */
//    public boolean isOpaque() {
//        return false;
//    }
//   
//    /* (non-Javadoc)
//     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
//     */
//    protected void paintComponent(Graphics g) {
//       
//        g.setColor(getBackground());
//        g.fillRect(0, 0, getSize().width, getSize().height);
//    }
//   
//    /* (non-Javadoc)
//     * @see java.awt.Container#paint(java.awt.Graphics)
//     */
//    public void paint(Graphics graphics) {
//       
//        Dimension size = getSize();
//        if (backbuffer == null || backbuffer.getWidth(this) != size.width || backbuffer.getHeight(this) != size.height) {
//           
//            GraphicsConfiguration gc = getGraphicsConfiguration();
//            backbuffer = gc.createCompatibleImage(size.width, size.height, Transparency.BITMASK);
//        }
//       
//        Graphics g = backbuffer.getGraphics();
//       
//        // TODO Auto-generated method stub
//        super.paint(g);
//
//        Composite oldComposite = ((Graphics2D)graphics).getComposite();
//        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
//
//        ((Graphics2D)graphics).setComposite(composite);
//        graphics.drawImage(backbuffer, 0, 0, this);
//        ((Graphics2D)graphics).setComposite(oldComposite);
//       
//        g.dispose();
//    }
//   
}