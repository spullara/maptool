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
package net.rptools.maptool.client.ui.macrobuttons.buttons;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolUtil;
import net.rptools.maptool.client.ui.MacroButtonDialog;
import net.rptools.maptool.client.ui.MacroButtonHotKeyManager;
import net.rptools.maptool.client.ui.macrobuttons.panels.AbstractMacroPanel;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.MacroButtonProperties;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.util.GraphicsUtil;
import net.rptools.maptool.client.ui.macrobuttons.buttongroups.AbstractButtonGroup;
import net.rptools.maptool.client.ui.macrobuttons.buttongroups.ButtonGroup;

/**
 * Base class of CampaignMacroButton and GlobalMacroButton.
 * TokenMacroButton doesn't extend this class because it is very simple.
 * MacroButtons that extend this class use MacroButtonProperties
 * @see net.rptools.maptool.model.MacroButtonProperties as data object.
 * 
 * These buttons are used in Macro Button Panel in the UI.
 */
public class MacroButton extends JButton implements MouseListener
{
	private final MacroButtonProperties properties;
	private final MacroButtonHotKeyManager hotKeyManager;
	private final ButtonGroup buttonGroup;
	private final AbstractMacroPanel panel;
	private final String panelClass;
	private final GUID tokenId;
	private static final Insets buttonInsets = new Insets(2, 2, 2, 2);
	private DragSource dragSource;
	private DragGestureListener dgListener;
	private DragSourceListener dsListener;

	public MacroButton(MacroButtonProperties properties, ButtonGroup buttonGroup){
		this(properties, buttonGroup, null);
	}
	
	public MacroButton(MacroButtonProperties properties, ButtonGroup buttonGroup, Token token) {
		this.properties = properties;
		this.buttonGroup = buttonGroup;
		this.panel = buttonGroup.getPanel();
		this.panelClass = buttonGroup.getPanel().getPanelClass();
		if (token==null){
			this.tokenId = null;
		} else {
			this.tokenId = token.getId();
		}
		this.properties.setTokenId(this.tokenId);
		this.properties.setSaveLocation(this.panelClass);
		this.properties.setButton(this);
		// we have to call setColor() and setText() here since properties only hold "dumb" data.
		setColor(properties.getColorKey());
		setText(getButtonText());
		hotKeyManager = new MacroButtonHotKeyManager(this);
		hotKeyManager.assignKeyStroke(properties.getHotKey());
		setMargin(buttonInsets);
		makeDraggable(DragSource.DefaultCopyDrop);
		addMouseListener(this);
	}

	public MacroButtonProperties getProperties() {
		return properties;
	}
	public MacroButtonHotKeyManager getHotKeyManager()
	{
		return hotKeyManager;
	}
	public GUID getTokenId(){
		return tokenId;
	}
	public Token getToken() {
		return MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
	}
	public AbstractButtonGroup getButtonGroup() {
		return buttonGroup;
	}
	public String getPanelClass() {
		return panelClass;
	}

	public void setColor(String colorKey) {

		//If the key doesn't correspond to one of our colors, then use the default
		if (!MapToolUtil.isValidColor(colorKey))
			setBackground(null);
		else {
			setBackground(MapToolUtil.getColor(colorKey));
		}
	}

	/*
	 *  Get the text for the macro button by filtering out
	 *   label macro (if any), and add hotkey hint (if any)
	 */
	public String getButtonText() {

		String buttonLabel;
		final Pattern MACRO_LABEL = Pattern.compile("^(/\\w+\\s+)(.*)$");
		String label = properties.getLabel();
		Matcher m = MACRO_LABEL.matcher(label);
		if(m.matches())
			buttonLabel = m.group(2);
		else
			buttonLabel = label;

		String formatButtonLabel = "<div style='text-align:center; font-size:" + properties.getFontSize() + "; color:"+properties.getFontColorKey()+"; "+getMinWidth()+getMaxWidth()+"'>" + buttonLabel;
		// if there is no hotkey (HOTKEY[0]) then no need to add hint
		String hotKey = properties.getHotKey();
		if( hotKey.equals(MacroButtonHotKeyManager.HOTKEYS[0]) )
			return "<html>"+formatButtonLabel+"</html>";
		else
			return "<html>"+formatButtonLabel+"<font style='font-size:0.8em'> (" + hotKey + ")</font></div></html>";
	}
	
	public String getMinWidth(){
		// the min-width style doesn't appear to work in the current java, so I'm 
		// using width instead.
		String newMinWidth = properties.getMinWidth();
		if (newMinWidth != null && !newMinWidth.equals("")) {
			return " width:"+newMinWidth+";";
			// return " min-width:"+newMinWidth+";";
		}
		return "";
	}

	public String getMaxWidth(){
		// doesn't appear to work in current java, leaving it in just in case 
		// it is supported in the future
		String newMaxWidth = properties.getMaxWidth();
		if (newMaxWidth != null && !newMaxWidth.equals("")) {
			return " max-width:"+newMaxWidth+";"; 
		}
		return "";
	}

	// Override these mouse events in subclasses to specify component specific behavior.
	public void mouseClicked(MouseEvent event) {
	}

	public void mousePressed(MouseEvent event) {
	}

	public void mouseReleased(MouseEvent event)	{
		if (SwingUtilities.isLeftMouseButton(event)) {
			properties.executeMacro(SwingUtil.isShiftDown(event));
		} else if (SwingUtilities.isRightMouseButton(event) && !buttonGroup.getGroupLabel().equalsIgnoreCase("Common Macros")) {
			if (getPanelClass()=="CampaignPanel" && !MapTool.getPlayer().isGM()) {
				return;
			}
			new MacroButtonPopupMenu(this, panelClass).show(this, event.getX(), event.getY());
		}
	}
	
	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	private void makeDraggable(Cursor cursor) {
		dragSource = DragSource.getDefaultDragSource();
		dgListener = new DGListener(cursor);
		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, dgListener);
		dsListener = new DSListener();
	}
	
	private class DGListener implements DragGestureListener {

		final Cursor cursor;
		
		public DGListener(Cursor cursor) {
			this.cursor = cursor;
		}

		public void dragGestureRecognized(DragGestureEvent dge) {
			Transferable t = new TransferableMacroButton(MacroButton.this);
			dge.startDrag(cursor, t, dsListener);
		}
	}

	private class DSListener implements DragSourceListener {
		
		public void dragEnter(DragSourceDragEvent event) {
			//System.out.println("TMB: drag enter");
			//DragSourceContext context = event.getDragSourceContext();
			//context.getComponent()
		}

		public void dragOver(DragSourceDragEvent event) {
			//System.out.println("TMB: drag over");
		}

		public void dropActionChanged(DragSourceDragEvent event) {
			//System.out.println("TMB: drop action changed");
		}

		public void dragExit(DragSourceEvent event) {
			//System.out.println("TMB: drag exit");
		}

		public void dragDropEnd(DragSourceDropEvent event) {
			//System.out.println("TMB: drag drop end");
			// js commented out for testing - MapTool.getFrame().updateSelectionPanel();
		}
	}
	

}
