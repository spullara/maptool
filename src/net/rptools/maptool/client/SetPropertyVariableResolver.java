package net.rptools.maptool.client;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import net.rptools.maptool.client.macro.impl.SetTokenPropertyMacro;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.TokenProperty;
import net.rptools.parser.ParserException;
import net.rptools.parser.VariableModifiers;
import net.rptools.parser.VariableResolver;

/**
 * This class implements the variable resolver used by the parser when setting
 * token properties.
 * 
 * @see net.rptools.maptool.client.macro.impl.SetTokenStateMacro
 * 
 */
public class SetPropertyVariableResolver implements VariableResolver {

    // internal flags
    /** The variable name to set to force the parser to re-prompt for variables. */
    public final static String          FLAG_REPROMPT = "f_reprompt";

    /** The prefix for querying and setting state values . */
    public final static String          STATE_PREFIX  = "state_";

    /** The variable name for querying and setting token halos. */
    public final static String          TOKEN_HALO    = "token_halo";

    /** Properties cached from the last <code>setVariable()</code> call. */
    private Set<String>                 cachedProperties;

    /** The tokens property type that is cached. */
    private String                      cachedPropertyType;

    /**
     * Will there be multiple updates done. Setting
     * <code>batchMode<code> to <code>true</code> 
     * causes the resolver to cache valid token property names when setting a variable in an
     * attempt to speed up subsequent setting of properties on the same token or other tokens
     * with the same property type.
     */
    private boolean                     batchMode;

    /**
     * Don't save the result of a prompt for the value of an unknown variable.
     * This will cause the parser to prompt for a value every time it sees the
     * variable.
     */
    private boolean                     reprompt      = false;

    /** {@link #SetTokenPropertyMacro} used for parsing prompted values. */
    private final SetTokenPropertyMacro inputParser;

    /** Variable storage. */
    private final Map<String, Object>   variables     = new HashMap<String, Object>();

    // TODO: This is a copy of the array in the TokenPopupMenu (which is
    // apparently temporary)
    // There should probably one place for halo colors in the future.
    /** Halo Colors */
    private static final Object[][]     COLOR_ARRAY   = new Object[][] {
            { "Black", Color.black, Color.white },
            { "Green", Color.green, Color.black },
            { "Yellow", Color.yellow, Color.black },
            { "Orange", new Color(255, 156, 0), Color.black }, // default
                                                                // orange is too
                                                                // light
            { "Red", Color.red, Color.black }, { "Blue", Color.blue, Color.black },
            { "Cyan", Color.cyan, Color.black }, { "Dark Gray", Color.darkGray, Color.black },
            { "Magenta", Color.magenta, Color.black }, { "Pink", Color.pink, Color.black },
            { "White", Color.white, Color.black }    };

    /**
     * Creates a new <code>SetPropertyVariableResolver</code> in non batch
     * mode.
     * 
     * @param stpm
     *            The {@link SetTokenPropertyMacro} suppling the
     *            <code>parse()</code> call back.
     */
    public SetPropertyVariableResolver(SetTokenPropertyMacro stpm) {
        this(stpm, false);
    }

    /**
     * Creates a new <code>SetPropertyVariableResolver</code>.
     * 
     * @param stpm
     *            The {@link SetTokenPropertyMacro} suppling the
     *            <code>parse()</code> call back.
     * @param batch
     *            <code>true</code> to create in batch mode. When the resolver
     *            is in batch mode calls to
     *            {@link #setVariable(String, VariableModifiers, Object)} and
     *            {@link #setVariable(String, Object)} will cache the properties
     *            from the token in an attempt to speed up subsequent calls to
     *            these methods.
     */
    public SetPropertyVariableResolver(SetTokenPropertyMacro stpm, boolean batch) {
        super();
        batchMode = batch;
        if (batchMode) {
            cachedProperties = new HashSet<String>();
        }
        inputParser = stpm;
    }

