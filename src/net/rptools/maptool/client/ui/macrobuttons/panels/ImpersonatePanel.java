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
package net.rptools.maptool.client.ui.macrobuttons.panels;

import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.MapToolFrame;
import net.rptools.maptool.client.ui.MapToolFrame.MTFrame;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;

import com.jidesoft.docking.DockableFrame;

public class ImpersonatePanel extends AbstractMacroPanel {
	private boolean currentlyImpersonating = false;

	public ImpersonatePanel() {
		setPanelClass("ImpersonatePanel");
		MapTool.getEventDispatcher().addListener(this,
				MapTool.ZoneEvent.Activated);
	}

	public void init() {
		boolean panelVisible = true;
		final MapToolFrame mtf = MapTool.getFrame();

		// Get the current visibility / autohide state of the Impersonate panel
		if (mtf != null) {
			DockableFrame impersonatePanel = mtf.getDockingManager().getFrame("IMPERSONATED");
			if (impersonatePanel != null)
				panelVisible = (impersonatePanel.isVisible() && !impersonatePanel.isAutohide()) || impersonatePanel.isAutohideShowing() ? true : false;
		}
		// Only repaint the panel if its visible
		if (panelVisible && mtf != null && mtf.getCurrentZoneRenderer() != null) {
			List<Token> selectedTokenList = mtf.getCurrentZoneRenderer().getSelectedTokensList();

			if (currentlyImpersonating && getToken() != null) {
				Token token = getToken();
				mtf.getFrame(MTFrame.IMPERSONATED).setFrameIcon(token.getIcon(16, 16));
				mtf.getFrame(MTFrame.IMPERSONATED).setTitle(getTitle(token));
				addArea(getTokenId());
			} else if (selectedTokenList.size() != 1) {
				return;
			} else {
				// add the "Impersonate Selected" button
				final Token t = selectedTokenList.get(0);

				if (AppUtil.playerOwns(t)) {
					JButton button = new JButton(I18N.getText("panel.Impersonate.button.impersonateSelected"), t.getIcon(16, 16)) {
						@Override
						public Insets getInsets() {
							return new Insets(2, 2, 2, 2);
						}
					};
					button.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent event) {
							MapTool.getFrame().getCommandPanel().quickCommit("/im " + t.getId(), false);
						}
					});
					button.setBackground(null);
					add(button);
				}
			}
		}
	}

	public void startImpersonating(Token token) {
		stopImpersonating();
		setTokenId(token);
		currentlyImpersonating = true;
		token.setBeingImpersonated(true);
		reset();
	}

	public void stopImpersonating() {
		Token token = getToken();
		if (token != null) {
			token.setBeingImpersonated(false);
		}
		setTokenId((GUID) null);
		currentlyImpersonating = false;
		clear();
	}

	public String getTitle(Token token) {
		if (token.getGMName() != null && token.getGMName().trim().length() > 0) {
			return token.getName() + " (" + token.getGMName() + ")";
		} else {
			return token.getName();
		}
	}

	@Override
	public void clear() {
		removeAll();
		MapTool.getFrame().getFrame(MTFrame.IMPERSONATED).setFrameIcon(new ImageIcon(AppStyle.impersonatePanelImage));
		MapTool.getFrame().getFrame(MTFrame.IMPERSONATED).setTitle(Tab.IMPERSONATED.title);
		if (getTokenId() == null) {
			currentlyImpersonating = false;
		}
		doLayout();
		revalidate();
		repaint();
	}

	@Override
	public void reset() {
		clear();
		init();
	}

	/**
	 * This method is currently not used and (likely) hasn't been kept up to date with the rest of the code. I've marked
	 * it as deprecated to reflect this and to warn anyone who calls it.
	 */
	@Deprecated
	public void addCancelButton() {
		ImageIcon i = new ImageIcon(AppStyle.cancelButton);
		JButton button = new
				JButton("Cancel Impersonation", i) {
					@Override
					public Insets getInsets() {
						return new Insets(3, 3, 3, 3);
					}
				};
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				MapTool.getFrame().getCommandPanel().quickCommit("/im");
			}
		});
		button.setBackground(null);
		add(button);
	}
}