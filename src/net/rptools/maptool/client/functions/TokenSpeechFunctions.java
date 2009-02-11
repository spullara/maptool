package net.rptools.maptool.client.functions;

import java.util.List;

import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.model.Token;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.json.JSONArray;

public class TokenSpeechFunctions extends AbstractFunction {
	

	private static final TokenSpeechFunctions instance = new TokenSpeechFunctions();

	private TokenSpeechFunctions() {
		super(0, 2, "getSpeech", "setSpeech", "getSpeechNames");
	}

	
	public static TokenSpeechFunctions getInstance() {
		return instance;
	}


	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		final Token token = ((MapToolVariableResolver)parser.getVariableResolver()).getTokenInContext();
		if (token == null) {
			throw new ParserException(functionName + "(): No impersonated token.");
		}
		
		if (functionName.equals("getSpeech")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough arguments for getSpeech(name)");
			}
			String speech = token.getSpeech(parameters.get(0).toString());
			return speech == null ? "" : speech;
		}
		
		if (functionName.equals("setSpeech")) {
			if (parameters.size() < 2) {
				throw new ParserException("Not enough arguments for setSpeech(name, value)");
			}
			token.setSpeech(parameters.get(0).toString(), parameters.get(1).toString());
			return "";
		}
		
		
		if (functionName.equals("getSpeechNames")) {
			String[] speech = new String[token.getSpeechNames().size()];
			String delim = parameters.size() > 0 ? parameters.get(0).toString() : ",";
			if ("json".equals(delim)) {
				return JSONArray.fromObject(token.getSpeechNames()).toString();
			} else {
				return StringFunctions.getInstance().join(token.getSpeechNames().toArray(speech), delim);
			}
		}
		return null;
	}

}