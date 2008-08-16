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
package net.rptools.maptool.client.ui.tokenpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.ImageLabel;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.token.TokenOverlay;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.InitiativeList.TokenInitiative;
import net.rptools.maptool.util.GraphicsUtil;
import net.rptools.maptool.util.ImageManager;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints.Alignment;

/**
 * This is the renderer that shows a token in the initiative panel.
 * 
 * @author Jay
 */
public class InitiativeListCellRenderer extends JPanel implements ListCellRenderer {

    /*---------------------------------------------------------------------------------------------
     * Instance Variables
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * The label used to display the current item indicator.
     */
    private JLabel currentIndicator;

    /**
     * The label used to display the item's name and icon.
     */
    private JLabel name;
    
    /**
     * This is the panel showing initiative. It contains the state for display.
     */
    private InitiativePanel panel;

    /**
     * Used to draw the background of the item.
     */
    private ImageLabel backgroundImageLabel;
    
    /**
     * The text height for the background image label. Only the text is painted inside, the token remains on the outside,
     */
    private int textHeight;
    
    /*---------------------------------------------------------------------------------------------
     * Class Variables
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * The size of an indicator.
     */
    public static final Dimension INDICATOR_SIZE = new Dimension(18, 16);
    
    /**
     * The icon for the current indicator.
     */
    public static final Icon CURRENT_INDICATOR_ICON = 
        new ImageIcon(InitiativePanel.class.getClassLoader().getResource("net/rptools/maptool/client/image/currentIndicator.png"));
    
    /**
     * Border used to show that an item is selected
     */
    public static final Border SELECTED_BORDER = BorderFactory.createLineBorder(Color.BLACK);
    
    /**
     * Border used to show that an item is not selected
     */
    public static final Border UNSELECTED_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    
    /**
     * Border used to show that an item is selected
     */
    public static final Border NAME_BORDER = BorderFactory.createEmptyBorder(2, 4, 2, 4);
    
    /** 
     * The size of the ICON shown in the list renderer 
     */
    public static int ICON_SIZE = 50;
    
    /*---------------------------------------------------------------------------------------------
     * Constructor
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * Create a renderer for the initiative panel.
     * 
     * @param aPanel The initiative panel containing view state.
     */
    public InitiativeListCellRenderer(InitiativePanel aPanel) {
        
        // Set up the panel
        panel = aPanel;
        setLayout(new FormLayout("1px pref 1px pref:grow", "fill:pref"));
        setBorder(SELECTED_BORDER);
        setBackground(Color.WHITE);
        
        // The current indicator 
        currentIndicator = new JLabel();
        currentIndicator.setPreferredSize(INDICATOR_SIZE);
        currentIndicator.setHorizontalAlignment(SwingConstants.CENTER);
        currentIndicator.setVerticalAlignment(SwingConstants.CENTER);
        add(currentIndicator, new CellConstraints(2, 1));
        
        // And the name
        name = new NameLabel();
        name.setText("Ty");
        name.setBorder(NAME_BORDER);
        name.setFont(getFont().deriveFont(Font.BOLD).deriveFont(14.0F));
        textHeight = name.getPreferredSize().height;
        add(name, new CellConstraints(4, 1, CellConstraints.LEFT, CellConstraints.CENTER));
        validate();
    }

