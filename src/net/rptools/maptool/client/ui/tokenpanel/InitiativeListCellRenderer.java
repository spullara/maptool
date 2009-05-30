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
package net.rptools.maptool.client.ui.tokenpanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
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
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.token.AbstractTokenOverlay;
import net.rptools.maptool.client.ui.token.BarTokenOverlay;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.InitiativeListModel;
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
    public static final Border NAME_BORDER = BorderFactory.createEmptyBorder(2, 4, 3, 4);
    
    /** 
     * The size of the ICON shown in the list renderer 
     */
    public static final int ICON_SIZE = 50;
    
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
        name.setFont(getFont().deriveFont(Font.BOLD));
        textHeight = getFontMetrics(getFont()).getHeight();
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
        if (token == null) { // Can happen when deleting a token before all events have propagated
            currentIndicator.setIcon(null);
            name.setText(null);
            name.setIcon(null);
            setBorder(UNSELECTED_BORDER);
            return this;
        } // endif
        backgroundImageLabel = InitiativeListModel.isTokenVisible(token, panel.getList().isHideNPC()) ? token.getType() == Token.Type.NPC ? GraphicsUtil.BLUE_LABEL : GraphicsUtil.GREY_LABEL : GraphicsUtil.DARK_GREY_LABEL;
        Color foreground = InitiativeListModel.isTokenVisible(token, panel.getList().isHideNPC()) ? token.getType() == Token.Type.NPC ? Color.BLACK : Color.BLACK : Color.WHITE;
        name.setForeground(foreground);
        
        // Show the indicator?
        int currentIndex = panel.getList().getCurrent();
        if (currentIndex >= 0 && ti == panel.getList().getTokenInitiative(currentIndex)) {
            currentIndicator.setIcon(CURRENT_INDICATOR_ICON);
        } else {
            currentIndicator.setIcon(null);
        } // endif
        
        // Get the name string, add the state if displayed, then get the icon if needed
        String sName = ti.getToken().getName();
        if (MapTool.getFrame().getInitiativePanel().hasGMPermission() && token.getGMName() != null && token.getGMName().trim().length() != 0)
            sName += " (" + token.getGMName().trim() + ")";
        if (panel.isShowInitState() && ti.getState() != null)
            sName += " = " + ti.getState();
        Icon icon = null;
        if (panel.isShowTokens()) {
            icon = ti.getDisplayIcon();
            if (icon == null) {
                icon = new InitiativeListIcon(ti);
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
            backgroundImageLabel.renderLabel((Graphics2D)g, 0, (s.height - textHeight) / 2, s.width, textHeight + 2);
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
        private TokenInitiative tokenInitiative;

        /** The image that is displayed when states are not being shown. */
        private Image textTokenImage;
        
        /** The image that is displayed when states are being shown. */
        private Image stateTokenImage;
        
        /** Size of the text only token */
        private int textTokenSize = Math.max(textHeight + 4, 16);
        
        /**
         * Create the image from the token and then build an icon suitable for displaying state.
         * 
         * @param aTokenInitiative The initiative item being rendered
         */
        public InitiativeListIcon(TokenInitiative aTokenInitiative) {
            tokenInitiative = aTokenInitiative;
            if (panel.isShowTokenStates()) {
                stateTokenImage = scaleImage();
            } else {
                textTokenImage = scaleImage();
            } // endif
        }
        
        /**
         * Scale the token's image to fit in the allotted space for text or state painting.
         * 
         * @return The properly scaled image.
         */
        public Image scaleImage() {
            Image image = ImageManager.getImageAndWait(AssetManager.getAsset(tokenInitiative.getToken().getImageAssetId()));
            BufferedImage bi = ImageUtil.createCompatibleImage(getIconWidth(), getIconHeight(), Transparency.TRANSLUCENT);
            Dimension d = new Dimension(image.getWidth(null), image.getHeight(null));
            SwingUtil.constrainTo(d, getIconWidth(), getIconHeight());
            Graphics2D g = bi.createGraphics();
            g.drawImage(image, (getIconWidth() - d.width) / 2, (getIconHeight() - d.height) / 2, d.width, d.height, null);
            setImage(bi);
            return bi;
        }
        
        /**
         * @see javax.swing.ImageIcon#getIconHeight()
         */
        public int getIconHeight() { return panel.isShowTokenStates() ? ICON_SIZE : textTokenSize; }
        
        /**
         * @see javax.swing.ImageIcon#getIconWidth()
         */
        public int getIconWidth() { return panel.isShowTokenStates() ? ICON_SIZE : textTokenSize; }
        
        /**
         * Paint the icon and then the image.
         * 
         * @see javax.swing.ImageIcon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
         */
        public void paintIcon(Component c, Graphics g, int x, int y) {

            // Paint the halo if needed
            Token token = tokenInitiative.getToken();
            if (panel.isShowTokenStates() && token.hasHalo()) {
                Graphics2D g2d = (Graphics2D)g;
                Stroke oldStroke = g2d.getStroke();
                Color oldColor = g.getColor(); 
                g2d.setStroke( new BasicStroke(AppPreferences.getHaloLineWidth()));
                g.setColor(token.getHaloColor());
                g2d.draw(new Rectangle2D.Double(x, y, ICON_SIZE, ICON_SIZE));
                g2d.setStroke(oldStroke);
                g.setColor(oldColor);
            } // endif
            
            // Paint the icon, is that all that's needed?
            if (panel.isShowTokenStates() && getImage() != stateTokenImage) {
                if (stateTokenImage == null) stateTokenImage = scaleImage();
                setImage(stateTokenImage);
            } else if (!panel.isShowTokenStates() && getImage() != textTokenImage) {
                if (textTokenImage == null) textTokenImage = scaleImage();
                setImage(textTokenImage);
            } // endif
            super.paintIcon(c, g, x, y);
            if (!panel.isShowTokenStates()) return; 
            
            // Paint all the states
            g.translate(x, y);
            Shape old = g.getClip();
            g.setClip(bounds.intersection(old.getBounds()));
            for (String state : MapTool.getCampaign().getTokenStatesMap().keySet()) {
                Object stateSet = token.getState(state);
                AbstractTokenOverlay overlay =  MapTool.getCampaign().getTokenStatesMap().get(state);
                if (stateSet instanceof AbstractTokenOverlay || overlay == null || !overlay.showPlayer(token, MapTool.getPlayer())) continue;
                overlay.paintOverlay((Graphics2D)g, token, bounds, stateSet);
            } // endfor
            for (String bar : MapTool.getCampaign().getTokenBarsMap().keySet()) {
                Object barSet = token.getState(bar);
                BarTokenOverlay overlay = MapTool.getCampaign().getTokenBarsMap().get(bar);
                if (overlay == null || !overlay.showPlayer(token, MapTool.getPlayer())) continue;
                overlay.paintOverlay((Graphics2D)g, token, bounds, barSet);
            } // endfor            
            g.setClip(old);
            g.translate(-x, -y);
        }
    }
}
