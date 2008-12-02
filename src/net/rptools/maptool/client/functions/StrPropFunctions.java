package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.rptools.parser.function.ParameterException;

/**
 * Implements the <code>getStrProp(), setStrProp(), deleteStrProp()</code> functions.
 * <br>
 * <br>The <code>properties</code> string is of the form "key1 = value1 ; key2=value2; ...".
 * <br>
 * <br><code>getStrProp(properties, key)</code> looks up a key and returns the value, or <code>""</code> if not found.
 * <br>
 * <br><code>setStrProp(properties, key, value)</code> adds or replaces a key's value, 
 * respecting the order and case of existing keys.
 * <br>It returns the new property string.
 * <br>
 * <br><code>deleteStrProp(properties, key)</code> deletes a key from the properties string.
 * <br>It returns the new property string.
 * <br>If the key is not present, the function still succeeds.
 * <br>
 * <br><code>varsFromStrProp(properties)</code> assigns each of the values to variables named by the keys.
 * <br>It returns the number of entries found.
 * <br>
 * <br><code>countStrProp(properties)</code> returns the number of property entries in the string.
 * <br>
 * <br><code>indexKeyStrProp(properties, index)</code> returns the key at the position given by <code>index</code>.
 * <br>
 * <br><code>indexValueStrProp(properties, index)</code> returns the value at the position given by <code>index</code>. 
 * 
 * @author knizia.fan
 */
public class StrPropFunctions extends AbstractFunction {
    public StrPropFunctions() {
        super(1, 3, "getStrProp", "setStrProp", "deleteStrProp", "varsFromStrProp", 
        		"countStrProp", "indexKeyStrProp", "indexValueStrProp");
    }
    
	/** The singleton instance. */
	private final static StrPropFunctions instance = new StrPropFunctions();
	
	/** Gets the Input instance.
	 * @return the instance. */
	public static StrPropFunctions getInstance() {
		return instance;
	}
	
    
    /** Parses a property string.
     * @param props has the form "key1=val1 ; key2=val2 ; ..."
     * @param map is populated with the settings.  The keys are normalized to upper case.
     * @param oldKeys holds the un-normalized keys, in their original order.
     */
    public static void parse(String props, Map<String,String> map, List<String> oldKeys) {
    	Pattern entryParser = Pattern.compile("\\w+\\s*=\\s*[^;]*");
    	Pattern keyValueParser = Pattern.compile("(\\w+)\\s*=\\s*([^;]*)");
    	Matcher entryMatcher = entryParser.matcher(props);
    	// Extract the keys and values already in the props string.
    	// Save the old keys so we can rebuild the props string in the same order.
    	while (entryMatcher.find()) {
    		String entry = entryMatcher.group();
    		Matcher keyValueMatcher = keyValueParser.matcher(entry);
    		if (keyValueMatcher.find()) {
    			String propKey = keyValueMatcher.group(1).trim();
    			String propValue = keyValueMatcher.group(2).trim();
    			map.put(propKey.toUpperCase(), propValue);
    			oldKeys.add(propKey);
    		}
    	}
    	
    }
    
    @Override
    public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws ParserException {
    	Object retval = "";
    	String props = parameters.get(0).toString();		// contains property settings
    	
    	// Extract the keys and values already in the props string.
    	// Save the old keys so we can rebuild the props string in the same order.
    	HashMap<String,String> map = new HashMap<String,String>();
    	ArrayList<String> oldKeys = new ArrayList<String>();
    	ArrayList<String> oldKeysNormalized = new ArrayList<String>();
    	parse(props, map, oldKeys);
    	for (String key : oldKeys)
    		oldKeysNormalized.add(key.toUpperCase());
    	
    	if ("getStrProp".equalsIgnoreCase(functionName)) 
    		retval = getStrProp(parameters, props, map);
    	else if ("setStrProp".equalsIgnoreCase(functionName))
    		retval = setStrProp(parameters, props, map, oldKeys, oldKeysNormalized);
    	else if ("deleteStrProp".equalsIgnoreCase(functionName))
    		retval = deleteStrProp(parameters, props, map, oldKeys, oldKeysNormalized);
    	else if ("varsFromStrProp".equalsIgnoreCase(functionName))
    		retval = varsFromStrProp(parameters, props, map, oldKeys, oldKeysNormalized, parser);
    	else if ("countStrProp".equalsIgnoreCase(functionName))
    		retval = countStrProp(parameters, props, map, oldKeys, oldKeysNormalized);
    	else if ("indexKeyStrProp".equalsIgnoreCase(functionName))
    		retval = indexKeyStrProp(parameters, props, map, oldKeys, oldKeysNormalized);
    	else if ("indexValueStrProp".equalsIgnoreCase(functionName))
    		retval = indexValueStrProp(parameters, props, map, oldKeys, oldKeysNormalized);
    	
    	return retval;
    }
    
    public Object getStrProp(List<Object> parameters, String props, Map<String,String> map) 
    throws ParserException {
    	Object retval = "";
    	String userKey;
    	if (parameters.size()==3) {
    		// Don't check parameter 3, so we accept either String or BigDecimal
    		checkVaryingParameters("getStrProp()", 3, parameters, new Class[] {String.class, String.class});
    		retval = parameters.get(2);	// the third parameter is returned if the key is not found
    	} else {
    		checkVaryingParameters("getStrProp()", 2, parameters, new Class[] {String.class, String.class});
    	}
    	userKey = parameters.get(1).toString();		// the key being passed in
		String value = map.get(userKey.toUpperCase());
		if (value != null) {
			try {	// convert to numeric value if possible
				Integer intval = Integer.decode(value);
				retval = new BigDecimal(intval);
			} catch (Exception e) {
				retval = value;
			}
		}
		return retval;
    }
    
