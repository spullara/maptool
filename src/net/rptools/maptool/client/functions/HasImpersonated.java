package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Zone;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class HasImpersonated extends AbstractFunction {


	private static final HasImpersonated instance = new HasImpersonated();

	private HasImpersonated() {
		super(0, 0, "hasImpersonated");
	}

	
	public static HasImpersonated getInstance() {
		return instance;
	}


	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		
		Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
		String identity = MapTool.getFrame().getCommandPanel().getIdentity();
		return zone.resolveToken(identity) == null ? BigDecimal.ZERO : BigDecimal.ONE;
	}

}
