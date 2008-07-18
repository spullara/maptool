package net.rptools.maptool.client.macro;

import java.util.LinkedList;
import java.util.List;

public class MacroContext {

	private List<String> transformHistory;
	
	public void addTransform(String transform) {
		if (transformHistory == null) {
			transformHistory = new LinkedList<String>();
		}
		transformHistory.add(transform);
	}
	
	public List<String> getTransformationHistory() {
		return transformHistory;
	}
}
