package net.rptools.maptool.util;

import junit.framework.TestCase;

public class StringUtilTest extends TestCase {

	
	public void testCountOccurances() throws Exception {
		
		String str = "<div>";
		
		assertEquals(0, StringUtil.countOccurances("", str));
		assertEquals(1, StringUtil.countOccurances("<div>", str));
		assertEquals(1, StringUtil.countOccurances("one<div>two", str));
		assertEquals(2, StringUtil.countOccurances("one<div>two<div>three", str));
		assertEquals(3, StringUtil.countOccurances("one<div>two<div>three<div>", str));
		
	}
}
