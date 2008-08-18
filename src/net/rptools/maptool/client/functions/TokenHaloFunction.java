package net.rptools.maptool.client.functions;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class TokenHaloFunction extends AbstractFunction {

	
	
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
            { "White", Color.white, Color.black }    
    };

    private final static TokenHaloFunction instance = new TokenHaloFunction();
    
    
	private TokenHaloFunction() {
		super(0, 2, "getHalo", "setHalo");
	}
	
	/**
	 * Gets the singleton Halo instance.
	 * @return the Halo instance.
	 */
	public static TokenHaloFunction getInstance() {
		return instance;
	}
	
	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args)
			throws ParserException {
		
		if (functionName.equals("getHalo")) {
			return getHalo(parser, args);
		} else {
			return setHalo(parser, args);
		}

	}

	/**
	 * Gets the halo for the token.
 	 * @param token the token to get the halo for.
	 * @return the halo.
	 */
	public Object getHalo(Token token) {
		if (token.getHaloColor() != null) {
			return "#" + Integer.toHexString(token.getHaloColor().getRGB()).substring(2);
		} else {
			return "None";
		}
	}
	
	/**
	 * Sets the halo color of the token.
	 * @param token the token to set halo of.
	 * @param value the value to set.
	 * @throws ParserException if there is an error determining color.
	 */
	public void setHalo(Token token, Object value) throws ParserException {
		
		if (value instanceof Color) {
        	token.setHaloColor((Color) value);
        } else if (value instanceof BigDecimal) {
        	token.setHaloColor(new Color(((BigDecimal)value).intValue()));
        } else {
       
            String col = value.toString();
            if (col.equals("None")) {
            	token.setHaloColor(null);
            } else if (col.startsWith("#") || col.startsWith("0x")) { // Its a hexadecimal color representation
            	String hex;
            	if (col.startsWith("#")) {
            		hex = col.substring(1);
            	} else {
            		hex = col.substring(2);
            	}
            	if (hex.length() == 3) {
                    try {
                        Color color = new Color(Integer.parseInt(hex.substring(0, 1), 16) * 15, 
                        						Integer.parseInt(hex.substring(1, 2), 16) * 15, 
                        						Integer.parseInt(hex.substring(2, 3), 16) * 15);
                        token.setHaloColor(color);
                    } catch (NumberFormatException e) {
                        throw new ParserException("Invalid Color (" + col + ")");
                    }            		
            	} else if (hex.length() == 6) {
                    try {
                        Color color = new Color(Integer.parseInt(hex.substring(0, 2), 16), 
                        						Integer.parseInt(hex.substring(2, 4), 16), 
                        						Integer.parseInt(hex.substring(4, 6), 16));
                        token.setHaloColor(color);
                    } catch (NumberFormatException e) {
                        throw new ParserException("Invalid Color (" + col + ")");
                    }            		
            	} else if (hex.length() == 8) {
            		try {
                        Color color = new Color(Integer.parseInt(hex.substring(2, 4), 16), 
                        						Integer.parseInt(hex.substring(4, 6), 16), 
                        						Integer.parseInt(hex.substring(6, 8), 16));
                        token.setHaloColor(color);
                    } catch (NumberFormatException e) {
                        throw new ParserException("Invalid Color (" + col + ")");
                    }
            	} else {
            		throw new ParserException("Invalid Color (" + col + ")");
            	}
            } else {
                // Try to find the halo color in the array
            	boolean found = false;
            	String cname = (String) value;
                for (Object[] colval : COLOR_ARRAY) {
                    if (cname.equalsIgnoreCase((String)	colval[0])) {
                    	token.setHaloColor((Color) colval[1]);
                    	found = true;
                    	break;
                    }
                }
                if (!found) {
            		throw new ParserException("Invalid Color (" + col + ")");
                }
            }
        }
        // TODO: This works for now but could result in a lot of resending of data
        MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);		
	}
	
	/**
	 * Gets the halo of the token.
	 * @param parser The parser that called the object.
	 * @param args The arguments.
	 * @return the halo color.
	 * @throws ParserException if an error occurs.
	 */
	private Object getHalo(Parser parser, List<Object> args) throws ParserException {
		Token token;
		
		if (args.size() > 0 && args.get(0) instanceof GUID) {
			token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken((GUID)args.get(0));
		} else if (args.size() > 0) {
			throw new ParserException("Usage: getHalo() or getHalo(target)");
		} else {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
		}
		
		return getHalo(token);

	}
	
	/**
	 * Sets the halo of the token.
	 * @param parser The parser that called the object.
	 * @param args The arguments.
	 * @return the halo color.
	 * @throws ParserException if an error occurs.
	 */	
	private Object setHalo(Parser parser, List<Object> args) throws ParserException {
	
		Token token;
		Object value;
		
		if (args.size() > 1 && args.get(0) instanceof GUID) {
			token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken((GUID)args.get(0));
			value = args.get(1);
		} else if (args.size() > 1) {
			throw new ParserException("Usage: setHalo(color) or setHalo(target, color)");
		} else {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
			value = args.get(0);
		}
		setHalo(token, value);
        return value;


	}
}
