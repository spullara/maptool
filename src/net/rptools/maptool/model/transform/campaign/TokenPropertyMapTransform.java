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