    public Object setStrProp(List<Object> parameters, String props, Map<String,String> map, 
    		List<String> oldKeys, List<String> oldKeysNormalized) 
    throws ParserException {
    	Object retval = "";
    	String userKey, userValue;

    	// Note: we don't check type of parameter 3, so that either String or BigDecimal will be accepted
		checkVaryingParameters("setStrProp()", 3, parameters, new Class[] {String.class, String.class});
    	userKey = parameters.get(1).toString();		// the key being passed in
		userValue = parameters.get(2).toString();
		map.put(userKey.toUpperCase(), userValue);
		// Reconstruct the property string, in the same order as the original.
		// If the key already existed, preserve the case of the original key string.
		StringBuilder sb = new StringBuilder();
		if (!oldKeysNormalized.contains(userKey.toUpperCase())) {
			oldKeys.add(userKey);
		}
		for (String k : oldKeys) {
			String v = map.get(k.toUpperCase());
			sb.append(k);
			sb.append("=");
			sb.append(v);
			sb.append(" ; ");
		}
		retval = sb.toString();
		return retval;
    }
    
    public Object deleteStrProp(List<Object> parameters, String props, Map<String,String> map, 
    		List<String> oldKeys, List<String> oldKeysNormalized) 
    throws ParserException {
    	Object retval = "";
    	String userKey;

		checkVaryingParameters("deleteStrProp()", 2, parameters, new Class[] {String.class, String.class});
    	userKey = parameters.get(1).toString();		// the key being passed in
		// reconstruct the property string, without the specified key
		StringBuilder sb = new StringBuilder();
		for (String k : oldKeys) {
			if (k.compareToIgnoreCase(userKey) == 0)
				continue;
			String v = map.get(k.toUpperCase());
			sb.append(k);
			sb.append("=");
			sb.append(v);
			sb.append(" ; ");
		}
		retval = sb.toString();
		return retval;
    }
    
    public Object varsFromStrProp(List<Object> parameters, String props, Map<String,String> map, 
    		List<String> oldKeys, List<String> oldKeysNormalized, Parser parser) 
    throws ParserException {
    	Object retval = "";

		checkVaryingParameters("varsFromStrProp()", 1, parameters, new Class[] {String.class});
		for (String k : oldKeys) {
			String v = map.get(k.toUpperCase());
    		if (v != null)
    			parser.setVariable(k, v);
		}
		retval = String.valueOf(oldKeys.size());
		return retval;
    }
    	
    public Object countStrProp(List<Object> parameters, String props, Map<String,String> map, 
    		List<String> oldKeys, List<String> oldKeysNormalized) 
    throws ParserException {
    	Object retval = "";

		checkVaryingParameters("countStrProp()", 1, parameters, new Class[] {String.class});
		retval = new BigDecimal(oldKeys.size());
		return retval;
    }
    	
    public Object indexKeyStrProp(List<Object> parameters, String props, Map<String,String> map, 
    		List<String> oldKeys, List<String> oldKeysNormalized) 
    throws ParserException {
    	Object retval = "";

		checkVaryingParameters("indexKeyStrProp", 2, parameters, new Class[] {String.class, BigDecimal.class});
		int index = ((BigDecimal)parameters.get(1)).intValue();
		if (index < 0 || index >= oldKeys.size()) {
			retval = "";
		} else {
			retval = oldKeys.get(index);
		}
		return retval;
    }

    public Object indexValueStrProp(List<Object> parameters, String props, Map<String,String> map, 
    		List<String> oldKeys, List<String> oldKeysNormalized) 
    throws ParserException {
    	Object retval = "";

		checkVaryingParameters("indexValueStrProp", 2, parameters, new Class[] {String.class, BigDecimal.class});
		int index = ((BigDecimal)parameters.get(1)).intValue();
		if (index < 0 || index >= oldKeys.size()) {
			retval = "";
		} else {
			retval = map.get(oldKeys.get(index).toUpperCase());
		}
    	
    	return retval;
    }

    
    @Override
    public void checkParameters(List<Object> parameters) throws ParameterException {
        super.checkParameters(parameters);
        // The work is done in checkVaryingParameters() instead.
    }

    public void checkVaryingParameters(
    		String funcName, int numParams, List<Object> parameters, Class[] expected) throws ParameterException {
    	if (numParams != parameters.size()) 
    		throw new ParameterException(String.format("%s requires %d parameters", funcName, numParams));
    	
    	if (numParams > expected.length) numParams = expected.length;

    	for (int i=0; i<numParams; i++) {
    		if (expected[i]!= null && !(expected[i].isInstance(parameters.get(i))))
    			throw new ParameterException(String.format("Illegal type for argument %d to %s, expected %s but got %s",
    					i+1, funcName, expected[i].getName(), parameters.get(i).getClass().getName()));
    	}
    }
}

/* Here is a test macro

[prop = "a=33; b = bob ; c=cat ; D=99"] [h: n=2] [h: k="c"]
<br>The value of {k} is [getStrProp(prop, k)]
<br>The key of entry {n} is [indexKeyStrProp(prop, n)]
<br>The value of entry {n} is [indexValueStrProp(prop, 2)]
<br>Reading in variables yields [varsFromStrProp(prop)] variables to use: a+1={a+1}, b={b}, c={c}, D-9={d-9}
<br>There are [cnt = countStrProp(prop)] settings. 
They are [c(cnt, ", "): indexKeyStrProp(prop, roll.count-1) +" is "+ indexValueStrProp(prop, roll.count-1)]
<br>Eliminating {k} from [prop] yields [deleteStrProp(prop, k)]

*/
 

