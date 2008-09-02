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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 * @author trevor
 */
public class StatusPanel extends JPanel {

	private JLabel statusLabel;
	
	public StatusPanel() {
		
		statusLabel = new JLabel();
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.BOTH;
		
		add(wrap(statusLabel), constraints);
	}

	public void setStatus(String status) {
		statusLabel.setText(status);
	}
	
	public void addPanel(JComponent component) {
		
		int nextPos = getComponentCount();
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		
		constraints.gridx = nextPos;
		
		add(wrap(component), constraints);
		
		invalidate();
		doLayout();
	}

	private JComponent wrap(JComponent component) {
		
		component.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		return component;
	}
}
