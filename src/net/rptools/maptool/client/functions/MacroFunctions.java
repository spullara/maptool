package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.model.MacroButtonProperties;
import net.rptools.maptool.model.Token;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MacroFunctions extends AbstractFunction {

	private static final MacroFunctions instance = new MacroFunctions();

	private MacroFunctions() {
		super(0, 4, "hasMacro", "createMacro", "setMacroProps", "getMacros", "getMacroProps", "getMacroIndexes",
				     "getMacroName", "getMacroLocation", "setMacroCommand", "getMacroCommand", "getMacroButtonIndex");
	}
	
	
	public static MacroFunctions getInstance() {
		return instance;
	}


	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		Token token = ((MapToolVariableResolver)parser.getVariableResolver()).getTokenInContext();
				
		if (functionName.equals("hasMacro")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough arguments for getMacro(name)");				
			}
			if (token.getMacroNames(false).contains(parameters.get(0).toString())) {
				return true; 
			} else {
				return false;
			}
 		} else if (functionName.equals("createMacro")){
 			if (parameters.size() < 2) {
 				throw new ParserException("Not enough arguments for createMacro(name, command)");
 			}
 			MacroButtonProperties mbp = new MacroButtonProperties(token.getMacroNextIndex());
 			mbp.setCommand(parameters.get(1).toString());
 			String delim = parameters.size() > 3 ? parameters.get(3).toString() : ";";
 			setMacroProps(mbp, parameters.get(2).toString(), delim);
 			mbp.setLabel(parameters.get(0).toString());
 			mbp.setSaveLocation("Token");
 			mbp.setTokenId(token);
 			mbp.setApplyToTokens(true);
 			mbp.save();
    		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(),
            		token);
 			return mbp.getIndex();
 		} else if (functionName.equals("getMacros")) {
 			String[] names = new String[token.getMacroNames(false).size()];
 			String delim = parameters.size() > 0 ? parameters.get(0).toString() : ",";
 			if ("json".equals(delim)) {
 				return JSONArray.fromObject(token.getMacroNames(false).toArray(names)).toString();
 			} else {
 				return StringFunctions.getInstance().join(token.getMacroNames(false).toArray(names), delim);
 			}
 		} else if (functionName.equals("getMacroProps")) {
 			if (parameters.size() < 1) {
 				throw new ParserException("Not enough arguments to getMacroProp(index)");
 			} 
 			
 			if (!(parameters.get(0) instanceof BigDecimal)) {
 				throw new ParserException("Argument to getMacroProps(index) must be a number");
 			}
 			String delim = parameters.size() > 1 ? parameters.get(1).toString() : ";";
 			return getMacroButtonProps(token, ((BigDecimal)parameters.get(0)).intValue(), delim);
 		} else if (functionName.equals("setMacroProps")) {
 			if (parameters.size() < 2) {
 				throw new ParserException("Not enough arguments to setMacroProps(index)");
 			} 
 			
 			if ((parameters.get(0) instanceof BigDecimal)) {
 				int index = ((BigDecimal)parameters.get(0)).intValue();
 	 			
 	 			MacroButtonProperties mbp = token.getMacro(index, false); 			
 	 			String delim = parameters.size() > 2 ? parameters.get(2).toString() : ";";
 	 			setMacroProps(mbp, parameters.get(1).toString(), delim);
 	 			mbp.save();
	 		} else {
	 			for (MacroButtonProperties mbp : token.getMacroList(false)) {
	 	 			String delim = parameters.size() > 2 ? parameters.get(2).toString() : ";";
	 				if (mbp.getLabel().equals(parameters.get(0).toString())) {
	 	 	 			setMacroProps(mbp, parameters.get(1).toString(), delim);
	 	 	 			mbp.save();	 					
	 				}
	 			}
	 		}
 			
    		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(),
            		token);
    		return ""; 			
 		} else if (functionName.equals("getMacroIndexes")) {
 			if (parameters.size() < 1) {
 				throw new ParserException("Not enough arguments to getMacroIndexes(name)");
 			} 
 			String label = parameters.get(0).toString();
 			String delim = parameters.size() > 1 ? parameters.get(1).toString() : ",";

 			List<String> indexes = new ArrayList<String>();
 			for (MacroButtonProperties mbp : token.getMacroList(false)) {
 				if (mbp.getLabel().equals(label)) {
 					indexes.add(Integer.toString(mbp.getIndex()));
 				}
 			}
 			if ("json".equals(delim)) {
 				return JSONArray.fromObject(indexes).toString();
 			} else {
 				return StringFunctions.getInstance().join(indexes, delim);
 			}
 		} else if (functionName.equals("getMacroName")) {
 			return MapTool.getParser().getMacroName();
 		} else if (functionName.equals("getMacroLocation")) {
 			return MapTool.getParser().getMacroSource();
 		} else if (functionName.equals("setMacroCommand")) { 
			if (parameters.size() < 2) {
 				throw new ParserException("Not enough arguments to setMacroCommand(index, command)");
 			} 
			
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException("You do not have permission to call the setMacroCommand() function");
			}
 
 			if (!(parameters.get(0) instanceof BigDecimal)) {
 				throw new ParserException("Argument to setMacroCommand(index, command) must be a number");
 			}
 			
 			MacroButtonProperties mbp = token.getMacro(((BigDecimal)parameters.get(0)).intValue(), false);
 			mbp.setCommand(parameters.get(1).toString());
 			mbp.save();
    		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(),
            		token);
    		return ""; 			
 		} else if (functionName.equals("getMacroCommand")) {
			if (parameters.size() < 1) {
 				throw new ParserException("Not enough arguments to getMacroCommand(index)");
 			} 
 
 			if (!(parameters.get(0) instanceof BigDecimal)) {
 				throw new ParserException("Argument to getMacroCommand(index) must be a number");
 			}
 			
 			MacroButtonProperties mbp = token.getMacro(((BigDecimal)parameters.get(0)).intValue(), false);
 			String cmd = mbp.getCommand();
 			return cmd != null ? cmd : "";
		} else if (functionName.equals("getMacroButtonIndex")) {
			return BigInteger.valueOf(MapTool.getParser().getMacroButtonIndex());
		} else {
 		 return token.getMacro(parameters.get(0).toString(), false).getCommand();
 		}
	}
	
	public String getMacroButtonProps(Token token, int index, String delim) throws ParserException {
 		MacroButtonProperties mbp = token.getMacro(index, !MapTool.getParser().isMacroTrusted());
 		if (mbp == null) {
 			throw new ParserException("No macro at index "+ index);
 		}
 		
 		if ("json".equals(delim)) {
 			Map<String, Object> props = new HashMap<String, Object>();
 			props.put("autoExecute", mbp.getAutoExecute());
 			props.put("color", mbp.getColorKey());
 			props.put("fontColor", mbp.getFontColorKey());
 			props.put("group", mbp.getGroup());
 			props.put("includeLabel", mbp.getIncludeLabel());
 			props.put("sortBy", mbp.getSortby());
 			props.put("index", mbp.getIndex());
 			props.put("label", mbp.getLabel());
 			props.put("fontSize", mbp.getFontSize());
 			props.put("minWidth", mbp.getMinWidth());
 			return JSONObject.fromObject(props).toString();
 		} else {
 			StringBuilder sb = new StringBuilder();
 			sb.append("autoExecute=").append(mbp.getAutoExecute()).append(delim);
 			sb.append("color=").append(mbp.getColorKey()).append(delim);
 			sb.append("fontColor=").append(mbp.getFontColorKey()).append(delim);
 			sb.append("group=").append(mbp.getGroup()).append(delim);
 			sb.append("includeLabel=").append(mbp.getIncludeLabel()).append(delim);
 			sb.append("sortBy=").append(mbp.getSortby()).append(delim);
 			sb.append("index=").append(mbp.getIndex()).append(delim);
 			sb.append("label=").append(mbp.getLabel()).append(delim);
 			sb.append("fontSize=").append(mbp.getFontSize()).append(delim);
 			sb.append("minWidth=").append(mbp.getMinWidth()).append(delim);
 			return sb.toString();
 		}
	}
	
	public void setMacroProps(MacroButtonProperties mbp, String propString, String delim) {
		JSONObject jobj;
		if (propString.trim().startsWith("{")) {
			// We are either a JSON string or an illegal string.
			jobj = JSONObject.fromObject(propString);
		} else {
			jobj = JSONMacroFunctions.getInstance().fromStrProp(propString, delim);
		}
		
		for (Object o : jobj.keySet()) {
			String key = o.toString();
			String value = jobj.getString(key); 
			
 			if ("autoexecute".equalsIgnoreCase(key)) {
 				mbp.setAutoExecute(boolVal(value));
 			} else if ("color".equalsIgnoreCase(key)) {
				mbp.setColorKey(value);
 			} else if ("fontColor".equalsIgnoreCase(key)) {
 				mbp.setFontColorKey(value);
 			} else if ("fontSize".equalsIgnoreCase(key)) {
 				mbp.setFontSize(value);
 			} else if ("group".equalsIgnoreCase(key)) {
 				mbp.setGroup(value);
 			} else if ("includeLabel".equalsIgnoreCase(key)) {
 				mbp.setIncludeLabel(boolVal(value));
 			} else if ("sortBy".equalsIgnoreCase(key)) {
 				mbp.setSortby(value);
 			} else if ("index".equalsIgnoreCase(key)) {
 				mbp.setIndex(Integer.parseInt(value));
 			} else if ("label".equalsIgnoreCase(key)) {
 				mbp.setLabel(value);
 			} else if ("fontSize".equalsIgnoreCase(key)) {
 				mbp.setFontSize(value);
 			} else if ("minWidth".equalsIgnoreCase(key)) {
 				mbp.setMinWidth(value);
 			}
		}
	}
	
	private boolean boolVal(String val) {
		if ("true".equalsIgnoreCase(val)) {
			return true;
		} 
		
		if ("false".equalsIgnoreCase(val)) {
			return true;
		}

		try {
			if (Integer.parseInt(val) == 0) {
				return false;
			} else { 
				return true;
			}
		} catch (NumberFormatException e) {
			return true;
		}
	}

}
