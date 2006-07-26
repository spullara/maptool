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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.drawing.AbstractTemplate.Direction;
import net.rptools.maptool.model.drawing.AbstractTemplate.Quadrant;

import com.jeta.forms.components.colors.JETAColorWell;
import com.jeta.forms.components.panel.FormPanel;

/**
 * Dialog used to select properties of the light state.
 * 
 * @author jgorrell
 * @version $Revision$ $Date: 2006-02-26 13:46:26 -0600 (Sun, 26 Feb
 *          2006) $ $Author$
 */
public class LightDialog extends JDialog {

	/*---------------------------------------------------------------------------------------------
	 * Instance Variables
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * North west corner component
	 */
	JRadioButton sourceCornerNW;

	/**
	 * North east corner component
	 */
	JRadioButton sourceCornerNE;

	/**
	 * South west corner component
	 */
	JRadioButton sourceCornerSW;

	/**
	 * South east corner component
	 */
	JRadioButton sourceCornerSE;

	/**
	 * The shape of the light
	 */
	private JComboBox shape;

	/**
	 * The radius of the bright light
	 */
	private JTextField brightRadius;

	/**
	 * Show a border on the bright light
	 */
	private JCheckBox brightBorder;

	/**
	 * The transparency of the bright light
	 */
	private JSlider brightTransparency;

	/**
	 * The radius of shadow illumination
	 */
	private JTextField shadowRadius;

	/**
	 * Show a border on the shadow illumination
	 */
	private JCheckBox shadowBorder;

	/**
	 * the transparency of the shadow illumination
	 */
	private JSlider shadowTransparency;

	/**
	 * The component that allows choosing a bright color.
	 */
	private JETAColorWell brightColor;

	/**
	 * The component that allows choosing a shadow color.
	 */
	private JETAColorWell shadowColor;

	/**
	 * The template returned by this dialog
	 */
	private TokenTemplate template;

	/**
	 * Flag used to indicate that the light template should be turned off for
	 * the token.
	 */
	private boolean templateOff;

	/*---------------------------------------------------------------------------------------------
	 * Class Variables
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * Prefix for all of the cone shapes
	 */
	private static final String CONE = "Cone - ";

	/**
	 * Model used to list the various shapes.
	 */
	private static final String[] SHAPES = { "Radius", CONE + Direction.NORTH,
			CONE + Direction.NORTH_EAST, CONE + Direction.EAST,
			CONE + Direction.SOUTH_EAST, CONE + Direction.SOUTH,
			CONE + Direction.SOUTH_WEST, CONE + Direction.WEST,
			CONE + Direction.NORTH_WEST };

	/**
	 * Ok button key. This action sets the light sorurce for the token.
	 */
	private static final String OK_BUTTON = "lightDialog.ok";

	/**
	 * Off button key. This action turns the light source off for the token.
	 */
	private static final String OFF_BUTTON = "lightDialog.off";

	/**
	 * Cancel button key. This action just closes the dialog and leaves the
	 * light source unchanged.
	 */
	private static final String CANCEL_BUTTON = "lightDialog.cancel";

	/**
	 * Colors that can be selected.
	 */
	private static final Color[] DEFAULT_COLORS = new Color[] { Color.black,
			Color.darkGray, Color.lightGray, Color.white, Color.pink,
			new Color(127, 0, 0), Color.red, Color.orange, Color.yellow,
			new Color(0, 127, 0), Color.green, Color.blue, Color.cyan,
			new Color(127, 0, 127), Color.magenta,
			new Color(127 + 32, 127, 61), };