    /*---------------------------------------------------------------------------------------------
     * ListCellRenderer Interface Methods
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
        // Set the background by type
        TokenInitiative ti = (TokenInitiative)value;
        Token token = ti.getToken();
        backgroundImageLabel = token.isVisible() ? token.getType() == Token.Type.NPC ? GraphicsUtil.BLUE_LABEL : GraphicsUtil.GREY_LABEL : GraphicsUtil.DARK_GREY_LABEL;
        Color foreground = token.isVisible() ? token.getType() == Token.Type.NPC ? Color.white : Color.black : Color.white;
        name.setForeground(foreground);
        
        // Show the indicator?
        if (index == panel.getModel().getDisplayIndex(panel.getList().getCurrent())) {
            currentIndicator.setIcon(CURRENT_INDICATOR_ICON);
        } else {
            currentIndicator.setIcon(null);
        } // endif
        
        // Get the name string, add the state if displayed, then get the icon if needed
        String sName = MapTool.getPlayer().isGM() && token.getGMName() != null ? token.getGMName() : token.getName();
        if (panel.isShowInitState() && ti.getState() != null)
            sName += " - " + ti.getState();
        Icon icon = null;
        if (panel.isShowTokens()) {
            icon = ti.getDisplayIcon();
            if (icon == null) {
                icon = new InitiativeListIcon(token);
                ti.setDisplayIcon(icon);
            } // endif
        } // endif
        name.setText(sName);
        name.setIcon(icon);
        
        // Align it properly
        Alignment alignment = ti.isHolding() ? CellConstraints.RIGHT : CellConstraints.LEFT;
        FormLayout layout = (FormLayout)getLayout();
        layout.setConstraints(name, new CellConstraints(4, 1, alignment, CellConstraints.CENTER));
        if (alignment == CellConstraints.RIGHT) {
            name.setHorizontalTextPosition(SwingConstants.LEFT);
        } else {
            name.setHorizontalTextPosition(SwingConstants.RIGHT);
        } // endif 
        
        
        // Selected?
        if (isSelected) {
            setBorder(SELECTED_BORDER);
        } else {
            setBorder(UNSELECTED_BORDER);
        } // endif
        return this;
    }
    
    /*---------------------------------------------------------------------------------------------
     * NameLabel Inner Class
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * This label contains the sized background image for the name component.
     *  
     * @author Jay
     */
    public class NameLabel extends JLabel {
        
        /**
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        @Override
        protected void paintComponent(Graphics g) {
            Dimension s = name.getSize();
            backgroundImageLabel.renderLabel((Graphics2D)g, 0, (s.height - textHeight) / 2 + NAME_BORDER.getBorderInsets(this).top, s.width, textHeight);
            super.paintComponent(g);
        }
    }
    
    /*---------------------------------------------------------------------------------------------
     * InitiativeListIcon Inner Class
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * An icon that will show a token image and all of the states as needed.
     * 
     * @author Jay
     */
    public class InitiativeListIcon extends ImageIcon {

        /** Bounds sent to the token state */
        private Rectangle bounds = new Rectangle(0, 0, ICON_SIZE, ICON_SIZE);
        
        /** The token painted by this icon */
        private Token token;
        
        /**
         * Create the image from the token and then build an icon suitable for displaying state.
         * 
         * @param aToken
         */
        public InitiativeListIcon(Token aToken) {
            token = aToken;
            setImage(((ImageIcon)aToken.getIcon(ICON_SIZE, ICON_SIZE)).getImage());
            Image image = ImageManager.getImageAndWait(AssetManager.getAsset(token.getImageAssetId()));
            BufferedImage bi = ImageUtil.createCompatibleImage(ICON_SIZE, ICON_SIZE, Transparency.TRANSLUCENT);
            Dimension d = new Dimension(image.getWidth(null), image.getHeight(null));
            SwingUtil.constrainTo(d, ICON_SIZE, ICON_SIZE);
            Graphics2D g = bi.createGraphics();
            g.drawImage(image, (ICON_SIZE - d.width) / 2, (ICON_SIZE - d.height) / 2, d.width, d.height, null);
            setImage(bi);
        }
        
        /**
         * @see javax.swing.ImageIcon#getIconHeight()
         */
        public int getIconHeight() { return ICON_SIZE; }
        
        /**
         * @see javax.swing.ImageIcon#getIconWidth()
         */
        public int getIconWidth() { return ICON_SIZE; }
        
        /**
         * Paint the icon and then the image.
         * 
         * @see javax.swing.ImageIcon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
         */
        public void paintIcon(Component c, java.awt.Graphics g, int x, int y) {
            
            // Paint the icon, is that all that's needed?
            super.paintIcon(c, g, x, y);
            if (!panel.isShowTokenStates()) return; 
            
            // Paint all the states
            g.translate(x, y);
            Shape old = g.getClip();
            g.setClip(bounds.intersection(old.getBounds()));
            for (String state : token.getStatePropertyNames()) {
                Object stateSet = token.getState(state);
                if (stateSet instanceof Boolean && ((Boolean)stateSet).booleanValue()) {
                    TokenOverlay overlay =  MapTool.getCampaign().getTokenStatesMap().get(state);
                    if (overlay != null) overlay.paintOverlay((Graphics2D)g, token, bounds);
                } // endif
            } // endfor
            g.setClip(old);
            g.translate(-x, -y);
        }
    }
}
