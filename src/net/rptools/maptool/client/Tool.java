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
package net.rptools.maptool.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 */
public abstract class Tool extends JToggleButton {

	private InputMap oldInputMap;
	private ActionMap oldActionMap;
	
    public Tool () {

        setFocusPainted(false);
        addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {

                if (toolbox != null) {
                    if (isSelected()) {
                        
                        toolbox.setSelectedTool(Tool.this);
                    } else {
                        toolbox.unselectTool(Tool.this);
                    }
                }
                setSelected(isSelected());
            }
        });
    }
    
	void addListeners(JComponent comp) {
		
		if (comp == null) {
			return;
		}
		
		if (this instanceof MouseListener) {
			comp.addMouseListener((MouseListener)this);
		}
		if (this instanceof MouseMotionListener) {
			comp.addMouseMotionListener((MouseMotionListener)this);
		}
		if (this instanceof MouseWheelListener) {
			comp.addMouseWheelListener((MouseWheelListener)this);
		}
		
		// Keystrokes
		oldInputMap = comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		oldActionMap = comp.getActionMap();

		Map<KeyStroke, Action> keyActionMap = getKeyActionMap();
		if (keyActionMap != null) {
			
			SwingUtilities.replaceUIActionMap(comp, createActionMap(keyActionMap));
			SwingUtilities.replaceUIInputMap(comp, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, createInputMap(keyActionMap));
		}
	}
	
	void removeListeners(JComponent comp) {
		
		if (comp == null) {
			return;
		}
		
		if (this instanceof MouseListener) {
			comp.removeMouseListener((MouseListener)this);
		}
		if (this instanceof MouseMotionListener) {
			comp.removeMouseMotionListener((MouseMotionListener)this);
		}
		if (this instanceof MouseWheelListener) {
			comp.removeMouseWheelListener((MouseWheelListener)this);
		}

		// Keystrokes
		SwingUtilities.replaceUIActionMap(comp, oldActionMap);
		SwingUtilities.replaceUIInputMap(comp, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, oldInputMap);
	}
	
	protected void attachTo(ZoneRenderer renderer) {
		// No op
	}
	
	protected void detachFrom(ZoneRenderer renderer) {
		// No op
	}

	/**
	 * Tool instances may override this method to supply keystoke mappings
	 * specific to the tool
	 * @return
	 */
	protected Map<KeyStroke, Action> getKeyActionMap() {
		// No op
		return null;
	}
	
	private ToolboxBar toolbox;
	
	public void setToolbox(ToolboxBar toolbox) {
		this.toolbox = toolbox;
	}
	
    public ToolboxBar getToolbox() {
        return toolbox;
    }
    
    private InputMap createInputMap (Map<KeyStroke, Action> keyActionMap) {
    	
    	InputMap inputMap = new InputMap();
    	for (KeyStroke keyStroke : keyActionMap.keySet()) {
    		
    		inputMap.put(keyStroke, keyStroke.toString());
    	}
    	
    	return inputMap;
    }
    
    private ActionMap createActionMap(Map<KeyStroke, Action> keyActionMap) {
    	
    	ActionMap actionMap = new ActionMap();

    	for (KeyStroke keyStroke : keyActionMap.keySet()) {
    		
    		actionMap.put(keyStroke.toString(), keyActionMap.get(keyStroke));
    	}
    	
    	return actionMap;
    }
    
}
