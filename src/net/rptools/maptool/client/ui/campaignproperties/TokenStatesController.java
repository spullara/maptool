/* The MIT License
 * 
 * Copyright (c) 2008 Jay Gorrell
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
package net.rptools.maptool.client.ui.campaignproperties;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.MapPropertiesDialog;
import net.rptools.maptool.client.ui.PreviewPanelFileChooser;
import net.rptools.maptool.client.ui.token.ColorDotTokenOverlay;
import net.rptools.maptool.client.ui.token.CornerImageTokenOverlay;
import net.rptools.maptool.client.ui.token.CrossTokenOverlay;
import net.rptools.maptool.client.ui.token.DiamondTokenOverlay;
import net.rptools.maptool.client.ui.token.FlowColorDotTokenOverlay;
import net.rptools.maptool.client.ui.token.FlowColorSquareTokenOverlay;
import net.rptools.maptool.client.ui.token.FlowImageTokenOverlay;
import net.rptools.maptool.client.ui.token.ImageTokenOverlay;
import net.rptools.maptool.client.ui.token.OTokenOverlay;
import net.rptools.maptool.client.ui.token.ShadedTokenOverlay;
import net.rptools.maptool.client.ui.token.TokenOverlay;
import net.rptools.maptool.client.ui.token.TriangleTokenOverlay;
import net.rptools.maptool.client.ui.token.XTokenOverlay;
import net.rptools.maptool.client.ui.token.YieldTokenOverlay;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.CampaignProperties;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.drawing.AbstractTemplate.Quadrant;
import net.rptools.maptool.util.ImageManager;

import com.jeta.forms.components.colors.JETAColorWell;
import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.store.properties.ListItemProperty;

/**
 * This controller will handle all of the components on the States panel of the {@link CampaignPropertiesDialog}. 
 * 
 * @author Jay
 */
public class TokenStatesController implements ActionListener, DocumentListener, ListSelectionListener {

    private FormPanel formPanel;
    private Set<String> names = new HashSet<String>();
    private PreviewPanelFileChooser imageFileChooser;
    
    public static String NAME = "tokenStatesName";
    public static String TYPE = "tokenStatesType";
    public static String COLOR = "tokenStatesColor";
    public static String WIDTH = "tokenStatesWidth";
    public static String CORNER = "tokenStatesCorner";
    public static String ADD = "tokenStatesAddState";
    public static String DELETE = "tokenStatesDeleteState";
    public static String STATES = "tokenStatesStates";
    public static String FLOW_GRID = "tokenStatesFlowGrid";
    public static String IMAGE = "tokenStatesImageFile";
    public static String BROWSE = "tokenStatesBrowseImage";
    public static int ICON_SIZE = 50;
    
    public TokenStatesController(FormPanel panel) {
        formPanel = panel;
        panel.getButton(ADD).addActionListener(this);
        panel.getButton(ADD).setEnabled(false);
        panel.getButton(DELETE).addActionListener(this);
        panel.getButton(DELETE).setEnabled(false);
        panel.getButton(BROWSE).addActionListener(this);
        panel.getSpinner(WIDTH).setModel(new SpinnerNumberModel(5, 1, 10, 1));
        panel.getSpinner(FLOW_GRID).setModel(new SpinnerNumberModel(4, 2, 10, 1));
        panel.getList(STATES).setCellRenderer(new StateListRenderer());
        panel.getList(STATES).addListSelectionListener(this);
        panel.getTextComponent(NAME).getDocument().addDocumentListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        String name = ((JComponent)e.getSource()).getName();
        JList list = formPanel.getList(STATES);
        DefaultListModel model = (DefaultListModel)list.getModel();
        if (ADD.equals(name)) {
            TokenOverlay overlay = createToken();
            if (overlay != null) {
                model.addElement(overlay);
                names.add(overlay.getName());
                formPanel.setText(NAME, "");
            }
        } else if (DELETE.equals(name)) {
            int[] selected = list.getSelectedIndices();
            for (int j = selected.length - 1; j >= 0; j--) {
                TokenOverlay overlay = (TokenOverlay)model.remove(selected[j]);
                names.remove(overlay.getName());
            } // endfor
            changedUpdate(null);
        } else if (BROWSE.equals(name)) {
            if (getImageFileChooser().showOpenDialog(formPanel) == JFileChooser.APPROVE_OPTION) {
                File imageFile = getImageFileChooser().getSelectedFile();
                if (imageFile == null || imageFile.isDirectory() || !imageFile.exists() || !imageFile.canRead()) return;
                formPanel.setText(IMAGE, imageFile.getPath());
            } // endif
        }
    }

