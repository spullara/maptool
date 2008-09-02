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
