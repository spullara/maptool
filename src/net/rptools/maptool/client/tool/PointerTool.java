/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
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
package net.rptools.maptool.client.tool;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.rptools.clientserver.hessian.client.ClientConnection;
import net.rptools.maptool.client.MapToolClient;
import net.rptools.maptool.client.Tool;
import net.rptools.maptool.client.ZoneOverlay;
import net.rptools.maptool.client.ZoneRenderer;
import net.rptools.maptool.client.swing.TitleMenuItem;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.TokenSize;



/**
 */
public class PointerTool extends Tool implements MouseListener, MouseMotionListener, ZoneOverlay {
    private static final long serialVersionUID = 3258411729238372921L;
    
    private boolean isDraggingMap;
	private int dragStartX;
	private int dragStartY;
	
	private int mouseX;
	private int mouseY;
	
	private Token tokenUnderMouse;
	
	public PointerTool () {
		try {
			setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/Tool_Draw_Select.gif"))));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	////
	// Mouse
	
	public void mousePressed(MouseEvent e) {

        isDraggingMap = false;
		ZoneRenderer renderer = (ZoneRenderer) e.getSource();
        
        if (e.isPopupTrigger()) {
        	
        	if (renderer.getSelectedTokenSet().size() > 0) {
        		showTokenContextMenu(e);
        	}
        	return;
        }

        if (SwingUtilities.isLeftMouseButton(e)) {
			
			renderer.clearSelectedTokens();
			
			// Token
			Token token = renderer.getTokenAt (e.getX(), e.getY());
			if (token != null) {
				renderer.selectToken(token);
				return;
			}

			// Then it's a movement
            renderer.clearSelectedTokens();
			isDraggingMap = true; // possibly dragging the map
			dragStartX = e.getX();
			dragStartY = e.getY();
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		
		ZoneRenderer renderer = (ZoneRenderer) e.getSource();
        if (e.isPopupTrigger()) {
        	
        	if (renderer.getSelectedTokenSet().size() > 0) {
        		showTokenContextMenu(e);
        	}
        	return;
        }
        
		isDraggingMap = false;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	
	}	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	
	////
	// MouseMotion
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
		
		mouseX = e.getX();
		mouseY = e.getY();
		
		ZoneRenderer renderer = (ZoneRenderer) e.getSource();
			
		Token token = renderer.getTokenAt(mouseX, mouseY);
		if (token == null) {
			if (tokenUnderMouse != null) {

				renderer.unzoomToken(tokenUnderMouse);

				tokenUnderMouse = null;
				renderer.repaint();
			}
		} else {
			if (token != tokenUnderMouse) {

				// TODO: PLEEEEASE, for all that it HOLY ..... CLEAN UP THIS CODE !
				int tokenWidth = (int)(TokenSize.getWidth(token, renderer.getZone().getGridSize()) * renderer.getScale());
				int tokenHeight = (int)(TokenSize.getHeight(token, renderer.getZone().getGridSize()) * renderer.getScale());

				boolean tokenTooSmall = tokenWidth < ZoneRenderer.HOVER_SIZE_THRESHOLD && tokenHeight < ZoneRenderer.HOVER_SIZE_THRESHOLD;
				
				renderer.unzoomToken(tokenUnderMouse);

				tokenUnderMouse = token;
				renderer.repaint();
				if (tokenTooSmall) {
					renderer.zoomToken(token);
				}
			}
		}
	}
	public void mouseDragged(MouseEvent e) {
		
		ZoneRenderer renderer = (ZoneRenderer) e.getSource();
		if (SwingUtilities.isLeftMouseButton(e) && !isDraggingMap) {
			
			// Might be dragging a token
			Point cell = renderer.getCellAt(e.getX(), e.getY());
			Set<Token> selectedTokenSet = renderer.getSelectedTokenSet();
			if (selectedTokenSet.size() > 0) {
				
				for (Token token : selectedTokenSet) {

					if (cell != null) {
						
						if (token.getX() == cell.x && token.getY() == cell.y) {
							continue;
						}
						token.setX(cell.x);
						token.setY(cell.y);

						// TODO: this needs to be better abstracted
						if (MapToolClient.isConnected()) {
							ClientConnection conn = MapToolClient.getInstance().getConnection();
							
							conn.callMethod(MapToolClient.COMMANDS.putToken.name(), renderer.getZone().getId(), token);
						}

						renderer.getZone().putToken(token);
						renderer.repaint();
					}
				}
			}
			
			return;
		}
		
		if (SwingUtilities.isLeftMouseButton(e)) {
			// Map movement
			int dx = e.getX() - dragStartX;
			int dy = e.getY() - dragStartY;
	
			dragStartX = e.getX();
			dragStartY = e.getY();
			
			renderer.moveViewBy(dx, dy);
		}
	}	
	
	/* (non-Javadoc)
	 * @see net.rptools.maptool.client.Tool#getKeyActionMap()
	 */
	protected Map<KeyStroke, Action> getKeyActionMap() {
		return new HashMap<KeyStroke, Action>() {
			{
				// TODO: Optimize this by making it non anonymous
				put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), new AbstractAction() {
				
					public void actionPerformed(java.awt.event.ActionEvent e) {
						
						ZoneRenderer renderer = (ZoneRenderer) e.getSource();
						
						Set<Token> selectedTokenSet = renderer.getSelectedTokenSet();
						
						for (Token token : selectedTokenSet) {
							
			                if (MapToolClient.isConnected()) {
			                	
			                	// TODO: abstract this
			                    ClientConnection conn = MapToolClient.getInstance().getConnection();
			                    
			                    conn.callMethod(MapToolClient.COMMANDS.removeToken.name(), renderer.getZone().getId(), token.getId());
			                }
						}
						
						renderer.clearSelectedTokens();
					}
				});
			}
		};
	}
	
