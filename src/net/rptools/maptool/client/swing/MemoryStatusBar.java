/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
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
    	
    	setToolTipText("Used Memory:" + (totalMegs - freeMegs) + "M Total Memory:" + totalMegs + "M");
    }
}
