package net.rptools.maptool.client.functions;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Transparency;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.util.ImageManager;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.VariableResolver;
import net.rptools.parser.function.AbstractFunction;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;

/**
 * <pre><span style="font-family:sans-serif;">The input() function prompts the user to input several variable values at once.
 * 
 * Each of the string parameters has the following format:
 *     "varname|value|prompt|inputType|options"
 *     
 * Only the first section is required.
 *     varname   - the variable name to be assigned
 *     value     - sets the initial contents of the input field
 *     prompt    - UI text shown for the variable
 *     inputType - specifies the type of input field
 *     options   - a string of the form "opt1=val1; opt2=val2; ..."
 *     
 * The inputType field can be any of the following (defaults to TEXT):
 *     TEXT  - A text field.  
 *             "value" sets the initial contents.
 *             The return value is the string in the text field.
 *             Option: WIDTH=nnn sets the width of the text field (default 16).
 *     LIST  - An uneditable combo box.
 *             "value" populates the list, and has the form "item1,item2,item3..." (trailing empty strings are dropped)
 *             The return value is the numeric index of the selected item.
 *             Option: SELECT=nnn sets the initial selection (default 0).
 *             Option: VALUE=STRING returns the string contents of the selected item (default NUMBER).
 *             Option: TEXT=FALSE suppresses the text of the list item (default TRUE).
 *             Option: ICON=TRUE causes icon asset URLs to be extracted from the "value" and displayed (default FALSE).
 *             Option: ICONSIZE=nnn sets the size of the icons (default 50).
 *     CHECK - A checkbox.
 *             "value" sets the initial state of the box (anything but "" or "0" checks the box)
 *             The return value is 0 or 1.
 *             No options.
 *     RADIO - A group of radio buttons.
 *             "value" is a list "name1, name2, name3, ..." which sets the labels of the buttons.
 *             The return value is the index of the selected item.
 *             Option: SELECT=nnn sets the initial selection (default 0).
 *             Option: ORIENT=H causes the radio buttons to be laid out on one line (default V). 
 *             Option: VALUE=STRING causes the return value to be the string of the selected item (default NUMBER).
 *     LABEL - A label.
 *             The "varname" is ignored and no value is assigned to it.
 *             Option: TEXT=FALSE, ICON=TRUE, ICONSIZE=nnn, as in the LIST type.
 *     PROPS - A sub-panel with multiple text boxes.
 *             "value" contains a StrProp of the form "key1=val1; key2=val2; ..."
 *             One text box is created for each key, populated with the matching value.
 *             Option: SETVARS=TRUE causes variable assignment to each key name (default FALSE).
 * </span></pre>
 * 
 * @author knizia.fan
 */

public class InputFunction extends AbstractFunction {

	/** The singleton instance. */
	private final static InputFunction instance = new InputFunction();
	
	private InputFunction() {
		super(1, -1, "input");
	}

	
	/** 
	 * Gets the Input instance.
	 * @return the instance.
	 */
	public static InputFunction getInstance() {
		return instance;
	}
	
	
	/********************************************************************
	 * Enum of input types; also stores their default option values. 
	 ********************************************************************/
	public enum InputType {
		// The regexp for the option strings is strict: no spaces, and trailing semicolon required.
		TEXT (false, false, "WIDTH=16;"),
		LIST (true, false, "VALUE=NUMBER;TEXT=TRUE;ICON=FALSE;ICONSIZE=50;SELECT=0;"),
		CHECK (false, false, ""),
		RADIO (true, false, "ORIENT=V;VALUE=NUMBER;SELECT=0;"),
		LABEL (false, false, "TEXT=TRUE;ICON=FALSE;ICONSIZE=50;"),
		PROPS (false, true, "SETVARS=FALSE;");

		public final OptionMap defaultOptions;	// maps option name to default value
		public final boolean isValueComposite;		// can "value" section be a list of values?
		public final boolean isControlComposite;	// does this control contain sub-controls?
		
		InputType(boolean isValueComposite, boolean isControlComposite, String nameval) {
			this.isValueComposite = isValueComposite;
			this.isControlComposite = isControlComposite;
			
			defaultOptions = new OptionMap();
			Pattern pattern = Pattern.compile("(\\w+)=([\\w-]+)\\;");	// no spaces allowed, semicolon required
			Matcher matcher = pattern.matcher(nameval);
			while (matcher.find()) {
				defaultOptions.put(matcher.group(1).toUpperCase(), matcher.group(2).toUpperCase());
			}
		}
		
		
		/** Obtain one of the enum values, or null if <code>strName</code> doesn't match any of them. */
		public static InputType inputTypeFromName(String strName) {
			for (InputType it : InputType.values()) {
				if (strName.equalsIgnoreCase(it.name()))
					return it;
			}
			return null;
		}
		
