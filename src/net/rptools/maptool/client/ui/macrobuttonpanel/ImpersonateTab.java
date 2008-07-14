package net.rptools.maptool.client.ui.macrobuttonpanel;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.swing.ScrollableFlowPanel;
import net.rptools.maptool.client.ui.macrobutton.TokenMacroButton;
import net.rptools.maptool.model.Token;

public class ImpersonateTab extends ScrollableFlowPanel {

	private Token token;
	private static final String titlePrefix = "Im: ";
	
	public ImpersonateTab() {
		super(FlowLayout.LEFT);
	}
	
	public ImpersonateTab(Token token) {
		super(FlowLayout.LEFT);
		this.token = token;
		addButtons(token);
	}

	private void addButtons(Token token) {
		List<String> keyList = new ArrayList<String>(token.getMacroNames());
		Collections.sort(keyList);
		for (String key : keyList) {
			add(new TokenMacroButton(token, key, token.getMacro(key)));
		}
		doLayout();
	}

	public String getTitle() {
		if (MapTool.getPlayer().isGM() && token.getGMName() != null && token.getGMName().trim().length() > 0) {
			return titlePrefix + token.getGMName();
		} else {
			return titlePrefix + token.getName();
		}
	}

	public void clear() {
		removeAll();
		doLayout();
	}
	
	public void update(Token token) {
		this.token = token;
		clear();
		addButtons(token);
	}
}
