package net.rptools.maptool.client;

import java.math.BigDecimal;
import java.util.List;

import javax.swing.JOptionPane;

import net.rptools.maptool.client.functions.TokenGMNameFunction;
import net.rptools.maptool.client.functions.TokenHaloFunction;
import net.rptools.maptool.client.functions.TokenLabelFunction;
import net.rptools.maptool.client.functions.TokenNameFunction;
import net.rptools.maptool.client.functions.TokenStateFunction;
import net.rptools.maptool.client.functions.TokenVisibleFunction;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.TokenProperty;
import net.rptools.maptool.model.Zone;
import net.rptools.parser.MapVariableResolver;
import net.rptools.parser.ParserException;
import net.rptools.parser.VariableModifiers;

public class MapToolVariableResolver extends MapVariableResolver {

    /** The prefix for querying and setting state values . */
    public final static String          STATE_PREFIX  = "state.";
    
    /** The variable name for querying and setting token halos. */
    public final static String          TOKEN_HALO    = "token.halo";
    
    /** The variable name for querying and setting token name */
    private final static String			TOKEN_NAME 	  = "token.name";
	
    /** The variable name for querying and setting token the gm name */
    private final static String			TOKEN_GMNAME  = "token.gm_name";

    /** The variable name for querying and setting token name */
    private final static String			TOKEN_LABEL   = "token.label";

    /** The variable name for querying and setting the initiative of the current token. */
    public final static String          TOKEN_INITIATIVE    = "token.init";
    
    /** The variable name for querying and setting token visible state */
    private final static String			TOKEN_VISIBLE   = "token.visible";
    
    
    
    
    private Token tokenInContext;

    public MapToolVariableResolver(Token tokenInContext) {
    	this.tokenInContext = tokenInContext;
    }

	@Override
	public boolean containsVariable(String name, VariableModifiers mods) {

		// If we don't have the value then we'll prompt for it
		return true;
	}

	/**
	 * Gets the token in context.
	 * @return the token in context
	 */
	public Token getTokenInContext() {
		return tokenInContext;
	}
	
	@Override
	public Object getVariable(String name, VariableModifiers mods) throws ParserException {

		Object result = null;
		if (tokenInContext != null) {
			
			if (name.startsWith(STATE_PREFIX)) {
                String stateName = name.substring(STATE_PREFIX.length());
                return TokenStateFunction.getInstance().getState(tokenInContext, stateName);
            } else if (name.equals(TOKEN_HALO)) {
            	// We don't want this evaluated as the # format is more useful to us then the evaluated format.
            	return TokenHaloFunction.getInstance().getHalo(tokenInContext).toString();
            } else if (name.equals(TOKEN_NAME)) {
            	// Don't evaluate return value.
            	return TokenNameFunction.getInstance().getName(tokenInContext);
            } else if (name.equals(TOKEN_GMNAME)) {
            	// Don't evaluate return value.
            	return TokenGMNameFunction.getInstance().getGMName(tokenInContext);
            } else if (name.equals(TOKEN_LABEL)) {
            	// Don't evaluate return value.
            	return TokenLabelFunction.getInstance().getLabel(tokenInContext);
            } else if (name.equals(TOKEN_VISIBLE)) {
            	// Don't evaluate return value.
            	return TokenVisibleFunction.getInstance().getVisible(tokenInContext);
            } else if (name.equals(TOKEN_INITIATIVE)) {
                Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
                List<Integer> list = zone.getInitiativeList().indexOf(tokenInContext);
                if (list.isEmpty()) 
                    throw new ParserException("The token is not in the initiative list so no value can be set");                
                return zone.getInitiativeList().getTokenInitiative(list.get(0).intValue()).getState();
            } // endif
	
			
			if (tokenInContext.getPropertyNames().contains(name)) {
				
				result = tokenInContext.getEvaluatedProperty(name);
			}
		}
		
		// Default
		if (result == null) {
			result = super.getVariable(name, mods);
		}

		// Prompt
		if (result == null || mods == VariableModifiers.Prompt) {
			String DialogTitle = "Input Value";
			if(tokenInContext != null && tokenInContext.getGMName() != null && MapTool.getPlayer().isGM()) {
				DialogTitle = DialogTitle + " for " + tokenInContext.getGMName();
			}
			if(tokenInContext != null && (tokenInContext.getGMName() == null || !MapTool.getPlayer().isGM())) {
				DialogTitle = DialogTitle + " for " + tokenInContext.getName();
			}
			result = JOptionPane.showInputDialog(MapTool.getFrame(), "Value for: " + name, DialogTitle, JOptionPane.QUESTION_MESSAGE, null, null, result != null ? result.toString() : "0");
		}
		if (result == null) {
			throw new ParserException("Unresolved value '" + name + "'");
		}

		Object value = MapTool.getParser().parseLine(tokenInContext, result.toString()); 
		// Attempt to convert to a number ...
		try {
			value = new BigDecimal((String)value);
		} catch (Exception e) {
			// Ignore, use previous value of "value"
		}
		
		return value;
	}
	
	@Override
	public void setVariable(String varname, VariableModifiers modifiers, Object value) throws ParserException {

		if (tokenInContext != null) {
            if (validTokenProperty(varname, tokenInContext)) {
            	tokenInContext.setProperty(varname, value.toString());
        		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(),
                		tokenInContext);
                return;
            }
        }
		
        // Check to see if it is a token state.
        if (varname.startsWith(STATE_PREFIX)) {
            String stateName = varname.substring(STATE_PREFIX.length());
            TokenStateFunction.getInstance().setState(tokenInContext, stateName, value);
            return;
        } else if (varname.equals(TOKEN_HALO)) {
        	TokenHaloFunction.getInstance().setHalo(tokenInContext, value);
        	return;
        } else if (varname.equals(TOKEN_NAME)) {
        	TokenNameFunction.getInstance().setName(tokenInContext, value.toString());
        	return;
        } else if (varname.equals(TOKEN_GMNAME)) {
        	TokenGMNameFunction.getInstance().setGMName(tokenInContext, value.toString());
        	return;
        } else if (varname.equals(TOKEN_LABEL)) {
        	TokenLabelFunction.getInstance().setLabel(tokenInContext, value.toString());
        	return;
        } else if (varname.endsWith(TOKEN_VISIBLE)) {
        	TokenVisibleFunction.getInstance().setVisible(tokenInContext, value.toString());
        	return;
        } else if (varname.equals(TOKEN_INITIATIVE)) {
            Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
            List<Integer> list = zone.getInitiativeList().indexOf(tokenInContext);
            if (list.isEmpty()) 
                throw new ParserException("The token is not in the initiative list so no value can be set");
            if (value != null && !(value instanceof String)) value = value.toString();
            for (Integer index : list) {
                zone.getInitiativeList().getTokenInitiative(index).setState((String)value);
            } // endfor
            
            // TODO: This works for now but could result in a lot of resending of data
            MapTool.serverCommand().putToken(zone.getId(), tokenInContext);
        }
		super.setVariable(varname, modifiers, value);
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
    /**
     * Sets the value of all token states.
     * @param token The token to set the state of.
     * @param val set or unset the state.
     */
    private void setAllBooleanTokenStates(Token token, Object value) {
    	for (Object stateName : MapTool.getCampaign().getTokenStatesMap().keySet()) {
    		setBooleanTokenState(token, stateName.toString(), value);
    	}
    }
    
}
