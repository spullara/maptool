package net.rptools.maptool.client.macro.impl;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.common.expression.ExpressionParser;
import net.rptools.common.expression.Result;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.client.SetPropertyVariableResolver;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.parser.ParserException;

@MacroDefinition(name = "settokenproperty", 
        aliases = { "stp" }, 
        description = "Set the property of a token",
        expandRolls = false
)

/**
 * Class that implements the settokenproperty command.
 */
public class SetTokenPropertyMacro implements Macro {

	/** The pattern used to match the token name and commands. */
	private static final Pattern COMMAND_PATTERN = Pattern.compile("(\\w+)?\\s*\\[([^\\]]+)\\]");
	
	/** The maximum recursion depth for parsing. */
    private static final int PARSER_MAX_RECURSE = 50;
    
    /** The current recursion depth. */
    private static int parserRecurseDepth;
    
    /** The parser used to parse assignment expressions. */
	private ExpressionParser parser;

	/** The variable resolver. */
	private SetPropertyVariableResolver variableResolver;
	

	/** 
	 * Execute the command.
	 * @param args The arguments to the command.
	 */
	public void execute(String args) {
		
		if (parser == null) {
			if (variableResolver == null) {
				variableResolver = new SetPropertyVariableResolver(this);
			}
			parser = new ExpressionParser(variableResolver);
		}
	       		
		if (args.length() == 0) {
			MapTool.addLocalMessage("You must specify a token name and an expression, or select token(s) and supply an expression.");
			return;
		}



		// Extract the token name if any and the the assignment expressions.
		List<String> commands = new ArrayList<String>(); 
		String tokenName = null;
		Matcher cMatcher = COMMAND_PATTERN.matcher(args);
		while (cMatcher.find()) {
			if (tokenName == null && cMatcher.group(1) != null) {
				tokenName = cMatcher.group(1);
			}
			if (cMatcher.group(2) != null) {
				commands.add(cMatcher.group(2));
			}
		}
		
		
		// Make sure there is at least something to do
		if (commands.isEmpty()) {
			MapTool.addLocalMessage("You must specify a token name and an expression, or select token(s) and supply an expression.");
			return;
		}

		
		Set<Token> selectedTokenSet = getTokens(tokenName);

		assert selectedTokenSet != null : "Unable to locate tokens to change";
		if (selectedTokenSet.isEmpty()) {
			MapTool.addLocalMessage("Unable to determine which tokens to set the property of.");
			return;
		}
	
		if (commands.size() > 1 || selectedTokenSet.size() > 1) {
			variableResolver.setBatchMode(true);
		} else {
			variableResolver.setBatchMode(false);
		}
		
		for (Token token : selectedTokenSet) {

			// Impersonate the token so we have access to its properties in the rolls
			String oldIdentity = MapTool.getFrame().getCommandPanel().getIdentity();
			MapTool.getFrame().getCommandPanel().setIdentity(token.getName());

			for (String command : commands) {
				try {
					parse(command);
				} catch (ParserException e) {
					MapTool.addLocalMessage("Error Evaluating Expression: " + e.getMessage());
					break;
				} catch (RuntimeException re) {
					if (re.getCause() instanceof ParserException) {
						MapTool.addLocalMessage("Error Evaluating Expression: " + re.getCause().getMessage());						
					} else {
						MapTool.addLocalMessage("Error Evaluating Expression: " + re.getMessage());
					}
					break;
				}
			}
		
			MapTool.getFrame().getCommandPanel().setIdentity(oldIdentity);
			MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(),
					token); // update so others see the changes.


		}
		
		// Clear variables and cache for next run. Its important to clear the cache as properties may
		// Change before next run.
		variableResolver.clearVariables();
		variableResolver.clearCache();
		
	}
	
	/**
	 * Parse the expression that is passed in.
	 * @param expression The expression to parse.
 	 * @return The result of the execution of the parsed expression.
	 * @throws ParserException if an error occurs during parsing.
	 */
	public Result parse(String expression) throws ParserException {       
    	if (parserRecurseDepth > PARSER_MAX_RECURSE) {
    		throw new ParserException("Max recurse limit reached");
    	}
        try {
        	parserRecurseDepth ++;
        	return parser.evaluate(expression);
        } catch (RuntimeException re) {
        	
        	if (re.getCause() instanceof ParserException) {
        		throw (ParserException) re.getCause();
        	}
        	
        	throw re;
        } finally {
        	parserRecurseDepth--;
        }
    }
	
	/**
	 * Gets the token specified on command line or the selected tokens if no token is specified.
	 * @param tokenName The name of the token to try retrieve.
	 * @return The tokens.
	 * If the token in <code>tokenName</code> is empty or <code>tokenName</code> is null then
	 * the selected tokens are returned.
	 */
	protected Set<Token> getTokens(String tokenName) {
		
		Set<Token> selectedTokenSet = new HashSet<Token>();

		
		if (tokenName != null && tokenName.length() > 0) {
			Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
			Token token = zone.getTokenByName(tokenName);
			// Give the player the benefit of the doubt. If they specified a
			// token that is invisible
			// then try the name as a property. This will also stop players that
			// are trying to "guess" token names
			// and trying to change properties figuring out that there is a token
			// there because they are
			// getting a different error message (benefit of the doubt only goes
			// so far ;) )
			if (!MapTool.getPlayer().isGM()
					&& (!zone.isTokenVisible(token) || token.getLayer() == Zone.Layer.GM)) {
				token = null;
			}
			if (!token.isOwner(MapTool.getPlayer().getName())) {
				token = null;
			}
			if (token != null) {
				selectedTokenSet.add(token);
			}
			return selectedTokenSet;
		}  
		 
		
		// Use the selected tokens.
		selectedTokenSet = new HashSet<Token>();
		Set<GUID> sTokenSet = MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokenSet();
		for (GUID tokenId : sTokenSet) {
			Token tok = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
			selectedTokenSet.add(tok);
		}
		
				
		return selectedTokenSet;

	}

	
	
	
}
