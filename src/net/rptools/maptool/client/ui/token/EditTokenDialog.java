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

package net.rptools.maptool.client.ui.token;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolUtil;
import net.rptools.maptool.client.functions.AbstractTokenAccessorFunction;
import net.rptools.maptool.client.functions.TokenBarFunction;
import net.rptools.maptool.client.swing.AbeillePanel;
import net.rptools.maptool.client.swing.GenericDialog;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Association;
import net.rptools.maptool.model.Grid;
import net.rptools.maptool.model.ObservableList;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.TokenFootprint;
import net.rptools.maptool.util.ImageManager;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jidesoft.grid.AbstractPropertyTableModel;
import com.jidesoft.grid.Property;
import com.jidesoft.grid.PropertyPane;
import com.jidesoft.grid.PropertyTable;
import com.jidesoft.swing.CheckBoxListWithSelectable;
import com.jidesoft.swing.DefaultSelectable;
import com.jidesoft.swing.Selectable;

/**
 * This dialog is used to display all of the token states and notes to the user.
 */
public class EditTokenDialog extends AbeillePanel {

	private Token token;
	private boolean tokenSaved;
	
	private GenericDialog dialog;
	
	private ImageAssetPanel imagePanel;

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
	public EditTokenDialog() {
		super("net/rptools/maptool/client/ui/forms/tokenPropertiesDialog.jfrm");

		panelInit();
	}
	
	public void initPlayerNotesTextArea() {
		getNotesTextArea().addMouseListener(new MouseHandler(getNotesTextArea()));
	}
	
	public void initGMNotesTextArea() {
		getGMNotesTextArea().addMouseListener(new MouseHandler(getGMNotesTextArea()));		

		getComponent("@GMNotes").setEnabled(MapTool.getPlayer().isGM());
	}
	
	public void showDialog(Token token) {
		this.token = token;
		
		dialog = new GenericDialog("Edit Token", MapTool.getFrame(), this) {
			@Override
			public void closeDialog() {
				// TODO: I don't like this.  There should really be a AbeilleDialog class that does this
				unbind();
				super.closeDialog();
			}
		};
		
		bind(token);

		getRootPane().setDefaultButton(getOKButton());
		dialog.showDialog();
	}

	@Override
	public void bind(Object model) {
		
		// ICON
		getTokenIconPanel().setImageId(token.getImageAssetId());

		// PROPERTIES
		updatePropertyTypeCombo();
		updatePropertiesTable(token.getPropertyType());
		
		// SIGHT
		updateSightTypeCombo();
		
		// STATES
		Component[] statePanels = getStatesPanel().getComponents();
		Component barPanel = null;
		for (int j = 0; j < statePanels.length; j++) {
		    if ("bar".equals(statePanels[j].getName())) {
		        barPanel = statePanels[j];
		        continue;
		    }
		    Component[] states = ((Container)statePanels[j]).getComponents(); 
		    for (int i = 0; i < states.length; i++) {
		        JCheckBox state = (JCheckBox) states[i];
		        state.setSelected(AbstractTokenAccessorFunction.getBooleanValue(token.getState(state.getText())));
		    } 
		} // endfor
		
		// BARS
		if (barPanel != null) {
		    Component[] bars = ((Container)barPanel).getComponents(); 
		    for (int i = 0; i < bars.length; i += 2) {
                JCheckBox cb = (JCheckBox)((Container)bars[i]).getComponent(1);
		        JSlider bar = (JSlider) bars[i + 1];
		        if (token.getState(bar.getName()) == null) {
		            cb.setSelected(true);
		            bar.setEnabled(false);
		            bar.setValue(100);
		        } else {
                    cb.setSelected(false);
                    bar.setEnabled(true);
		            bar.setValue((int)(TokenBarFunction.getBigDecimalValue(token.getState(bar.getName())).doubleValue() * 100));
		        } // endif
		    }  // endfor
		} // endif
		
		// OWNER LIST
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				getOwnerList().setModel(new OwnerListModel());
			}
		});

		// MACRO TABLE
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				getMacroTable().setModel(new MacroTableModel(token));
			}
		});

		// SPEECH TABLE
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				getSpeechTable().setModel(new SpeechTableModel(token));
			}
		});

