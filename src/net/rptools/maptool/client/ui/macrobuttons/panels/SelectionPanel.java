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
package net.rptools.maptool.client.ui.macrobuttons.panels;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.util.List;

import net.rptools.maptool.client.ui.macrobuttons.buttongroups.ButtonGroup;
import net.rptools.maptool.model.Token;

public class SelectionPanel extends JPanel implements Scrollable {
	
	public SelectionPanel() {
		//TODO: refactoring reminder
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridy = 0;
		constraints.gridx = 0;
		constraints.weightx = 1;

		add(new MenuButtonsPanel(), constraints);
		constraints.gridy++;

		// Spacer
		constraints.weighty = 1;
		add(new JLabel(), constraints);
	}

	public void update(List<Token> tokenList) {
		clear();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridy = 0;
		constraints.gridx = 0;
		constraints.weightx = 1;

		add(new MenuButtonsPanel(), constraints);
		constraints.gridy++;
		
		// draw common group only when there is more than one token selected
		if (tokenList.size() > 1) {
			add(new ButtonGroup(tokenList), constraints);
			constraints.gridy++;
		}
		for (Token token : tokenList) {
			add(new ButtonGroup(token, this), constraints);
			constraints.gridy++;
		}

		// Spacer
		constraints.weighty = 1;
		add(new JLabel(), constraints);
		
		revalidate();
		repaint();
	}
	
	public void clear() {
		removeAll();
		revalidate();
		repaint();
	}

	////
	// SCROLLABLE
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 75;
	}
	public boolean getScrollableTracksViewportHeight() {
		return getPreferredSize().height < getParent().getSize().height;
	}
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 25;
	}
}