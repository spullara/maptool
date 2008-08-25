package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.InitiativeList;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Token.Type;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

/**
 * Add a set of tokens to initiative
 * 
 * @author Jay
 */
public class AddAllToInitiativeFunction extends AbstractFunction {

    /** Handle adding one, all, all PCs or all NPC tokens. */
	private AddAllToInitiativeFunction() {
		super(0, 1, "addAllToInitiative", "addAllPCsToInitiative", "addAllNPCsToInitiative");
	}
	
    /** singleton instance of this function */
    private final static AddAllToInitiativeFunction instance = new AddAllToInitiativeFunction();

    /** @return singleton instance */
	public static AddAllToInitiativeFunction getInstance() { return instance; }	
	
	/**
	 * @see net.rptools.parser.function.AbstractFunction#childEvaluate(net.rptools.parser.Parser, java.lang.String, java.util.List)
	 */
	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args) throws ParserException {
	    if (!MapTool.getFrame().getInitiativePanel().hasGMPermission())
	        throw new ParserException("Only the GM has the permission to execute the " + functionName + " function.");

	    // Check for duplicates flag
        InitiativeList list = MapTool.getFrame().getCurrentZoneRenderer().getZone().getInitiativeList();
	    boolean allowDuplicates = false;
        if (!args.isEmpty()) { 
            allowDuplicates = TokenInitFunction.getBooleanValue(args.get(0));
            args.remove(0);
        } // endif
	    
	    // Handle the All functions
        List<Token> tokens = new ArrayList<Token>();
        boolean all = functionName.equals("addAllToInitiative");
        boolean pcs = functionName.equals("addAllPCsToInitiative");
        for (Token token : list.getZone().getTokens())
            if ((all || token.getType() == Type.PC && pcs || token.getType() == Type.NPC && !pcs) 
                    && (allowDuplicates || list.indexOf(token).isEmpty())) {
                tokens.add(token);
            } // endif
        list.insertTokens(tokens);
        return new BigDecimal(tokens.size());
	}
}