//		Player player = MapTool.getPlayer();
//				boolean editable = player.isGM()
//						|| !MapTool.getServerPolicy().useStrictTokenManagement() || token.isOwner(player.getName());
//		getAllPlayersCheckBox().setSelected(token.isOwnedByAll());

		// OTHER
		getShapeCombo().setSelectedItem(token.getShape());
		if (token.isSnapToScale()) {
			getSizeCombo().setSelectedItem(token.getFootprint(MapTool.getFrame().getCurrentZoneRenderer().getZone().getGrid()));
		} else {
			getSizeCombo().setSelectedIndex(0);
		}
		getPropertyTypeCombo().setSelectedItem(token.getPropertyType());
		getSightTypeCombo().setSelectedItem(token.getSightType() != null ? token.getSightType() : MapTool.getCampaign().getCampaignProperties().getDefaultSightType());
		getCharSheetPanel().setImageId(token.getCharsheetImage());
		getPortraitPanel().setImageId(token.getPortraitImage());
		getTokenLayoutPanel().setToken(token);
		
		super.bind(model);
	}
	
	public JTabbedPane getTabbedPane() {
		return (JTabbedPane)getComponent("tabs");
	}

	public JTextArea getNotesTextArea() {
		return (JTextArea) getComponent("@notes");
	}
	
	public JTextArea getGMNotesTextArea() {
		return (JTextArea) getComponent("@GMNotes");
	}
	
