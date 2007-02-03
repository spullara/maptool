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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.SwingUtilities;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneOverlay;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.language.I18N;


/**
 */
public class Toolbox {

	private ZoneRenderer currentRenderer;

	private Tool currentTool;
	
	private Map<Class, Tool> toolMap = new HashMap<Class, Tool>();
	
	private ButtonGroup buttonGroup = new ButtonGroup();
	
	public void updateTools() {
		for (Tool tool : toolMap.values()) {
			tool.setEnabled(tool.isAvailable());
		}
	}
	
	public void setSelectedTool(Class toolClass) {
		Tool tool = toolMap.get(toolClass);
		if (tool != null && tool.isAvailable()) {
			tool.setSelected(true);
		}
	}
	
	public Tool getSelectedTool() {
		return currentTool;
	}
	
	public Tool getTool(Class toolClass) {
		return toolMap.get(toolClass);
	}
	
	public Tool createTool(Class toolClass) {
		
		Tool tool;
		try {
			Constructor constructor = toolClass.getDeclaredConstructor(new Class[]{});
			tool = (Tool) constructor.newInstance(new Object[]{});
			
			buttonGroup.add(tool);
			toolMap.put(toolClass, tool);
			tool.setToolbox(this);
		} catch (InstantiationException e) {
			e.printStackTrace();
			MapTool.showError("Could not instantiate tool class: " + toolClass);
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			MapTool.showError("Constructor must be public for tool: " + toolClass);
			return null;
		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
			MapTool.showError("Constructor must have a public constructor with a Toolbox argument for tool: " + toolClass);
			return null;
		} catch (InvocationTargetException ite) {
			ite.printStackTrace();
			MapTool.showError("Failed in constructor of tool: " + toolClass + " - " + ite);
			return null;
		}
		
		return tool;
	}
	
	public void setTargetRenderer(ZoneRenderer renderer) {
		
		if (currentRenderer != null && currentTool != null) {
			currentTool.removeListeners(currentRenderer);
			currentTool.detachFrom(currentRenderer);
			
			if (renderer != null && currentTool instanceof ZoneOverlay) {
				renderer.removeOverlay((ZoneOverlay)currentTool);
			}
            if (renderer != null) {
                renderer.removeOverlay(MapTool.getFrame().getNotificationOverlay());
            }
		}
		
		currentRenderer = renderer;
		if (currentRenderer != null && currentTool != null) {
			currentTool.addListeners(currentRenderer);
			currentTool.attachTo(currentRenderer);
			
			if (currentTool instanceof ZoneOverlay) {
				renderer.addOverlay((ZoneOverlay) currentTool);
			}
            
            currentRenderer.addOverlay(MapTool.getFrame().getNotificationOverlay());
		}
		
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
            				currentRenderer.removeOverlay((ZoneOverlay)currentTool);
            			}
                    }
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
                    
                    if (MapTool.getFrame() != null) {
                    	MapTool.getFrame().setStatusMessage(I18N.getText(currentTool.getInstructions()));
                    }
				}
			}
		});
	}
}
