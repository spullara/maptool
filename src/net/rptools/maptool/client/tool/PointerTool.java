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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppActions;
import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.CellPoint;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ZonePoint;
import net.rptools.maptool.client.ui.StackSummaryPanel;
import net.rptools.maptool.client.ui.TokenPopupMenu;
import net.rptools.maptool.client.ui.zone.ZoneOverlay;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Pointer;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.TokenSize;
import net.rptools.maptool.model.Zone;

/**
 */
public class PointerTool extends DefaultTool implements ZoneOverlay {

    private boolean isShowingPointer; 
    private boolean isDraggingToken;
    private boolean isNewTokenSelected;
    private boolean isDrawingSelectionBox;
    private boolean isSpaceDown;
    private boolean isMovingWithKeys;
    private Rectangle selectionBoundBox;

	private Token tokenBeingDragged;
	private Token tokenUnderMouse;
	
    // Offset from token's X,Y when dragging. Values are in cell coordinates.
    private int dragOffsetX;
    private int dragOffsetY;
	private int dragStartX;
	private int dragStartY;

	public PointerTool () {
        try {
            setIcon(new ImageIcon(ImageUtil.getImage("net/rptools/maptool/client/image/tool/PointerBlue16.png")));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
	@Override
	public String getInstructions() {
		return I18N.getText("tool.pointer.instructions");
	}
	
    @Override
    public String getTooltip() {
        return "Pointer tool";
    }

    public void startTokenDrag(Token keyToken) {
		tokenBeingDragged = keyToken;
		
		renderer.addMoveSelectionSet(MapTool.getPlayer().getName(), tokenBeingDragged.getId(), renderer.getSelectedTokenSet(), false);
		MapTool.serverCommand().startTokenMove(MapTool.getPlayer().getName(), renderer.getZone().getId(), tokenBeingDragged.getId(), renderer.getSelectedTokenSet());
		
		isDraggingToken = true;
		
    }
    
    public void stopTokenDrag() {
        renderer.commitMoveSelectionSet(tokenBeingDragged.getId()); // TODO: figure out a better way
        isDraggingToken = false;
        isMovingWithKeys = false;
    }
    
    private void showTokenStackPopup(List<Token> tokenList) {

		int gridSize = (int)renderer.getScaledGridSize();
		StackSummaryPanel summaryPanel = new StackSummaryPanel (gridSize, tokenList);
		
		Integer x = null;
		Integer y = null;
		
		// Calculate the top left corner of the stack
		for (Token token : tokenList) {
			if (x == null || token.getX() < x) {
				x = token.getX();
			}
			if (y == null || token.getY() < y) {
				y = token.getY();
			}
		}
		
		ScreenPoint sp = ScreenPoint.fromZonePoint(renderer, x, y);
		
		Point p = SwingUtilities.convertPoint(renderer, sp.x, sp.y, MapTool.getFrame().getGlassPane());
		
		x = p.x - StackSummaryPanel.PADDING - summaryPanel.getPreferredSize().width/2 + gridSize/2;
		y = p.y - StackSummaryPanel.PADDING;
		
		MapTool.getFrame().showNonModalGlassPane(summaryPanel, x, y);
    }
    
    ////
	// Mouse
	public void mousePressed(MouseEvent e) {

		// So that keystrokes end up in the right place
		renderer.requestFocus();
		
		if (isDraggingMap()) {
			return;
		}
		
		if (isDraggingToken) {
			return;
		}
		
		dragStartX = e.getX();
		dragStartY = e.getY();

		// Properties
		if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
			List<Token> tokenList = renderer.getTokenStackAt(mouseX, mouseY);
			if (tokenList != null) {
				renderer.clearSelectedTokens();
				showTokenStackPopup(tokenList);
			}
			
			return;
		}
		
		// SELECTION
		Token token = renderer.getTokenAt (e.getX(), e.getY());
		if (token != null && !isDraggingToken) {

			// Permission
			if (!AppUtil.playerOwnsToken(token)) {
				if (!SwingUtil.isShiftDown(e)) {
					renderer.clearSelectedTokens();
				}
				return;
			}
			
			// Don't select if it's already being moved by someone
			isNewTokenSelected = false;
			if (!renderer.isTokenMoving(token)) {
				if (!renderer.getSelectedTokenSet().contains(token.getId()) && 
						!SwingUtil.isShiftDown(e)) {
					isNewTokenSelected = true;
                    renderer.clearSelectedTokens();
				}
				renderer.selectToken(token.getId());
        
		        // Dragging offset for currently selected token
		        ZonePoint pos = ZonePoint.fromScreenPoint(renderer, e.getX(), e.getY());
		        dragOffsetX = pos.x - token.getX();
		        dragOffsetY = pos.y - token.getY();
			}
		} else {

			if (SwingUtilities.isLeftMouseButton(e)) {
				// Starting a bound box selection
				isDrawingSelectionBox = true;
				selectionBoundBox = new Rectangle(e.getX(), e.getY(), 0, 0);
			}
		}
		
		super.mousePressed(e);
	}
	
	public void mouseReleased(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e)) {
	        try {
	            SwingUtil.showPointer(renderer);
	
		        // SELECTION BOUND BOX
		        if (isDrawingSelectionBox) {
		        	isDrawingSelectionBox = false;
		
		        	if (!SwingUtil.isShiftDown(e)) {
		        		renderer.clearSelectedTokens();
		        	}
		        	
		        	renderer.selectTokens(selectionBoundBox);
		        	
		        	selectionBoundBox = null;
		        	renderer.repaint();
		        	return;
		        }
		
				// DRAG TOKEN COMPLETE
				if (isDraggingToken) {
					stopTokenDrag();
				}
				
		        // SELECT SINGLE TOKEN
		        Token token = renderer.getTokenAt(e.getX(), e.getY());
		        if (token != null && SwingUtilities.isLeftMouseButton(e) && !isDraggingToken && !SwingUtil.isShiftDown(e)) {
		
		    		// Only if it isn't already being moved
					if (!renderer.isTokenMoving(token)) {
			        	renderer.clearSelectedTokens();
			        	renderer.selectToken(token.getId());
					}
		        }
	        } finally {
	        	isDraggingToken = false;
	        	isDrawingSelectionBox = false;
	        }
	        
	        return;
		}
        
