package net.rptools.maptool.client.ui.chat;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class ChatProcessor {

	private List<ChatTranslationRule> translationRuleList = new ArrayList<ChatTranslationRule>();
	
	public void addTranslationRule(ChatTranslationRule rule) {
		translationRuleList.add(rule);
	}
	
	public void removeTranslationRule(ChatTranslationRule rule) {
		translationRuleList.remove(rule);
	}
	
	public String process(String incoming) {
		for (ChatTranslationRule rule : translationRuleList) {
			incoming = rule.translate(incoming);
		}
		return incoming;
	}
	
	public void install(Properties translationMap) {

		for (Enumeration e = translationMap.propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = translationMap.getProperty(key);
			
			addTranslationRule(new RegularExpressionTranslationRule(key, value));
		}
	}
}
