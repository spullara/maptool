package net.rptools.maptool.client.macro;

import java.util.List;

import junit.framework.TestCase;

public class MacroManagerTest extends TestCase {

	public void testSplit() throws Exception {
		
		assertEquals(0, MacroManager.split("").size());

		compare(MacroManager.split("one"), "one");
		compare(MacroManager.split(" one"), "one");
		compare(MacroManager.split("one "), "one");
		compare(MacroManager.split(" one "), "one");

		compare(MacroManager.split("one two"), "one", "two");
		compare(MacroManager.split("one two three"), "one", "two", "three");
		compare(MacroManager.split("  one   two   three  "), "one", "two", "three");
		
		compare(MacroManager.split("\"one\""), "one");
		compare(MacroManager.split("\"one two\""), "one two");
		compare(MacroManager.split("\"one two\" three"), "one two", "three");

		compare(MacroManager.split("\"one \\\"two\\\"\" three"), "one \"two\"", "three");
	}
	
	public void testPerformSubstitution() throws Exception {
		
		compare("", "", "");
		compare("one", "one", "one");
		
		compare("one $1", "one", "one one");
		compare("one $2 $1", "one two", "one two one");

		compare("one ${1}", "one", "one one");
		compare("one ${2} ${1}", "one two", "one two one");
	}

	private void compare(String text, String details, String result) {
		
		String subResult = MacroManager.performSubstitution(text, details);
		assertEquals("\"" + subResult + "\" != \"" + result + "\"", result, subResult);
	}
	
	private void compare(List<String> parsed, String... expected) {
		if (parsed.size() != expected.length) {
			fail("Sizes do not match:" + parsed);
		} 
		
		for (int i = 0; i < parsed.size(); i++) {
			if (!parsed.get(i).equals(expected[i])) {
				fail ("Does not match: " + parsed.get(i) + " != " + expected[i]);
			}
		}
	}
}
