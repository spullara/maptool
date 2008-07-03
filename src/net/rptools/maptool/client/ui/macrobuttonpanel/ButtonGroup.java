package net.rptools.maptool.client.ui.macrobuttonpanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.macrobutton.TokenMacroButton;
import net.rptools.maptool.model.Token;

public class ButtonGroup extends JPanel {
	
	private String title;
	private Token token;
	// macro buttons that belong to the button group
	//private List<AbstractMacroButton> buttonList = new ArrayList<AbstractMacroButton>();
	
	//TODO: combine the constructors
	// constructor for creating a single token's macro buttons 
	public ButtonGroup(Token token) {
		this.token = token;
		setOpaque(false);
		if (hasMacros(token)) {
			addButtons(token);
		} else {
			add(new JLabel("None"));
		}
		// apparently you cannot change the insets of a titled border.
		// we have to create a new border from scratch to reduce padding of the title. 
		setBorder(BorderFactory.createTitledBorder(getName(token)));
		setLayout(new FlowLayout(FlowLayout.LEFT));
	}

	// constructor for creating common macro buttons between tokens
	public ButtonGroup(List<Token> tokenList) {
		setOpaque(false);
		if (tokenList.size() <= 1) {
			return;
		}
		
		//TODO: this can be made better using Tokens themselves instead of Tuples
		// get the common macros of the tokens
		// Macro Name => Token Tuple list
		// example:
		// "Attack" => [["Elf", "1d6"], ["Mystic", "1d8 + Str"]]
		// meaning "Attack" macro belongs to both "Elf" and "Mystic"
		// but they have different macro commands (bodies)
		Map<String, List<Tuple>> encounteredMacros = new HashMap<String, List<Tuple>>();
		for (Token token : tokenList) {
			for (String macro : token.getMacroNames()) {
				List<Tuple> l = encounteredMacros.get(macro);
				if (l == null) {
					l = new ArrayList<Tuple>();
					encounteredMacros.put(macro, l);
				}
				
				l.add(new Tuple(token.getName(), token.getMacro(macro)));
			}
		}
		
		// since we are only interested in finding common macros between tokens
		// we skip the map keys which have only 1 item in the arraylist
		// so we skip those like "Attack" => ["Elf"]
		Set<String> keys = encounteredMacros.keySet();
		for (String macro : keys) {
			List<Tuple> l = encounteredMacros.get(macro);
			if (l.size() > 1) {
				//TODO: sorting?
				add(new TokenMacroButton(l, macro));
			}
		}
		
		// if there are no common macros, add a label to indicate this.
		if (getComponents().length == 0) {
			add(new JLabel("None"));
		}
		
		setBorder(BorderFactory.createTitledBorder("Common"));
		setLayout(new FlowLayout(FlowLayout.LEFT));
	}

	@Override
	public Dimension getPreferredSize() {
		
		Dimension size = getParent().getSize();

		FlowLayout layout = (FlowLayout) getLayout();
		Insets insets = getInsets();
		
		// This isn't exact, but hopefully it's close enough
		int x = layout.getHgap() + insets.left;
		int y = layout.getVgap();
		int rowHeight = 0;
		for (Component c : getComponents()) {

			Dimension cSize = c.getPreferredSize();
			if (x + cSize.width + layout.getHgap() > size.width - insets.right && x > 0) {
				x = 0;
				y += rowHeight + layout.getVgap(); 
				rowHeight = 0;
			}
			
			x += cSize.width + layout.getHgap();
			rowHeight = Math.max(cSize.height, rowHeight);
		}
		
		y += rowHeight + layout.getVgap();

		y += getInsets().top;
		y += getInsets().bottom;
		
		Dimension prefSize = new Dimension(size.width, y);
		return prefSize;
	}
	
	public static class Tuple {

		public String tokenName;

		public String macro;

		private Tuple(String tokenName, String macro) {
			this.tokenName = tokenName;
			this.macro = macro;
		}

	}

	private void addButtons(Token token) {
		List<String> keyList = new ArrayList<String>(token.getMacroNames());
		Collections.sort(keyList);
		for (String key : keyList) {
			add(new TokenMacroButton(token.getName(), key, token.getMacro(key)));
		}
	}

	private boolean hasMacros(Token token) {
		return !token.getMacroNames().isEmpty();
	}

	private String getName(Token token) {
		// if a token has a GM name, put that to button title instead
		// only if the player is GM
		if (MapTool.getPlayer().isGM() && token.getGMName() != null && token.getGMName().trim().length() > 0) {
			return token.getGMName();
		} else {
			return token.getName();
		}
	}
}
