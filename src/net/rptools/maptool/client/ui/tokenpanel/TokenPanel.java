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
package net.rptools.maptool.client.ui.tokenpanel;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.rptools.maptool.client.ZonePoint;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.ModelChangeEvent;
import net.rptools.maptool.model.ModelChangeListener;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;

public class TokenPanel extends JPanel implements ModelChangeListener {

    private ZoneRenderer currentZoneRenderer;
    private JList tokenList;

    public TokenPanel() {
        setLayout(new BorderLayout());
        tokenList = new JList();
        tokenList.setCellRenderer(new TokenListCellRenderer());
        tokenList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // TODO: make this not an aic
                if (e.getClickCount() == 2) {
                    
                    Token token = (Token) tokenList.getSelectedValue();
                    currentZoneRenderer.centerOn(new ZonePoint(token.getX(), token.getY()));
                    currentZoneRenderer.clearSelectedTokens();
                    currentZoneRenderer.selectToken(token.getId());
                }
            }
        });
        
        add(BorderLayout.CENTER, new JScrollPane(tokenList));
    }
    
    public void setZoneRenderer(ZoneRenderer renderer) {
        if (currentZoneRenderer != null) {
            currentZoneRenderer.getZone().removeModelChangeListener(this);
        }
        
        currentZoneRenderer = renderer;
        
        if (currentZoneRenderer != null) {
            currentZoneRenderer.getZone().addModelChangeListener(this);

            repaint();
        }

        // TODO: make this not a aic
        EventQueue.invokeLater(new Runnable(){
            
            public void run() {
                Zone zone = currentZoneRenderer != null ? currentZoneRenderer.getZone() : null;
                tokenList.setModel(new TokenListModel(zone));
            }
        });
    }
    
    ////
    // ModelChangeListener
    public void modelChanged(ModelChangeEvent event) {

        // Tokens are added and removed, just repaint ourself
        ((TokenListModel)tokenList.getModel()).update();
        repaint();
    }
}