		/** Gets the default value for an option. */
		public String getDefault(String option) {
			return defaultOptions.get(option.toUpperCase());
		}

		/** Parses a string and returns a Map of options for the given type.
		 *  Options not found are set to the default value for the type. */
		public OptionMap parseOptionString(String s) throws OptionException {
			OptionMap ret = new OptionMap();
			ret.putAll(defaultOptions);	// copy the default values first
			Pattern pattern = Pattern.compile("\\s*(\\w+)\\s*\\=\\s*([\\w-]+)\\s*");
			Matcher matcher = pattern.matcher(s);
			while (matcher.find()) {
				String key = matcher.group(1);
				String value = matcher.group(2);
				if (ret.get(key) == null)
					throw new OptionException(this, key, value);
				if (ret.getNumeric(key, -9998) != -9998) {	// minor hack to detect if the option is numeric
					boolean valueIsNumeric;
					try {
						Integer.decode(value);
						valueIsNumeric = true;
					} catch (Exception e) {
						valueIsNumeric = false;
					}
					if (!valueIsNumeric)
						throw new OptionException(this, key, value);
				}
				ret.put(key, value);
			}
			return ret;
		}

		/*********************************************************
		 * Stores option settings as case-insensitive strings. 
		 *********************************************************/
		@SuppressWarnings("serial")
		public final class OptionMap extends HashMap<String,String>{
			/** Case-insensitive put. */
			public String put(String key, String value) {
				return super.put(key.toUpperCase(), value.toUpperCase());
			}
			
			/** Case-insensitive string get. */
			public String get(Object key) {
				return super.get(key.toString().toUpperCase());
			}
			
			/** Case-insensitive numeric get.
			 *  <br>Returns <code>defaultValue</code> if the option's value is non-numeric. 
			 *  <br>Use when caller wants to override erroneous option settings. */
			public int getNumeric(String key, int defaultValue) {
				int ret;
				try {
					ret = Integer.decode(get(key));
				} catch (Exception e) {
					ret = defaultValue;
				}
				return ret;
			}
			
			/** Case-insensitive numeric get.
			 *  <br>Returns the default value for the input type if option's value is non-numeric. 
			 *  <br>Use when caller wants to ignore erroneous option settings. */
			public int getNumeric(String key) {
				String defstr = getDefault(key);
				int def;
				try {
					def = Integer.decode(defstr);
				} catch (Exception e) {
					def = -1;
					// Should never happen, since the defaults are set in the source code.
				}
				return getNumeric(key, def);
			}
			
			/** Tests for a given option value. */
			public boolean optionEquals(String key, String value) {
				return get(key).equalsIgnoreCase(value);
			}
		} ////////////////////////// end of OptionMap class
		
		/** Thrown when an option value is invalid. */
		@SuppressWarnings("serial")
		public class OptionException extends Exception {
			public String key, value, type;
			
			public OptionException(InputType it, String key, String value) {
				super();
				this.key = key;
				this.value = value;
				this.type = it.name();
			}
		}
	} ///////////////////// end of InputType enum
	
	
	/**********************************************************************************
	 * Variable Specifier structure - holds extracted bits of info for a variable. 
	 **********************************************************************************/
	final class VarSpec {
		public String name, value, prompt;
		public InputType inputType;
		public InputType.OptionMap optionValues;
		public List<String> valueList;				// used for types with composite "value" properties
		
		public VarSpec(String name, String value, String prompt, InputType inputType, String options) 
		throws InputType.OptionException {
			this.name = name;
			this.value = value;
			this.prompt = prompt;
			this.inputType = inputType;
			this.optionValues = inputType.parseOptionString(options);
			
			if (inputType != null && inputType.isValueComposite)
				this.valueList = parseStringList(this.value);
		}
		
