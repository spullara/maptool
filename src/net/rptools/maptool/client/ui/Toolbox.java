/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.client.ui;

import java.awt.EventQueue;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneOverlay;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.language.I18N;

/**
 */
public class Toolbox {
	private ZoneRenderer currentRenderer;
	private Tool currentTool;
	private final Map<Class<? extends Tool>, Tool> toolMap = new HashMap<Class<? extends Tool>, Tool>();
	private final ButtonGroup buttonGroup = new ButtonGroup();

	public void updateTools() {
		for (Tool tool : toolMap.values()) {
			tool.setEnabled(tool.isAvailable());
		}
	}

	public Tool getSelectedTool() {
		return currentTool;
	}

	public Tool getTool(Class<? extends Tool> toolClass) {
		return toolMap.get(toolClass);
	}

	public Tool createTool(Class<? extends Tool> toolClass) {
		Tool tool;
		try {
			Constructor<? extends Tool> constructor = toolClass.getDeclaredConstructor(new Class[] {});
			tool = constructor.newInstance(new Object[] {});
//			tool = constructor.newInstance((Object) null);

			buttonGroup.add(tool);
			toolMap.put(toolClass, tool);
			tool.setToolbox(this);
			return tool;
		} catch (InstantiationException e) {
			MapTool.showError(I18N.getText("msg.error.toolCannotInstantiate", toolClass.getName()), e);
		} catch (IllegalAccessException e) {
			MapTool.showError(I18N.getText("msg.error.toolNeedPublicConstructor", toolClass.getName()), e);
		} catch (NoSuchMethodException nsme) {
			MapTool.showError(I18N.getText("msg.error.toolNeedValidConstructor", toolClass.getName()), nsme);
		} catch (InvocationTargetException ite) {
			MapTool.showError(I18N.getText("msg.error.toolConstructorFailed", toolClass.getName()), ite);
		}
		return null;
	}

	public void setTargetRenderer(final ZoneRenderer renderer) {
		// Need to be synchronous with the timing of the invokes within this method
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				final Tool oldTool = currentTool;

				// Disconnect the current tool from the current renderer
				setSelectedTool((Tool) null);

				// Update the renderer
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						currentRenderer = renderer;
					}
				});
				// Attach the old tool to the new renderer
				setSelectedTool(oldTool);
			}
		});
	}

	public void setSelectedTool(Class<? extends Tool> toolClass) {
		Tool tool = toolMap.get(toolClass);
		if (tool != null && tool.isAvailable()) {
			tool.setSelected(true);
			setSelectedTool(tool);
		}
	}

	public void setSelectedTool(final Tool tool) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				if (tool == currentTool) {
					return;
				}
				if (currentTool != null) {
					if (currentRenderer != null) {
						currentTool.removeListeners(currentRenderer);
//						currentTool.addGridBasedKeys(currentRenderer, false);
						currentTool.detachFrom(currentRenderer);

						if (currentTool instanceof ZoneOverlay) {
							currentRenderer.removeOverlay((ZoneOverlay) currentTool);
						}
					}
				}
				// Update
				currentTool = tool;

				if (currentTool != null) {
					if (currentRenderer != null) {
						// We have a renderer at this point so we can figure out the grid type and add its keystrokes
						// to the PointerTool.
//						currentTool.addGridBasedKeys(currentRenderer, true);
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
