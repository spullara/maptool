package net.rptools.maptool.model;

import java.util.ArrayList;
import java.util.List;

import net.rptools.common.expression.ExpressionParser;
import net.rptools.common.expression.Result;
import net.rptools.parser.ParserException;

public class LookupTable {

	private static ExpressionParser expressionParser = new ExpressionParser();
	
	private List<LookupEntry> entryList;
	private String name;
	private String defaultRoll;

	public LookupTable(String name) {
		this.name = name;
	}
	
	public void setRoll(String roll) {
		defaultRoll = roll;
	}
	
	public void addEntry(int min, int max, String result) {
		getEntryList().add(new LookupEntry(min, max, result));
	}
	
	public String getLookup() {
		return getLookup(null);
	}

	public String getRoll() {
		return getDefaultRoll();
	}
	
	public String getName() {
		return name;
	}
	
	public String getLookup(String roll) {

		if (roll == null) {
			roll = getDefaultRoll();
		}
		
		int tableResult = 0;
		try {
			Result result = expressionParser.evaluate(roll);
			tableResult = Integer.parseInt(result.getValue().toString());

			Integer minmin = Integer.MAX_VALUE;
			Integer maxmax = Integer.MIN_VALUE;
			
			for (LookupEntry entry : getEntryList()) {
				if(entry.min < minmin) { minmin = entry.min; }
				if(entry.max > maxmax) { maxmax = entry.max; }
			}
			if(tableResult > maxmax) { tableResult = maxmax; }
			if(tableResult < minmin) { tableResult = minmin; }
			
			for (LookupEntry entry : getEntryList()) {
				if (tableResult >= entry.min && tableResult <= entry.max) {
					// Support for "/" commands
					return entry.result.startsWith("/") ? entry.result : "[" + tableResult + "] " + entry.result;
				}
			}
			
		} catch (ParserException pe) {
			return "Error parsing roll: " + roll;
		} catch (NumberFormatException nfe) {
			return "Error lookup up value: " + tableResult; 
		}
		
		return "Unknown table lookup: " + tableResult;
	}

	private String getDefaultRoll() {
		if (defaultRoll != null && defaultRoll.length() > 0) {
			return defaultRoll;
		}
		
		// Find the min and max range
		Integer min = null;
		Integer max = null;
		
		for (LookupEntry entry : getEntryList()) {
			if (min == null || entry.min < min) {
				min = entry.min;
			}
			if (max == null || entry.max > max) {
				max = entry.max;
			}
		}
		
		return "d" + (max - min + 1) + (min - 1 != 0 ? "+" + (min-1) : "");
	}
	
	private List<LookupEntry> getEntryList() {
		if (entryList == null) {
			entryList = new ArrayList<LookupEntry>();
		}
		return entryList;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (LookupEntry entry : getEntryList()) {
			
			if (entry.min == entry.max) {
				builder.append(entry.min);
			} else {
				builder.append(entry.min).append("-").append(entry.max);
			}
			builder.append("=").append(entry.result).append("\n");
		}
		
		return builder.toString();
	}
	
	public static class LookupEntry {
		
		private int min;
		private int max;
		
		private String result;
		
		public LookupEntry(int min, int max, String result) {
			this.min = min;
			this.max = max;
			this.result = result;
		}
	}
	
//	public static void main(String[] args) {
//		
//		LookupTable lt = new LookupTable("Testing");
//		lt.addEntry(1, 1, "You got 1!");
//		lt.addEntry(2, 2, "You got 2!");
//		lt.addEntry(3, 5, "You got something between 3 and 5");
//
//		for (int i = 0; i < 5; i++) {
//			System.out.println(lt.getLookup());
//		} 
//	}
}
