/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *  
 *	http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.rptools.maptool.model.transform.campaign;

import java.util.regex.Pattern;

import net.rptools.lib.ModelVersionTransformation;

public class TokenPropertyMapTransform implements ModelVersionTransformation {
	private static final String blockStart = "<propertyMap>\\s*<store";
	private static final String blockEnd = "</propertyMap>";
	private static final String regex = blockStart + "(/?>.*?)" + blockEnd;
	private static final String replacement = "<propertyMapCI><store$1</propertyMapCI>";

	private static final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);

	/**
	 * Delete the block containing the now-obsolete exportInfo class data, since there is no place to put it (and
	 * therefore generates an XStream error)
	 */
	public String transform(String xml) {
		// Same as: return xml.replaceAll(regex, replacement);
		// except that we can specify the flag DOTALL
		return pattern.matcher(xml).replaceAll(replacement);
	}
}
