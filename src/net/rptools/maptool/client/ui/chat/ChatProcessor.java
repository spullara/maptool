package net.rptools.maptool.client.ui.chat;

import java.util.ArrayList;
import java.util.List;

public class ChatProcessor {

	private List<ChatTranslationRuleGroup> translationRuleGroupList = new ArrayList<ChatTranslationRuleGroup>();
	
	public String process(String incoming) {
		if (incoming == null) {
			return null;
		}
		
		for (ChatTranslationRuleGroup ruleGroup : translationRuleGroupList) {
			if (!ruleGroup.isEnabled()) {
				continue;
			}
			incoming = ruleGroup.translate(incoming);
		}
		return incoming;
	}
	
	public void install(ChatTranslationRuleGroup ruleGroup) {
		translationRuleGroupList.add(ruleGroup);
	}
}
