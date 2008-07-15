package net.rptools.maptool.client.ui.macrobuttonpanel;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.macrobutton.TokenMacroButton;
import net.rptools.maptool.client.ui.macrobutton.TransferData;
import net.rptools.maptool.client.ui.macrobutton.TransferableMacroButton;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.util.ImageManager;

public class ButtonGroup extends JPanel implements DropTargetListener {
	
	//private String title;
	private Token token;
	private DropTarget dt;
	private List<Token> tokenList;
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
		
		setBorder(createBorder(getName(token)));
		setLayout(new FlowLayout(FlowLayout.LEFT));
		dt = new DropTarget(this, this);
		addMouseListener(new MouseHandler());
	}

	// constructor for creating common macro buttons between tokens
	public ButtonGroup(List<Token> tokenList) {
		setOpaque(false);
		if (tokenList.size() <= 1) {
			return;
		}
		this.tokenList = tokenList;

		// get the common macros of the tokens
		// Macro Name => Token list
		// example:
		// "Attack" => [Elf, Mystic] (which are tokens themselves)
		// meaning "Attack" macro belongs to both "Elf" and "Mystic"
		// but the common macros can have different macro commands (bodies)
		Map<String, List<Token>> encounteredMacros = new HashMap<String, List<Token>>();
		for (Token token : tokenList) {
			for (String macro : token.getMacroNames()) {
				List<Token> l = encounteredMacros.get(macro);
				if (l == null) {
					l = new ArrayList<Token>();
					encounteredMacros.put(macro, l);
				}
				
				l.add(token);
			}
		}
		
		// since we are only interested in finding common macros between tokens
		// we skip the map keys which have only 1 item in the arraylist
		// so we skip those like "Attack" => ["Elf"]
		TreeSet<String> keys = new TreeSet<String>();
		// done only to sort the list alphabetically.
		keys.addAll(encounteredMacros.keySet()); 
		for (String macro : keys) {
			List<Token> l = encounteredMacros.get(macro);
			if (l.size() > 1) {
				add(new TokenMacroButton(l, macro));
			}
		}
		
		// if there are no common macros, add a label to indicate this.
		if (getComponents().length == 0) {
			add(new JLabel("None"));
		}
		
		setBorder(new ThumbnailedBorder(null, "Common"));
		setLayout(new FlowLayout(FlowLayout.LEFT));
		dt = new DropTarget(this, this);
		addMouseListener(new MouseHandler());
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

	public Insets getInsets() {
		return new Insets(18,5,5,0);
	}

	private void addButtons(Token token) {
		List<String> keyList = new ArrayList<String>(token.getMacroNames());
		Collections.sort(keyList);
		for (String key : keyList) {
			add(new TokenMacroButton(token, token.getName(), key, token.getMacro(key)));
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

	public void dragEnter(DropTargetDragEvent event) {
		//System.out.println("BG: drag enter");
	}

	public void dragOver(DropTargetDragEvent event) {
		//System.out.println("BG: drag over");
	}

	public void dropActionChanged(DropTargetDragEvent event) {
		//System.out.println("BG: drag action changed");
	}

	public void dragExit(DropTargetEvent event) {
		//System.out.println("BG: drag exit");
	}

	public void drop(DropTargetDropEvent event) {
		//System.out.println("BG: drop!");
		
		try {
			Transferable t = event.getTransferable();
			TransferData data = (TransferData) t.getTransferData(TransferableMacroButton.tokenMacroButtonFlavor);
			//System.out.println(data.macro);
			//System.out.println(data.command);

			if (tokenList != null) {
				// this is a common group, copy macro to all selected tokens
				event.acceptDrop(event.getDropAction());
				for (Token token : tokenList) {
					token.addMacro(data.macro, data.command);
				}
			} else if (token != null) {
				// this is a token group, copy macro to this.Token only
				event.acceptDrop(event.getDropAction());
				token.addMacro(data.macro, data.command);
			} else {
				// if this happens, it's a bug
				throw new Exception("Drag & Drop problem");
			}
			//System.out.println("drop accepted");
			event.dropComplete(true);
		} catch (Exception e) {
			e.printStackTrace();
			event.dropComplete(false);
		}
	}
	
	private class MouseHandler extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				new ButtonGroupPopupMenu(ButtonGroup.this).show(ButtonGroup.this, e.getX(), e.getY());
			}
		}
	}
	
	public Token getToken() {
		return token;
	}
	
	public List<Token> getTokenList() {
		return tokenList;
	}
	
	private Border createBorder(String label) {
		//return BorderFactory.createTitledBorder(label);
		ImageIcon i = new ImageIcon(ImageManager.getImageAndWait(AssetManager.getAsset(token.getImageAssetId())));
		Image icon = i.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
		return new ThumbnailedBorder(icon, label);
	}
	
	private class ThumbnailedBorder extends AbstractBorder {
		
		private Image image;
		private String label;
		
		//private final int X_OFFSET = 5;
		
		private ThumbnailedBorder(Image image, String label) {
			this.image = image;
			this.label = label;
		}

		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			//((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			
			//TODO: change magic numbers to final fields
			// match line color to default titledborder line color
			g.setColor(new Color(165, 163, 151));
			g.drawRoundRect(2, 12, c.getWidth()-5, c.getHeight()-13, 6, 6);
			
			// clear the left and right handside of the image to show space between border line and image
			g.setColor(c.getBackground());
			g.fillRect(8, 0, 24, 20);
			g.drawImage(image, 10, 2, null);

			int strx = image != null ? 30 : 5;
			
			// clear the left and right of the label
			FontMetrics metrics = g.getFontMetrics();
			int stringHeight = metrics.getHeight();
			int stringWidth = metrics.stringWidth(label);
			g.fillRect(strx, 0, stringWidth + 5, stringHeight);

			// this workaround is needed for windows platforms, otherwise the fonts will be much larger
			//int screenRes = Toolkit.getDefaultToolkit().getScreenResolution();
			//float fontSize = (float) Math.round(12.0 * screenRes / 72.0);
			//g.setFont(g.getFont().deriveFont(fontSize));
			
			g.setColor(Color.BLACK);
			//TODO: -4 is probably wrong
			g.drawString(label, strx+3, (20-stringHeight)/2+stringHeight-2);
			//
		}

		public Insets getBorderInsets(Component component) {
			return new Insets(20, 5, 5, 5);
		}

		public boolean isBorderOpaque() {
			return true;
		}
	}
}