	/**
	 * Create a new dialog
	 */
	public LightDialog() {
		super(MapTool.getFrame(), "Light Source", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		FormPanel panel = new FormPanel(
				"net/rptools/maptool/client/ui/forms/lightSourceDialog.jfrm");

		// Get value components
		brightRadius = panel.getTextField("brightRadius");
		brightBorder = panel.getCheckBox("brightBorder");
		brightTransparency = (JSlider) panel
				.getComponentByName("brightTransparency");
		shadowRadius = panel.getTextField("shadowRadius");
		shadowBorder = panel.getCheckBox("shadowBorder");
		shadowTransparency = (JSlider) panel
				.getComponentByName("shadowTransparency");
		shape = panel.getComboBox("shape");
		sourceCornerNW = panel.getRadioButton("sourceCornerNW");
		sourceCornerNE = panel.getRadioButton("sourceCornerNE");
		sourceCornerSW = panel.getRadioButton("sourceCornerSW");
		sourceCornerSE = panel.getRadioButton("sourceCornerSE");

		// Button components
		AbstractButton okButton = panel.getButton("okButton");
		AbstractButton cancelButton = panel.getButton("cancelButton");
		AbstractButton offButton = panel.getButton("offButton");

		// Color selection support and initialiation
		brightColor = (JETAColorWell) panel.getComponentByName("brightColor");
		shadowColor = (JETAColorWell) panel.getComponentByName("shadowColor");
		ColorWellListener listener = new ColorWellListener();
		for (int i = 0; i < DEFAULT_COLORS.length; i++) {
			JETAColorWell colorWell = (JETAColorWell) panel
					.getComponentByName("recentColor" + i);
			for (MouseListener ml : colorWell.getMouseListeners())
				colorWell.removeMouseListener(ml);
			colorWell.addMouseListener(listener);
			colorWell.setColor(DEFAULT_COLORS[i]);
		} // endfor

		// initialize models and actions
		shape.setModel(new DefaultComboBoxModel(SHAPES));
		okButton.setAction(new ButtonAction(OK_BUTTON));
		cancelButton.setAction(new ButtonAction(CANCEL_BUTTON));
		offButton.setAction(new ButtonAction(OFF_BUTTON));

		// Set up the dialog
		getRootPane().setDefaultButton((JButton) okButton);
		add(panel);
		pack();
	}

	/**
	 * Handle button presses
	 * 
	 * @author jgorrell
	 * @version $Revision$ $Date: 2006-02-26 13:46:26 -0600 (Sun, 26 Feb
	 *          2006) $ $Author$
	 */
	private class ButtonAction extends AbstractAction {

		/**
		 * Create a button
		 * 
		 * @param key
		 *            Key used to create the button.
		 */
		private ButtonAction(String key) {
			putValue(ACTION_COMMAND_KEY, key);
			I18N.setAction(key, this);
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent aE) {

			// Determine shape
			template = null;
			templateOff = false;
			if (aE.getActionCommand().equals(OK_BUTTON)) {

				// Get ranges
				int lightRange = readRadius(brightRadius, "Light Radius", true);
				int shadowRange = readRadius(shadowRadius, "Shadow Radius",
						false);

				// Get corner
				Quadrant c = null;
				if (sourceCornerNE.isSelected())
					c = Quadrant.NORTH_EAST;
				if (sourceCornerNW.isSelected())
					c = Quadrant.NORTH_WEST;
				if (sourceCornerSE.isSelected())
					c = Quadrant.SOUTH_EAST;
				if (sourceCornerSW.isSelected())
					c = Quadrant.SOUTH_WEST;

				// Get type
				if (shape.getSelectedIndex() == 0) {
					RadiusLightTokenTemplate tt = new RadiusLightTokenTemplate();
					tt.setRadius(shadowRange + lightRange);
					tt.setShadowRadius(lightRange);
					tt.setCorner(c);
					tt.setBrightBorder(brightBorder.isSelected());
					tt.setShadowBorder(shadowBorder.isSelected());
					tt.setBrightColor(getTransparentColor(brightColor
							.getColor(), brightTransparency.getValue()));
					tt.setShadowColor(getTransparentColor(shadowColor
							.getColor(), shadowTransparency.getValue()));
					template = tt;
				} else {
					ConeLightTokenTemplate tt = new ConeLightTokenTemplate();
					tt.setRadius(shadowRange + lightRange);
					tt.setShadowRadius(lightRange);
					Direction dir = Direction.valueOf(((String) shape
							.getSelectedItem()).substring(CONE.length()));
					tt.setDirection(dir);
					tt.setCorner(c);
					tt.setBrightBorder(brightBorder.isSelected());
					tt.setShadowBorder(shadowBorder.isSelected());
					tt.setBrightColor(getTransparentColor(brightColor
							.getColor(), brightTransparency.getValue()));
					tt.setShadowColor(getTransparentColor(shadowColor
							.getColor(), shadowTransparency.getValue()));
					template = tt;
				} // endif
			} else if (aE.getActionCommand().equals(OFF_BUTTON)) {
				templateOff = true;
			} // endif
			setVisible(false);
		}

		/**
		 * Get a color with a given transparency.
		 * 
		 * @param color
		 *            Color being created.
		 * @param transparency
		 *            Transparency percentage.
		 * @return The passed color with the given transparncy.
		 */
		private Color getTransparentColor(Color color, int transparency) {
			return new Color(color.getRed(), color.getGreen(), color.getBlue(),
					(int) ((float) transparency * 255 / 100));
		}

		/**
		 * Get the radius from a text field
		 * 
		 * @param field
		 *            Field being read.
		 * @param fieldName
		 *            The name of the field placed in messages.
		 * @param required
		 *            Is the field requires?
		 * @return The value of the field as an integer
		 */
		private int readRadius(JTextField field, String fieldName,
				boolean required) {

			// Read the text, make sure it exists
			String text = field.getText();
			if (text == null || (text = text.trim()).length() == 0) {
				if (required) {
					JOptionPane.showMessageDialog(field, fieldName
							+ " must be set.", "Problem!",
							JOptionPane.ERROR_MESSAGE);
					throw new IllegalArgumentException(fieldName
							+ " must be set.");
				} else {
					return 0;
				} // endif
			} // endif

			// Parse it into a number
			try {
				int ret = Integer.parseInt(text);
				if (ret < 0 || required && ret == 0) {
					JOptionPane.showMessageDialog(field, fieldName
							+ " has a value that is too small.", "Problem!",
							JOptionPane.ERROR_MESSAGE);
					throw new IllegalArgumentException(fieldName
							+ " has a value that is too small.");
				} // endif
				return ret;
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(field, fieldName
						+ " does not contain a number.", "Problem!",
						JOptionPane.ERROR_MESSAGE);
				throw new IllegalArgumentException(fieldName
						+ " is not a number.");
			} // endtry
		}
	}

	/**
	 * Get the template for this LightDialog.
	 * 
	 * @return Returns the current value of template.
	 */
	public TokenTemplate getTemplate() {
		return template;
	}

	/**
	 * Set the default values from a token template.
	 * 
	 * @param tt
	 *            Get the default values from this template.
	 */
	public void setDefaults(TokenTemplate tt) {

		// Generic default values
		brightRadius.setText("4");
		brightBorder.setSelected(false);
		brightTransparency.setValue(20);
		shadowRadius.setText("4");
		shadowBorder.setSelected(false);
		shadowTransparency.setValue(10);
		sourceCornerNW.setSelected(true);
		shape.setSelectedIndex(0);
		brightColor.setColor(Color.YELLOW);
		shadowColor.setColor(Color.YELLOW.darker());

		// get valus from the 2 supported light token templates.
		if (tt instanceof RadiusLightTokenTemplate) {
			RadiusLightTokenTemplate rt = (RadiusLightTokenTemplate) tt;
			shape.setSelectedIndex(0);
			selectCorner(rt.getCorner());
			setDistances(rt.getRadius(), rt.getShadowRadius());
			brightBorder.setSelected(rt.isBrightBorder());
			shadowBorder.setSelected(rt.isShadowBorder());
			brightTransparency.setValue(getTransparency(rt.getBrightColor()));
			shadowTransparency.setValue(getTransparency(rt.getShadowColor()));
			brightColor.setColor(rt.getBrightSolidColor());
			shadowColor.setColor(rt.getShadowSolidColor());
		} else if (tt instanceof ConeLightTokenTemplate) {
			ConeLightTokenTemplate ct = (ConeLightTokenTemplate) tt;
			shape.setSelectedItem(CONE + ct.getDirection());
			selectCorner(ct.getCorner());
			setDistances(ct.getRadius(), ct.getShadowRadius());
			brightBorder.setSelected(ct.isBrightBorder());
			shadowBorder.setSelected(ct.isShadowBorder());
			brightTransparency.setValue(getTransparency(ct.getBrightColor()));
			shadowTransparency.setValue(getTransparency(ct.getShadowColor()));
			brightColor.setColor(ct.getBrightSolidColor());
			shadowColor.setColor(ct.getShadowSolidColor());
		} // endif
	}

	/**
	 * Get the transparency percentage from a color's alpha value.
	 * 
	 * @param color
	 *            Read the alpha value from this color.
	 * @return The percent transparency for the color.
	 */
	private int getTransparency(Color color) {
		return (int) ((float) color.getAlpha() * 100 / 255);
	}

	/**
	 * Set the distance components from the given distances
	 * 
	 * @param aBrightRadius
	 *            Radius in cells for bright light
	 * @param aShadowRadius
	 *            Radius in cells for shadowy light
	 */
	private void setDistances(int aBrightRadius, int aShadowRadius) {
		brightRadius.setText(Integer.toString(aShadowRadius));
		shadowRadius.setText(Integer.toString(aBrightRadius - aShadowRadius));
	}

	/**
	 * Set the proper corner radio button for a quadrant.
	 * 
	 * @param quandrant
	 *            Set the corner for this quadrant.
	 */
	private void selectCorner(Quadrant quandrant) {
		switch (quandrant) {
		case NORTH_EAST:
			sourceCornerNE.setSelected(true);
			break;
		case NORTH_WEST:
			sourceCornerNW.setSelected(true);
			break;
		case SOUTH_EAST:
			sourceCornerSE.setSelected(true);
			break;
		case SOUTH_WEST:
			sourceCornerSW.setSelected(true);
			break;
		} // endswitch
	}

	/**
	 * Show the dialog and set the light template state.
	 * 
	 * @param token
	 *            The token having its state modified.
	 * @param state
	 *            The state to be modified.
	 */
	public static void show(Token token, String state) {
		LightDialog dialog = new LightDialog();
		dialog.setDefaults((TokenTemplate) token.getState(state));
		Rectangle b = MapTool.getFrame().getBounds();
		dialog.setLocation(b.x + (b.width - dialog.getWidth()) / 2, b.y
				+ (b.height - dialog.getHeight()) / 2);
		dialog.setVisible(true);
		if (dialog.getTemplate() != null)
			token.setState(state, dialog.getTemplate());
		else if (dialog.isTemplateOff())
			token.setState(state, null);
	}

	/**
	 * Handles mouse selections on the recent color values.
	 * 
	 * @author unascribed
	 * @version $Revision$ $Date: 2006-06-20 20:42:31 -0500 (Tue, 20 Jun
	 *          2006) $ $Author$
	 */
	public class ColorWellListener extends MouseAdapter {
		/**
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent evt) {

			// Double click will change the color well value as well as the
			// color value.
			JETAColorWell comp = (JETAColorWell) evt.getSource();
			if (evt.getClickCount() > 1) {
				Color result = JColorChooser.showDialog(LightDialog.this,
						"Choose a color", comp.getColor());
				if (result != null)
					comp.setColor(result);
			} // endif

			// Set the color by mouse button
			switch (evt.getButton()) {
			case MouseEvent.BUTTON1:
				brightColor.setColor(comp.getColor());
				break;
			case MouseEvent.BUTTON3:
				shadowColor.setColor(comp.getColor());
				break;
			} // endswitch
		}
	}

	/** @return Getter for templateOff */
	boolean isTemplateOff() {
		return templateOff;
	}
}
