package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
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
		super(1, UNLIMITED_PARAMETERS, "json.get", "json.type", "json.fields", 
				    "json.length", "json.fromList", "json.set", 
				    "json.fromStrProp", "json.toStrProp", "json.toList",
				    "json.append", "json.remove", "json.indent", "json.contains",
				    "json.sort", "json.shuffle", "json.reverse", "json.evaluate", 
				    "json.isEmpty", "json.equals");
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
			return fromStrList(parameters.get(0).toString(), delim);
		}

		if (functionName.equals("json.fromStrProp")) {
			String delim = ";";
			if (parameters.size() > 1) {
				delim = parameters.get(1).toString();
			}
			return fromStrProp(parameters.get(0).toString(), delim);
		}
		
		
		if (functionName.equals("json.set")) {
			if (parameters.size() < 3) {
				throw new ParserException("Not enough parameters for json.set(obj, key, value)");
			}
			return JSONSet(asJSON(parameters.get(0)), parameters);
			
		}
		
		if (functionName.equals("json.length")) {
			return JSONLength(asJSON(parameters.get(0)));
		}
		
		if (functionName.equals("json.fields")) {
			String delim = ",";
			if (parameters.size() > 1) {
				delim = parameters.get(1).toString();
			}
			return JSONFields(asJSON(parameters.get(0)), delim);
		}
		
		if (functionName.equals("json.type")) {
			return getJSONObjectType(asJSON(parameters.get(0))).toString();
		}
		
		if (functionName.equals("json.toList")) {
			String delim = ",";
			if (parameters.size() > 1) {
				delim = parameters.get(1).toString();
			}
			return JSONToList(asJSON(parameters.get(0)), delim);
		}
		
		if (functionName.equals("json.toStrProp")) {
			String delim = ";";
			if (parameters.size() > 1) {
				delim = parameters.get(1).toString();
			}
			return JSONToStrProp(asJSON(parameters.get(0)), delim);
		}
		
		
		
		if (functionName.equals("json.get")) {
			if (parameters.size() < 2) {
				throw new ParserException("Not enough parameters for json.get(obj, key)");
			}
			return JSONGet(asJSON(parameters.get(0)), parameters.subList(1, parameters.size()));
		}
		
		
		if (functionName.equals("json.append")) {
			if (parameters.size() < 2) {
				throw new ParserException("Not enough parameters for json.append(obj, value)");
			}
			return JSONAppend(asJSON(parameters.get(0)), parameters);
		}
		
		if (functionName.equals("json.remove")) {
			if (parameters.size() < 2) {
				throw new ParserException("Not enough parameters for json.delete(obj, key)");
			}
			return JSONDelete(asJSON(parameters.get(0)), parameters.get(1).toString());
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
			return JSONIndent(asJSON(parameters.get(0)), indent);

		}
		
		if (functionName.equals("json.contains")) {
			if (parameters.size() < 2) {
				throw new ParserException("Not enough parameters for json.contains(obj, key)");
			}
			return JSONContains(asJSON(parameters.get(0)), parameters.get(1).toString()) ? BigDecimal.ONE : BigDecimal.ZERO;
		}
		
		if (functionName.equals("json.sort")) {
			return JSONSort(asJSON(parameters.get(0)), parameters.size() > 1 ? parameters.get(1).toString() : "ascending");
		}
		
		if (functionName.equals("json.shuffle")) {
			return JSONShuffle(asJSON(parameters.get(0)));
		}
		
		if (functionName.equals("json.reverse")) {
			return JSONReverse(asJSON(parameters.get(0)));
		}
		
		if (functionName.equals("json.evaluate")) {
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException("You do not have permission to execute json.evaluate()");
			}
			Object j = asJSON(parameters.get(0));
			if (!(j instanceof JSONObject) && !(j instanceof JSONArray)) {
				throw new ParserException("json.evaluate() can only be called on json objects or arrays");				
			}
			// Create a new object or array so that we preserve immutability for macros.
			Object json;
			if (j instanceof JSONObject) {
				json = JSONObject.fromObject(j);
			} else {
				json = JSONArray.fromObject(j);
			}
			return JSONEvaluate((MapToolVariableResolver)parser.getVariableResolver(), json);
		}
		
		if (functionName.equals("json.isEmpty")) {
			Object j = asJSON(parameters.get(0));
			if (j instanceof JSONObject) {
				return ((JSONObject)j).isEmpty() ? BigDecimal.ONE : BigDecimal.ZERO;
			}
			if (j instanceof JSONArray) {
				return ((JSONArray)j).isEmpty() ? BigDecimal.ONE : BigDecimal.ZERO;
			}
			if (j instanceof String) {
				return j.toString().length() == 0 ? BigDecimal.ONE : BigDecimal.ZERO;
			}
			return BigDecimal.ZERO;
		}
		
		if (functionName.equals("json.equals")) {
			if (parameters.size() < 2) {
				throw new ParserException("Not enough parameters for json.equals(json1, json2)");
			}
			Object left = asJSON(parameters.get(0));
			Object right = asJSON(parameters.get(1));
			
			if (left instanceof JSONArray) {
				if (right instanceof JSONArray) {
					JSONArray la = (JSONArray)left;
					JSONArray ra = (JSONArray)right;
					if (la.size() != ra.size()) {
						return BigDecimal.ZERO;
					}
					for (int i = 0; i < la.size(); i++) {
						if (!la.get(i).equals(ra.get(i))) {
							return BigDecimal.ZERO;
						}
					}
					return BigDecimal.ONE;
				}
				return BigDecimal.ZERO;
			}
			
			if (left instanceof JSONObject) {
				if (right instanceof JSONObject) {
					JSONObject lo = (JSONObject) left;
					JSONObject ro = (JSONObject) right;
					
					if (lo.size() != ro.size()) {
						return BigDecimal.ZERO;
					}
					
					for (Object key : lo.keySet()) {
						if (!lo.get(key).equals(ro.get(key))) {
							return BigDecimal.ZERO;
						}
					}
					return BigDecimal.ONE;
				}
			}
			
			if (left instanceof String) {
				if (right instanceof String) {
					if (left.toString().length() == 0 || right.toString().length() == 0) {
						return BigDecimal.ONE;
					}
				}
				return BigDecimal.ZERO;
			}
			return BigDecimal.ZERO;
		}
		
		throw new ParserException(functionName + "(): Unknown function");
	}
	
	

	/**
	 * Evaluates each of the strings in the JSON object or array.
	 * @param res The variable resolver.
	 * @param json the JSON object.
	 * @return the json object with the strings evaluated.
	 * @throws ParserException if there is an error.
	 */
	private Object JSONEvaluate(MapToolVariableResolver res, Object json) throws ParserException {
		if (json instanceof JSONObject) {
			JSONObject jobj = (JSONObject) json;
			for (Object key : jobj.keySet()) {
				Object o = jobj.get(key);
				if (o instanceof JSONObject || o instanceof JSONArray) {
					jobj.put(key, JSONEvaluate(res, o));
				} else if (o instanceof String) {
					jobj.put(key, EvalMacroFunctions.evalMacro(res, res.getTokenInContext(), o.toString()));
				}
			}
		} else {
			JSONArray jarr = (JSONArray) json;
			for (int i = 0; i < jarr.size(); i++) {
				Object o = jarr.get(i);
				if (o instanceof JSONObject || o instanceof JSONArray) {
					jarr.set(i, JSONEvaluate(res, o));
				} else if (o instanceof String) {
					// For arrays we may have an extra "" so it can be stored in the array
					String line = o.toString();
					line = line.replaceFirst("^\"", "").replaceFirst("\"$", "");	
					jarr.set(i,EvalMacroFunctions.evalMacro(res, res.getTokenInContext(), line));
				}
			}	
		}
		return json;
	}

	/**
	 * Shuffles the values in a json array.
	 * @param jsonArray the array to shuffle.
	 * @return the shuffled array.
	 * @throws ParserException if the object is not a JSON Array. 
	 */
	private JSONArray JSONShuffle(Object jsonArray) throws ParserException {
		if (!(jsonArray instanceof JSONArray)) {
			throw new ParserException("json.shuffle() can only be used on JSON Arrays");
		}
		// Create a new JSON Array to support immutable types in macros.
		JSONArray jarr = JSONArray.fromObject(jsonArray);
		Collections.shuffle(jarr);
		return jarr;
	}

	/**
	 * Sorts a json aray. If all values in the array are number then the values are 
	 * sorted in numeric order, otherwise values are sorted in string order.
	 * @param jsonArray The json array to sort.
	 * @param direction The direction "ascending" or "descending" to sort the array.
	 * @return The sorted array.
	 * @throws ParserException  if the object is not a JSON array.
	 */
	@SuppressWarnings("unchecked")
	private JSONArray JSONSort(Object jsonArray, String direction) throws ParserException {

		if (!(jsonArray instanceof JSONArray)) {
			throw new ParserException("json.sort() can only be used on JSON Arrays");
		}
		
		// Create a new JSON Array to support immutable types in macros.
		JSONArray jarr = JSONArray.fromObject(jsonArray);
		//Check to see if we are all numbers 
		boolean sortAsNumber = true;
		for (Object o : jarr) {
			if (!(o instanceof Double) &&! (o instanceof Integer)) {
				sortAsNumber = false;
			}
		}
		
		boolean ascending = true;
		if (direction.toLowerCase().startsWith("d")) {
			ascending = false;
		}

		if (sortAsNumber) {
			Collections.sort(jarr, new JSONNumberComparator(ascending));
		} else {
			Collections.sort(jarr, new JSONStringComparator(ascending));
		}
		return jarr;
	}

	/**
	 * Attempts to convert a string to a JSON object.
	 * @param obj The string to attempt to convert.
	 * @return Either a JSONObject, JSONArray, or null.
	 */
	public static Object convertToJSON(String obj) {
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
	 * @param values The values to append to the array.
	 * @return the JSON array.
	 * @throws ParserException
	 */
	private JSONArray JSONAppend(Object obj, List<Object> values) throws ParserException {
		if (obj == null || obj.toString().length() == 0) {
			obj = new JSONArray();
		} 
		
		if (obj != null && obj instanceof JSONArray) {
			// Create a new JSON Array to support immutable types in macros.
			JSONArray jarr = JSONArray.fromObject(obj);
			for (Object val : values.subList(1, values.size())) {
				jarr.add(val);
			}
			return jarr;
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
	private Object JSONGet(Object obj, List<Object> keys) throws ParserException {
		if (obj == null) {
			throw new ParserException("Unknown JSON Object type");			
		}
		Object val = null;
		if (obj instanceof JSONObject) {
			JSONObject jobj = (JSONObject) obj;
			if (keys.size() == 1) {
				Object oval = jobj.get(keys.get(0).toString());
				if (oval == null) {
					val = "";
				} else {
					val = oval.toString();
				}
			} else {
				Map<String, Object> values = new HashMap<String, Object>();
				for (Object key : keys) {
					Object oval = jobj.get(key.toString());
					values.put(key.toString(), oval != null ? oval : "");
				}
				val = JSONObject.fromObject(values);
			}
		} else if (obj instanceof JSONArray) {
			JSONArray jarr = (JSONArray) obj;
			if (keys.size() == 1) {
				val =  jarr.get(Integer.parseInt(keys.get(0).toString())).toString();
			} else {
				int start = Integer.parseInt(keys.get(0).toString());
				// Wrap around
				if (start < 0) {
					start = jarr.size()+start;
				}
				if (start >= jarr.size() || start < 0) {
					throw new ParserException("json.get(): Invalid Start index "+ start + "for array (size = " + jarr.size() + ")");
				}
				int end = Integer.parseInt(keys.get(1).toString());
				if (end < 0) {
						end = jarr.size()+end;
				}
				if (end >= jarr.size() || end < 0) {
					throw new ParserException("json.get(): Invalid End index "+ end + "for array (size = " + jarr.size() + ")");
				}
				
				List<Object> values = new ArrayList<Object>();
				if (start > end) {
					for (int i = start; i >= end; i--) {
						values.add(jarr.get(i));
					}
				} else {
					for (int i = start; i <= end; i++) {
						values.add(jarr.get(i));
					}					
				}
				val = JSONArray.fromObject(values);
			}
		} else {
			throw new ParserException("json.get(): Not a valid JSON object or array");
		}
		 
		if (keys.size() == 1) {
			// Attempt to convert to a number ...
			try {
				return new BigDecimal((String)val);
			} catch (Exception e) {
				// Ignore
			}
		}
		return val == null ? "" : val;
	}

	/**
	 * Converts a JSON object to a string property.
	 * @param obj The object to convert.
	 * @param delim The delimiter used in the string property.
	 * @return The string property of the object.
	 * @throws ParserException If the object is not a JSON object.
	 */
	private String JSONToStrProp(Object obj, String delim) throws ParserException {

		StringBuilder sb = new StringBuilder();
		if (obj instanceof JSONObject) {
			JSONObject jobj = (JSONObject) obj;
			for (Object ob :jobj.keySet()) {
				if (sb.length() > 0) {
					sb.append(delim);
				}
				sb.append(ob).append("=").append(jobj.get(ob));
			}
			return sb.toString();
		} else if (obj instanceof JSONArray){
			JSONArray jarr = (JSONArray) obj;
			for (int i = 0; i < jarr.size(); i++) {
				if (sb.length() > 0) {
					sb.append(delim);
				}
				sb.append(i).append("=").append(jarr.get(i));					
			}
			return sb.toString();
		} else if (obj instanceof String && ((String)obj).trim().length() == 0) {
			return obj.toString();
		} else {
			throw new ParserException("Unknown JSON Object type");			
		}
	}

	/**
	 * Converts a JSON object to a string list.
	 * @param obj The object to convert.
	 * @param delim The delimiter used in the string list.
	 * @return The string list of the object.
	 * @throws ParserException If the object is not a JSON object.
	 */
	private Object JSONToList(Object obj, String delim) throws ParserException {
	
		StringBuilder sb = new StringBuilder();
		if (obj instanceof JSONObject) {
			JSONObject jobj = (JSONObject) obj;
			for (Object ob :jobj.keySet()) {
				if (sb.length() > 0) {
					sb.append(delim);
				}
				sb.append(ob);
			}
			return sb.toString();
		} else if (obj instanceof JSONArray){
			JSONArray jarr = (JSONArray) obj;
			for (int i = 0; i < jarr.size(); i++) {
				if (sb.length() > 0) {
					sb.append(delim);
				}
				sb.append(jarr.get(i));					
			}
			return sb.toString();
		} else if (obj instanceof String && ((String)obj).trim().length() == 0) {
			return obj.toString();
		} else {
			throw new ParserException("Unknown JSON Object type");			
		}
	}

	/**
	 * Gets a list of the JSON objects fields.
	 * @param obj The object to get the fields of.
	 * @param delim The delimiter used in the string list.
	 * @return The string property of the object.
	 * @throws ParserException If the object is not a JSON object.
	 */
	private Object JSONFields(Object obj, String delim) throws ParserException {

		StringBuilder sb = new StringBuilder();
		if (obj instanceof JSONObject) {
			JSONObject jobj = JSONObject.fromObject(obj);
			if ("json".equals(delim)) {
				return JSONArray.fromObject(jobj.keySet());
			} else {
				for (Object ob :jobj.keySet()) {
					if (sb.length() > 0) {
						sb.append(delim);
					}
				sb.append(ob.toString());
				}
				return sb.toString();
			}
		} else if (obj instanceof JSONArray){
			JSONArray jarr = (JSONArray) obj;
			for (int i = 0; i < jarr.size(); i++) {
				if (sb.length() > 0) {
					sb.append(delim);
				}
				sb.append(i);					
			}
			return sb.toString();
		} else {
			throw new ParserException("Unknown JSON Object type");			
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
	private BigDecimal JSONLength(Object obj) throws ParserException {
		
		if (obj instanceof JSONObject) {
			JSONObject jobj = (JSONObject) obj;
			return BigDecimal.valueOf(jobj.keySet().size());
		} else if (obj instanceof JSONArray) {
			JSONArray jarr = (JSONArray) obj;
			return BigDecimal.valueOf(jarr.size());
		} else {
			throw new ParserException("Unknown JSON Object type");			
		}
	}

	/**
	 * Sets the value of an element in a JSON Array or a Field in a JSON Object.
	 * @param obj The JSON object.
	 * @param param The key/value pairs to set.
	 * @return new JSON object.
	 * @throws ParserException if the obj is not a JSON object.
	 */
	private Object JSONSet(Object obj, List<Object> param) throws ParserException {
		
		if (obj == null || obj.toString().length() == 0) {
			obj = new JSONObject();
		} 
		
		
		if (param.size() % 2 != 1) {
			throw new ParserException("No matching value for key in json.set()");
		}
		
		if (obj instanceof JSONObject) {
			// Create a new JSON object to preserve macro object immutable types.
			JSONObject jobj = JSONObject.fromObject(obj);
			for (int i = 1; i < param.size(); i += 2) {
				jobj.put(param.get(i).toString(), param.get(i+1));
			}
			return jobj;
		} else if (obj instanceof JSONArray ){
			// Create a new JSON array to preserve macro object immutable types.
			JSONArray jarr = JSONArray.fromObject(obj);
			for (int i = 1; i < param.size(); i += 2) {
				jarr.set(Integer.parseInt(param.get(i).toString()), param.get(i+1));
			}
			return jarr;
		} else {
			throw new ParserException("Unknown JSON Object type");			
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
			if (vals.length > 1) {
				// Try to convert it to a number and if that works we store it that way
				try {
					obmap.put(vals[0].trim(), new BigDecimal(vals[1].trim()));				
				} catch (Exception e) {
					obmap.put(vals[0].trim(), vals[1].trim());
				}
			} else {
				obmap.put(vals[0].trim(), "");
			}
		}
		return JSONObject.fromObject(obmap);
	}
	
	/**
	 * Gets the type of the JSON object.
	 * @param obj The json object.
	 * @return the type of object.
	 */
	private JSONObjectType getJSONObjectType(Object obj) {
		if (obj instanceof JSONObject) {
			return JSONObjectType.OBJECT;
		} else if (obj instanceof JSONArray) {
			return JSONObjectType.ARRAY;
		} else { 
			return JSONObjectType.UNKNOWN;	
		}
	}
	
	
	/**
	 * Deletes a field from a JSON object or element from a JSON array.
	 * @param obj The JSON object.
	 * @param key
	 * @return The new JSON object.
	 * @throws ParserException if obj can not be converted to a JSON object.
	 */
	private Object JSONDelete(Object obj, String key) throws ParserException {
	
		if (obj instanceof JSONObject) {
			// Create a new JSON object so that old one remains immutable to macro
			JSONObject jobj = JSONObject.fromObject(obj);
			jobj.remove(key);
			return jobj;
		} else if (obj instanceof JSONArray){
			// Create a new JSON array so that old one remains immutable to macro
			JSONArray jarr = JSONArray.fromObject(obj);
			jarr.remove(Integer.parseInt(key));
			return jarr;
		} else {
			throw new ParserException("Unknown JSON type.");
		}
 	}
	
	/**
	 * Returns and indented version of a JSON string.
	 * @param obj The JSON string to ident.
	 * @param indent The indention factor.
	 * @return The indented string.
	 * @throws ParserException If an error occurs parsing the JSON String.
	 */
	private String JSONIndent(Object obj, int indent) throws ParserException {
		if (obj instanceof JSONObject) {
			return ((JSONObject)obj).toString(indent);
		} else if (obj instanceof JSONArray){
			return ((JSONArray)obj).toString(indent);
		} else {
			throw new ParserException("Unknown JSON type");			
		}
		
	}
	
	/**
	 * Checks to see if a JSON object contains the specified key.
	 * @param obj The JSON Object.
	 * @param key The key to check for.
	 * @return true if the JSON object contains the key.
	 * @throws ParserException
	 */
	private boolean JSONContains(Object obj, String key) throws ParserException {
		
		if (obj != null && obj instanceof JSONObject) {
			return ((JSONObject)obj).containsKey(key);
		} 
		
		if (obj != null && obj instanceof JSONArray) {
			try {
				return ((JSONArray)obj).contains(Integer.parseInt(key));
			} catch (Exception e) {
				// Do nothing as we will try another conversion
			}
			
			try {
				return ((JSONArray)obj).contains(Double.parseDouble(key));
			} catch (Exception e) {
				// Do nothing as we will try it as a string
			}
			return ((JSONArray)obj).contains(key);
		}
		
		throw new ParserException("json.contains() can only be used on json objects or arrays.");
	}
	
	
	/**
	 * Reverses a json array.
	 * @param jsonArray The json array to reverse.
	 * @return the reversed json array.
	 * @throws ParserException if jsonArray is not a json array.
	 */
	private JSONArray JSONReverse(Object jsonArray) throws ParserException {
		if (!(jsonArray instanceof JSONArray)) {
			throw new ParserException("json.reverse() can only be used on json arrays. ");
		}
		
		// Create a new JSON Array to preserve immutable state for macros.
		JSONArray jarr = JSONArray.fromObject(jsonArray);
		List<Object> arr = new LinkedList<Object>();
		for (int i = jarr.size() - 1; i >= 0; i--) {
			arr.add(jarr.get(i));
		}
		
		return JSONArray.fromObject(jsonArray);
	}
	
	/**
	 * Compares two numbers from a json array.
	 */
	@SuppressWarnings("unchecked")
	private static class JSONNumberComparator implements Comparator {

		private final boolean ascending;
		
		public JSONNumberComparator(boolean ascending) {
			this.ascending = ascending;
		}
		
		public int compare(Object o1, Object o2) {
			BigDecimal v1;
			BigDecimal v2;
			
			if (o1 instanceof Integer) {
				v1 = BigDecimal.valueOf((Integer)o1);
			} else {
				v1 = BigDecimal.valueOf((Double)o1);
			}
			
			if (o2 instanceof Integer) {
				v2 = BigDecimal.valueOf((Integer)o2);
			} else {
				v2 = BigDecimal.valueOf((Double)o2);
			}
			
			return ascending ? v1.compareTo(v2) : v2.compareTo(v1);
		}
		
	}
	
	/**
	 * 
	 * Compares two strings from a json array.
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static class JSONStringComparator implements Comparator {

		private final boolean ascending;
		
		public JSONStringComparator(boolean ascending) {
			this.ascending = ascending;
		}
		
		public int compare(Object o1, Object o2) {
			String s1 = o1.toString();
			String s2 = o2.toString();
			return ascending ? s1.compareTo(s2) : s2.compareTo(s1);
		}
		
	}
	

	/**
	 * Returns a JSON object from the parameter.
	 * @param o The parameter.
	 * @return a JSON object.
	 */
	@SuppressWarnings("unused")
	private static JSONObject asJSONObject(Object o) {
		if (o instanceof JSONObject) {
			return (JSONObject) o;
		}
		
		return JSONObject.fromObject(o.toString());
	}
	
	/**
	 * Returns a JSON Array from the parameter.
	 * @param o The parameter.
	 * @return a JSON array.
	 */
	@SuppressWarnings("unused")
	private static JSONArray asJSONArray(Object o) {
		if (o instanceof JSONArray) {
			return (JSONArray) o;
		} 
		// Special cases we have to deal with cases where the parser
		// has already had a go at single index arrays.
		if (o instanceof BigDecimal) {
			JSONArray jarr = new JSONArray();
			jarr.add(o);
			return jarr;
		} else if (o instanceof String) {
			String s = ((String)o).trim();
			if (!s.startsWith("[") && !s.startsWith("{")) {
				JSONArray jarr = new JSONArray();
				if (o.toString().length() > 0) {
					jarr.add(o);
				}
				return jarr;
			}
		}
		
		return JSONArray.fromObject(o.toString());
	}
	
	/**
	 * Returns a JSONObject or JSONArray from the parameter.
	 * @param o The parameter to convert.
	 * @return The JSONObject or JSONArray.
	 */
	public static Object asJSON(Object o) {
		if (o instanceof JSONArray) {
			return o;
		} else if (o instanceof JSONObject) {
			return o;
		}
		// Special cases we have to deal with cases where the parser
		// has already had a go at single index arrays.
		if (o instanceof BigDecimal) {
			JSONArray jarr = new JSONArray();
			jarr.add(o);
			return jarr;
		} else if (o instanceof String) {
			String s = ((String)o).trim();
			if (!s.startsWith("[") && !s.startsWith("{")) {
				if (o.toString().length() == 0) {
					return o.toString();
				}
				JSONArray jarr = new JSONArray();
				if (o.toString().length() > 0) {
					jarr.add(o);
				}
				return jarr;
			}
		}
		return convertToJSON(o.toString());
	}
}