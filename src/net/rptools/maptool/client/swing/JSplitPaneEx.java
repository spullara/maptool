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

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

/**
 * Extension that provides the ability to show/hide one of the panels
 * in the split pane
 */
public class JSplitPaneEx extends JSplitPane {

    private int lastVisibleSize;

    private static final int HORIZONTAL = 0;
    private static final int VERITICAL = 1;
    
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    
    private boolean isEitherHidden;
    
    private int dividerSize;
    
    public void setInitialDividerPosition (int position) {
        lastVisibleSize = position;
        setDividerLocation(position);
    }

    public int getDividerLocation() {
        return getDividerSize() > 0 ? super.getDividerLocation() : lastVisibleSize;
    }
    
    public boolean isTopHidden() {
        return !getTopComponent().isVisible();
    }
    
    public boolean isLeftHidden() {
        return !getLeftComponent().isVisible();
    }
    
    public boolean isBottomHidden() {
        return !getBottomComponent().isVisible();
    }
    
    public boolean isRightHidden() {
        return !getRightComponent().isVisible();
    }
    
    public void hideLeft() {
        hideInternal((JComponent) getLeftComponent(), LEFT, HORIZONTAL);
    }
    
    public void hideTop() {
        hideInternal((JComponent) getLeftComponent(), LEFT, VERITICAL);
    }
    
    public void showLeft() {
        showInternal((JComponent) getLeftComponent(), LEFT, HORIZONTAL);
    }
    
    public void showTop() {
        showInternal((JComponent) getLeftComponent(), LEFT, VERITICAL);
    }

    public void hideRight() {
        hideInternal((JComponent) getRightComponent(), RIGHT, HORIZONTAL);
    }
    
    public void hideBottom() {
        hideInternal((JComponent) getRightComponent(), RIGHT, VERITICAL);
    }
    
    public void showRight() {
        showInternal((JComponent) getRightComponent(), RIGHT, HORIZONTAL);
    }
    
    public void showBottom() {
        showInternal((JComponent) getRightComponent(), RIGHT, VERITICAL);
    }
    
    public int getLastVisibleSize() {
        return lastVisibleSize;
    }
    private synchronized void hideInternal(JComponent component, int which, int orientation) {
        if (isEitherHidden) {
            return;
        }
        
        Dimension componentSize = component.getSize();
        Dimension mySize = getSize();
        
        lastVisibleSize = orientation == HORIZONTAL ? componentSize.width : componentSize.height;
        if (which == RIGHT) {
            lastVisibleSize = (orientation == HORIZONTAL ? mySize.width : mySize.height) - lastVisibleSize - getDividerSize(); 
        }
        component.setVisible(false);
        dividerSize = getDividerSize();

        setDividerSize(0);
        
        isEitherHidden = true;
    }
    
    private synchronized void showInternal(JComponent component, int which, int orientation) {
        if (!isEitherHidden) {
            return;
        }
        
        if (lastVisibleSize == 0) {
            
            Dimension mySize = getSize();
            Dimension preferredSize = component.getSize();

            if (preferredSize.width == 0 && preferredSize.height == 0) {
                preferredSize = component.getMinimumSize();
            }
            
            lastVisibleSize = orientation == HORIZONTAL ? preferredSize.width : preferredSize.height;
            if (which == RIGHT) {
                lastVisibleSize = (orientation == HORIZONTAL ? mySize.width : mySize.height) - lastVisibleSize; 
            }
        }
        setDividerSize(dividerSize);
        setDividerLocation(lastVisibleSize);

        component.setVisible(true);
        isEitherHidden = false;
    }
    
}