		// POPUP MENU
        if (SwingUtilities.isRightMouseButton(e) && !isDraggingToken) {
        	
        	if (tokenUnderMouse != null && renderer.getSelectedTokenSet().size() > 0) {
        		
        		new TokenPopupMenu(renderer.getSelectedTokenSet(), e.getX(), e.getY(), renderer, tokenUnderMouse).showPopup(renderer);
        		return;
        	}
        }

        super.mouseReleased(e);
	}
    
	////
	// MouseMotion
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
		
		mouseX = e.getX();
		mouseY = e.getY();
		
		CellPoint cp = renderer.getCellAt(new ScreenPoint(e.getX(), e.getY()));
		if (cp != null) {	
			MapTool.getFrame().setStatusMessage("Cell: " + cp.x + ", " + cp.y);
		} else {
		    MapTool.getFrame().setStatusMessage("");
        }

		if (isDraggingToken) {
			if (isMovingWithKeys) {
				return;
			}
			
			ZonePoint zonePoint = ZonePoint.fromScreenPoint(renderer, mouseX, mouseY);
			handleDragToken(zonePoint);
			return;
		}
		
		tokenUnderMouse = renderer.getTokenAt(mouseX, mouseY);
		renderer.setMouseOver(tokenUnderMouse);
		
	}
	
	public void mouseDragged(MouseEvent e) {

		mouseX = e.getX();
		mouseY = e.getY();
		
		CellPoint cellUnderMouse = renderer.getCellAt(new ScreenPoint(e.getX(), e.getY()));
		if (cellUnderMouse != null) {
			MapTool.getFrame().setStatusMessage("Cell: " + cellUnderMouse.x + ", " + cellUnderMouse.y);
		}
		
		if (SwingUtilities.isLeftMouseButton(e) && !SwingUtilities.isRightMouseButton(e)) {
			
			if (isDrawingSelectionBox) {
				
				int x1 = dragStartX;
				int y1 = dragStartY;
				
				int x2 = e.getX();
				int y2 = e.getY();
				
				selectionBoundBox.x = Math.min(x1, x2);
				selectionBoundBox.y = Math.min(y1, y2);
				selectionBoundBox.width = Math.abs(x1 - x2);
				selectionBoundBox.height = Math.abs(y1 - y2);
				
				renderer.repaint();
				return;
			}
			
			if (tokenUnderMouse == null || !renderer.getSelectedTokenSet().contains(tokenUnderMouse.getId())) {
				return;
			}

			if (!isDraggingToken && renderer.isTokenMoving(tokenUnderMouse)) {
                return;
            }
			
			if (isNewTokenSelected) {
				renderer.clearSelectedTokens();
				renderer.selectToken(tokenUnderMouse.getId());
			}
			isNewTokenSelected = false;
			
			// Might be dragging a token
			String playerId = MapTool.getPlayer().getName();
			Set<GUID> selectedTokenSet = renderer.getSelectedTokenSet();
			if (selectedTokenSet.size() > 0) {
				
				// Make sure we can do this
				// LATER: This might be able to be removed since you can't 
				// select an unowned token, check later
				if (!MapTool.getPlayer().isGM() && MapTool.getServerPolicy().useStrictTokenManagement()) {
					for (GUID tokenGUID : selectedTokenSet) {
						Token token = renderer.getZone().getToken(tokenGUID);
						if (!token.isOwner(playerId)) {
							return;
						}
					}
				}
				
				Point origin = new Point(tokenUnderMouse.getX(), tokenUnderMouse.getY());
				
				origin.translate(dragOffsetX, dragOffsetY);
        
				int x = e.getX();
				int y = e.getY();
				
				if (!isDraggingToken) {
					startTokenDrag(tokenUnderMouse);
				} else {
					if (isMovingWithKeys) {
						return;
					}
					ZonePoint zonePoint = ZonePoint.fromScreenPoint(renderer, x, y);
					handleDragToken(zonePoint);
				}
				isDraggingToken = true;
                SwingUtil.hidePointer(renderer);
			}
			
			return;
		}
		
		super.mouseDragged(e);
	}	
	
	public boolean isDraggingToken() {
		return isDraggingToken;
	}
	
	/**
	 * Move the keytoken being dragged to this zone point
	 * @param zonePoint
	 * @return true if the move was successful
	 */
	public boolean handleDragToken(ZonePoint zonePoint) {
		// TODO: Optimize this (combine with calling code)
		if (tokenBeingDragged.isSnapToGrid()) {

			CellPoint cellUnderMouse = zonePoint.convertToCell(renderer);
			zonePoint = cellUnderMouse.convertToZone(renderer);
			MapTool.getFrame().setStatusMessage("Cell: " + cellUnderMouse.x + ", " + cellUnderMouse.y);
			
		} else {
		    zonePoint.translate(-dragOffsetX, -dragOffsetY);
        }
		
		// Make sure it's a valid move
		if (!validateMove(tokenBeingDragged, renderer.getSelectedTokenSet(), zonePoint)) {
			return false;
		}

		dragStartX = zonePoint.x;
		dragStartY = zonePoint.y;

		renderer.updateMoveSelectionSet(tokenBeingDragged.getId(), zonePoint);
		MapTool.serverCommand().updateTokenMove(renderer.getZone().getId(), tokenBeingDragged.getId(), zonePoint.x, zonePoint.y);
		return true;
	}

	private boolean validateMove(Token leadToken, Set<GUID> tokenSet, ZonePoint point) {

		Zone zone = renderer.getZone();
		if (MapTool.getPlayer().isGM()) {
			return true;
		}
		
		if (zone.hasFog()) {
			
			// Check that the new position for each token is within the exposed area
			Area fow = zone.getExposedArea();
			if (fow == null) {
				return true;
			}

			int deltaX = point.x - leadToken.getX();
			int deltaY = point.y - leadToken.getY();
            Rectangle bounds = new Rectangle();
			for (GUID tokenGUID : tokenSet) {
				Token token = zone.getToken(tokenGUID);
				if (token == null) {
					continue;
				}
				
				int x = token.getX() + deltaX;
				int y = token.getY() + deltaY;
	            int width = TokenSize.getWidth(token, zone.getGridSize());
	            int height = TokenSize.getHeight(token, zone.getGridSize());

	            int fudgeW = (int)(width*.25);
	            int fudgeH = (int)(height*.25);
	            bounds.setBounds(x+fudgeW, y+fudgeH, width-fudgeW*2, height-fudgeH*2);

	            if (!fow.contains(bounds)) {
	            	return false;
	            }
			}
		}
		
		return true;
	}

	@Override
	protected void installKeystrokes(Map<KeyStroke, Action> actionMap) {
		super.installKeystrokes(actionMap);
		
		actionMap.put(KeyStroke.getKeyStroke("control C"), AppActions.COPY_TOKENS);
		actionMap.put(KeyStroke.getKeyStroke("control V"), AppActions.PASTE_TOKENS);
		
		// TODO: Optimize this by making it non anonymous
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), new AbstractAction() {
		
			public void actionPerformed(java.awt.event.ActionEvent e) {
				
				ZoneRenderer renderer = (ZoneRenderer) e.getSource();
				
				Set<GUID> selectedTokenSet = renderer.getSelectedTokenSet();
				
				for (GUID tokenGUID : selectedTokenSet) {
					
					Token token = renderer.getZone().getToken(tokenGUID);
					
					if (AppUtil.playerOwnsToken(token)) {
                        renderer.getZone().removeToken(tokenGUID);
                        MapTool.serverCommand().removeToken(renderer.getZone().getId(), tokenGUID);
					}
				}
				
				renderer.clearSelectedTokens();
			}
		});
		
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				if (isShowingPointer) {
					isShowingPointer = false;
					MapTool.serverCommand().hidePointer(MapTool.getPlayer().getName());
				}
				
				isSpaceDown = false;
			}
		});
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				if (isSpaceDown) {
					return;
				}
				
				if (isDraggingToken) {
					
					// Waypoint
		            CellPoint cp = ZonePoint.fromScreenPoint(renderer, mouseX, mouseY).convertToCell(renderer);
		            
		            renderer.toggleMoveSelectionSetWaypoint(tokenBeingDragged.getId(), cp);
		            
		            MapTool.serverCommand().toggleTokenMoveWaypoint(renderer.getZone().getId(), tokenBeingDragged.getId(), cp);
					
				} else {
					
					// Pointer
					isShowingPointer = true;
					
					ZonePoint p = ZonePoint.fromScreenPoint(renderer, mouseX, mouseY);
					Pointer pointer = new Pointer(renderer.getZone(), p.x, p.y, 0);
					
					MapTool.serverCommand().showPointer(MapTool.getPlayer().getName(), pointer);
				}
				
				isSpaceDown = true;
			}
		});
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				if (!isDraggingToken) {
					return;
				}
					
				// Stop
				stopTokenDrag();
			}
		});
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD5, 0), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				if (!isDraggingToken) {
					return;
				}
					
				// Stop
				stopTokenDrag();
			}
		});
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD6, 0), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				handleKeyMove(1, 0);
			}
		});
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD4, 0), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				handleKeyMove(-1, 0);
			}
		});
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD8, 0), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				handleKeyMove(0, -1);
			}
		});
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, 0), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				handleKeyMove(0, 1);
			}
		});
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD7, 0), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				handleKeyMove(-1, -1);
			}
		});
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD9, 0), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				handleKeyMove(1, -1);
			}
		});
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, 0), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				handleKeyMove(-1, 1);
			}
		});
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD3, 0), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				handleKeyMove(1, 1);
			}
		});
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				handleKeyMove(1, 0);
			}
		});
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				cycleSelectedToken(1);
			}
		});
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.SHIFT_DOWN_MASK), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				
				cycleSelectedToken(-1);
			}
		});
	}

	private void cycleSelectedToken(int direction) {
		
		List<Token> visibleTokens = renderer.getTokensOnScreen();
		Set<GUID> selectedTokenSet = renderer.getSelectedTokenSet();
		Integer newSelection = null;
		
		if (visibleTokens.size() == 0) {
			return;
		}
		
		if (selectedTokenSet.size() == 0) {
			newSelection = 0;
		} else {
			
			// Find the first selected token on the screen
			for (int i = 0; i < visibleTokens.size(); i++) {
				Token token = visibleTokens.get(i);
				if (!renderer.isTokenSelectable(token.getId())) {
					continue;
				}
				if (renderer.getSelectedTokenSet().contains(token.getId())) {
					newSelection = i;
					break;
				}
			}

			// Pick the next
			newSelection += direction;
		}
		
		if (newSelection < 0) {
			newSelection = visibleTokens.size()-1;
		}
		if (newSelection >= visibleTokens.size()) {
			newSelection = 0;
		}
		
		// Make the selection
		renderer.clearSelectedTokens();
		renderer.selectToken(visibleTokens.get(newSelection).getId());
		
	}
	
	private void handleKeyMove(int dx, int dy) {

		if (!isDraggingToken) {
			
			// Start
			Set<GUID> selectedTokenSet = renderer.getSelectedTokenSet();
			if (selectedTokenSet.size() != 1) {
				// only allow one at a time
				return;
			}
			
			Token token = renderer.getZone().getToken(selectedTokenSet.iterator().next());
			if (token == null) {
				return;
			}

			// Only one person at a time
			if (renderer.isTokenMoving(token)) {
				return;
			}
			
			dragStartX = token.getX();
			dragStartY = token.getY();
			startTokenDrag(token);
		}
		
		ZonePoint zp = null;
		if (tokenBeingDragged.isSnapToGrid()) {
			
			CellPoint cp = new ZonePoint(dragStartX, dragStartY).convertToCell(renderer);

			cp.x += dx;
			cp.y += dy;
			
			zp = cp.convertToZone(renderer);
		} else {
			int size = TokenSize.getWidth(tokenBeingDragged, renderer.getZone().getGridSize());
			
			int x = dragStartX + (size*dx);
			int y = dragStartY + (size*dy);
			
			zp = new ZonePoint(x, y);
		}

		isMovingWithKeys = true;
		handleDragToken(zp);
	}

	//// 
	// ZoneOverlay
	/* (non-Javadoc)
	 * @see net.rptools.maptool.client.ZoneOverlay#paintOverlay(net.rptools.maptool.client.ZoneRenderer, java.awt.Graphics2D)
	 */
	public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
		
		if (selectionBoundBox != null) {
			
			Stroke stroke = g.getStroke();
			g.setStroke(new BasicStroke(2));
			
			Composite composite = g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, .25f));
			g.setColor(AppStyle.selectionBoxFill);
			g.fillRoundRect(selectionBoundBox.x, selectionBoundBox.y, selectionBoundBox.width, selectionBoundBox.height,10, 10);
			g.setComposite(composite);
			
			g.setColor(AppStyle.selectionBoxOutline);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawRoundRect(selectionBoundBox.x, selectionBoundBox.y, selectionBoundBox.width, selectionBoundBox.height,10, 10);

			g.setStroke(stroke);
		}
		
	}
	
}
