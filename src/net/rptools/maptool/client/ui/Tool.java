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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.language.I18N;

/**
 */
public abstract class Tool extends JToggleButton implements ChangeListener, ActionListener {

	private Toolbox toolbox;
    
    protected Map<KeyStroke, Action> keyActionMap = new HashMap<KeyStroke, Action>();
  
    public Tool () {
      
        // Map the escape key reset this tool.
    	installKeystrokes(keyActionMap);

        addChangeListener(this);
        addActionListener(this);
        
        setToolTipText(I18N.getText(getTooltip()));
        setFocusable(false);
        setFocusPainted(false);
    }

    void setToolbox(Toolbox toolbox) {
    	this.toolbox = toolbox;
    }
    
	protected void installKeystrokes(Map<KeyStroke, Action> actionMap) {
		actionMap.put(KeyStroke.getKeyStroke("ESCAPE"), new EscapeAction());		
	}
	
    public abstract String getTooltip();
    public abstract String getInstructions();
    
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
		comp.setActionMap(createActionMap(keyActionMap));
		comp.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, createInputMap(keyActionMap));
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

	}
	
	protected void attachTo(ZoneRenderer renderer) {
		// No op
	}
	
	protected void detachFrom(ZoneRenderer renderer) {
		// No op
	}

    private InputMap createInputMap (Map<KeyStroke, Action> keyActionMap) {
    	
    	ComponentInputMap inputMap = new ComponentInputMap((JPanel) MapTool.getFrame().getContentPane());
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
    
    /**
     * Implement this method to clear internal data to a start
     * drawing state. This method must repaint whatever it is being
     * displayed upon.
     */
    protected abstract void resetTool();
    
    /**
     * Perform the escape action on a tool.
     * 
     * @author jgorrell
     * @version $Revision$ $Date$ $Author$
     */
    private class EscapeAction extends AbstractAction {

      /**
       * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
       */
      public void actionPerformed(ActionEvent e) {
        resetTool();
      }
    }
    
    ////
    // CHANGE LISTENER
    public void stateChanged(ChangeEvent e) {

        if (isSelected()) {
            toolbox.setSelectedTool(Tool.this);
        }
    }
    
    ////
    // ACTION LISTENER
    public void actionPerformed(ActionEvent e) {
        if (!isSelected()) {
            // Don't let us unselect
            setSelected(true);
        }
    }
}