    /**
     * Checks to see if the specified variable can be resolved.
     * 
     * @param name
     *            The name of the variable.
     * @param mods
     *            the modifiers for the variable. Note: This method will always
     *            return <code>true</code> as the user will be prompted to
     *            specify the value of any variables that are not already set.
     * @return <code>true</code> if the variable can be resolved.
     */
    public boolean containsVariable(String name, VariableModifiers mods) {
        // If we don't have the value then we'll prompt for it
        return true;
    }

    /**
     * Gets the value of the variable.
     * 
     * @param name
     *            The name of the variable to get the value of.
     * @param mods
     *            The variable modifiers.
     * @return the value of the variable.
     * @throws ParserException
     *             when an error occurs retrieving the variables value. Notes:
     *             If the name starts with the prefix of {@link STATE_PREFIX}
     *             and the remainder of the variable is the name of a token
     *             state then <code>1</code> will be returned if the state is
     *             set or <code>0</code> if the state is unset. If the name
     *             matches a property for the token which already has a value
     *             then this value is returned. If the variable is unknown or
     *             there is not value in the property for the token then the
     *             user will be prompted to supply the value. If reprompt is
     *             <code>false</code> then when the user is prompted for a
     *             value it is saved in the variable list so that the user will
     *             not be prompted for the variable again while using the
     *             instance of the resolver.
     */
    public Object getVariable(String name, VariableModifiers mods) throws ParserException {

        Object result = null;

        Token token = getTokenInContext();
        if (token != null) {

            if (token.getPropertyNames().contains(name)) {
                result = token.getProperty(name);
            }

            if (name.startsWith(STATE_PREFIX)) {
                String stateName = name.substring(STATE_PREFIX.length());
                
                if (MapTool.getCampaign().getTokenStatesMap().containsKey(stateName)) {
                    return (getBooleanTokenState(token, stateName) ? 1 : 0);
                }
            }
        }

        // Default
        if (result == null) {
            result = variables.get(name);
        }

        // Prompt
        if (result == null || mods == VariableModifiers.Prompt) {
            String prompt;
            // If we will be prompting for every token then we want to display
            // the token name in the prompt
            if (reprompt) {
                prompt = "Value for: " + name + " (" + token.getName() + ")";
            } else {
                prompt = "Value for: " + name;
            }
            result = JOptionPane.showInputDialog(MapTool.getFrame(), prompt, "Input Value",
                    JOptionPane.QUESTION_MESSAGE, null, null, result != null ? result.toString() : "0");

            if (reprompt) {
                clearVariable(name); // Clear the variable in case someone
                                        // has unwisely set reprompt mode after
                                        // an assignment
            } else {
                setVariable(name, mods, result.toString());
            }
        }

        return inputParser.parse(result.toString()).getValue();
    }

    /**
     * Removes all the variables from the resolver.
     */
    public void clearVariables() {
        variables.clear();
    }

    /**
     * Clears the property cache.
     */
    public void clearCache() {
        if (cachedProperties != null) {
            cachedProperties.clear();
        }
        cachedPropertyType = null;
    }

    /**
     * 
     * @param mode
     *            When the resolver is in batch mode calls to
     *            {@link #setVariable(String, VariableModifiers, Object)} and
     *            {@link #setVariable(String, Object)} will cache the properties
     *            from the token in an attempt to speed up subsequent calls to
     *            these methods.
     */
    public void setBatchMode(boolean mode) {
        batchMode = mode;
        if (batchMode && cachedProperties == null) {
            cachedProperties = new HashSet<String>();
        }
    }

    /**
     * Removes the specified variable from the resolver.
     * 
     * @param name
     *            The name of the variable to remove.
     */
    private void clearVariable(String name) {
        variables.remove(name);
    }