		/** Parses a string into a list of values, for composite types. 
		 *  <br>Before calling, the <code>inputType</code> and <code>value</code> must be set.
		 *  <br>After calling, the <code>listIndex</code> member is adjusted if necessary. */
		public List<String> parseStringList(String valueString) {
			List<String> ret = new ArrayList<String>();
			if (valueString != null) {
   				String[] values = valueString.split(",");
   				int i=0;
   				for (String s : values) {
   					ret.add(s.trim());
   					i++;
   				}
   			}
			return ret;
		}
	} ///////////////////// end of VarSpec class
	
	
	// The function that does all the work
    @Override
    public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws EvaluationException, ParserException {
    	
    	int numVars = parameters.size();
    	ArrayList<VarSpec> varSpecs = new ArrayList<VarSpec>(numVars);
    	
    	// Extract the info from each variable's specifier string
    	// "name | value | prompt | inputType | options" 
    	for (Object param : parameters) {
    		if (StringUtils.isEmpty((String)param)) {
    			numVars--;
    			continue;					// Silently skip blank strings
    		}
    		
    		String[] parts = ((String)param).split("\\|");
    		int numparts = parts.length;
    		
    		String name, value, prompt;
    		InputType inputType;

    		name = (numparts>0) ? parts[0].trim() : "";
    		if (StringUtils.isEmpty(name)) 
    			throw new ParameterException(String.format(
    					"Empty variable name in the parameter string '%s'", param));

    		value = (numparts>1) ? parts[1].trim() : "";
   			if (StringUtils.isEmpty(value)) value = "0";	// Avoids having a default value of ""
			
			prompt = (numparts>2) ? parts[2].trim() : "";
   			if (StringUtils.isEmpty(prompt)) prompt = name;

   			String inputTypeStr = (numparts>3) ? parts[3].trim() : "";
   			inputType = InputType.inputTypeFromName(inputTypeStr);
   			if (inputType == null) {
   				if (StringUtils.isEmpty(inputTypeStr)) {
	   				inputType = InputType.TEXT;	// default
   				} else {
	   				throw new ParameterException(String.format(
	   						"Invalid input type '%s' in the parameter string '%s'", inputTypeStr, param));
   				}
   			}
   			
   			String options = (numparts>4) ? parts[4].trim() : "";

   			VarSpec vs = null;
    		try {
    			vs = new VarSpec(name, value, prompt, inputType, options);
    		} catch (InputType.OptionException e) {
    			String msg;
    			msg = String.format(
    					"The option '%s=%s' is invalid for input type '%s' in parameter '%s'.", 
    					e.key, e.value, e.type, param);
    			throw new ParserException(msg);
    		}
    		varSpecs.add(vs);
    	}
    	
    	if (numVars == 0)
    		return new BigDecimal(1);	// No work to do, so treat it as a successful invocation.
    	
    	// UI step 1 - First, see if a token is in context.
    	VariableResolver varRes = parser.getVariableResolver();
    	Token tokenInContext = null;
    	if (varRes instanceof MapToolVariableResolver) {
    		tokenInContext = ((MapToolVariableResolver)varRes).getTokenInContext();
    	}
    	String dialogTitle = "Input Values";
    	if (tokenInContext!=null) {
    		String name = tokenInContext.getName(), gm_name = tokenInContext.getGMName();
    		boolean isGM = MapTool.getPlayer().isGM();
    		String extra = "";

    		if (isGM && gm_name != null  && gm_name.compareTo("")!=0) 
    			extra = " for " + gm_name;
    		else if (name != null && name.compareTo("")!=0) 
    			extra = " for " + name;
    		
    		dialogTitle = dialogTitle + extra;
    	}
    	
		// UI step 2 - build the panel with the input fields
    	@SuppressWarnings("serial")
    	final class InputPanel extends JPanel {
			public List<JComponent> 		inputFields;	// the input controls at the top level
    		public List<JLabel>     		labels;			// the labels at the top level
    		public List<List<JComponent>>	subInputFieldsList;		// list of lists of input controls in nested panels
    		
    		public InputPanel(ArrayList<VarSpec> varSpecs) {
    			labels = new ArrayList<JLabel>(varSpecs.size());
    			inputFields = new ArrayList<JComponent>(varSpecs.size());
    			subInputFieldsList = new ArrayList<List<JComponent>>();

    			setLayout(new GridBagLayout());
    			GridBagConstraints gbc = new GridBagConstraints();
    			gbc.anchor = GridBagConstraints.NORTHWEST;
    			gbc.insets = new Insets(2,2,2,2);
    			
    			// These are used for composite controls
    			JPanel panelNest = null;
    			GridBagConstraints gbcNest = null;
    			
    			Insets textInsets = new Insets(0,2,0,2);
    			int componentCount = 0;

    			for (VarSpec vs : varSpecs) {
    				List<JComponent> subInputFields = null;
    				gbc.gridy = componentCount;
    				
    				// add the label
    				gbc.gridx = 0;
    				JLabel l = new JLabel(vs.prompt + ":");
    				labels.add(l);
    				add(l, gbc);
    				
    				// prepare a subpanel for use by composite controls
	    			if (vs.inputType.isControlComposite) {
    					// Create a sub-panel for use by the composite control
    					panelNest = new JPanel();
    					panelNest.setLayout(new GridBagLayout());
    					TitledBorder borderNest = new TitledBorder(new EtchedBorder());
    					panelNest.setBorder(borderNest);
    					gbcNest = new GridBagConstraints();
		    			gbcNest.anchor = GridBagConstraints.NORTHWEST;
		    			gbcNest.insets = new Insets(2,2,2,2);
		    			
		    			subInputFields = new ArrayList<JComponent>();
		    			subInputFieldsList.add(subInputFields);
    				}
    				
    				// add the input component
    				gbc.gridx = 1;
    				JComponent inputField = null;
    				switch (vs.inputType) {
    				case TEXT:
    				{
    					int width = vs.optionValues.getNumeric("Width");
    					JTextField txt = new JTextField(vs.value, width);
    					txt.setMargin(textInsets);
    					inputField = txt;
    					break;
    				}
    				case LIST:
    				{
    					JComboBox combo;
    					boolean showText = vs.optionValues.optionEquals("TEXT", "TRUE");
    					boolean showIcons = vs.optionValues.optionEquals("ICON", "TRUE");
    					int iconSize = vs.optionValues.getNumeric("ICONSIZE", 0);
    					if (iconSize <= 0) showIcons = false;
    					
    					// Build the combo box
    					for (int j = 0; j<vs.valueList.size(); j++) {
    						if (StringUtils.isEmpty(vs.valueList.get(j))) {
    							// Using a non-empty string prevents the list entry from having zero height.
    							vs.valueList.set(j, " ");	
    						}
    					}
    					if (!showIcons) {
    						// Swing has an UNBELIEVABLY STUPID BUG when multiple items in a JComboBox compare as equal.
    						// The combo box then stops supporting navigation with arrow keys, and
    						// no matter which of the identical items is chosen, it returns the index
    						// of the first one.  Sun closed this bug as "by design" in 1998.
    						// A workaround found on the web is to use this alternate string class (defined below)
    						// which never reports two items as being equal.
    						NoEqualString[] nesValues = new NoEqualString[vs.valueList.size()];
    						for (int i=0; i<nesValues.length; i++)
    							nesValues[i] = new NoEqualString(vs.valueList.get(i));
    						combo = new JComboBox(nesValues);
    					} else {
    						combo = new JComboBox();
    						combo.setRenderer(new ComboBoxRenderer());
							Pattern pattern = Pattern.compile("^(.*)asset\\:\\/\\/(\\w+)");
	
							for (String value : vs.valueList) {
								Matcher matcher = pattern.matcher(value);
		    					String valueText, assetID;
		    					Icon icon = null;
		
		    					// See if the value string for this item has an image URL inside it
								if (matcher.find()) {
									valueText = matcher.group(1);
									assetID = matcher.group(2);
								} else {
									valueText = value;
									assetID = null;
								}
								
	    						icon = getIcon(assetID, iconSize);
		    					
		    					// Assemble a JLabel and put it in the list
		    					JLabel label = new JLabel();
		    					label.setOpaque(true);	// needed to make selection highlighting show up
		    					if (showText) label.setText(valueText);
		    					if (icon != null) label.setIcon(icon);
		    					combo.addItem(label);
							}
    					}
    					int listIndex = vs.optionValues.getNumeric("SELECT");
    					if (listIndex < 0 || listIndex >= vs.valueList.size())
    						listIndex = 0;
    					combo.setSelectedIndex(listIndex);
    					combo.setMaximumRowCount(16);
    					inputField = combo;
						break;
    				}
    				case CHECK:
    				{
    					JCheckBox check = new JCheckBox();
    					check.setText("    ");	// so a focus indicator will appear
    					if (vs.value.compareTo("0")!=0)
    						check.setSelected(true);
    					inputField = check;
    					break;
    				}
    				case RADIO:
    				{
    					int listIndex = vs.optionValues.getNumeric("SELECT");
    					if (listIndex < 0 || listIndex >= vs.valueList.size())
    						listIndex = 0;
    					ButtonGroup bg = new ButtonGroup();
    					Box box = (vs.optionValues.optionEquals("ORIENT", "H"))
    							? Box.createHorizontalBox()
    							: Box.createVerticalBox();
    					box.setBorder(new EtchedBorder());
    					int radioCount = 0;
    					for (String value : vs.valueList) {
    						JRadioButton radio = new JRadioButton(value, false);
    						bg.add(radio);
    						box.add(radio);
    						if (listIndex == radioCount) radio.setSelected(true);
    						radioCount++;
    					}
    					inputField = box;
    					break;
    				}
    				case LABEL:
    				{
    					boolean hasText = vs.optionValues.optionEquals("TEXT", "TRUE");
    					boolean hasIcon = vs.optionValues.optionEquals("ICON", "TRUE");
						int iconSize = vs.optionValues.getNumeric("ICONSIZE", 0);
						if (iconSize <= 0) hasIcon = false;
    					String valueText = "", assetID = "";
    					Icon icon = null;

    					// See if the string has an image URL inside it
						Matcher matcher = Pattern.compile("^(.*)asset\\:\\/\\/(\\w+)").matcher(vs.value);
						if (matcher.find()) {
							valueText = matcher.group(1);
							assetID = matcher.group(2);
						} else {
							hasIcon = false;
							valueText = vs.value;
						}

						// Try to get the icon
    					if (hasIcon) {
    						icon = getIcon(assetID, iconSize);
    						if (icon == null) hasIcon = false;
    					}
    					
    					// Assemble the JLabel
    					JLabel label = new JLabel();
    					if (hasText) label.setText(valueText);
    					if (hasIcon) label.setIcon(icon);
    					inputField = label;
    					break;
    				}
    				case PROPS: {
    					// Get the key/value pairs from the property string
    					Map<String,String> map = new HashMap<String,String>();
    					List<String> oldKeys = new ArrayList<String>();
    					StrPropFunctions.parse(vs.value, map, oldKeys);

    					int componentCountNest = 0;
    					for (String key : oldKeys) {
    						String value = map.get(key.toUpperCase());
    						gbcNest.gridy = componentCountNest;
    						// add a label
    						gbcNest.gridx = 0;
    						JLabel lbl = new JLabel(key + ":");
    						panelNest.add(lbl, gbcNest);
    						// add the text box
    						gbcNest.gridx = 1;
    						JTextField txt = new JTextField(value, 14);
    						txt.setMargin(textInsets);
    						panelNest.add(txt, gbcNest);
    						// Save the input controls in a list so our ComponentListener can
    						// modify them when the dialog is shown.
    						subInputFields.add(txt);
    						
    						componentCountNest++;
    					}
    					inputField = panelNest;
    					break;
    				}
    				default:
    					// should never happen
    					inputField = null;
    					break;
    				}
   					inputFields.add(inputField);
   					add(inputField, gbc);
    				componentCount++;
    			}
    		}
    	}
    	
    	InputPanel ip = new InputPanel(varSpecs);
    	
    	// UI step 3 - show the dialog
    	JOptionPane jop = new JOptionPane(ip, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
    	JDialog dlg = jop.createDialog(MapTool.getFrame(), dialogTitle);

    	// Find the first focusable control
    	JComponent first = null;
    	for (JComponent c : ip.inputFields) {
    		first = c;
    		if (c instanceof JPanel) {
    			// Grab the first sub-control (no verification that it's focusable)
    			first = ip.subInputFieldsList.get(0).get(0);
    			break;
    		}
    		if (! (c instanceof JLabel) ) 
    			break;
    	}

    	// Set up callbacks that assign focus to the first control when the dialog is shown,
    	// fix JComboBox bug, and cause text fields to auto-select when they gain focus.
    	dlg.addComponentListener(new FixupComponentAdapter(first, ip.inputFields));
    	for (List<JComponent> inputFields : ip.subInputFieldsList) {
    		dlg.addComponentListener(new FixupComponentAdapter(null, inputFields));
    	}

    	dlg.setVisible(true);
    	int dlgResult = (Integer)jop.getValue();
    	dlg.dispose();
    	
    	if (dlgResult == JOptionPane.CANCEL_OPTION || dlgResult == JOptionPane.CLOSED_OPTION)
    		return new BigDecimal(0);
    	
    	// Finally, assign values from the dialog box to the variables
    	for (int varCount=0; varCount<numVars; varCount++) {
    		VarSpec vs = varSpecs.get(varCount);
    		JComponent comp = ip.inputFields.get(varCount);
    		String newValue = null;
    		switch (vs.inputType) {
    			case TEXT:
    			{
	    			newValue = ((JTextField)comp).getText();
    				break;
    			}
    			case LIST:
    			{
    				Integer index = ((JComboBox)comp).getSelectedIndex();
    				if (vs.optionValues.optionEquals("VALUE", "STRING")) {
    					newValue = vs.valueList.get(index);
    				} else {	// default is "NUMBER"
    					newValue = index.toString();
    				}
    				break;
    			}
    			case CHECK:
    			{
    				Integer value = ((JCheckBox)comp).isSelected() ? 1 : 0;
    				newValue = value.toString();
    				break;
    			}
    			case RADIO:
    			{
    				// This code assumes that the Box container returns components
    				// in the same order that they were added.
    				Component[] comps = ((Box)comp).getComponents();
    				int componentCount = 0;
    				Integer index = 0;
    				for (Component c : comps) {
    					if (c instanceof JRadioButton) {
    						JRadioButton radio = (JRadioButton)c;
    						if (radio.isSelected()) index = componentCount;
    					}
    					componentCount++;
    				}
    				if (vs.optionValues.optionEquals("VALUE", "STRING")) {
    					newValue = vs.valueList.get(index);
    				} else {	// default is "NUMBER"
    					newValue = index.toString();
    				}
    				break;
    			}
    			case LABEL:
    			{
    				newValue = null;
    				// The variable name is ignored and not set.
    				break;
    			}
    			case PROPS: 
    			{
    				// Read out and assign all the subvariables.
    				// The overall return value is a property string (as in StrProp.java) with all the new settings.
    				Component[] comps = ((JPanel)comp).getComponents();
    				StringBuilder sb = new StringBuilder();
    				boolean setVars = vs.optionValues.optionEquals("SETVARS", "TRUE");
    				for (int compCount=0; compCount < comps.length; compCount += 2) {
    					String key = ((JLabel)comps[compCount]).getText().split("\\:")[0];	// strip trailing colon
    					String value = ((JTextField)comps[compCount+1]).getText();
    					sb.append(key);
    					sb.append("=");
    					sb.append(value);
    					sb.append(" ; ");
    					if (setVars) parser.setVariable(key, value);
    				}
    				newValue = sb.toString();
    				break;
    			}
    			default:
    				// should never happen
    				newValue = null;
    				break;
    		}
    		
    		// Set the variable to the value we got from the dialog box.
    		if (newValue != null)
    			parser.setVariable(vs.name, newValue.trim());
    	}
    	
    	return new BigDecimal(1);	// success
    	
    	// for debugging:
    	//return debugOutput(varSpecs);
    }
    
    
    @Override
    public void checkParameters(List<Object> parameters) throws ParameterException {
        super.checkParameters(parameters);
        
        for (Object param : parameters) {
            if (!(param instanceof String)) throw new ParameterException(String.format(
            		"Illegal argument type %s, expecting %s", param.getClass().getName(), String.class.getName()));
            
        }
    }
    
    /** Gets icon from the asset manager.
     *  Code copied and modified from EditTokenDialog.java  */
    private Icon getIcon(String id, int size) {
    	// Extract the MD5Key from the URL
    	if (id == null)
    		return null;
    	MD5Key assetID = new MD5Key(id);
    	
		// Get the base image && find the new size for the icon
		BufferedImage assetImage = null;
		Asset asset = AssetManager.getAsset(assetID);
		if (asset == null) {
			assetImage = ImageManager.UNKNOWN_IMAGE;
		} else {
			assetImage = ImageManager.getImage(asset, (ImageObserver)null);
		}

		// Resize
		if (assetImage.getWidth() > size || assetImage.getHeight() > size) {
			Dimension dim = new Dimension(assetImage.getWidth(), assetImage.getWidth());
			if (dim.height < dim.width) {
				dim.height = (int) ((dim.height / (double) dim.width) * size);
				dim.width = size;
			} else {
				dim.width = (int) ((dim.width / (double) dim.height) * size);
				dim.height = size;
			}
			BufferedImage image = new BufferedImage(dim.width, dim.height,
					Transparency.BITMASK);
			Graphics2D g = image.createGraphics();
			g.drawImage(assetImage, 0, 0, dim.width, dim.height, null);
			assetImage = image;
		}
		return new ImageIcon(assetImage);
	}
    
    /** Custom renderer to display icons and text inside a combo box */
	private class ComboBoxRenderer implements ListCellRenderer {
        public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
        {
        	JLabel label = null;
        	if (value instanceof JLabel) {
        		label = (JLabel)value;
	            if (isSelected) {
	                label.setBackground(list.getSelectionBackground());
	                label.setForeground(list.getSelectionForeground());
	            } else {
	                label.setBackground(list.getBackground());
	                label.setForeground(list.getForeground());
	            }
        	} 
        	return label;
        }
    }

	/** Adjusts the behavior of components */
	public class FixupComponentAdapter extends ComponentAdapter {
		final JComponent compFirst;
		final List<JComponent> comps;

		FixupComponentAdapter(JComponent compFirst, List<JComponent> comps) {
			super();
			this.compFirst = compFirst;
			this.comps = comps;
		}
		
		public void componentShown(ComponentEvent e) {
			fixupComponents(e);
		}
		
		public void fixupComponents(ComponentEvent e) {
			for (JComponent c : comps) {
				if (c instanceof JComboBox) {
			    	// HACK: to fix a Swing issue.
			    	// The stupid JComboBox has two subcomponents, BOTH of which accept the focus.
			    	// Thus it takes two Tab presses to move to the next control, and if you
			    	// tab once and then hit the down arrow, you can then tab away while the dropdown
			    	// list remains displayed.  (Other comboboxes in MapTool have similar problems.)
			    	// Since the user is likely to tab between values when inputting, this is a
			    	// confusing nuisance.
			    	//
			    	// The hack used here is to make one of the two components (TinyComboBoxButton)
			    	// not focusable.  We have to do it in a callback like this because the subcomponents
			    	// don't exist until the dialog is created (I think?).  The code has a hardcoded index of 0,
			    	// which is where the TinyComboBoxButton lives on my Windows box (discovered using the debugger).
			    	// The code may fail on other OSs, or if a future version of Swing is used.
			    	// You're not supposed to mess with the internals like this.
			    	// But the resulting behavior is so much nicer with this fix in place, that I'm keeping it in.
					if (c.getComponents().length > 0) {
						c.getComponents()[0].setFocusable(false);		// HACK!
					}
				} else if (c instanceof JTextField) {
					// Select all text when the text field gains focus
					final JTextField textFieldFinal = (JTextField)c;
					textFieldFinal.addFocusListener(new FocusListener() {
						public void focusGained(FocusEvent e) {
							textFieldFinal.selectAll();
						}
						public void focusLost(FocusEvent e) { }
					});
				}
			}
			// Start the focus in the first input field, so the user can type immediately
			if (compFirst != null) 
				compFirst.requestFocusInWindow();
		}
	}

	
	
	/** Class found on web to work around a STUPID SWING BUG with JComboBox */
	public class NoEqualString
	{
	    private String text;
	    public NoEqualString(String txt) {
	        text = txt;
	    }
	    public String toString() {
	        return text;
	    }
	}


  
/*
    // Dumps out the parsed input specifications for debugging purposes
   	private String debugOutput(ArrayList<VarSpec> varSpecs) {
    	StringBuilder builder = new StringBuilder();
    	builder.append("<br><table border='1' padding='2px 2px'>");
    	builder.append("<tr style='font-weight:bold'><td>Name</td><td>Value</td><td>Prompt</td><td>Input Type</td><td>Options</td></tr>");
    	for (VarSpec vs : varSpecs) {
    		builder.append("<tr>");
    		builder.append("<td>");
    		builder.append(vs.name);
    		builder.append("</td><td>");
    		if (vs.inputType == InputType.LIST) {
    			builder.append("(( ");
    			for (String s : vs.valueList) {
    				builder.append(s);
    				builder.append(",");
    			}
    			builder.append(" ))");
    		} else {
    			builder.append(vs.value);
    		}
    		builder.append("</td><td>");
    		builder.append(vs.prompt);
    		builder.append("</td><td>");
    		builder.append(vs.inputType);
    		builder.append("</td><td>");
    		for (Map.Entry<String,String> entry : vs.optionValues.entrySet())
    			builder.append(entry.getKey() + "=" + entry.getValue() + "<br>");
    		builder.append("</td></tr>");
    	}
    	builder.append("</table>");
    	return builder.toString();
    }
*/ 
}


// Here's a sample input to exercise the options
/*

Original props = [props = "Name=Longsword +1; Damage=1d8+1; Crit=1d6; Keyword=fire;"]
[H: input(
"foo", 
"YourName|George Washington|Your name|TEXT", 
"Weapon|Axe,Sword,Mace|Choose weapon|LIST", 
"WarCry|Attack!,No surrender!,I give up!|Pick a war cry|LIST|VALUE=STRING select=1",
"CA || Combat advantage|     CHECK|",
"props |"+props+"|Weapon properties|PROPS|setvars=true",
"UsePower |1|Use the power|CHECK",
"Weight|light,medium,heavy||RADIO|ORIENT=H select=1",
"Ambition|Survive today, Defeat my enemies, Rule the world, Become immortal||RADIO|VALUE=STRING",
"bar | a, b, c, d, e, f, g , h     ,i  j, k   |Radio button test   |  RADIO       | select=5 value = string ; oRiEnT   =h;;;;"
)]<br>
<i>New values of variables:</i>
<br>foo is [foo]
<br>YourName is [YourName]
<br>Weapon is [Weapon]
<br>WarCry is [WarCry]
<br>CA is [CA]
<br>props is [props]
<br>UsePower is [UsePower]
<br>Weight is [Weight]
<br>Ambition is [Ambition]
<br>
<br>Name is [Name], Damage is [Damage], Crit is [Crit], Keyword is [Keyword]


 */


// Here's a longer version of that sample, but the 9/14/08 checked in version of MapTool gets
// a stack overflow when this is pasted into chat (due to its length?)
/*

Original props = [props = "Name=Longsword +1; Damage=1d8+1; Crit=1d6; Keyword=fire;"]
[h: setPropVars = 1]
[H: input(
"foo", 
"YourName|George Washington|Your name|TEXT", 
"Weapon|Axe,Sword,Mace|Choose weapon|LIST", 
"WarCry|Attack!,No surrender!,I give up!|Pick a war cry|LIST|VALUE=STRING select=1",
"CA || Combat advantage|     CHECK|",
"props |"+props+"|Weapon properties|PROPS|setvars=true",
"UsePower |1|Use the power|CHECK",
"Weight|light,medium,heavy||RADIO|ORIENT=H select=1",
"Ambition|Survive today, Defeat my enemies, Rule the world, Become immortal||RADIO|VALUE=STRING",
"bar | a, b, c, d, e, f, g , h     ,i  j, k   |Radio button test   |  RADIO       | select=5 value = string ; oRiEnT   =h;;;;"
)]<br>
<i>New values of variables:</i>
<table border=0><tr style='font-weight:bold;'><td>Name&nbsp;&nbsp;&nbsp;</td><td>Value</td></tr>
<tr><td>foo&nbsp;&nbsp;&nbsp;</td><td>{foo}</td></tr>
<tr><td>YourName&nbsp;&nbsp;&nbsp;</td><td>{YourName}</td></tr>
<tr><td>Weapon&nbsp;&nbsp;&nbsp;</td><td>{Weapon}</td></tr>
<tr><td>WarCry&nbsp;&nbsp;&nbsp;</td><td>{WarCry}</td></tr>
<tr><td>CA&nbsp;&nbsp;&nbsp;</td><td>{CA}&nbsp;&nbsp;&nbsp;</td></tr>
<tr><td>props&nbsp;&nbsp;&nbsp;</td><td>{props}&nbsp;&nbsp;&nbsp;</td></tr>
<tr><td>UsePower&nbsp;&nbsp;&nbsp;</td><td>{UsePower}&nbsp;&nbsp;&nbsp;</td></tr>
<tr><td>Weight&nbsp;&nbsp;&nbsp;</td><td>{Weight}&nbsp;&nbsp;&nbsp;</td></tr>
<tr><td>Ambition&nbsp;&nbsp;&nbsp;</td><td>{Ambition}&nbsp;&nbsp;&nbsp;</td></tr>
<tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td></tr>
<tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td></tr>
{if (setPropVars, 
  "<tr><td>Name&nbsp;&nbsp;&nbsp;</td><td>"+Name+"&nbsp;&nbsp;&nbsp;</td></tr>
  <tr><td>Damage&nbsp;&nbsp;&nbsp;</td><td>"+Damage+"&nbsp;&nbsp;&nbsp;</td></tr>
  <tr><td>Crit&nbsp;&nbsp;&nbsp;</td><td>"+Crit+"&nbsp;&nbsp;&nbsp;</td></tr>
  <tr><td>Keyword&nbsp;&nbsp;&nbsp;</td><td>"+Keyword+"&nbsp;&nbsp;&nbsp;</td></tr>",
  "")}
  </td></tr>
</table>
New props = [props]
	
*/

