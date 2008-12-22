package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSONMacroFunctions extends AbstractFunction {

	public enum JSONObjectType {
		OBJECT,
		ARRAY,
		UNKNOWN
	}

	
	private static final JSONMacroFunctions instance = 
							new JSONMacroFunctions();
	
	private JSONMacroFunctions() {
		super(1, 3, "json.get", "json.type", "json.fields", 
				    "json.length", "json.fromList", "json.set", 
				    "json.fromStrProp", "json.toStrProp", "json.toList",
				    "json.append", "json.remove", "json.indent", "json.contains");
	}
	
	public static JSONMacroFunctions getInstance() {
		return instance;
	}
	


	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		
		
	
		if (functionName.equals("json.fromList")) {
			String delim = ",";
			if (parameters.size() > 1) {
				delim = parameters.get(1).toString();
			}
			return fromStrList(parameters.get(0).toString(), delim).toString();
		}

		if (functionName.equals("json.fromStrProp")) {
			String delim = ";";
			if (parameters.size() > 1) {
				delim = parameters.get(1).toString();
			}
			return fromStrProp(parameters.get(0).toString(), delim).toString();
		}
		
		if (functionName.equals("json.append")) {
			
		}
		
		if (functionName.equals("json.set")) {
			if (parameters.size() < 3) {
				throw new ParserException("Not enough parameters for json.set(obj, key, value)");
			}
			return JSONSet(parameters.get(0).toString(), parameters.get(1).toString(),
						   parameters.get(2));
			
		}
		
		if (functionName.equals("json.length")) {
			return JSONLength(parameters.get(0).toString());
		}
		
		if (functionName.equals("json.fields")) {
			String delim = ",";
			if (parameters.size() > 1) {
				delim = parameters.get(1).toString();
			}
			return JSONFields(parameters.get(0).toString(), delim);
		}
		
		if (functionName.equals("json.type")) {
			return getJSONObjectType(parameters.get(0).toString()).toString();
		}
		
		if (functionName.equals("json.toList")) {
			String delim = ",";
			if (parameters.size() > 1) {
				delim = parameters.get(1).toString();
			}
			return JSONToList(parameters.get(0).toString(), delim);
		}
		
		if (functionName.equals("json.toStrProp")) {
			String delim = ";";
			if (parameters.size() > 1) {
				delim = parameters.get(1).toString();
			}
			return JSONToStrProp(parameters.get(0).toString(), delim);
		}
		
		
		
		if (functionName.equals("json.get")) {
			if (parameters.size() < 2) {
				throw new ParserException("Not enough parameters for json.get(obj, key)");
			}
			return JSONGet(parameters.get(0).toString(), parameters.get(1).toString());
		}
		
		
		if (functionName.equals("json.append")) {
			if (parameters.size() < 2) {
				throw new ParserException("Not enough parameters for json.append(obj, value)");
			}
			return JSONAppend(parameters.get(0).toString(), parameters.get(1));
		}
		
		if (functionName.equals("json.remove")) {
			if (parameters.size() < 2) {
				throw new ParserException("Not enough parameters for json.delete(obj, key)");
			}
			return JSONDelete(parameters.get(0).toString(), parameters.get(1).toString());
		}
		
		if (functionName.equals("json.indent")) {
			int indent = 4;
			if (parameters.size() > 1) {
				try {
					indent = Integer.parseInt(parameters.get(1).toString()); 
				} catch (Exception e) {
					// Do nothing as we will just use the default.
				}		
			}
			return JSONIndent(parameters.get(0).toString(), indent);

		}
		
		if (functionName.equals("json.contains")) {
			if (parameters.size() < 2) {
				throw new ParserException("Not enough parameters for json.contains(obj, key)");
			}
			return JSONContains(parameters.get(0).toString(), parameters.get(1).toString()) ? BigDecimal.ONE : BigDecimal.ZERO;
		}
		return null;
	}
	
	
	
	/**
	 * Attempts to convert a string to a JSON object.
	 * @param obj The string to attempt to convert.
	 * @return Either a JSONObject, JSONArray, or null.
	 */
	public Object convertToJSON(String obj) {
		if (obj.trim().startsWith("[")) {
			try {
				return JSONArray.fromObject(obj);
			} catch (Exception e) {
				return null;
			}
		}
		
		if (obj.trim().startsWith("{")) {
			try {
				return JSONObject.fromObject(obj);
			} catch (Exception e) {
				return null;
			}	
		}
	
		return null;

	}
	/**
	 * Append a value to a JSON array.
	 * @param obj The JSON object.
	 * @param value The value to append to the array.
	 * @return the JSON array.
	 * @throws ParserException
	 */
	private String JSONAppend(String obj, Object value) throws ParserException {
		Object o = convertToJSON(obj);
		if (o != null && o instanceof JSONArray) {
			JSONArray jarr = (JSONArray) o;
			jarr.add(value);
			return jarr.toString();
		} else {
			throw new ParserException("You can only append to JSON arrays.");
		}
	}

	/**
	 * Gets a value from the JSON Object or Array.
	 * @param obj The JSON Object or Array.
	 * @param key The key for the object or index for the array.
	 * @return the value.
	 * @throws ParserException
	 */
	private Object JSONGet(String obj, String key) throws ParserException {
		Object o = convertToJSON(obj);
		if (o == null) {
			throw new ParserException("Unknown JSON Object type");			
		}
		Object val = null;
		if (o instanceof JSONObject) {
			JSONObject jobj = (JSONObject) o;
			val = jobj.get(key).toString();
		} else {
			JSONArray jarr = (JSONArray) o;
			jarr.get(Integer.parseInt(key)).toString();
			val = jarr.toString();
		}
		
		// Attempt to convert to a number ...
		try {
			return new BigDecimal((String)val);
		} catch (Exception e) {
			// Ignore
		}
		return val.toString();
	}

	/**
	 * Converts a JSON object to a string property.
	 * @param obj The object to convert.
	 * @param delim The delimiter used in the string property.
	 * @return The string property of the object.
	 * @throws ParserException If the object is not a JSON object.
	 */
	private String JSONToStrProp(String obj, String delim) throws ParserException {
		Object o = convertToJSON(obj);
		if (o == null) {
			throw new ParserException("Unknown JSON Object type");			
		}

		StringBuilder sb = new StringBuilder();
		if (o instanceof JSONObject) {
			JSONObject jobj = (JSONObject) o;
			for (Object ob :jobj.keySet()) {
				if (sb.length() > 0) {
					sb.append(delim);
				}
				sb.append(ob).append("=").append(jobj.get(ob));
			}
			return sb.toString();
		} else {
			JSONArray jarr = (JSONArray) o;
			for (int i = 0; i < jarr.size(); i++) {
				if (sb.length() > 0) {
					sb.append(delim);
				}
				sb.append(i).append("=").append(jarr.get(i));					
			}
			return sb.toString();
		}
	}

	/**
	 * Converts a JSON object to a string list.
	 * @param obj The object to convert.
	 * @param delim The delimiter used in the string list.
	 * @return The string list of the object.
	 * @throws ParserException If the object is not a JSON object.
	 */
	private Object JSONToList(String obj, String delim) throws ParserException {
		Object o = convertToJSON(obj);
		if (o == null) {
			throw new ParserException("Unknown JSON Object type");			
		}
	
		StringBuilder sb = new StringBuilder();
		if (o instanceof JSONObject) {
			JSONObject jobj = JSONObject.fromObject(obj);
			for (Object ob :jobj.keySet()) {
				if (sb.length() > 0) {
					sb.append(delim);
				}
				sb.append(ob);
			}
			return sb.toString();
		} else {
			JSONArray jarr = (JSONArray) o;
			for (int i = 0; i < jarr.size(); i++) {
				if (sb.length() > 0) {
					sb.append(delim);
				}
				sb.append(jarr.get(i));					
			}
			return sb.toString();
		}
	}

	/**
	 * Gets a list of the JSON objects fields.
	 * @param obj The object to get the fields of.
	 * @param delim The delimiter used in the string list.
	 * @return The string property of the object.
	 * @throws ParserException If the object is not a JSON object.
	 */
	private String JSONFields(String obj, String delim) throws ParserException {
		Object o = convertToJSON(obj);
		if (o == null) {
			throw new ParserException("Unknown JSON Object type");			
		}

		StringBuilder sb = new StringBuilder();
		if (o instanceof JSONObject) {
			JSONObject jobj = JSONObject.fromObject(obj);
			if ("json".equals(delim)) {
				return JSONArray.fromObject(jobj.keySet()).toString();
			} else {
				for (Object ob :jobj.keySet()) {
					if (sb.length() > 0) {
						sb.append(delim);
					}
				sb.append(ob.toString());
				}
				return sb.toString();
			}
		} else {
			JSONArray jarr = (JSONArray) o;
			for (int i = 0; i < jarr.size(); i++) {
				if (sb.length() > 0) {
					sb.append(delim);
				}
				sb.append(i);					
			}
			return sb.toString();
		}			
	}

	/**
	 * Gets the length of a JSON object. If the object is an array then the number
	 * of elements is returned, if it is an object then the number of fields is 
	 * returned.
	 * @param obj The JSON object.
	 * @return the number of elements or fields.
	 * @throws ParserException if obj is not a JSON object.
	 */
	private BigDecimal JSONLength(String obj) throws ParserException {
		Object o = convertToJSON(obj);
		if (o == null) {
			throw new ParserException("Unknown JSON Object type");			
		}
		
		if (o instanceof JSONObject) {
			JSONObject jobj = (JSONObject) o;
			return BigDecimal.valueOf(jobj.keySet().size());
		} else {
			JSONArray jarr = JSONArray.fromObject(obj);
			return BigDecimal.valueOf(jarr.size());
		}
	}

	/**
	 * Sets the value of an element in a JSON Array or a Field in a JSON Object.
	 * @param obj The JSON object.
	 * @param key The key or index to set.
	 * @param value The value to set.
	 * @return new JSON object.
	 * @throws ParserException if the obj is not a JSON object.
	 */
	private String JSONSet(String obj, String key, Object value) throws ParserException {
		Object o = convertToJSON(obj);
		if (o == null) {
			throw new ParserException("Unknown JSON Object type");			
		}
		
		if (o instanceof JSONObject) {
			JSONObject jobj = (JSONObject) o;
			jobj.put(key, value);
			return jobj.toString();
		} else {
			JSONArray jarr = (JSONArray) o;
			jarr.set(Integer.parseInt(key), value);
			return jarr.toString();
		} 
	}

	/**
	 * Creates a JSON Array from a String list.
	 * @param list The string list.
	 * @param delim The delimiter used to separate items in the string list.
	 * @return The JSON Array.
	 */
	public JSONArray fromStrList(String list, String delim) {
		delim = delim != null ? delim : ",";
		String[] stringArray = list.split(delim);
		Object[] array = new Object[stringArray.length];
		// Try to convert it to a number and if that works we store it that way
		for (int i = 0; i < stringArray.length; i++) {
			try {
				BigDecimal bd = new BigDecimal(stringArray[i].toString().trim());
				array[i] = bd;
			} catch (NumberFormatException nfe) {
				array[i] = stringArray[i].trim();
			}
		}
		return JSONArray.fromObject(array);
	}
	
	/**
	 * Creates a JSON object from a String property list.
	 * @param prop The String Property list.
	 * @param delim The delimeter used to seperate items in the list.
	 * @return The JSON Object.
	 */
	public JSONObject fromStrProp(String prop, String delim) {
		delim = delim != null ? delim : ";";
		String[] props = prop.split(delim);
		HashMap<String, Object> obmap = new HashMap<String, Object>();
		for (String s : props) {
			String[] vals = s.split("=");
			// Try to convert it to a number and if that works we store it that way
			try {
				obmap.put(vals[0].trim(), new BigDecimal(vals[1].trim()));				
			} catch (Exception e) {
				obmap.put(vals[0].trim(), vals[1].trim());
			}
		}
		return JSONObject.fromObject(obmap);
	}
	
	/**
	 * Gets the type of the JSON object.
	 * @param obj The json object.
	 * @return the type of object.
	 */
	private JSONObjectType getJSONObjectType(String obj) {
		Object o = convertToJSON(obj);
		
		if (o == null) {
			return JSONObjectType.UNKNOWN;
		} else if (o instanceof JSONObject) {
			return JSONObjectType.OBJECT;
		} else {
			return JSONObjectType.ARRAY;
		}
	}
	
	
	/**
	 * Deletes a field from a JSON object or element from a JSON array.
	 * @param obj The JSON object.
	 * @param key
	 * @return The new JSON object.
	 * @throws ParserException if obj can not be converted to a JSON object.
	 */
	private String JSONDelete(String obj, String key) throws ParserException {
		Object o = convertToJSON(obj);
		if (o == null) {
			throw new ParserException("Unknown JSON type.");
		}
	
		if (o instanceof JSONObject) {
			JSONObject jobj = (JSONObject) o;
			jobj.remove(key);
			return jobj.toString();
		} else {
			JSONArray jarr = (JSONArray) o;
			jarr.remove(Integer.parseInt(key));
			return jarr.toString();
		}
	}
	
	/**
	 * Returns and indented version of a JSON string.
	 * @param obj The JSON string to ident.
	 * @param indent The indention factor.
	 * @return The indented string.
	 * @throws ParserException If an error occurs parsing the JSON String.
	 */
	private String JSONIndent(String obj, int indent) throws ParserException {
		Object o = convertToJSON(obj);
		if (o == null) {
			throw new ParserException("Unknown JSON type");
		}
		
		if (o instanceof JSONObject) {
			return ((JSONObject)o).toString(indent);
		} else {
			return ((JSONArray)o).toString(indent);
		}
		
	}
	
	/**
	 * Checks to see if a JSON object contains the specified key.
	 * @param obj The JSON Object.
	 * @param key The key to check for.
	 * @return true if the JSON object contains the key.
	 * @throws ParserException
	 */
	private boolean JSONContains(String obj, String key) throws ParserException {
		Object o = convertToJSON(obj);
		
		if (o != null && o instanceof JSONObject) {
			return ((JSONObject)o).containsKey(key);
		} 
		
		if (o != null && o instanceof JSONArray) {
			return ((JSONArray)o).contains(key);
		}
		
		throw new ParserException("json.contains() can only be used on json objects");
	}
	

}