    /**
     * Sets the value of the specified variable.
     * 
     * @param name
     *            The name of the variable to set.
     * @param mods
     *            The modifiers for the variable.
     * @param value
     *            The value to set. If the name of the variable matches a valid
     *            property for the token in context then the tokens property
     *            will be updated, if the name of the variable begins with
     *            {@link #STATE_PREFIX} and the remainder of the name matches
     *            the name of a Token state then that state will be set on the
     *            token if value is an non zero integer, the String
     *            <code>"true"</code> or boolean value <code>true</code>,
     *            other values unset the state. If the name matches any of the
     *            internal flags then these will be set accordingly.
     */
    public void setVariable(String name, VariableModifiers mods, Object value) throws ParserException {

        Token token = getTokenInContext();
        if (token != null) {
            if (validTokenProperty(name, token)) {
                token.setProperty(name, value.toString());
                return;
            }
        }

        // Check to see if it is a special property
        if (name.equals(FLAG_REPROMPT)) {
            reprompt = Boolean.parseBoolean(value.toString());
            return;
        }

        // Check to see if it is a token state.
        if (name.startsWith(STATE_PREFIX)) {
            String stateName = name.substring(STATE_PREFIX.length());
            if (MapTool.getCampaign().getTokenStatesMap().containsKey(stateName)) {
                setBooleanTokenState(token, stateName, value);
                return;
            }
        } else if (name.equals(TOKEN_HALO)) {
            if (value instanceof Color) {
                token.setHaloColor((Color) value);
            } else {
                String col = value.toString();
                if (col.equals("None")) {
                    token.setHaloColor(null);
                    return;
                }
                if (col.startsWith("#")) {
                    if (col.length() < 7) {
                        throw new ParserException("Invalid Halo Color (" + col + ")");
                    }
                    try {
                        Color color = new Color(Integer.parseInt(col.substring(1, 3), 16), Integer.parseInt(col
                                .substring(3, 5), 16), Integer.parseInt(col.substring(5, 7), 16));
                        token.setHaloColor(color);
                    } catch (NumberFormatException e) {
                        throw new ParserException("Invalid Halo Color (" + col + ")");
                    }
                } else {
                    // Try to find the halo color in the array
                    for (Object[] colval : COLOR_ARRAY) {
                        if (value.equals(colval[0])) {
                            token.setHaloColor((Color) colval[1]);
                            return;
                        }
                    }
                    throw new ParserException("Invalid Halo Color (" + col + ")");
                }
                return;
            }
        }

        // Set a parser variable
        variables.put(name, value);
    }

    /**
     * Gets the token that is currently in context.
     * 
     * @return the token that is currently in context.
     */
    private Token getTokenInContext() {
        ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
        if (renderer != null) {
            return renderer.getZone().resolveToken(MapTool.getFrame().getCommandPanel().getIdentity());
        }
        return null;
    }

    /**
     * Gets the boolean value of the tokens state.
     * 
     * @param token
     *            The token to get the state of.
     * @param stateName
     *            The name of the state to get.
     * @return the value of the state.
     */
    private boolean getBooleanTokenState(Token token, String stateName) {
        Object val = token.getState(stateName);
        if (val instanceof Integer) {
            return ((Integer) val).intValue() != 0;
        } else if (val instanceof Boolean) {
            return ((Boolean) val).booleanValue();
        } else {
            try {
                return Integer.parseInt(val.toString()) != 0;
            } catch (NumberFormatException e) {
                return Boolean.parseBoolean(val.toString());
            }

        }
    }

    /**
     * Sets the boolean state of a token.
     * 
     * @param token
     *            The token to set the state of.
     * @param stateName
     *            The state to set.
     * @param val
     *            set or unset the state.
     */
    private void setBooleanTokenState(Token token, String stateName, Object val) {
        boolean set;
        if (val instanceof Integer) {
            set = ((Integer) val).intValue() != 0;
        } else if (val instanceof Boolean) {
            set = ((Boolean) val).booleanValue();
        } else {
            try {
                set = Integer.parseInt(val.toString()) != 0;
            } catch (NumberFormatException e) {
                set = Boolean.parseBoolean(val.toString());
            }
            token.setState(stateName, set);
        }
    }

