package net.rptools.maptool.model.transform.campaign;

import net.rptools.lib.ModelVersionTransformation;

public class PCVisionTransform implements ModelVersionTransformation {

	public String transform(String xml) {

		int index = 0;
		while (xml.indexOf("<tokenType>PC", index) > 0) {
			int sightPos = xml.indexOf("<hasSight>", xml.indexOf("<tokenType>PC", index)) + "<hasSight>".length();
			if (xml.charAt(sightPos) == 'f') {
				String pre = xml.substring(0, sightPos);
				String post = xml.substring(sightPos + "false".length());
				
				xml = pre + "true" + post;
			}
			
			index = sightPos;
		}

		return xml;
	}

}
