package net.rptools.maptool.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.rptools.common.expression.ExpressionParser;
import net.rptools.common.expression.Result;
import net.rptools.lib.MD5Key;
import net.rptools.parser.ParserException;

public class LookupTable {

	private static ExpressionParser expressionParser = new ExpressionParser();
	
	private List<LookupEntry> entryList;
	private String name;
	private String defaultRoll;
	private MD5Key tableImage;

	
	public LookupTable() {
	}
	
	public LookupTable(LookupTable table) {
		name = table.name;
		defaultRoll = table.defaultRoll;
		tableImage = table.tableImage;
		
		if (table.entryList != null) {
			getInternalEntryList().addAll(table.entryList);
		}
	}
	
	public void setRoll(String roll) {
		defaultRoll = roll;
	}
	
	public void clearEntries() {
		getInternalEntryList().clear();
	}
	
	public void addEntry(int min, int max, String result, MD5Key imageId) {
		getInternalEntryList().add(new LookupEntry(min, max, result, imageId));
	}
	
	public LookupEntry getLookup() throws ParserException {
		return getLookup(null);
	}

	public String getRoll() {
		return getDefaultRoll();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public LookupEntry getLookup(String roll) throws ParserException {

		if (roll == null) {
			roll = getDefaultRoll();
		}
		
		int tableResult = 0;
		try {
			Result result = expressionParser.evaluate(roll);
			tableResult = Integer.parseInt(result.getValue().toString());

			Integer minmin = Integer.MAX_VALUE;
			Integer maxmax = Integer.MIN_VALUE;
			
			for (LookupEntry entry : getInternalEntryList()) {
				if(entry.min < minmin) { minmin = entry.min; }
				if(entry.max > maxmax) { maxmax = entry.max; }
			}
			if(tableResult > maxmax) { tableResult = maxmax; }
			if(tableResult < minmin) { tableResult = minmin; }
			
			for (LookupEntry entry : getInternalEntryList()) {
				if (tableResult >= entry.min && tableResult <= entry.max) {
					// Support for "/" commands
					return entry;
				}
			}
			
		} catch (NumberFormatException nfe) {
			throw new ParserException("Error lookup up value: " + tableResult); 
		}
		
		throw new ParserException("Unknown table lookup: " + tableResult);
	}

	private String getDefaultRoll() {
		if (defaultRoll != null && defaultRoll.length() > 0) {
			return defaultRoll;
		}
		
		// Find the min and max range
		Integer min = null;
		Integer max = null;
		
		for (LookupEntry entry : getInternalEntryList()) {
			if (min == null || entry.min < min) {
				min = entry.min;
			}
			if (max == null || entry.max > max) {
				max = entry.max;
			}
		}
		
		return min != null ? "d" + (max - min + 1) + (min - 1 != 0 ? "+" + (min-1) : "") : "";
	}
	
	private List<LookupEntry> getInternalEntryList() {
		if (entryList == null) {
			entryList = new ArrayList<LookupEntry>();
		}
		return entryList;
	}

	public List<LookupEntry> getEntryList() {
		return Collections.unmodifiableList(getInternalEntryList());
	}
	
	public MD5Key getTableImage() {
		return tableImage;
	}

	public void setTableImage(MD5Key tableImage) {
		this.tableImage = tableImage;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (LookupEntry entry : getInternalEntryList()) {
			
			if (entry.min == entry.max) {
				builder.append(entry.min);
			} else {
				builder.append(entry.min).append("-").append(entry.max);
			}
			builder.append("=").append(entry.value).append("\n");
		}
		
		return builder.toString();
	}
	
	public static class LookupEntry {
		
		private int min;
		private int max;
		
		private String value;
		
		private MD5Key imageId;
		
		/**
		 * @Deprecated here to prevent xstream from breaking b24-b25
		 */
		private String result;
		
		public LookupEntry(int min, int max, String result, MD5Key imageId) {
			this.min = min;
			this.max = max;
			this.value = result;
			this.imageId = imageId;
		}

		public MD5Key getImageId() {
			return imageId;
		}

		public int getMax() {
			return max;
		}

		public int getMin() {
			return min;
		}

		public String getValue() {
			// Temporary fix to convert b24 to b25
			if (result != null) {
				value = result;
				result = null;
			}
			return value;
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
