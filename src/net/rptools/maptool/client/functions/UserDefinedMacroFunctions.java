/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *  
 *	http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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

	private Map<String, FunctionDefinition> userDefinedFunctions = new HashMap<String, FunctionDefinition>();
	private Map<String, FunctionRedefinition> redefinedFunctions = new HashMap<String, FunctionRedefinition>();
	private Stack<String> currentFunction = new Stack<String>();
	
	

	private static UserDefinedMacroFunctions instance = new UserDefinedMacroFunctions();

	private static String ON_LOAD_CAMPAIGN_CALLBACK = "onCampaignLoad";

	private static int nameCounter = 0;

	
	
	private static int getNameCounter() {
		return nameCounter++;
	}
	
	
	private static class FunctionDefinition {
		
		public FunctionDefinition(String macroName, boolean ignoreOutput, boolean newVariableContext) {
			this.macroName = macroName;
			this.ignoreOutput = ignoreOutput;
			this.newVariableContext = newVariableContext;
		}
		
		String macroName;
		boolean ignoreOutput;
		boolean newVariableContext;
	}
	
	private static class FunctionRedefinition { 
		String functionName;
		Function function;
	}
	
	public static UserDefinedMacroFunctions getInstance() {
		return instance;
	}

	private UserDefinedMacroFunctions() {
	}

	public void checkParameters(List<Object> parameters)
			throws ParameterException {
		// Do nothing as we do not know what we will need.
	}

	public Object evaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		MapToolVariableResolver resolver = (MapToolVariableResolver) parser
				.getVariableResolver();
		MapToolVariableResolver newResolver;
		JSONArray jarr = new JSONArray();

		jarr.addAll(parameters);
		String macroArgs = jarr.size() > 0 ? jarr.toString() : "";
		String output;
		FunctionDefinition funcDef = userDefinedFunctions.get(functionName);
		
		if (funcDef.newVariableContext) {
			 newResolver = new MapToolVariableResolver(
						resolver.getTokenInContext());
		} else {
			newResolver = resolver;
		}
		
		try {
			currentFunction.push(functionName);
			output = MapTool.getParser().runMacro(resolver,
					newResolver.getTokenInContext(),
					funcDef.macroName, macroArgs, funcDef.newVariableContext);
		} finally {
			currentFunction.pop();
		}
		//resolver.setVariable("macro.return", newResolver
		//		.getVariable("macro.return"));
		
		if (funcDef.ignoreOutput) {
			return resolver.getVariable("macro.return");
		}
		
		String stripOutput = output.replaceAll("(?s)<!--.*?-->", ""); // Strip comments
		if (stripOutput.trim().length() == 0) {
			output = resolver.getVariable("macro.return").toString();
			stripOutput=output;
		}
		Object out = JSONMacroFunctions.convertToJSON(stripOutput);
		if (out != null) {
			return out;
		}

		try {
			return new BigDecimal(stripOutput.trim());
		} catch (Exception e) {
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

	public void defineFunction(Parser parser, String name, String macro, boolean ignoreOutput, boolean newVariableContext)
			throws ParserException {
		if (parser.getFunction(name) != null) {
			FunctionRedefinition fr = new FunctionRedefinition();
			fr.function = parser.getFunction(name);
			fr.functionName = name;
			if (isFunctionDefined(name)) {
				
				// If it is already defined as what this then do nothing...
				if (userDefinedFunctions.get(name).equals(macro)) {
					return;
				}
				
				// We have to rename the old function
				fr.functionName = "redefined_" + getNameCounter() + "_" + name;
				redefinedFunctions.put(fr.functionName, redefinedFunctions.get(name));
				redefinedFunctions.put(name, fr);
				userDefinedFunctions.put(fr.functionName, userDefinedFunctions.get(name));
			}	
			redefinedFunctions.put(name, fr);
		}
		
		userDefinedFunctions.put(name, new FunctionDefinition(macro, ignoreOutput, newVariableContext));
	}

	
	public Object executeOldFunction(Parser parser,  List<Object> parameters) throws ParserException {
		FunctionRedefinition functionRedef = redefinedFunctions.get(currentFunction.peek());
		if (functionRedef == null) {
			throw new ParserException("Old definition for function " + currentFunction.peek() + "does not exist");
		}
		
		Function function = functionRedef.function;
		function.checkParameters(parameters);
		
		return function.evaluate(parser, functionRedef.functionName, parameters);
		
	}
	
	public boolean isFunctionDefined(String name) {
		return userDefinedFunctions.containsKey(name);
	}

	public void loadCampaignLibFunctions() {
		userDefinedFunctions.clear();
		
		List<ZoneRenderer> zrenderers = MapTool.getFrame().getZoneRenderers();
		for (ZoneRenderer zr : zrenderers) {
			List<Token> tokenList = zr.getZone().getTokensFiltered(
					new Zone.Filter() {
						public boolean matchToken(Token t) {
							return t.getName().toLowerCase().startsWith("lib:");
						}
					});

			for (Token token : tokenList) {
				// If the token is not owned by everyone and all owners are GMs
				// then we are in
				// its a trusted Lib:token so we can run the macro
				if (token != null) {
					if (token.isOwnedByAll()) {
						continue;
					} else {
						Set<String> gmPlayers = new HashSet<String>();
						for (Object o : MapTool.getPlayerList()) {
							Player p = (Player) o;
							if (p.isGM()) {
								gmPlayers.add(p.getName());
							}
						}
						for (String owner : token.getOwners()) {
							if (!gmPlayers.contains(owner)) {
								continue;
							}
						}
					}
				}
				// If we get here it is trusted so try to execute it.
				if (token.getMacro(ON_LOAD_CAMPAIGN_CALLBACK, false) != null) {
					try {
						MapTool.getParser().runMacro(
								new MapToolVariableResolver(token),
								token,
								ON_LOAD_CAMPAIGN_CALLBACK + "@"
										+ token.getName(), "");
					} catch (AbortFunctionException afe) {
						// Do nothing
					} catch (Exception e) {
						MapTool.addLocalMessage("Error running "
								+ ON_LOAD_CAMPAIGN_CALLBACK + " on "
								+ token.getName() + " : " + e.getMessage());
					}
				}
			}
		}

	}

}
