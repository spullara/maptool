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

import javax.swing.JProgressBar;

/**
 */
public class ProgressStatusBar extends JProgressBar {

    private static final Dimension minSize = new Dimension(75, 10);
    
    int indeterminateCount = 0;
    int determinateCount = 0;
    int totalWork = 0;
    int currentWork = 0;
    
    public ProgressStatusBar() {
        setMinimum(0);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#getMinimumSize()
     */
    public Dimension getMinimumSize() {
        return minSize;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#getPreferredSize()
     */
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }
    
    public void startIndeterminate() {
        indeterminateCount ++;
        setIndeterminate(true);
    }
    
    public void endIndeterminate() {
        indeterminateCount --;
        if (indeterminateCount < 1) {
            setIndeterminate(false);
            
            indeterminateCount = 0;
        }
    }
    
    public void startDeterminate(int totalWork) {
        determinateCount ++;
        this.totalWork += totalWork;
        
        setMaximum(this.totalWork);
    }
    
    public void updateDeterminateProgress(int additionalWorkCompleted) {
        currentWork += additionalWorkCompleted;
        setValue(currentWork);
    }
    
    public void endDeterminate() {
        determinateCount --;
        if (determinateCount == 0) {
            totalWork = 0;
            currentWork = 0;
            
            setMaximum(0);
            setValue(0);
        }
    }
    
}
