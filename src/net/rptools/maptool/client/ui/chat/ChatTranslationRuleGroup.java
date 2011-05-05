/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
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
