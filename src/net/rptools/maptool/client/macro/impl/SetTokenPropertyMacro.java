package net.rptools.maptool.client.macro.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroContext;
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
	private static final Pattern COMMAND_PATTERN = Pattern.compile("\"?([ \\w]+)?\"?\\s*\\[([^\\]]+)\\]");
	
    
	/** 
	 * Execute the command.
	 * @param args The arguments to the command.
	 */
	public void execute(MacroContext context, String args) {
		
	       		
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
				tokenName = cMatcher.group(1).trim().replaceAll("\"", "");
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
	
		
		for (Token token : selectedTokenSet) {

			// Impersonate the token so we have access to its properties in the rolls
			String oldIdentity = MapTool.getFrame().getCommandPanel().getIdentity();
			MapTool.getFrame().getCommandPanel().setIdentity(token.getName());

			for (String command : commands) {
				try {
					MapTool.getParser().parseExpression(command);
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
			// TODO: This is currently done as part of the MapToolVariableResolver. I know this is bad
			//       as it is an implementation issue of MapToolVariableResolver that we should know nothing
			//       about or depend on here but at the moment it can't be helped.
			/*MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(),
					token); // update so others see the changes.
	 */
        	
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
			if (!MapTool.getPlayer().isGM()) {
				if ((!zone.isTokenVisible(token) || token.getLayer() == Zone.Layer.GM)) {
				token = null;
			}
			if (!token.isOwner(MapTool.getPlayer().getName())) {
				token = null;
			}
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
