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

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * ButtonPanel.java
 * 
 * Trevor Croft 2002 CopyRight
 */
public class OutlookPanel extends JPanel {
    // TODO: Variable size buttons ?
    public static final int BUTTON_HEIGHT = 20;

    public OutlookPanel() {
        m_compList = new ArrayList<JButtonEx>();
        m_pane = createPane();
        add("pane", m_pane);

        setLayout(null);
    }
    
    public void addButton(String label, JComponent component) {
        // Create the button
        JButtonEx button = new JButtonEx(label, m_compList.size(), component);
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                int index = ((JButtonEx) ae.getSource()).getIndex();
                if (m_active.getIndex() == index && index > 0) {
                    index --;
                }
                setActive(index);
            }

        });

        // Update
        component.setVisible(false);
        m_compList.add(button);
        add(label, button);
        add(label, component);

        if (m_compList.size() == 1) {
            setActive(0);
        }
    }

    public int getButtonCount() {
        return m_compList.size();
    }
    
    public void setActive(int index) {
        // Sanity check
        if (index < 0 || index >= m_compList.size()) { return; }

        // Update old active
        if (m_active != null) {
            m_active.getComponent().setVisible(false);
        }

        // Set it
        m_active = m_compList.get(index);
        m_active.getComponent().setVisible(true);

        m_pane.setViewportView(m_active.getComponent());
        repaint();
    }

    public void paint(Graphics g) {
        int y = 0;

        Rectangle bounds = g.getClipBounds();

        g.setColor(getBackground());
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        // TODO: This can be pulled out and put in a layout manager
        for (int count = 0; count < m_compList.size(); count++) {
            JButtonEx button = m_compList.get(count);

            // Position the button
            button.setBounds(0, y, bounds.width, BUTTON_HEIGHT);

            // Update
            y += BUTTON_HEIGHT;

            // Active Panel ?
            if (button == m_active) {
                // Calculate
                int height = getSize().height - (m_compList.size() * BUTTON_HEIGHT);

                // Stretch to take the available space
                m_pane.setBounds(5, y, bounds.width - 6, height - 2);
                m_pane.revalidate();

                y += height;
            }

        }

        // Paint them
        paintChildren(g);
    }

    protected JScrollPane createPane() {
        JScrollPane pane = new JScrollPane();

        return pane;
    }

    // For convenience
    private class JButtonEx extends JButton {
        public JButtonEx(String label, int index, JComponent component) {
            super(label);
            m_index = index;
            setHorizontalAlignment(LEFT);
            m_component = component;
            setFocusPainted(false);
        }

        public int getIndex() {
            return m_index;
        }

        /**
         * We don't ever want the focus 
         */
        public boolean isRequestFocusEnabled() {
            return false;
        }

        public JComponent getComponent() {
            return m_component;
        }

        private int        m_index;
        private JComponent m_component;
    }

    // Internal
    private List<JButtonEx> m_compList;
    private JButtonEx   m_active;
    private JScrollPane m_pane;
}