    /**
     * Checks to see if the specified property is valid for the token.
     * 
     * @param prop
     *            The name of the property to check.
     * @param token
     *            The token to check.
     * @return <code>true</code> if the property is valid for the token.
     *         Notes: If the resolver was created in batch mode then this method
     *         will call {@link #validTokenPropertyBatch(String, Token)} to
     *         check the property against the cache.
     */
    private boolean validTokenProperty(String prop, Token token) {
        if (batchMode) {
            return validTokenPropertyBatch(prop, token);
        }
        for (TokenProperty tp : MapTool.getCampaign().getTokenPropertyList(token.getPropertyType())) {
            if (tp.getName().equals(prop)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks against the cached property list to see if the specified property
     * is valid for the token.
     * 
     * @param prop
     *            The name of the property to check.
     * @param token
     *            The token to check.
     * @return <code>true</code> if the property is valid for the token.
     *         Notes: You should not call this method directly, instead call
     *         {@link #validTokenProperty(String, Token)} and it will call this
     *         method if required. Calling this method will load the cache if it
     *         is empty or the previous token checked had a different property
     *         type.
     */
    private boolean validTokenPropertyBatch(String prop, Token token) {

        if (cachedPropertyType == null || cachedPropertyType.equals(token.getPropertyType()) == false) {
            loadCache(token.getPropertyType());
        }
        if (cachedProperties.contains(prop)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Load the cache with the valid properties for the specified property type.
     * 
     * @param propertyType
     *            The property type to load the properties for.
     */
    private void loadCache(String propertyType) {
        for (TokenProperty tp : MapTool.getCampaign().getTokenPropertyList(propertyType)) {
            cachedProperties.add(tp.getName());
        }
        cachedPropertyType = propertyType;
    }

    /**
     * Checks to see if the specified variable can be resolved.
     * 
     * @param name
     *            The name of the variable. Note: This method will always return
     *            <code>true</code> as the user will be prompted to specify
     *            the value of any variables that are not already set.
     * @return <code>true</code> if the variable can be resolved.
     */
    public boolean containsVariable(String name) throws ParserException {
        return containsVariable(name, VariableModifiers.None);
    }

    /**
     * Gets the value of the variable.
     * 
     * @param name
     *            The name of the variable to get the value of.
     * @return the value of the variable.
     * @throws ParserException
     *             when an error occurs retrieving the variables value. Notes:
     *             If the name starts with the prefix of {@link STATE_PREFIX}
     *             and the remainder of the variable is the name of a token
     *             state then <code>1</code> will be returned if the state is
     *             set or <code>0</code> if the state is unset. If the name
     *             matches a property for the token which already has a value
     *             then this value is returned. If the variable is unknown or
     *             there is not value in the property for the token then the
     *             user will be prompted to supply the value. If reprompt is
     *             <code>false</code> then when the user is prompted for a
     *             value it is saved in the variable list so that the user will
     *             not be prompted for the variable again while using the
     *             instance of the resolver.
     */
    public Object getVariable(String variableName) throws ParserException {
        return getVariable(variableName, VariableModifiers.None);
    }

    /**
     * Sets the value of the specified variable.
     * 
     * @param name
     *            The name of the variable to set.
     * @param mods
     *            The modifiers for the variable.
     * @param value
     *            The value to set. If the name of the variable matches a valid
     *            property for the token in context then the tokens property
     *            will be updated, if the name of the variable begins with
     *            {@link #STATE_PREFIX} and the remainder of the name matches
     *            the name of a Token state then that state will be set on the
     *            token if value is an non zero integer, the String
     *            <code>"true"</code> or boolean value <code>true</code>,
     *            other values unset the state. If the name matches any of the
     *            internal flags then these will be set accordingly.
     */
    public void setVariable(String name, Object value) throws ParserException {
        setVariable(name, VariableModifiers.None, value);
    }

}