	//// 
	// ZoneOverlay
	/* (non-Javadoc)
	 * @see net.rptools.maptool.client.ZoneOverlay#paintOverlay(net.rptools.maptool.client.ZoneRenderer, java.awt.Graphics2D)
	 */
	public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {

//		if (tokenUnderMouse != null && renderer.getSelectedTokenSet().contains(tokenUnderMouse)) {
//
//			Rectangle rect = renderer.getTokenBounds(tokenUnderMouse);
//			
//			g.setColor(Color.black);
//			g.fillRect(rect.x + rect.width - 10, rect.y + rect.height - 10, 10, 10);
//
//			g.setColor(Color.white);
//			g.fillRect(rect.x + rect.width - 8, rect.y + rect.height - 8, 8, 8);
//		}
	}
	
	////
	// INTERNAL
	
	private void showTokenContextMenu(MouseEvent e) {
		
    	JPopupMenu popup = new JPopupMenu();
    	
    	// TODO: Genericize the heck out of this.
    	JMenu sizeMenu = new JMenu("Size");
    	JMenuItem freeSize = new JMenuItem("Free Size");
    	freeSize.setEnabled(false);
        sizeMenu.add(freeSize);
        sizeMenu.addSeparator();
        
        for (TokenSize.Size size : TokenSize.Size.values()) {
            JMenuItem menuItem = new JMenuItem(new ChangeSizeAction(size.name(), size));
            sizeMenu.add(menuItem);
        }
        
    	popup.add(sizeMenu);

    	popup.show((ZoneRenderer)e.getSource(), e.getX(), e.getY());
	}
	
	private class ChangeSizeAction extends AbstractAction {
		
		private TokenSize.Size size;
		
		public ChangeSizeAction(String label, TokenSize.Size size) {
			super(label);
			this.size = size;
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {

			ZoneRenderer renderer = MapToolClient.getInstance().getCurrentZoneRenderer();
			for (Token token : renderer.getSelectedTokenSet()) {
				
				token.setSize(size.value());
				if (MapToolClient.isConnected()) {
					ClientConnection conn = MapToolClient.getInstance().getConnection();
					
					conn.callMethod(MapToolClient.COMMANDS.putToken.name(), renderer.getZone().getId(), token);
				}
			}
			
			renderer.repaint();
		}
		
	}
}
