package net.rptools.maptool.client.ui.chat;

import java.util.regex.Pattern;

public class RegularExpressionTranslationRule implements ChatTranslationRule {

	private Pattern pattern;
	private String replaceWith;

	public RegularExpressionTranslationRule(String pattern, String replaceWith) {
		try {
			this.pattern = Pattern.compile(pattern);
		} catch (Exception e) {
			System.err.println("Could not parse regex: " + pattern);
		}
		this.replaceWith = replaceWith;
	}
	
	public String translate(String incoming) {
		if (pattern == null) {
			return incoming;
		}

		return pattern.matcher(incoming).replaceAll(replaceWith);
	}

}
