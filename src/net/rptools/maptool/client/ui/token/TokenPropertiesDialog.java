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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.ModelChangeEvent;
import net.rptools.maptool.model.ModelChangeListener;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.TokenSize;
import net.rptools.maptool.util.ImageManager;

import com.jeta.forms.components.image.ImageComponent;
import com.jeta.forms.components.panel.FormPanel;

/**
 * This dialog is used to display all of the token states and notes to the user.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class TokenPropertiesDialog extends JDialog implements ActionListener,
		ModelChangeListener {

	/*---------------------------------------------------------------------------------------------
	 * Instance Variables
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * This button will trigger the action to save the notes and close the
	 * dialog.
	 */
	private AbstractButton okButton;

	/**
	 * This button will trigger the action to close the dialog.
	 */
	private AbstractButton cancelButton;

	/**
	 * The component that contains the name of the token.
	 */
	private JTextField tokenName;

  /**
   * The game master's name for the token. It is only shown to the game master.
   */
	private JTextField tokenGMName;
	
  /**
   * The game master's name for the token. It is only shown to the game master.
   */
	private JLabel tokenGMNameLabel;

	/**
	 * The component that contains the icon for the token.
	 */
	private ImageComponent tokenIcon;

	/**
	 * The pane used to display token notes.
	 */
	private JEditorPane notes;

  /**
   * Notes that are only shown to the game master.
   */
	private JEditorPane gmNotes;

  /**
   * The label for notes that are only shown to the game master.
   */
	private JLabel gmNotesLabel;
	
	/**
	 * The component that allows the shape of the token to be selected.
	 */
	private JComboBox shape;

	/**
	 * The component that allows the size of the token to be selected.
	 */
	private JComboBox size;

	/**
	 * The component that allows the token to be snapped to grid or not.
	 */
	private JCheckBox snapToGrid;

	/**
	 * The component that allows the token to be shown or hidden.
	 */
	private JCheckBox visible;

	/**
	 * The panel that holds all of the token states combo boxes.
	 */
	private JPanel statesPanel;

	/**
	 * The token currently being displayed to the user.
	 */
	private Token token;

	/**
	 * Has the token been saved.
	 */
	private boolean tokenSaved;

	/*---------------------------------------------------------------------------------------------
	 * Class Variables
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * The size used to constrain the icon.
	 */
	public static final int SIZE = 64;

	/*---------------------------------------------------------------------------------------------
	 * Constructor
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * Create a new token notes dialog.
	 * 
	 * @param token
	 *            The token being displayed.
	 */
	public TokenPropertiesDialog(Token token) {
		super(MapTool.getFrame(), "Token Notes", true);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		FormPanel panel = new FormPanel(
				"net/rptools/maptool/client/ui/forms/tokenNotesDialog.jfrm");

		// Get the components
		tokenName = panel.getTextField("tokenName");
		tokenGMName = panel.getTextField("tokenGMName");
		tokenIcon = (ImageComponent) panel.getComponentByName("tokenIcon");
		notes = (JEditorPane) panel.getTextComponent("notes");
		gmNotes = (JEditorPane) panel.getTextComponent("gmNotes");
		snapToGrid = panel.getCheckBox("snapToGrid");
		gmNotesLabel = panel.getLabel("gmNotesLabel");
		tokenGMNameLabel = panel.getLabel("tokenGMNameLabel");
		
		// Does visible get displayed?
		visible = panel.getCheckBox("visible");
		if (!MapTool.getPlayer().isGM()) {
			visible.setVisible(false);
			panel.getLabel("visibleLabel").setVisible(false);
		} // endif

		// Set up the buttons
		okButton = panel.getButton("okButton");
		okButton.addActionListener(this);
		cancelButton = panel.getButton("cancelButton");
		cancelButton.addActionListener(this);

		// Set up the shape combo box
		shape = panel.getComboBox("shape");
		shape.setModel(new DefaultComboBoxModel(Token.Type.values()));

		// Set up the size combo box
		DefaultComboBoxModel model = new DefaultComboBoxModel(TokenSize.Size
				.values());
		model.insertElementAt("Free Size", 0);
		size = panel.getComboBox("size");
		size.setModel(model);

		// Set up all of the state combo boxes.
		statesPanel = panel.getPanel("statesPanel");
		statesPanel.removeAll();
		TitledBorder border = (TitledBorder) statesPanel.getBorder();
		border.setTitleFont(border.getTitleFont().deriveFont(Font.PLAIN));
		Set<String> states = TokenStates.getStates();
		statesPanel.setLayout(new GridLayout(0, 4));
		for (String state : states)
			statesPanel.add(new JCheckBox(state));

		// Set up the panel and view
		add(panel);
		setToken(token);
		pack();
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
			setVisible(false);
		} else if (aE.getSource() == cancelButton) {
			setVisible(false);
		} // endif
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
			throw new IllegalStateException("A name is set.");
		} // endif

		// Set the token values
		token.setName(name);
		token.setGMName(tokenGMName.getText());
		token.setNotes(notes.getText());
		token.setGMNote(gmNotes.getText());
		token.setTokenType((Token.Type) shape.getSelectedItem());
		token.setSnapToGrid(snapToGrid.isSelected());
		token.setVisible(visible.isSelected());

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
		} // endfor
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
		if (token != null)
			token.removeModelChangeListener(this);
		token = aToken;
		if (token != null)
			token.addModelChangeListener(this);
		setFields();
	}

	/**
	 * Set the fields to match the state of the current token
	 */
	public void setFields() {

		Player player = MapTool.getPlayer();

		// No token? clear the dialog
		boolean editable = player.isGM()
				|| token.isOwner(player.getName());
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
			return;
		} else {

			// Set the fields from the token.
			tokenName.setText(token.getName());
			tokenGMName.setText(token.getGMName());
			tokenIcon.setIcon(getTokenIcon());
			notes.setText(token.getNotes());
			gmNotes.setText(token.getGMNotes());
			shape.setSelectedItem(token.getTokenType());
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
			
		} // endif

		gmNotes.setVisible(player.isGM());
		gmNotesLabel.setVisible(player.isGM());
		tokenGMName.setVisible(player.isGM());
		tokenGMNameLabel.setVisible(player.isGM());
		
		// Handle the states
		Component[] states = statesPanel.getComponents();
		for (int i = 0; i < states.length; i++) {
			JCheckBox state = (JCheckBox) states[i];
			state.setEnabled(token != null && editable);
			Boolean stateValue = token != null ? (Boolean) token.getState(state
					.getText()) : null;
			state.setSelected(stateValue == null ? false : stateValue
					.booleanValue());
		} // endfor
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
			MapTool.serverCommand().getAsset(token.getAssetID());
			assetImage = ImageManager.UNKNOWN_IMAGE;
		} else {
			assetImage = ImageManager.getImage(AssetManager.getAsset(token
					.getAssetID()), this);
		} // endif

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
			} // endif
			BufferedImage image = new BufferedImage(dim.width, dim.height,
					Transparency.BITMASK);
			Graphics2D g = image.createGraphics();
			g.drawImage(assetImage, 0, 0, dim.width, dim.height, null);
			assetImage = image;
		} // endif
		return new ImageIcon(assetImage);
	}

	/** @return Getter for tokenSaved */
	public boolean isTokenSaved() {
		return tokenSaved;
	}
}
