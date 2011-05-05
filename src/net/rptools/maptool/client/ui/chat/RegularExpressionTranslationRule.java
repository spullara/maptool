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

import java.util.regex.Pattern;

public class RegularExpressionTranslationRule extends AbstractChatTranslationRule {

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
