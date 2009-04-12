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

import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.MapToolFrame.MTFrame;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;

public class ImpersonatePanel extends AbstractMacroPanel {

	private boolean currentlyImpersonating = false;
	
	public ImpersonatePanel() {
		setPanelClass("ImpersonatePanel");
		MapTool.getEventDispatcher().addListener(this, MapTool.ZoneEvent.Activated);
	}
	
	public void init(){
		List<Token> selectedTokenList = MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokensList();
		if (currentlyImpersonating && getToken() != null) {
			Token token = getToken();
			MapTool.getFrame().getFrame(MTFrame.IMPERSONATED).setFrameIcon(token.getIcon(16, 16));
			MapTool.getFrame().getFrame(MTFrame.IMPERSONATED).setTitle(getTitle(token));
			addArea(getTokenId());
		} else if (selectedTokenList.size() != 1) {
			return;
		} else {
			// add the "Impersonate Selected" button
			final Token t = selectedTokenList.get(0);
			
			JButton button = new JButton(I18N.getText("panel.Impersonate.button.impersonateSelected"), t.getIcon(16, 16)) {
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
		}

	}

	public void startImpersonating(Token token){
		stopImpersonating();
		setTokenId(token);
		currentlyImpersonating = true;
		token.setBeingImpersonated(true);
		reset();
	}

	public void stopImpersonating(){
		Token token = getToken();
		if (token!=null){
			token.setBeingImpersonated(false);
		}
		setTokenId((GUID)null);
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
	
	public void reset() {
		clear();
		init();
	}
	
/*
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
*/
	
}