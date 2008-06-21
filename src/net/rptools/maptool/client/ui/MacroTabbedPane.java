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
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.rptools.maptool.client.swing.ScrollableFlowPanel;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.macrobutton.TokenMacroButton;
import net.rptools.maptool.client.ui.macrobutton.CampaignMacroButton;
import net.rptools.maptool.client.ui.macrobutton.AbstractMacroButton;
import net.rptools.maptool.client.ui.macrobutton.GlobalMacroButton;
import net.rptools.maptool.client.ui.macrobutton.MacroButtonPrefs;
import net.rptools.maptool.client.ui.macrobuttonpanel.TabPopupListener;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.MacroButtonProperties;

public class MacroTabbedPane extends JTabbedPane {
	// Component Hierarchy:
	// JTabbedPane -> JScrollPanes -> JPanel -> MacroButtons
	private JPanel globalMacroPanel;
	private JPanel campaignMacroPanel;
	private JPanel tokenMacroPanel;

	private enum Tab {
		GLOBAL(0, "Global"),
		CAMPAIGN(1, "Campaign"),
		TOKEN(2, "Token");

		public final int index;
		public final String title;

		Tab(int index, String title) {
			this.index = index;
			this.title = title;
		}
	}

	public MacroTabbedPane() {
		init();
		updateKeyStrokes();
	}

	public void updateTokenPanel(Token token) {
		tokenMacroPanel = new ScrollableFlowPanel(FlowLayout.LEFT);

		List<String> keyList = new ArrayList<String>(token.getMacroNames());
		Collections.sort(keyList);
		for (String key : keyList) {
			tokenMacroPanel.add(new TokenMacroButton(key, token.getMacro(key)));
		}

		setComponentAt(Tab.TOKEN.index, scrollPaneFactory(tokenMacroPanel));
		setTitleAt(Tab.TOKEN.index, Tab.TOKEN.title + ": " + token.getName());
	}

	public void clearTokenPanel() {
		tokenMacroPanel.removeAll();
		setTitleAt(Tab.TOKEN.index, Tab.TOKEN.title);
	}
	
	// currently this method is only called when loading a campaign
	public void updatePanels() {
		// need to clear macro button keystrokes so we wont have
		// conflicts in the keystrokemap.
		MacroButtonHotKeyManager.clearKeyStrokes();

		// the order is important here. the latter one will have a higher priority
		// therefore global macro button keystrokes overwrite campaign ones.
		campaignMacroPanel = createCampaignMacroPanel();
		globalMacroPanel = createGlobalMacroPanel();
		
		JScrollPane global = scrollPaneFactory(globalMacroPanel);
		JScrollPane campaign = scrollPaneFactory(campaignMacroPanel);
		global.addMouseListener(new TabPopupListener(global, Tab.GLOBAL.index));
		campaign.addMouseListener(new TabPopupListener(campaign, Tab.CAMPAIGN.index));

		setComponentAt(Tab.GLOBAL.index, global);
		setComponentAt(Tab.CAMPAIGN.index, campaign);

		// bind the hotkeys
		updateKeyStrokes();
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

	// TODO: refactor this
	private void init() {
		globalMacroPanel = createGlobalMacroPanel();
		campaignMacroPanel = createCampaignMacroPanel();
		tokenMacroPanel = createTokenMacroPanel();

		JScrollPane global = scrollPaneFactory(globalMacroPanel);
		JScrollPane campaign = scrollPaneFactory(campaignMacroPanel);
		JScrollPane token = scrollPaneFactory(tokenMacroPanel);
		global.addMouseListener(new TabPopupListener(global, Tab.GLOBAL.index));
		campaign.addMouseListener(new TabPopupListener(campaign, Tab.CAMPAIGN.index));
		
		// using add() or addTab() instead of insertTab() causes brittleness in the code
		// because the order of addition would be important
		insertTab(Tab.GLOBAL.title, null, global, null, Tab.GLOBAL.index);
		insertTab(Tab.CAMPAIGN.title, null, campaign, null, Tab.CAMPAIGN.index);
		insertTab(Tab.TOKEN.title, null, token, null, Tab.TOKEN.index);
	}
	
	private JPanel createGlobalMacroPanel()	{
		JPanel panel = new ScrollableFlowPanel(FlowLayout.LEFT);
		List<MacroButtonProperties> properties = MacroButtonPrefs.getButtonProperties();

		for (MacroButtonProperties prop : properties) {
			panel.add(new GlobalMacroButton(prop));
		}
		
		return panel;
	}

	private JPanel createCampaignMacroPanel() {
		JPanel panel = new ScrollableFlowPanel(FlowLayout.LEFT);
		List<MacroButtonProperties> properties = MapTool.getCampaign().getMacroButtonPropertiesArray();
		
		for (MacroButtonProperties prop : properties) {
			panel.add(new CampaignMacroButton(prop));
		}
		
		return panel;
	}

	private JPanel createTokenMacroPanel() {
		return new ScrollableFlowPanel(FlowLayout.LEFT);
	}

	private JScrollPane scrollPaneFactory(JPanel panel)	{
		return new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	public void addGlobalMacroButton() {
		//TODO: can move this to globalmacrobutton constructor
		MacroButtonProperties properties = new MacroButtonProperties(MacroButtonPrefs.getNextIndex());
		globalMacroPanel.add(new GlobalMacroButton(properties));
		globalMacroPanel.doLayout();
	}

	public void addGlobalMacroButton(MacroButtonProperties properties) {
		MacroButtonProperties prop = new MacroButtonProperties(MacroButtonPrefs.getNextIndex(),
															   properties.getColorKey(),
															   MacroButtonHotKeyManager.HOTKEYS[0],
															   properties.getCommand(),
															   properties.getLabel(),
															   properties.getAutoExecute(),
															   properties.getIncludeLabel());		
		globalMacroPanel.add(new GlobalMacroButton(prop));
	}
	
	// TODO: refactor -> combine into one if possible
	public void addCampaignMacroButton() {
		//TODO: refactor -> can be moved to constructor
		final MacroButtonProperties properties = new MacroButtonProperties(MapTool.getCampaign().getMacroButtonNextIndex());
		MapTool.getCampaign().addMacroButtonProperty(properties);
		final CampaignMacroButton button  = new CampaignMacroButton(properties);
		campaignMacroPanel.add(button);
		campaignMacroPanel.doLayout();
	}

	public void addCampaignMacroButton(MacroButtonProperties properties) {
		MacroButtonProperties prop = new MacroButtonProperties(MapTool.getCampaign().getMacroButtonNextIndex(),
															   properties.getColorKey(),
															   MacroButtonHotKeyManager.HOTKEYS[0],
															   properties.getCommand(),
															   properties.getLabel(),
															   properties.getAutoExecute(),
															   properties.getIncludeLabel());
		MapTool.getCampaign().addMacroButtonProperty(properties);
		campaignMacroPanel.add(new CampaignMacroButton(prop));
	}
	
	// TODO: refactor -> combine into one method
	public void deleteGlobalMacroButton(GlobalMacroButton button) {
		globalMacroPanel.remove(button);
	}

	public void deleteCampaignMacroButton(CampaignMacroButton button) {
		campaignMacroPanel.remove(button);
	}
}

