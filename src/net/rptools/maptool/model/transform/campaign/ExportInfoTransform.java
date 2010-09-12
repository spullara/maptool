package net.rptools.maptool.model.transform.campaign;

import net.rptools.lib.ModelVersionTransformation;
import java.util.regex.Pattern;
/**
 * This should be applied to any campaign file version 1.3.70 and earlier
 * due to the deletion of the ExportInfo class afterwards.
 */
public class ExportInfoTransform implements ModelVersionTransformation {
	private static final String blockStart = "<exportInfo>";
	private static final String blockEnd   = "</exportInfo>";
	private static final String regex = blockStart + ".*" + blockEnd;
	private static final String replacement = ""; 

	/**
	 * Delete the block containing the now-obsolete exportInfo class data, since
	 * there is no place to put it (and therefore generates an xstream error)
	*/
	public String transform(String xml) {
	 
		// Same as: return xml.replaceAll(regex, replacement); 
		// except that we can specify the flag DOTALL
		return Pattern.compile(regex, Pattern.DOTALL).matcher(xml).replaceAll(replacement);
	}
}
