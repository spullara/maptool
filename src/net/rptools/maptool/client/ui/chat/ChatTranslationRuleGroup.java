package net.rptools.maptool.client.ui.chat;

import java.util.LinkedList;
import java.util.List;

public class ChatTranslationRuleGroup {

	private String name;
	private List<ChatTranslationRule> translationRuleList = new LinkedList<ChatTranslationRule>();

	public ChatTranslationRuleGroup(String name) {
		this(name, null);
	}
	
	public ChatTranslationRuleGroup(String name, List<ChatTranslationRule> translationRuleList) {
		this.name = name;
		
		if (translationRuleList != null) {
			this.translationRuleList.addAll(translationRuleList) ;
		}
	}

	public void addRule(ChatTranslationRule rule) {
		translationRuleList.add(rule);
	}
	
	public boolean isEnabled() {
		return true;
	}
	
	public String getName() {
		return name;
	}
	
	public String translate(String incoming) {

		if (incoming == null) {
			return null;
		}
		
		for (ChatTranslationRule rule : translationRuleList) {
			incoming = rule.translate(incoming);
		}

		return incoming;
	}
	
}
