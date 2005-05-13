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

import javax.swing.JToolBar;
import javax.swing.SwingUtilities;


/**
 */
public class ToolboxBar extends JToolBar {

	private ZoneRenderer currentRenderer;

	private Tool currentTool;
	
	public ToolboxBar () {
		setFloatable(false);
        setRollover(true);
	}
	
	public void setTargetRenderer(ZoneRenderer renderer) {
		
		if (currentRenderer != null && currentTool != null) {
			currentTool.removeListeners(currentRenderer);
			currentTool.detachFrom(currentRenderer);
			
			if (renderer != null && currentTool instanceof ZoneOverlay) {
				renderer.removeOverlay(null);
			}
		}
		
		currentRenderer = renderer;
		if (currentRenderer != null && currentTool != null) {
			currentTool.addListeners(currentRenderer);
			currentTool.attachTo(currentRenderer);
			
			if (currentTool instanceof ZoneOverlay) {
				renderer.addOverlay((ZoneOverlay) currentTool);
			}
		}
		
	}
	
	public void unselectTool(final Tool tool) {
		
		if (tool != currentTool) {
			return;
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				if (currentTool != null) {
					currentTool.removeListeners(currentRenderer);
					currentTool.detachFrom(currentRenderer);
					currentTool.setSelected(false);
				}
				
				if (currentTool instanceof ZoneOverlay) {
					currentRenderer.removeOverlay(null);
				}

				currentTool = null;
			}
		});
	}
	
	public void setSelectedTool(final Tool tool) {

		if (tool == currentTool) {
			return;
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				if (currentTool != null) {
                    if (currentRenderer != null) {
    					currentTool.removeListeners(currentRenderer);
    					currentTool.detachFrom(currentRenderer);
                        
            			if (currentTool instanceof ZoneOverlay) {
            				currentRenderer.removeOverlay(null);
            			}
                    }

        			currentTool.setSelected(false);
				}

				// Update
				currentTool = tool;
				
				if (currentTool != null) {
                    if (currentRenderer != null) {
    					currentTool.addListeners(currentRenderer);
    					currentTool.attachTo(currentRenderer);
    					
            			if (currentTool instanceof ZoneOverlay) {
            				currentRenderer.addOverlay((ZoneOverlay) currentTool);
            			}
                    }
                    

					currentTool.setSelected(true);
				}
			}
		});
	}
	
	public void addTool(Tool tool) {
		
		add(tool);
		
		tool.setToolbox(this);
		
		if (currentTool == null) {
			setSelectedTool(tool);
		}
	}
}
