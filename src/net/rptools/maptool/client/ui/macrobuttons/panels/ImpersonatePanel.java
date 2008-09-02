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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.macrobuttons.buttongroups.ButtonGroup;
import net.rptools.maptool.model.Token;

public class ImpersonatePanel extends JPanel implements Scrollable {

	private Token token;
	private boolean currentlyImpersonating = false;
	
	public ImpersonatePanel() {
		// no op
	}
	
	private void addButtons(Token token) {
		this.token = token;
		//addCancelButton();
		add(new ButtonGroup(token, this));
		doLayout();
		revalidate();
		repaint();
	}

	public String getTitle() {
		if (token.getGMName() != null && token.getGMName().trim().length() > 0) {
			return token.getName() + " (" + token.getGMName() + ")";
		} else {
			return token.getName();
		}
	}

	public void addCancelButton() {
		ImageIcon i = new ImageIcon(AppStyle.cancelButton);
		JButton button = new JButton("Cancel Impersonation", i) {
			public Insets getInsets() {
				return new Insets(3, 3, 3, 3);
			}
		};
		button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				MapTool.getFrame().getCommandPanel().quickCommit("/im");
			}
		});
		button.setBackground(null);
		add(button);
	}
	
	public void addImpersonateButton(List<Token> selectedTokenList) {
		if (currentlyImpersonating) {
			return;
		}

		removeAll();
		revalidate();
		repaint();

		if (selectedTokenList.size() != 1) {
			return;
		}
		
		// find our selected token
		final Token t = selectedTokenList.get(0);
		
		JButton button = new JButton("Impersonate Selected", t.getIcon(16, 16)) {
			public Insets getInsets() {
				return new Insets(2, 2, 2, 2);
			}
		};
		button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				MapTool.getFrame().getCommandPanel().quickCommit("/im " + t.getId(), false);
			}
		});
		button.setBackground(null);
		add(button);
		
		revalidate();
		repaint();
	}
	
	public void clear() {
		removeAll();
		
		if (token != null) {
			token.setBeingImpersonated(false);
			token = null;
			MapTool.getFrame().updateSelectionPanel();
		}
		
		currentlyImpersonating = false;
		addImpersonateButton(MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokensList());
		
		revalidate();
		repaint();
	}
	
	public void update(Token token) {
		if (this.token != null) {
			this.token.setBeingImpersonated(false);
		}
		this.token = token;
		token.setBeingImpersonated(true);
		removeAll();
		addButtons(token);
		currentlyImpersonating = true;
		MapTool.getFrame().updateSelectionPanel();
	}

	public void update(List<Token> selectedTokenList) {
		addImpersonateButton(selectedTokenList);
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
