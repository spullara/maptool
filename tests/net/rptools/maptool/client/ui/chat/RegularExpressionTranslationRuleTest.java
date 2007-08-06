package net.rptools.maptool.client.ui.chat;

import junit.framework.TestCase;

public class RegularExpressionTranslationRuleTest extends TestCase {

	public void testIt() throws Exception {

		ChatTranslationRule rule = new RegularExpressionTranslationRule("one", "two");
		assertEquals("two two three", rule.translate("one two three"));
		
		rule = new RegularExpressionTranslationRule("(t.o)", "*$1*");
		assertEquals("one *two* three", rule.translate("one two three"));
		
	}
}