    private PreviewPanelFileChooser getImageFileChooser() {
        if (imageFileChooser == null) {
            imageFileChooser = new PreviewPanelFileChooser();
            imageFileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory()
                            || AppConstants.IMAGE_FILE_FILTER.accept(f
                                    .getAbsoluteFile(), f.getName());
                }

                @Override
                public String getDescription() {
                    return "Images only";
                }
            });
        }
        return imageFileChooser;
    }

    public void changedUpdate(DocumentEvent e) {
        String text = formPanel.getText(NAME);
        formPanel.getButton(ADD).setEnabled((text != null && (text = text.trim()).length() != 0) && !names.contains(text));
    }

    public void insertUpdate(DocumentEvent e) {
        changedUpdate(e);
    }

    public void removeUpdate(DocumentEvent e) {
        changedUpdate(e);
    }

    public void valueChanged(ListSelectionEvent e) {
        formPanel.getButton(DELETE).setEnabled(formPanel.getList(STATES).getSelectedValue() != null);
    }
    
    private class StateListRenderer extends DefaultListCellRenderer {
        
        Rectangle bounds = new Rectangle(0, 0, ICON_SIZE, ICON_SIZE);
        Token token = new Token("name", null);
        TokenOverlay overlay;
        Icon icon = new Icon() {
            public int getIconHeight() { return ICON_SIZE + 2; }
            public int getIconWidth() { return ICON_SIZE + 2; }
            public void paintIcon(Component c, java.awt.Graphics g, int x, int y) {
                g.drawRect(x, y, ICON_SIZE + 2, ICON_SIZE + 2);
                g.setColor(Color.BLACK);
                g.translate(x + 1, y + 1);
                Shape old = g.getClip();
                g.setClip(bounds.intersection(old.getBounds()));
                overlay.paintOverlay((Graphics2D)g, token, bounds);
                g.setClip(old);
                g.translate(-(x + 1), -(y + 1));
            }
        };
        
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            overlay = (TokenOverlay)value;
            setText(overlay.getName());
            setIcon(icon);
            return this;
        }
    }

    public void copyCampaignToUI(CampaignProperties campaign) {
        names.clear();
        ArrayList<String> states = new ArrayList<String>(campaign.getTokenStatesMap().keySet());
        Collections.sort(states);
        DefaultListModel model = new DefaultListModel();
        for (String state : states) {
            TokenOverlay overlay = campaign.getTokenStatesMap().get(state);
            model.addElement(overlay);
            names.add(overlay.getName());
        }
        formPanel.getList(STATES).setModel(model);
    }
    
    public void copyUIToCampaign(Campaign campaign) {
        ListModel model = formPanel.getList(STATES).getModel();
        Map<String, TokenOverlay> states = new HashMap<String, TokenOverlay>();
        states.clear();
        for (int i = 0; i < model.getSize(); i++) {
            TokenOverlay overlay = (TokenOverlay)model.getElementAt(i);
            states.put(overlay.getName(), overlay);
        }
        campaign.getTokenStatesMap().clear();
        campaign.getTokenStatesMap().putAll(states);
    }

    public TokenOverlay createToken() {
        
        // Need the color and name for everything
        Color color = ((JETAColorWell)formPanel.getComponentByName(COLOR)).getColor();
        String name = formPanel.getText(NAME);
        String overlay = ((ListItemProperty)formPanel.getSelectedItem(TYPE)).getLabel();
        
        // Check for overlays that don't use width
        if (overlay.equals("Dot")) {
            String cornerName = formPanel.getSelectedItem(CORNER).toString().toUpperCase().replace(' ', '_');
            return new ColorDotTokenOverlay(name, color, Quadrant.valueOf(cornerName));
        } else if (overlay.equals("Shaded")) {
            return new ShadedTokenOverlay(name, color);
        } // endif
        
        // Get flow information
        int grid = getSpinner(FLOW_GRID, "grid size");
        if (overlay.equals("Flow Dot")) {
            return new FlowColorDotTokenOverlay(name, color, grid);
        } if (overlay.equals("Flow Square")) {
            return new FlowColorSquareTokenOverlay(name, color, grid);
        } // endif
        
        // Get the width for the items that need them
        int width = getSpinner(WIDTH, "width");
        
        // Handle all of the overlays with width
        if (overlay.equals("Circle")) {
            return new OTokenOverlay(name, color, width);
        } else if (overlay.equals("X")) {
            return new XTokenOverlay(name, color, width);
        } else if (overlay.equals("Cross")) {
            return new CrossTokenOverlay(name, color, width);
        } else if (overlay.equals("Diamond")) {
            return new DiamondTokenOverlay(name, color, width);
        } else if (overlay.equals("Yield")) {
            return new YieldTokenOverlay(name, color, width);
        } else if (overlay.equals("Triangle")) {
            return new TriangleTokenOverlay(name, color, width);
        } // endif
        
        // If we get here it is an image overlay, grab the image as an asset
        File file = new File(formPanel.getText(IMAGE));
        if (!file.exists() || !file.canRead() || file.isDirectory()) {
            JOptionPane.showMessageDialog(formPanel, "The image file was not specified, it doesn't exist, is a directory, or it can't be read: " 
                    + file.getAbsolutePath(), "Error!", JOptionPane.ERROR_MESSAGE);
            return null;
        } // endif
        Asset asset = null;
        try {
            asset = AssetManager.createAsset(file);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(formPanel, "Error reading the image file: " 
                    + file.getAbsolutePath(), "Error!", JOptionPane.ERROR_MESSAGE);
            return null;
        } // endif
        AssetManager.putAsset(asset);
        
        // Create all of the image overlays 
        if (overlay.equals("Image")) {
            return new ImageTokenOverlay(name, asset.getId());
        } else if (overlay.equals("Corner Image")) {
            String cornerName = formPanel.getSelectedItem(CORNER).toString().toUpperCase().replace(' ', '_');
            return new CornerImageTokenOverlay(name, asset.getId(), Quadrant.valueOf(cornerName));
        } else if (overlay.equals("Flow Image")) {
            return new FlowImageTokenOverlay(name, asset.getId(), grid);
        } // endif
        return null;
    }
    
    private int getSpinner(String name, String displayName) {
        int width = 0;
        JSpinner spinner = formPanel.getSpinner(name);
        try {
            spinner.commitEdit();
            width = ((Integer)spinner.getValue()).intValue();
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(spinner, "There is an invalid " + displayName + " specified: " + ((JTextField)spinner.getEditor()).getText(), 
                    "Error!", JOptionPane.ERROR_MESSAGE);
            throw new IllegalStateException(e);
        } // endtry
        return width;
    }
}
