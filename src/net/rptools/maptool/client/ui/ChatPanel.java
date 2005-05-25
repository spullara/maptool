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
package net.rptools.maptool.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;

import net.rptools.maptool.client.MapTool;

public class ChatPanel extends JComponent implements Observer, MouseListener {

    private static final int TEXT_BUFFER = 2;
    
    private BufferedImage backBuffer;
    private boolean renderBorder;
    
    public ChatPanel () {
        setLayout(new BorderLayout());
        setOpaque(false);
        setForeground(Color.white);
        
        MapTool.getMessageList().addObserver(this);
        
        addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Dimension size = getSize();
        if (backBuffer == null || backBuffer.getWidth() != size.width || backBuffer.getHeight() != size.height) {
            renderBackBuffer();
        }
        
        g.drawImage(backBuffer, 0, 0, this);
    }

    private void renderBackBuffer() {
        
        Dimension size = getSize();
        backBuffer = new BufferedImage(size.width, size.height, Transparency.BITMASK);
        
        Graphics2D g = null;
        try {
            g = backBuffer.createGraphics();
            
            g.setColor(getForeground());
            FontMetrics fm = g.getFontMetrics();
    
            // TEXT
            int listSize = MapTool.getMessageList().size();
            int visibleMessageCount = Math.min(listSize, size.height / (fm.getHeight() + TEXT_BUFFER));
            
            List<String> messageList = MapTool.getMessageList().subList(listSize - visibleMessageCount, listSize);
            
            int y = size.height - TEXT_BUFFER - fm.getDescent();
            for (int i = messageList.size()-1; i >= 0; i--) {

                g.drawString(messageList.get(i), TEXT_BUFFER, y);
                
                y -= TEXT_BUFFER + fm.getHeight();
            }
            
            // BORDER
            if (renderBorder) {
                g.setColor(Color.black);
                g.drawRect(0, 0, size.width - 1, size.height - 1);
            }

        } finally {
            if (g != null) {
                g.dispose();
            }
        }
    }
    
    private void refresh() {
        
        backBuffer = null;
        repaint();
    }
    
    ////
    // OBSERVER
    public void update(Observable o, Object arg) {

        refresh();
    }
    
    ////
    // MOUSE LISTENER
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {
        renderBorder = true;
        refresh();
    }
    
    public void mouseExited(MouseEvent e) {
        renderBorder = false;
        refresh();
    }
    
}
