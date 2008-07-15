package net.rptools.maptool.client;

import java.awt.Color;

import javax.swing.JOptionPane;

import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.TokenProperty;
import net.rptools.parser.MapVariableResolver;
import net.rptools.parser.ParserException;
import net.rptools.parser.VariableModifiers;

public class MapToolVariableResolver extends MapVariableResolver {

    /** The prefix for querying and setting state values . */
    public final static String          STATE_PREFIX  = "state.";

    /** The variable name for querying and setting token halos. */
    public final static String          TOKEN_HALO    = "token.halo";

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
	
	@Override
	public boolean containsVariable(String name, VariableModifiers mods) {

		// If we don't have the value then we'll prompt for it
		return true;
	}

	@Override
	public Object getVariable(String name, VariableModifiers mods) throws ParserException {

		Object result = null;
		Token token = getTokenInContext();
		if (token != null) {

			if (name.startsWith(STATE_PREFIX)) {
                String stateName = name.substring(STATE_PREFIX.length());
                
                if (MapTool.getCampaign().getTokenStatesMap().containsKey(stateName)) {
                    result =  getBooleanTokenState(token, stateName) ? Integer.valueOf(1) : Integer.valueOf(0);
                }
            }
	
			
			if (token.getPropertyNames().contains(name)) {
				
				result = token.getProperty(name);
			}
		}
		
		// Default
		if (result == null) {
			result = super.getVariable(name, mods);
		}

		// Prompt
		if (result == null || mods == VariableModifiers.Prompt) {
			result = JOptionPane.showInputDialog(MapTool.getFrame(), "Value for: " + name, "Input Value for " + token.getName() + "/" + token.getGMName(), JOptionPane.QUESTION_MESSAGE, null, null, result != null ? result.toString() : "0");
		}

		return MapTool.parse(result.toString()).getValue();
	}
	
	@Override
	public void setVariable(String varname, VariableModifiers modifiers, Object value) throws ParserException {
        Token token = getTokenInContext();
        if (token != null) {
            if (validTokenProperty(varname, token)) {
                token.setProperty(varname, value.toString());
                return;
            }
        }
        // Check to see if it is a token state.
        if (varname.startsWith(STATE_PREFIX)) {
            String stateName = varname.substring(STATE_PREFIX.length());
            if (MapTool.getCampaign().getTokenStatesMap().containsKey(stateName)) {
                setBooleanTokenState(token, stateName, value);
                // TODO: This works for now but could result in a lot of resending of data
                MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(),
    					token);
                return;
            }
        } else if (varname.equals(TOKEN_HALO)) {
            if (value instanceof Color) {
                token.setHaloColor((Color) value);
                // TODO: This works for now but could result in a lot of resending of data
                MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(),
    					token);
            } else {
                String col = value.toString();
                if (col.equals("None")) {
                    token.setHaloColor(null);
                } else if (col.startsWith("#")) {
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
                            // TODO: This works for now but could result in a lot of resending of data
                            MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(),
                					token);

                            return;
                        }
                    }
                    throw new ParserException("Invalid Halo Color (" + col + ")");
                }
                // TODO: This works for now but could result in a lot of resending of data
                MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(),
    					token);
                return;
            }
        }
		super.setVariable(varname, modifiers, value);
	}

	private Token getTokenInContext() {
		
		ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
		if (renderer == null) {
			return null;
		}
		
		return renderer.getZone().resolveToken(MapTool.getFrame().getCommandPanel().getIdentity());
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
     */
    private boolean validTokenProperty(String prop, Token token) {
        for (TokenProperty tp : MapTool.getCampaign().getTokenPropertyList(token.getPropertyType())) {
            if (tp.getName().equals(prop)) {
                return true;
            }
        }

        return false;
    }

}
