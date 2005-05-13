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
import java.awt.event.MouseAdapter;
import java.text.DecimalFormat;

import javax.swing.JProgressBar;

/**
 */
public class MemoryStatusBar extends JProgressBar {

    private static final Dimension minSize = new Dimension(75, 10);
    
    private static final DecimalFormat format = new DecimalFormat("#,##0.#");
    
    public MemoryStatusBar() {
        setMinimum(0);
        setStringPainted(true);

        new Thread() {
        	public void run() {
        		
        		while (true) {
        			
        			update();
        			try {
        				Thread.sleep(1000);
        			} catch (InterruptedException ie) {
        				break;
        			}
        		}
        	}
        }.start();
        
        addMouseListener(new MouseAdapter(){
        	
        	public void mouseClicked(java.awt.event.MouseEvent e) {
        		
        		System.gc();
        		update();
        	}
        });
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
    
    private void update() {
    	
    	double totalMegs = Runtime.getRuntime().totalMemory()/(1024 * 1024);
    	double freeMegs = Runtime.getRuntime().freeMemory()/(1024 * 1024);
    	setMaximum((int)totalMegs);
    	setValue((int)(totalMegs - freeMegs));
    	setString(format.format(totalMegs - freeMegs) + "M/" + format.format(totalMegs) + "M");
    }
}
