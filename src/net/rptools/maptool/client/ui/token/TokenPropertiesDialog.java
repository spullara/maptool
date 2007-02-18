/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft, Jay Gorrell
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

package net.rptools.maptool.client.ui.token;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Association;
import net.rptools.maptool.model.ModelChangeEvent;
import net.rptools.maptool.model.ModelChangeListener;
import net.rptools.maptool.model.ObservableList;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.TokenSize;
import net.rptools.maptool.util.ImageManager;

import com.jeta.forms.components.image.ImageComponent;
import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.form.FormAccessor;
import com.jidesoft.grid.AbstractPropertyTableModel;
import com.jidesoft.grid.Property;
import com.jidesoft.grid.PropertyPane;
import com.jidesoft.grid.PropertyTable;
import com.jidesoft.swing.CheckBoxListWithSelectable;
import com.jidesoft.swing.DefaultSelectable;
import com.jidesoft.swing.Selectable;

/**
 * This dialog is used to display all of the token states and notes to the user.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class TokenPropertiesDialog extends JDialog implements ActionListener,
		ModelChangeListener {

	private AbstractButton okButton;
	private AbstractButton cancelButton;
	private JTextField tokenName;
	private JTextField tokenGMName;
	private JLabel tokenGMNameLabel;
	private ImageComponent tokenIcon;
	private JTextArea notes;
	private JTextArea gmNotes;
	private JPanel gmNotesPanel;
	private JComboBox shape;
	private JComboBox size;
	private JCheckBox snapToGrid;
	private JCheckBox visible;
	private JPanel statesPanel;
	private Token token;
	private boolean tokenSaved;
	private JLabel visibleLabel;
	private PropertyTable propertyTable;
	private JComboBox propertyTypeCombo;
	private JComboBox tokenTypeCombo;
	private JCheckBox allPlayersCheckbox;
	private CheckBoxListWithSelectable ownerList;
	private JTabbedPane tabs;
	private JTable macroTable;

	/**
	 * The size used to constrain the icon.
	 */
	public static final int SIZE = 64;

	/**
	 * Create a new token notes dialog.
	 * 
	 * @param token
	 *            The token being displayed.
	 */
	public TokenPropertiesDialog() {
		super(MapTool.getFrame(), "Token Properties", true);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		FormPanel panel = new FormPanel(
				"net/rptools/maptool/client/ui/forms/tokenPropertiesDialog.jfrm");

		tabs = panel.getTabbedPane("tabs");
		
		initNotesPanel(panel);
		initTokenDetails(panel);
		initConfigPanel(panel);
		initButtons(panel);
		initPropertiesPanel(panel);
		initStatesPanel(panel);
		initOwnershipPanel(panel);
		initMacroPanel(panel);
		
		add(panel);
		pack();
	}
	
	private void initMacroPanel(FormPanel panel) {

		macroTable = panel.getTable("macroTable");
		
	}
	
	private void initOwnershipPanel(FormPanel panel) {
	
		allPlayersCheckbox = panel.getCheckBox("allPlayersCheckbox");
		
		ownerList = new CheckBoxListWithSelectable();
		
		panel.getFormAccessor("ownershipPanel").replaceBean("ownershipList", new JScrollPane(ownerList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
	}

	private void initNotesPanel(FormPanel panel) {
		
		notes = (JTextArea) panel.getTextComponent("notes");
		notes.addMouseListener(new MouseHandler(notes));

		gmNotes = (JTextArea) panel.getTextComponent("gmNotes");
		gmNotes.addMouseListener(new MouseHandler(gmNotes));

		gmNotesPanel = panel.getPanel("gmNotesPanel");
	}
	
	private void initTokenDetails(FormPanel panel) {
		tokenName = panel.getTextField("tokenName");
		tokenGMName = panel.getTextField("tokenGMName");
		tokenIcon = (ImageComponent) panel.getComponentByName("tokenIcon");
		tokenIcon.setPreferredSize(new Dimension(100, 100));
		tokenIcon.setMinimumSize(new Dimension(100, 100));
		
		tokenGMNameLabel = panel.getLabel("tokenGMNameLabel");
		
		tokenTypeCombo = panel.getComboBox("typeCombo");
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement(Token.Type.NPC);
		model.addElement(Token.Type.PC);
		tokenTypeCombo.setModel(model);
	}

	private void initConfigPanel(FormPanel panel) {

		visible = panel.getCheckBox("visible");
		visibleLabel = panel.getLabel("visibleLabel");

		shape = panel.getComboBox("shape");
		shape.setModel(new DefaultComboBoxModel(Token.TokenShape.values()));

		DefaultComboBoxModel model = new DefaultComboBoxModel(TokenSize.Size.values());
		model.insertElementAt("Free Size", 0);
		size = panel.getComboBox("size");
		size.setModel(model);
		
		snapToGrid = panel.getCheckBox("snapToGrid");
	}
	
	private void initButtons(FormPanel panel) {
		okButton = panel.getButton("okButton");
		okButton.addActionListener(this);
		getRootPane().setDefaultButton((JButton) okButton);
		
		cancelButton = panel.getButton("cancelButton");
		cancelButton.addActionListener(this);
	}
	
	private void initPropertiesPanel(FormPanel panel) {

		propertyTable = new PropertyTable();
		
		PropertyPane pane = new PropertyPane(propertyTable);
		pane.setPreferredSize(new Dimension(100, 300));
		
		FormAccessor accessor = panel.getFormAccessor("propertiesPanel");
		accessor.replaceBean("propertiesTable", pane);
		
		propertyTypeCombo = panel.getComboBox("propertyTypeCombo");
	}

	private void initStatesPanel(FormPanel panel) {
		// Set up all of the state combo boxes.
		statesPanel = panel.getPanel("statesPanel");
		statesPanel.removeAll();
		Set<String> states = TokenStates.getStates();
		statesPanel.setLayout(new GridLayout(0, 4));
		for (String state : states) {
			statesPanel.add(new JCheckBox(state));
		}		
	}
	
	@Override
	public void setVisible(boolean b) {
		if(b) {
			SwingUtil.centerOver(this, MapTool.getFrame());

			if (!MapTool.getPlayer().isGM()) {
				visible.setVisible(false);
				visibleLabel.setVisible(false);
			}
		}
		super.setVisible(b);
	}

	/*---------------------------------------------------------------------------------------------
	 * ActionListener Interface Methods
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent aE) {
		if (aE.getSource() == okButton) {
			updateToken();
			MapTool.getFrame().updateTokenTree();
			setVisible(false);
		} else if (aE.getSource() == cancelButton) {
			setVisible(false);
		} 
		
		setToken(null);
	}

	/*---------------------------------------------------------------------------------------------
	 * ModelChangeListener Interface Methods
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * @see net.rptools.maptool.model.ModelChangeListener#modelChanged(net.rptools.maptool.model.ModelChangeEvent)
	 */
	public void modelChanged(ModelChangeEvent aEvent) {
		// TODO Auto-generated method stub

	}

	/*---------------------------------------------------------------------------------------------
	 * Instance Methods
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * Update the token to match the state of the dialog
	 */
	public void updateToken() {

		// Check the name
		String name = tokenName.getText();
		if (name == null || (name = name.trim()).length() == 0) {
			JOptionPane.showMessageDialog(this, "A name is required.");
			throw new IllegalStateException("A name is required.");
		} // endif

		// Set the token values
		token.setName(name);
		token.setGMName(tokenGMName.getText());
		token.setNotes(notes.getText());
		token.setGMNote(gmNotes.getText());
		token.setShape((Token.TokenShape) shape.getSelectedItem());
		token.setSnapToGrid(snapToGrid.isSelected());
		token.setVisible(visible.isSelected());
		token.setType((Token.Type) tokenTypeCombo.getSelectedItem());

		// Get size
		if (size.getSelectedIndex() == 0) {
			token.setSnapToScale(false);
		} else {
			token.setSnapToScale(true);
			token.setSize(((TokenSize.Size) size.getSelectedItem()).value());
		} // endif

		// Get the states
		Component[] components = statesPanel.getComponents();
		for (int i = 0; i < components.length; i++) {
			JCheckBox cb = (JCheckBox) components[i];
			String state = cb.getText();
			token.setState(state, cb.isSelected() ? Boolean.TRUE
					: Boolean.FALSE);
		}
		
		// Ownership
		token.clearAllOwners();
		if (allPlayersCheckbox.isSelected()) {
			token.setAllOwners();
		}
		
		for (int i = 0; i < ownerList.getModel().getSize(); i++) {
			DefaultSelectable selectable = (DefaultSelectable) ownerList.getModel().getElementAt(i);
			if (selectable.isSelected()) {
				token.addOwner((String) selectable.getObject());
			}
		}
		
		// Macros
		token.setMacroMap(((MacroTableModel)macroTable.getModel()).getMap());
		
		tokenSaved = true;
	}

	/**
	 * Set the currently displayed token.
	 * 
	 * @param aToken
	 *            The token to be displayed
	 */
	public void setToken(Token aToken) {

		if (aToken == token)
			return;
		if (token != null) {
			token.removeModelChangeListener(this);
		}
		
		token = aToken;
		
		if (token != null) {
			token.addModelChangeListener(this);
			
			List<String> typeList = new ArrayList<String>();
			typeList.addAll(MapTool.getCampaign().getTokenTypes());
			Collections.sort(typeList);
			propertyTypeCombo.setModel(new DefaultComboBoxModel(typeList.toArray()));

			setFields();
			
		}

		tabs.setSelectedIndex(0);
	}

	/**
	 * Set the fields to match the state of the current token
	 */
	public void setFields() {

		Player player = MapTool.getPlayer();

		// No token? clear the dialog
		boolean editable = player.isGM()
				|| !MapTool.getServerPolicy().useStrictTokenManagement() || token.isOwner(player.getName());
		if (token == null) {
			tokenName.setText("");
			tokenName.setEditable(false);
			tokenIcon.setIcon(null);
			notes.setText("");
			notes.setEditable(false);
			shape.setSelectedIndex(-1);
			shape.setEnabled(false);
			size.setSelectedIndex(-1);
			size.setEnabled(false);
			snapToGrid.setSelected(false);
			snapToGrid.setEnabled(false);
			visible.setSelected(false);
			visible.setEnabled(false);
			okButton.setEnabled(false);
			
			propertyTable.setModel(new EmptyPropertyTableModel());
			return;
		}

		// Set the fields from the token.
		tokenName.setText(token.getName());
		tokenGMName.setText(token.getGMName());
		tokenIcon.setIcon(getTokenIcon());
		notes.setText(token.getNotes());
		gmNotes.setText(token.getGMNotes());
		shape.setSelectedItem(token.getShape());
		snapToGrid.setSelected(token.isSnapToGrid());
		visible.setSelected(token.isVisible());
		if (!token.isSnapToScale())
			size.setSelectedIndex(0);
		else
			size.setSelectedItem(TokenSize.getSizeInstance(
        token.getSize()));

		// Set the editable & enabled state
		okButton.setEnabled(editable);
		notes.setEditable(editable);
		tokenName.setEditable(editable);
		shape.setEnabled(editable);
		size.setEnabled(editable);
		snapToGrid.setEnabled(editable);
		visible.setEnabled(editable);
		tokenTypeCombo.setSelectedItem(token.getType());

		gmNotesPanel.setVisible(player.isGM());
		tokenGMName.setVisible(player.isGM());
		tokenGMNameLabel.setVisible(player.isGM());
		tokenTypeCombo.setEnabled(player.isGM());
		
		propertyTypeCombo.setSelectedItem(token.getPropertyType());
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				propertyTable.setModel(new TokenPropertyTableModel());
				propertyTable.expandAll();
			}
		});
				
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				ownerList.setModel(new OwnerListModel());
			}
		});

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				macroTable.setModel(new MacroTableModel(token));
			}
		});
		
		System.out.println(token.isOwnedByAll());
		allPlayersCheckbox.setSelected(token.isOwnedByAll());

		// Handle the states
		Component[] states = statesPanel.getComponents();
		for (int i = 0; i < states.length; i++) {
			JCheckBox state = (JCheckBox) states[i];
			state.setEnabled(token != null && editable);
			Boolean stateValue = token != null ? (Boolean) token.getState(state.getText()) : null;
			state.setSelected(stateValue == null ? false : stateValue.booleanValue());
		}
	}

	/**
	 * Get and icon from the asset manager and scale it properly.
	 * 
	 * @return An icon scaled to fit within a cell.
	 */
	private Icon getTokenIcon() {

		// Get the base image && find the new size for the icon
		BufferedImage assetImage = null;
		Asset asset = AssetManager.getAsset(token.getAssetID());
		if (asset == null) {
			assetImage = ImageManager.UNKNOWN_IMAGE;
		} else {
			assetImage = ImageManager.getImage(asset, this);
		}

		// Need to resize?
		if (assetImage.getWidth() > SIZE || assetImage.getHeight() > SIZE) {
			Dimension dim = new Dimension(assetImage.getWidth(), assetImage
					.getWidth());
			if (dim.height < dim.width) {
				dim.height = (int) ((dim.height / (double) dim.width) * SIZE);
				dim.width = SIZE;
			} else {
				dim.width = (int) ((dim.width / (double) dim.height) * SIZE);
				dim.height = SIZE;
			}
			BufferedImage image = new BufferedImage(dim.width, dim.height,
					Transparency.BITMASK);
			Graphics2D g = image.createGraphics();
			g.drawImage(assetImage, 0, 0, dim.width, dim.height, null);
			assetImage = image;
		}
		return new ImageIcon(assetImage);
	}

	/** @return Getter for tokenSaved */
	public boolean isTokenSaved() {
		return tokenSaved;
	}
	
	////
	// HANDLER
	public class MouseHandler extends MouseAdapter {
		
		JTextArea source;
		
		public MouseHandler(JTextArea source) {
			this.source = source;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				
				JPopupMenu menu = new JPopupMenu();
				JMenuItem sendToChatItem = new JMenuItem("Send to Chat");
				sendToChatItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						String selectedText = source.getSelectedText();
						if (selectedText == null) {
							selectedText = source.getText();
						}

						// TODO: COmbine this with the code int MacroButton
						JTextArea commandArea = MapTool.getFrame().getCommandPanel().getCommandTextArea();

						commandArea.setText(commandArea.getText() + selectedText);
						commandArea.requestFocusInWindow();
					}
				});
				
				menu.add(sendToChatItem);
				
				JMenuItem sendAsEmoteItem = new JMenuItem("Send as Emit");
				sendAsEmoteItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						String selectedText = source.getSelectedText();
						if (selectedText == null) {
							selectedText = source.getText();
						}

						// TODO: COmbine this with the code int MacroButton
						JTextArea commandArea = MapTool.getFrame().getCommandPanel().getCommandTextArea();

						commandArea.setText("/emit " + selectedText);
						commandArea.requestFocusInWindow();
						MapTool.getFrame().getCommandPanel().commitCommand();
					}
				});
				
				menu.add(sendAsEmoteItem);
				
				menu.show((JComponent)e.getSource(), e.getX(), e.getY());
			}
		}
	}
	
	////
	// MODELS
	private class TokenPropertyTableModel extends AbstractPropertyTableModel {

		private Token token;
		
		@Override
		public Property getProperty(int index) {
			
			List<String> propertyList = MapTool.getCampaign().getTokenPropertyList(token.getPropertyType());
			
			return new TokenProperty(propertyList.get(index));
		}

		private Token getToken() {
			if (token == null) {
				token = TokenPropertiesDialog.this.token;
			}
			return token;
		}
		
		@Override
		public int getPropertyCount() {
			List<String> propertyList = MapTool.getCampaign().getTokenPropertyList(getToken().getPropertyType()); 
			return propertyList != null ? propertyList.size() : 0;
		}
		
		private class TokenProperty extends Property {
			private String key;
			
			public TokenProperty(String key) {
				super(key, key, String.class, "Core");
				this.key = key;
			}
			
			@Override
			public Object getValue() {
				return getToken().getProperty(key);
			}

			@Override
			public void setValue(Object value) {
				getToken().setProperty(key, value);
			}

			@Override
			public boolean hasValue() {
				return getToken().getProperty(key) != null;
			}
		}
	}
	
	private class EmptyPropertyTableModel extends AbstractPropertyTableModel {
		@Override
		public Property getProperty(int arg0) {
			return null;
		}
		@Override
		public int getPropertyCount() {
			return 0;
		}
	}

	private class OwnerListModel extends AbstractListModel {

		List<Selectable> ownerList = new ArrayList<Selectable>();
		
		public OwnerListModel() {
			List<String> list = new ArrayList<String>();
			Set<String> ownerSet = token.getOwners();
			list.addAll(ownerSet);
			
			ObservableList<Player> playerList = MapTool.getPlayerList(); 
			for (Object item : playerList) {
				Player player = (Player) item;
				String playerId = player.getName();
				if (!list.contains(playerId)) {
					list.add(playerId);
				}
			}
			
			Collections.sort(list);
			
			for (String id : list) {
				Selectable selectable = new DefaultSelectable(id);
				selectable.setSelected(ownerSet.contains(id));
				ownerList.add(selectable);
			}
		}
		
		public Object getElementAt(int index) {
			return ownerList.get(index);
		}
		public int getSize() {
			return ownerList.size();
		}
	}
	
	private static class MacroTableModel extends AbstractTableModel {
		
		private Association<String, String> newRow = new Association<String, String>("", "");
		private List<Association<String, String>> rowList = new ArrayList<Association<String, String>>();
		
		public MacroTableModel(Token token) {
			for (String macroName : token.getMacroNames()) {
				rowList.add(new Association<String, String>(macroName, token.getMacro(macroName)));
			}
			
			Collections.sort(rowList, new Comparator<Association<String, String>>() {
				public int compare(Association<String, String> o1, Association<String, String> o2) {

					return o1.getLeft().compareToIgnoreCase(o2.getLeft());
				}
			});
		}
		
		public int getColumnCount() {
			return 2;
		}
		public int getRowCount() {
			return rowList.size() + 1;
		}
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex == getRowCount() - 1) {
				switch(columnIndex) {
				case 0: return newRow.getLeft();
				case 1: return newRow.getRight();
				}
				return "";
			}
			
			switch (columnIndex) {
			case 0: return rowList.get(rowIndex).getLeft();
			case 1: return rowList.get(rowIndex).getRight();
			}
			return "";
		}
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (rowIndex == getRowCount() - 1) {
				switch(columnIndex) {
				case 0: newRow.setLeft((String)aValue); break;
				case 1: newRow.setRight((String)aValue); break;
				}
				
				rowList.add(newRow);
				newRow = new Association<String, String>("", "");
				return;
			}
			
			switch(columnIndex) {
			case 0: rowList.get(rowIndex).setLeft((String)aValue); break;
			case 1: rowList.get(rowIndex).setRight((String)aValue); break;
			}
		}
		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0: return "Key";
			case 1: return "Value";
			}
			return "";
		}
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}
		
		public Map<String, String> getMap() {
			Map<String, String> map = new HashMap<String, String>();
			
			for (Association<String, String> row : rowList) {
				map.put(row.getLeft(), row.getRight());
			}
			
			return map;
		}
	}
	
	public static void main(String[] args) {
		
		Token token = new Token();
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 200, 200);
		frame.setLayout(new GridLayout());
		frame.add(new JScrollPane(new JTable(new MacroTableModel(token))));
		frame.setVisible(true);
	}
}
