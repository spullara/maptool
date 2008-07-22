/* The MIT License
 *
 * Copyright (c) 2008 Gokhan Ozcan
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
package net.rptools.maptool.client.ui.macrobuttons;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.macrobuttons.buttons.MacroPanelPopupListener;
import net.rptools.maptool.client.ui.macrobuttons.panels.CampaignPanel;
import net.rptools.maptool.client.ui.macrobuttons.panels.GlobalPanel;
import net.rptools.maptool.client.ui.macrobuttons.panels.ImpersonatePanel;
import net.rptools.maptool.client.ui.macrobuttons.panels.SelectionPanel;
import net.rptools.maptool.client.ui.macrobuttons.panels.Tab;

public class MacroTabbedPane extends JTabbedPane {
	
	private GlobalPanel globalPanel;
	private CampaignPanel campaignPanel;
	private ImpersonatePanel impersonatePanel;
	private SelectionPanel selectionPanel;
	
	public MacroTabbedPane() {
		init();
	}

	/*
	public void updateImpersonatePanel(Token token, boolean switchTo) {
		impersonatePanel.update(token);
		setTitleAt(Tab.IMPERSONATED.index, impersonatePanel.getTitle());
		setIconAt(Tab.IMPERSONATED.index, token.getIcon(16, 16));

		if (switchTo) {
			setSelectedIndex(Tab.IMPERSONATED.index);
		}
	}*/

	/*
	public void clearImpersonatePanel() {
		impersonatePanel.clear();
		setTitleAt(Tab.IMPERSONATED.index, Tab.IMPERSONATED.title);
		setIconAt(Tab.IMPERSONATED.index, null);
	}*/
	/*
	public void clearSelectionTab() {
		selectionPanel.clear();
		setIconAt(Tab.IMPERSONATED.index, null);
	}*/
	/*
	public void updateSelectionPanel() {
		ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
		if (renderer == null) {
			return;
		}
		
		List<Token> tokenList = new ArrayList<Token>();
		
		for (GUID tokenGUID : renderer.getSelectedTokenSet()) {
			Token token = renderer.getZone().getToken(tokenGUID);

			// if we don't own the token, we shouldn't see its macros
			if (AppUtil.playerOwns(token)) {
				tokenList.add(token);
			}
		}
		
		selectionPanel.update(tokenList);
		if (tokenList.size() == 1) {
			// if only one token selected, show its image as tab icon
			setIconAt(Tab.SELECTED.index, tokenList.get(0).getIcon(16, 16));
		} else {
			// if >1 or no token selected, don't display a tab icon
			setIconAt(Tab.SELECTED.index, null);
		}
	}
	*/
	/*public void resetTabs() {
		// need to clear macro button keystrokes so we wont have
		// conflicts in the keystrokemap.
		MacroButtonHotKeyManager.clearKeyStrokes();
		// remove all components
		removeAll();
		init();
	}*/
	
	public void updateKeyStrokes() {
		if (MapTool.getFrame() != null) {
			MapTool.getFrame().updateKeyStrokes();
		}
	}

	private void init() {
		// the order is important here. the latter one will have a higher priority
		// therefore global macro button keystrokes overwrite campaign ones.
		campaignPanel = new CampaignPanel();
		globalPanel = new GlobalPanel();
		selectionPanel = new SelectionPanel();
		impersonatePanel = new ImpersonatePanel();

		JScrollPane global = scrollPaneFactory(globalPanel);
		global.addMouseListener(new MacroPanelPopupListener(global, Tab.GLOBAL.index));

		JScrollPane campaign = scrollPaneFactory(campaignPanel);
		campaign.addMouseListener(new MacroPanelPopupListener(campaign, Tab.CAMPAIGN.index));

		JScrollPane selection = scrollPaneFactory(selectionPanel);
		JScrollPane impersonate = scrollPaneFactory(impersonatePanel);
		
		insertTab(Tab.GLOBAL.title, null, global, null, Tab.GLOBAL.index);
		insertTab(Tab.CAMPAIGN.title, null, campaign, null, Tab.CAMPAIGN.index);
		insertTab(Tab.SELECTED.title, null, selection, null, Tab.SELECTED.index);
		insertTab(Tab.IMPERSONATED.title, null, impersonate, null, Tab.IMPERSONATED.index);
		
		//addMouseListener(new TabPopupListener(this, 0));

		// bind the hotkeys
		updateKeyStrokes();
	}

	private JScrollPane scrollPaneFactory(JPanel panel)	{
		JScrollPane pane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.getViewport().setBorder(null);
		//pane.getViewport().setBackground(Color.white);
		return pane;
	}
	/*
	public void addGlobalMacroButton() {
		globalPanel.addButton();
	}

	public void addGlobalMacroButton(MacroButtonProperties properties) {
		globalPanel.addButton(properties);
	}
	
	public void addCampaignMacroButton() {
		campaignPanel.addButton();
	}

	public void addCampaignMacroButton(MacroButtonProperties properties) {
		campaignPanel.addButton(properties);
	}
	
	public void deleteGlobalMacroButton(GlobalMacroButton button) {
		globalPanel.deleteButton(button);
	}

	public void deleteCampaignMacroButton(CampaignMacroButton button) {
		campaignPanel.deleteButton(button);
	}*/
}