//	private JLabel getGMNameLabel() {
//		return (JLabel) getComponent("tokenGMNameLabel");
//	}
//	
//	public JTextField getNameTextField() {
//		return (JTextField) getComponent("tokenName");
//	}
//	
//	public JTextField getGMNameTextField() {
//		return (JTextField) getComponent("tokenGMName");
//	}
	
	public void initTypeCombo() {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement(Token.Type.NPC);
		model.addElement(Token.Type.PC);
//		getTypeCombo().setModel(model);
	}
	
	public JComboBox getTypeCombo() {
		return (JComboBox) getComponent("@type");
	}
	
	public void initTokenIconPanel() {
		getTokenIconPanel().setPreferredSize(new Dimension(100, 100));
		getTokenIconPanel().setMinimumSize(new Dimension(100, 100));
		
	}
	
	public ImageAssetPanel getTokenIconPanel() {
		if (imagePanel == null) {
			imagePanel = new ImageAssetPanel();
			imagePanel.setAllowEmptyImage(false);
			
			replaceComponent("mainPanel", "tokenImage", imagePanel);
		}
		return imagePanel;
	}
	
	public void initShapeCombo() {
		getShapeCombo().setModel(new DefaultComboBoxModel(Token.TokenShape.values()));
	}
	
	public JComboBox getShapeCombo() {
		return (JComboBox) getComponent("shape");
	}

	public void initSizeCombo() {
		DefaultComboBoxModel model = new DefaultComboBoxModel(MapTool.getFrame().getCurrentZoneRenderer().getZone().getGrid().getFootprints().toArray());
		model.insertElementAt("Free Size", 0);
		getSizeCombo().setModel(model);
	}

	public void initPropertyTypeCombo() {
		updatePropertyTypeCombo();
	}

	private void updatePropertyTypeCombo() {
		List<String> typeList = new ArrayList<String>(MapTool.getCampaign().getTokenTypes());
		Collections.sort(typeList);
		DefaultComboBoxModel model = new DefaultComboBoxModel(typeList.toArray());
		getPropertyTypeCombo().setModel(model);
		getPropertyTypeCombo().addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				updatePropertiesTable((String)getPropertyTypeCombo().getSelectedItem());
			}
		});
	}
	
	private void updateSightTypeCombo() {
		List<String> typeList = new ArrayList<String>(MapTool.getCampaign().getSightTypes());
		Collections.sort(typeList);

		DefaultComboBoxModel model = new DefaultComboBoxModel(typeList.toArray());
		getSightTypeCombo().setModel(model);
	}
	
	private void updatePropertiesTable(final String propertyType) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				getPropertyTable().setModel(new TokenPropertyTableModel());
				getPropertyTable().expandAll();
			}
		});
	}
	
	public JComboBox getSizeCombo() {
		return (JComboBox) getComponent("size");
	}

	public JComboBox getPropertyTypeCombo() {
		return (JComboBox) getComponent("propertyTypeCombo");
	}

	public JComboBox getSightTypeCombo() {
		return (JComboBox) getComponent("sightTypeCombo");
	}

	public void initOKButton() {
		getOKButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (commit()) {
					unbind();
					dialog.closeDialog();
				}
			}
		});
	}
	
	@Override
	public boolean commit() {

		// Commit any in-process edits
		if (getMacroTable().isEditing()) {
			getMacroTable().getCellEditor().stopCellEditing();
		}
		
		if (getSpeechTable().isEditing()) {
			getSpeechTable().getCellEditor().stopCellEditing();
		}
		
		if (getPropertyTable().isEditing()) {
			getPropertyTable().getCellEditor().stopCellEditing();
		}

		// Commit the changes to the token properties
		if (!super.commit()) {
			return false;
		}
		
		// SIZE
		token.setSnapToScale(getSizeCombo().getSelectedIndex() != 0);
		if (getSizeCombo().getSelectedIndex() > 0) {
			Grid grid = MapTool.getFrame().getCurrentZoneRenderer().getZone().getGrid();
			token.setFootprint(grid, (TokenFootprint) getSizeCombo().getSelectedItem());
		}
		
		// Other
		token.setPropertyType((String)getPropertyTypeCombo().getSelectedItem());
		token.setSightType((String)getSightTypeCombo().getSelectedItem());

		// Get the states
        Component[] stateComponents = getStatesPanel().getComponents();
        Component barPanel = null;
        for (int j = 0; j < stateComponents.length; j++) {
            if ("bar".equals(stateComponents[j].getName())) {
                barPanel = stateComponents[j];
                continue;
            }
            Component[] components = ((Container)stateComponents[j]).getComponents();
            for (int i = 0; i < components.length; i++) {
                JCheckBox cb = (JCheckBox) components[i];
                String state = cb.getText();
                token.setState(state, cb.isSelected() ? Boolean.TRUE : Boolean.FALSE);
            }
        } // endfor

        // BARS
        if (barPanel != null) {
            Component[] bars = ((Container)barPanel).getComponents(); 
            for (int i = 0; i < bars.length; i += 2) {
                JCheckBox cb = (JCheckBox)((Container)bars[i]).getComponent(1);
                JSlider bar = (JSlider) bars[i + 1];
                BigDecimal value = cb.isSelected() ? null : new BigDecimal(bar.getValue() / 100.0);
                token.setState(bar.getName(), value);
                bar.setValue((int)(TokenBarFunction.getBigDecimalValue(token.getState(bar.getName())).doubleValue() * 100));
            } 
        }

        // Ownership
		token.clearAllOwners();
		
		for (int i = 0; i < getOwnerList().getModel().getSize(); i++) {
			DefaultSelectable selectable = (DefaultSelectable) getOwnerList().getModel().getElementAt(i);
			if (selectable.isSelected()) {
				token.addOwner((String) selectable.getObject());
			}
		}

		// SHAPE
		token.setShape((Token.TokenShape)getShapeCombo().getSelectedItem());
		
		// Macros
		token.setMacroMap(((KeyValueTableModel)getMacroTable().getModel()).getMap());
		token.setSpeechMap(((KeyValueTableModel)getSpeechTable().getModel()).getMap());
		
		// Properties
		((TokenPropertyTableModel)getPropertyTable().getModel()).applyTo(token);
	
		// Charsheet
		token.setCharsheetImage(getCharSheetPanel().getImageId());
		if (token.getCharsheetImage() != null) {
			// Make sure the server has the image
			if (!MapTool.getCampaign().containsAsset(token.getCharsheetImage())) {
				MapTool.serverCommand().putAsset(AssetManager.getAsset(token.getCharsheetImage()));
			}
		}
		
		// IMAGE
		if (!token.getImageAssetId().equals(getTokenIconPanel().getImageId())) {
			token.setImageAsset(null, getTokenIconPanel().getImageId()); // Default image for now
			MapToolUtil.uploadAsset(AssetManager.getAsset(getTokenIconPanel().getImageId()));
		}
		
		// PORTRAIT
		token.setPortraitImage(getPortraitPanel().getImageId());
		if (token.getPortraitImage() != null) {
			// Make sure the server has the image
			if (!MapTool.getCampaign().containsAsset(token.getPortraitImage())) {
				MapTool.serverCommand().putAsset(AssetManager.getAsset(token.getPortraitImage()));
			}
		}

		// LAYOUT
		token.setSizeScale(getTokenLayoutPanel().getSizeScale());
		token.setAnchor(getTokenLayoutPanel().getAnchorX(), getTokenLayoutPanel().getAnchorY());

		// OTHER
		tokenSaved = true;
		
		// Update UI
		MapTool.getFrame().updateTokenTree();
		MapTool.getFrame().updateSelectionPanel();
		
		return true;
	}
	
	public JButton getOKButton() {
		return (JButton) getComponent("okButton");
	}

	public void initCancelButton() {
		getCancelButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				unbind();
				dialog.closeDialog();
			}
		});
	}
	
	public JButton getCancelButton() {
		return (JButton) getComponent("cancelButton");
	}

	public PropertyTable getPropertyTable() {
		return (PropertyTable) getComponent("propertiesTable");
	}

	public void initStatesPanel() {

	    // Group the states first into individual panels
        List<BooleanTokenOverlay> overlays = new ArrayList<BooleanTokenOverlay>(MapTool.getCampaign().getTokenStatesMap().values());
        Map<String, JPanel> groups = new TreeMap<String, JPanel>();
        groups.put("", new JPanel(new FormLayout("0px:grow 2px 0px:grow 2px 0px:grow 2px 0px:grow")));
        for (BooleanTokenOverlay overlay : overlays) {
            String group = overlay.getGroup();
            if (group != null && (group = group.trim()).length() != 0) {
                JPanel panel = groups.get(group);
                if (panel == null) {
                    panel = new JPanel(new FormLayout("0px:grow 2px 0px:grow 2px 0px:grow 2px 0px:grow"));
                    panel.setBorder(BorderFactory.createTitledBorder(group));
                    groups.put(group, panel);                    
                } // endif
            } // endif
        } // endfor
        
        // Add the group panels and bar panel to the states panel
	    JPanel panel = getStatesPanel();
		panel.removeAll();
		FormLayout layout = new FormLayout("0px:grow");
		panel.setLayout(layout);
		int row = 1;
		for (JPanel gPanel : groups.values()) {
            layout.appendRow(new RowSpec("pref"));
            layout.appendRow(new RowSpec("2px"));
            panel.add(gPanel, new CellConstraints(1, row));
            row += 2;
        } // endfor
        layout.appendRow(new RowSpec("pref"));
        layout.appendRow(new RowSpec("2px"));
        JPanel barPanel = new JPanel(new FormLayout("right:pref 2px pref 5px right:pref 2px pref"));
        panel.add(barPanel, new CellConstraints(1, row));
		
		// Add the individual check boxes.
        for (BooleanTokenOverlay state : overlays) {
            String group = state.getGroup();
            panel = groups.get("");
            if (group != null && (group = group.trim()).length() != 0)
                panel = groups.get(group);
            int x = panel.getComponentCount() % 4;
            int y = panel.getComponentCount() / 4;
            if (x == 0) {
                layout = (FormLayout)panel.getLayout();
                if (y != 0) layout.appendRow(new RowSpec("2px"));
                layout.appendRow(new RowSpec("pref"));
            } // endif
			panel.add(new JCheckBox(state.getName()), new CellConstraints(x * 2 + 1, y * 2 + 1));
		} // endif
        
        // Add sliders to the bar panel
        if (MapTool.getCampaign().getTokenBarsMap().size() > 0) {
            layout = (FormLayout)barPanel.getLayout();
            barPanel.setName("bar");
            barPanel.setBorder(BorderFactory.createTitledBorder("Bars"));
            int count = 0;
            row = 0;
            for (BarTokenOverlay bar : MapTool.getCampaign().getTokenBarsMap().values()) {
                int working = count % 2; 
                if (working == 0) { // slider row
                    layout.appendRow(new RowSpec("pref"));
                    row += 1;
                }
                JPanel labelPanel = new JPanel(new FormLayout("pref", "pref 2px:grow pref"));
                barPanel.add(labelPanel, new CellConstraints(1 + working * 4, row));
                labelPanel.add(new JLabel(bar.getName() + ":"), new CellConstraints(1, 1, CellConstraints.RIGHT, CellConstraints.TOP));
                JSlider slider = new JSlider(0, 100);
                JCheckBox hide = new JCheckBox("Hide");
                hide.putClientProperty("JSlider", slider);
                hide.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        JSlider js = (JSlider)((JCheckBox)e.getSource()).getClientProperty("JSlider");
                        js.setEnabled(!((JCheckBox)e.getSource()).isSelected());
                    }
                });
                labelPanel.add(hide, new CellConstraints(1, 3, CellConstraints.RIGHT, CellConstraints.TOP));
                slider.setName(bar.getName());
                slider.setPaintLabels(true);
                slider.setPaintTicks(true);
                slider.setMajorTickSpacing(20);
                slider.createStandardLabels(20);
                slider.setMajorTickSpacing(10);
                barPanel.add(slider, new CellConstraints(3 + working * 4, row));
                if (working != 0) { // spacer row
                    layout.appendRow(new RowSpec("2px"));
                    row += 1;
                }
                count += 1;
            }
        } // endif
	}
	
	public JPanel getStatesPanel() {
		return (JPanel) getComponent("statesPanel");
	}
	
	public JTable getMacroTable() {
		return (JTable) getComponent("macroTable");
	}
	
	public JTable getSpeechTable() {
		return (JTable) getComponent("speechTable");
	}

	public JButton getSpeechClearAllButton() {
		return (JButton) getComponent("speechClearAllButton");
	}
	
	public JButton getMacroClearAllButton() {
		return (JButton) getComponent("macroClearAllButton");
	}
	
	private JLabel getVisibleLabel() {
		return (JLabel) getComponent("visibleLabel");
	}
	
	private JPanel getGMNotesPanel() {
		return (JPanel) getComponent("gmNotesPanel");
	}
	
	public CheckBoxListWithSelectable getOwnerList() {
		return (CheckBoxListWithSelectable) getComponent("ownerList");
	}
	
	public void initMacroPanel() {
		
		getMacroClearAllButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (!MapTool.confirm("Are you sure you want to clear all macros for this token?")) {
					return;
				}
				
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						getMacroTable().setModel(new MacroTableModel());
					}
				});
			}
		});
	}
	
	public void initSpeechPanel() {
		
		getSpeechClearAllButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (!MapTool.confirm("Are you sure you want to clear all speech for this token?")) {
					return;
				}
				
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						getSpeechTable().setModel(new SpeechTableModel());
					}
				});
			}
		});
	}
	
	public void initOwnershipPanel() {
		
		CheckBoxListWithSelectable list = new CheckBoxListWithSelectable();
		list.setName("ownerList");
		replaceComponent("ownershipPanel", "ownershipList", new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		
	}

//	public void initNotesPanel() {
//		getNotesTextArea().addMouseListener(new MouseHandler(getNotesTextArea()));
//		getGMNotesTextArea().addMouseListener(new MouseHandler(getGMNotesTextArea()));
//	}
	
	public void initTokenDetails() {
//		tokenGMNameLabel = panel.getLabel("tokenGMNameLabel");
	}

	public void initTokenLayoutPanel() {
		TokenLayoutPanel layoutPanel = new TokenLayoutPanel();
		layoutPanel.setPreferredSize(new Dimension(150, 125));
		layoutPanel.setName("tokenLayout");
		
		replaceComponent("tokenLayoutPanel", "tokenLayout", layoutPanel);
	}
	
	public void initCharsheetPanel() {
		ImageAssetPanel panel = new ImageAssetPanel();
		panel.setPreferredSize(new Dimension(150, 125));
		panel.setName("charsheet");
		panel.setLayout(new GridLayout());
		
		replaceComponent("charsheetPanel", "charsheet", panel);
	}
	
	public void initPortraitPanel() {
		ImageAssetPanel panel = new ImageAssetPanel();
		panel.setPreferredSize(new Dimension(150, 125));
		panel.setName("portrait");
		panel.setLayout(new GridLayout());
		
		replaceComponent("portraitPanel", "portrait", panel);
	}
	
	public ImageAssetPanel getPortraitPanel() {
		return (ImageAssetPanel) getComponent("portrait");
	}
	
	public ImageAssetPanel getCharSheetPanel() {
		return (ImageAssetPanel) getComponent("charsheet");
	}
	
	public TokenLayoutPanel getTokenLayoutPanel() {
		return (TokenLayoutPanel) getComponent("tokenLayout");
	}
	
	public void initPropertiesPanel() {

		PropertyTable propertyTable = new PropertyTable();
		propertyTable.setName("propertiesTable");
		
		PropertyPane pane = new PropertyPane(propertyTable);
		pane.setPreferredSize(new Dimension(100, 300));
		
		replaceComponent("propertiesPanel", "propertiesTable", pane);
	}


//	/**
//	 * Set the currently displayed token.
//	 * 
//	 * @param aToken
//	 *            The token to be displayed
//	 */
//	public void setToken(Token aToken) {
//
//		if (aToken == token)
//			return;
//		if (token != null) {
//			token.removeModelChangeListener(this);
//		}
//		
//		token = aToken;
//		
//		if (token != null) {
//			token.addModelChangeListener(this);
//			
//			List<String> typeList = new ArrayList<String>();
//			typeList.addAll(MapTool.getCampaign().getTokenTypes());
//			Collections.sort(typeList);
//			getPropertyTypeCombo().setModel(new DefaultComboBoxModel(typeList.toArray()));
//
//			setFields();
//			updateView();
//		}
//
//		getTabbedPane().setSelectedIndex(0);
//	}
	
//	private void updateView() {
//		
//		Player player = MapTool.getPlayer();
//		
//		boolean isEnabled = player.isGM() || token.isOwner(player.getName());
//		
//		getTabbedPane().setEnabledAt(INDX_PROPERTIES, isEnabled);
//		getTabbedPane().setEnabledAt(INDX_STATE, isEnabled);
//		getTabbedPane().setEnabledAt(INDX_MACROS, isEnabled);
//		getTabbedPane().setEnabledAt(INDX_SPEECH, isEnabled);
//		getTabbedPane().setEnabledAt(INDX_OWNERSHIP, isEnabled);
//		getTabbedPane().setEnabledAt(INDX_CONFIG, isEnabled);
//		
//		// Set the editable & enabled state
//		boolean editable = player.isGM() || !MapTool.getServerPolicy().useStrictTokenManagement() || token.isOwner(player.getName());
//		getOKButton().setEnabled(editable);
//		
//		getNotesTextArea().setEditable(editable);
//		getNameTextField().setEditable(editable);
//		getShapeCombo().setEnabled(editable);
//		getSizeCombo().setEnabled(editable);
//		getSnapToGridCheckBox().setEnabled(editable);
//		getVisibleCheckBox().setEnabled(editable);
//		getTypeCombo().setSelectedItem(token.getType());
//
//		getGMNotesPanel().setVisible(player.isGM());
//		getGMNameTextField().setVisible(player.isGM());
//		getGMNameLabel().setVisible(player.isGM());
//		getTypeCombo().setEnabled(player.isGM());
//		getVisibleCheckBox().setVisible(player.isGM());
//		getVisibleLabel().setVisible(player.isGM());
//		
//	}

	/**
	 * Get and icon from the asset manager and scale it properly.
	 * 
	 * @return An icon scaled to fit within a cell.
	 */
	private Icon getTokenIcon() {

		// Get the base image && find the new size for the icon
		BufferedImage assetImage = null;
		Asset asset = AssetManager.getAsset(token.getImageAssetId());
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
						JTextPane commandArea = MapTool.getFrame().getCommandPanel().getCommandTextArea();

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
						JTextPane commandArea = MapTool.getFrame().getCommandPanel().getCommandTextArea();

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

		private Map<String, String> propertyMap;
		private List<net.rptools.maptool.model.TokenProperty> propertyList; 

		private Map<String, String> getPropertyMap() {
			if (propertyMap == null) {
				propertyMap = new HashMap<String, String>();
				
				List<net.rptools.maptool.model.TokenProperty> propertyList = getPropertyList();
				for (net.rptools.maptool.model.TokenProperty property : propertyList) {
					propertyMap.put(property.getName(), (String) token.getProperty(property.getName()));
				}
			}
			return propertyMap;
		}
		
		private List<net.rptools.maptool.model.TokenProperty> getPropertyList() {
			if (propertyList == null) {
				propertyList = MapTool.getCampaign().getTokenPropertyList((String)getPropertyTypeCombo().getSelectedItem());
			}
			return propertyList;
		}
		
		public void applyTo(Token token) {
			
			for (net.rptools.maptool.model.TokenProperty property : getPropertyList()) {
				token.setProperty(property.getName(), getPropertyMap().get(property.getName()));
			}
		}
		
		@Override
		public Property getProperty(int index) {
			
			return new TokenProperty(getPropertyList().get(index).getName());
		}

		@Override
		public int getPropertyCount() {
			return getPropertyList() != null ? getPropertyList().size() : 0;
		}
		
		private class TokenProperty extends Property {
			private String key;
			
			public TokenProperty(String key) {
				super(key, key, String.class, "Core");
				this.key = key;
			}
			
			@Override
			public Object getValue() {
				return getPropertyMap().get(key);
			}

			@Override
			public void setValue(Object value) {
				getPropertyMap().put(key, (String)value);
			}

			@Override
			public boolean hasValue() {
				return getPropertyMap().get(key) != null;
			}
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
	
	private static class MacroTableModel extends KeyValueTableModel {
		
		public MacroTableModel(Token token) {
			List<Association<String, String>> rowList = new ArrayList<Association<String, String>>();
			for (String macroName : token.getMacroNames()) {
				rowList.add(new Association<String, String>(macroName, token.getMacro(macroName)));
			}
			
			Collections.sort(rowList, new Comparator<Association<String, String>>() {
				public int compare(Association<String, String> o1, Association<String, String> o2) {

					return o1.getLeft().compareToIgnoreCase(o2.getLeft());
				}
			});
			init(rowList);
		}
		public MacroTableModel() {
			init(new ArrayList<Association<String, String>>());
		}
		
		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0: return "ID";
			case 1: return "Action";
			}
			return "";
		}
	}

	private static class SpeechTableModel extends KeyValueTableModel {
		
		public SpeechTableModel(Token token) {
			List<Association<String, String>> rowList = new ArrayList<Association<String, String>>();
			for (String speechName : token.getSpeechNames()) {
				rowList.add(new Association<String, String>(speechName, token.getSpeech(speechName)));
			}
			
			Collections.sort(rowList, new Comparator<Association<String, String>>() {
				public int compare(Association<String, String> o1, Association<String, String> o2) {

					return o1.getLeft().compareToIgnoreCase(o2.getLeft());
				}
			});
			init(rowList);
		}
		public SpeechTableModel() {
			init(new ArrayList<Association<String, String>>());
		}
		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0: return "ID";
			case 1: return "Speech Text";
			}
			return "";
		}
	}

	private static class KeyValueTableModel extends AbstractTableModel {
		
		private Association<String, String> newRow = new Association<String, String>("", "");
		private List<Association<String, String>> rowList;

		protected void init(List<Association<String, String>> rowList) {
			this.rowList = rowList;
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
				if (row.getLeft() == null || row.getLeft().trim().length() == 0) {
					continue;
				}
				
				map.put(row.getLeft(), row.getRight());
			}
			
			return map;
		}
	}
	
}
