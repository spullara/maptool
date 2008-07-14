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
package net.rptools.maptool.client.ui;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.macrobutton.AbstractMacroButton;
import net.rptools.maptool.client.ui.macrobutton.CampaignMacroButton;
import net.rptools.maptool.client.ui.macrobutton.GlobalMacroButton;
import net.rptools.maptool.client.ui.macrobuttonpanel.CampaignTab;
import net.rptools.maptool.client.ui.macrobuttonpanel.GlobalTab;
import net.rptools.maptool.client.ui.macrobuttonpanel.ImpersonateTab;
import net.rptools.maptool.client.ui.macrobuttonpanel.MacroPanelPopupListener;
import net.rptools.maptool.client.ui.macrobuttonpanel.SelectionTab;
import net.rptools.maptool.client.ui.macrobuttonpanel.Tab;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.MacroButtonProperties;
import net.rptools.maptool.model.Token;

public class MacroTabbedPane extends JTabbedPane {
	
	private GlobalTab globalTab;
	private CampaignTab campaignTab;
	private ImpersonateTab impersonateTab;
	private SelectionTab selectionTab;
	
	public MacroTabbedPane() {
		init();
	}

	public void updateImpersonateTab(Token token, boolean switchTo) {
		impersonateTab.update(token);
		setTitleAt(Tab.IMPERSONATED.index, impersonateTab.getTitle());
		
		if (switchTo) {
			setSelectedIndex(Tab.IMPERSONATED.index);
		}
	}

	public void clearImpersonateTab() {
		impersonateTab.clear();
		setTitleAt(Tab.IMPERSONATED.index, Tab.IMPERSONATED.title);
	}
	
	public void clearSelectionTab() {
		selectionTab.clear();
	}
	
	public void updateSelectionTab() {
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
		
		selectionTab.update(tokenList);
	}
	
	public void resetTabs() {
		// need to clear macro button keystrokes so we wont have
		// conflicts in the keystrokemap.
		MacroButtonHotKeyManager.clearKeyStrokes();
		// remove all components
		removeAll();
		init();
	}
		
	private JPanel wrapPanelWithHelp(JComponent component, String help) {
		
		JLabel helpLabel = new JLabel();
		helpLabel.setText(help);
		helpLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(BorderLayout.CENTER, component);
		panel.add(BorderLayout.SOUTH, helpLabel);
		
		return panel;
	}

	public void updateKeyStrokes() {
		this.getInputMap().clear();
		Map<KeyStroke, AbstractMacroButton> keyStrokeMap = MacroButtonHotKeyManager.getKeyStrokeMap();

		for (KeyStroke keyStroke : keyStrokeMap.keySet()) {
			final AbstractMacroButton button = keyStrokeMap.get(keyStroke);
			this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, button);
			this.getActionMap().put(button, new AbstractAction() {
				public void actionPerformed(ActionEvent event) {
					button.executeButton();
				}
			});
		}
	}

	private void init() {
		// the order is important here. the latter one will have a higher priority
		// therefore global macro button keystrokes overwrite campaign ones.
		campaignTab = new CampaignTab();
		globalTab = new GlobalTab();
		selectionTab = new SelectionTab();
		impersonateTab = new ImpersonateTab();

		JScrollPane global = scrollPaneFactory(globalTab);
		global.addMouseListener(new MacroPanelPopupListener(global, Tab.GLOBAL.index));

		JScrollPane campaign = scrollPaneFactory(campaignTab);
		campaign.addMouseListener(new MacroPanelPopupListener(campaign, Tab.CAMPAIGN.index));

		JScrollPane selection = scrollPaneFactory(selectionTab);
		JScrollPane impersonate = scrollPaneFactory(impersonateTab);
		
		// these can be replaced with addTab()
		insertTab(Tab.GLOBAL.title, null, wrapPanelWithHelp(global, "<html><b>Help:</b> Right click to create a new button or edit an existing button"), null, Tab.GLOBAL.index);
		insertTab(Tab.CAMPAIGN.title, null, wrapPanelWithHelp(campaign, "<html><b>Help:</b> Right click to create a new button or edit an existing button"), null, Tab.CAMPAIGN.index);
		insertTab(Tab.SELECTED.title, null, wrapPanelWithHelp(selection, "<html><b>Help:</b> Drag and drop buttons to copy macros around. Press right mouse button on buttons or groups."), null, Tab.SELECTED.index);
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
	
	public void addGlobalMacroButton() {
		globalTab.addButton();
	}

	public void addGlobalMacroButton(MacroButtonProperties properties) {
		globalTab.addButton(properties);
	}
	
	public void addCampaignMacroButton() {
		campaignTab.addButton();
	}

	public void addCampaignMacroButton(MacroButtonProperties properties) {
		campaignTab.addButton(properties);
	}
	
	public void deleteGlobalMacroButton(GlobalMacroButton button) {
		globalTab.deleteButton(button);
	}

	public void deleteCampaignMacroButton(CampaignMacroButton button) {
		campaignTab.deleteButton(button);
	}
}

