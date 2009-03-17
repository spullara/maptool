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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import net.rptools.maptool.client.MapTool;

/**
 */
public class ZoomStatusBar extends JTextField {

    private static final Dimension minSize = new Dimension(50, 10);
    
    public ZoomStatusBar() {
    	super("", RIGHT);
    	setToolTipText("Zoom Level");
    	addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextField target = (JTextField) e.getSource();
		    	if (MapTool.getFrame().getCurrentZoneRenderer() != null) {
					double zoom;
					try {
						zoom = Double.parseDouble(target.getText());
			    		MapTool.getFrame().getCurrentZoneRenderer().getZoneScale().setScale(zoom/100);
					} catch (NumberFormatException e1) {
						// If the number is invalid, ignore it.
					}
					update();
		    	}
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
    
    public void clear() {
    	setText("");
    }
    
    public void update() {
    	String zoom = "";
    	if (MapTool.getFrame().getCurrentZoneRenderer() != null) {
    		double scale = MapTool.getFrame().getCurrentZoneRenderer().getZoneScale().getScale();
    		scale *= 100;
    		zoom = String.format("%d%%", (int)scale);
    	}
    	setText(zoom);
    }
}
