package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.client.functions.AbortFunction.AbortFunctionException;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.Function;
import net.rptools.parser.function.ParameterException;
import net.sf.json.JSONArray;

public class UserDefinedMacroFunctions implements Function {

	private Map<String, String> userDefinedFunctions = new HashMap<String, String>();
	
	private static UserDefinedMacroFunctions instance = new UserDefinedMacroFunctions();

	private static String ON_LOAD_CAMPAIGN_CALLBACK = "onCampaignLoad";
	
	
	public static UserDefinedMacroFunctions getInstance() {
		return instance;
	}
	
	private UserDefinedMacroFunctions() {
		// Add these values for defining functions (also there has to be some functions returned by getAlises)
		//userDefinedFunctions.put("defineFunction", null);
		//userDefinedFunctions.put("isFunctionDefined", null);
	}
	
	public void checkParameters(List<Object> parameters) throws ParameterException {
		// Do nothing as we do not know what we will need.
	}

	public Object evaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {

		if (functionName.equals("defineFunction")) {
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException("You do not have permission to call defineFunction()");
			}
			if (parameters.size() < 2) {
				throw new ParserException("Not enough parameters for define function.");
			}
		
			UserDefinedMacroFunctions.getInstance().defineFunction(parameters.get(0).toString(), parameters.get(1).toString());
			return parameters.get(0) + "() function defined";
		} else if (functionName.equals("isFunctionDefined")) {
			return UserDefinedMacroFunctions.getInstance().isFunctionDefined(parameters.get(0).toString()) ? 
					BigDecimal.ONE : BigDecimal.ZERO;
		} else {
			MapToolVariableResolver resolver = (MapToolVariableResolver) parser.getVariableResolver();
			MapToolVariableResolver newResolver = new MapToolVariableResolver(resolver.getTokenInContext());
			JSONArray jarr = new JSONArray();
		
			jarr.addAll(parameters);
		
			String macroArgs = jarr.size() > 0 ? jarr.toString() : "";
			String output = MapTool.getParser().runMacro(newResolver, newResolver.getTokenInContext(), 
					userDefinedFunctions.get(functionName), macroArgs);
			resolver.setVariable("macro.return", newResolver.getVariable("macro.return"));
			return output;
		}
	}

	public String[] getAliases() {
		String[] aliases = new String[userDefinedFunctions.keySet().size()];
		aliases = userDefinedFunctions.keySet().toArray(aliases);
		if (aliases == null) {
			return new String[0];
		} else {
			return aliases;
		}
	}

	public int getMaximumParameterCount() {
		// User defined functions could accept any number of parameters.
		return Function.UNLIMITED_PARAMETERS;
	}

	public int getMinimumParameterCount() {
		// User defined functions could accept any number of parameters.
		return 0;
	}

	public boolean isDeterministic() {
		return true;
	}
	
	public void defineFunction(String name, String macro) throws ParserException {
		userDefinedFunctions.put(name, macro);
	}
	
	public boolean isFunctionDefined(String name) {
		return userDefinedFunctions.containsKey(name);
	}
	
	public void loadCampaignLibFunctions() {
		userDefinedFunctions.clear();
		List<ZoneRenderer> zrenderers = MapTool.getFrame().getZoneRenderers();
		for (ZoneRenderer zr : zrenderers) {
			List<Token> tokenList = zr.getZone().getTokensFiltered(new Zone.Filter() {
				public boolean matchToken(Token t) {
					return t.getName().toLowerCase().startsWith("lib:");
				}});

			for (Token token : tokenList) {
				// If the token is not owned by everyone and all owners are GMs then we are in
				// its a trusted Lib:token so we can run the macro
				if (token != null) {
					if (token.isOwnedByAll()) {
						continue;
					} else {
						Set<String> gmPlayers = new HashSet<String>(); 
						for (Object o : MapTool.getPlayerList()) {
							Player p = (Player)o;
							if (p.isGM()) {
								gmPlayers.add(p.getName());
							}
						}
						for (String owner : token.getOwners())  {
							if (!gmPlayers.contains(owner)) {
								continue;
							}
						}
					}
				}
				// If we get here it is trusted so try to execute it.
				if (token.getMacro(ON_LOAD_CAMPAIGN_CALLBACK, false) != null) {
					try {
						StringBuilder line = new StringBuilder();
						MapTool.getParser().runMacro(new MapToolVariableResolver(token), token, ON_LOAD_CAMPAIGN_CALLBACK + "@" + token.getName(), "");
					} catch (AbortFunctionException afe) {
						// Do nothing
					} catch (Exception e) {
						MapTool.addLocalMessage("Error running " + ON_LOAD_CAMPAIGN_CALLBACK + " on " + token.getName() + " : " + e.getMessage());
					}
				}
			}
		}

	}

}
