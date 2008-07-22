package net.rptools.maptool.client.ui.macrobuttons.buttongroups;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.TokenPopupMenu;
import net.rptools.maptool.client.ui.macrobuttons.buttons.TransferData;
import net.rptools.maptool.client.ui.macrobuttons.buttons.TransferableMacroButton;
import net.rptools.maptool.client.ui.token.EditTokenDialog;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;

public abstract class AbstractButtonGroup extends JPanel implements DropTargetListener {
	
	protected Token token;
	protected DropTarget dt;
	protected List<Token> tokenList;
	// macro buttons that belong to the button group
	//private List<AbstractMacroButton> buttonList = new ArrayList<AbstractMacroButton>();
	
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
			if (data == null) {
				return;
			}
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
				throw new RuntimeException("Drag & Drop problem");
			}
			//System.out.println("drop accepted");
			event.dropComplete(true);
		} catch (Exception e) {
			e.printStackTrace();
			event.dropComplete(false);
		}
	}

	public Token getToken() {
		return token;
	}

	public List<Token> getTokenList() {
		return tokenList;
	}

	protected class ThumbnailedBorder extends AbstractBorder {
		
		private Image image;
		private String label;
		private Rectangle imageBounds;
		
		//private final int X_OFFSET = 5;
		
		public ThumbnailedBorder(Image image, String label) {
			this.image = image;
			this.label = label;
						
			addMouseListener(new MouseHandler());
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

			// set the area for mouse listener
			if (image != null) {
				imageBounds = new Rectangle(10, 2, image.getWidth(null) + stringWidth, image.getHeight(null));
			}
			
			// display impersonated image if impersonated
			if (token != null && token.isBeingImpersonated()) {
				g.drawImage(AppStyle.impersonatePanelImage, (int) imageBounds.getMaxX() + 5, 4, null);
			}
			
			g.setColor(Color.BLACK);
			g.drawString(label, strx+3, (20-stringHeight)/2+stringHeight-2);
		}

		public Insets getBorderInsets(Component component) {
			return new Insets(20, 5, 5, 5);
		}

		public boolean isBorderOpaque() {
			return true;
		}
		
		private class MouseHandler extends MouseAdapter {
			public void mouseReleased(MouseEvent event) {
				if (imageBounds != null && imageBounds.contains(event.getPoint())) {
					if (SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2 && !SwingUtil.isShiftDown(event)) {
						// open edit token dialog
						EditTokenDialog tokenPropertiesDialog = MapTool.getFrame().getTokenPropertiesDialog();
						tokenPropertiesDialog.showDialog(token);

						// update token in the renderer if it is changed
						if (tokenPropertiesDialog.isTokenSaved()) {
							ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
							renderer.repaint();
							renderer.flush(token);
							MapTool.serverCommand().putToken(renderer.getZone().getId(), token);
							renderer.getZone().putToken(token);
							MapTool.getFrame().updateImpersonatePanel(token);
						}
					} else if (SwingUtilities.isRightMouseButton(event)) {
						// open token popup menu
						Set<GUID> GUIDSet = new HashSet<GUID>();
						GUIDSet.add(token.getId());
						ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
						new TokenPopupMenu(GUIDSet, event.getX(), event.getY(), renderer, token).showPopup(AbstractButtonGroup.this);
					} else if (SwingUtilities.isLeftMouseButton(event) && SwingUtil.isShiftDown(event)) {
						// impersonate token toggle
						if (token.isBeingImpersonated()) {
							MapTool.getFrame().getCommandPanel().quickCommit("/im");
						} else {
							MapTool.getFrame().getCommandPanel().quickCommit("/im " + token.getId(), false);
						}
					}
					
				} else if (SwingUtilities.isRightMouseButton(event)) {
					// open button group menu
					new ButtonGroupPopupMenu(AbstractButtonGroup.this).show(AbstractButtonGroup.this, event.getX(), event.getY());
				}
			}
		}

		public MouseAdapter getMouseAdapter() {
			return new MouseHandler();
		}
	}
}