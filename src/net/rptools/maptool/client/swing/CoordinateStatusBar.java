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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.language.I18N;

/**
 */
public class CoordinateStatusBar extends JLabel {

    private static final Dimension minSize = new Dimension(75, 10);
    
    public CoordinateStatusBar() {
    	setToolTipText(I18N.getString("CoordinateStatusBar.mapCoordinates")); //$NON-NLS-1$
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
    	setText(""); //$NON-NLS-1$
    }
    
    public void update(int x, int y) {
    	setText("  " + x + ", " + y); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
