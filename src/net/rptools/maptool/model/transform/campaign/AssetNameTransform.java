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

import net.rptools.lib.ModelVersionTransformation;

/**
 * This transform is for asset filenames, not the actual XML data.  So the XML passed
 * to the {@link #transform(String)} method should be the asset's base name, typically
 * <code>ASSET_DIR + key</code>.  This means that this transform should <b>NOT</b>
 * be registered with any ModelVersionManager or it will be executed in the wrong
 * context.
 *
 * pre-1.3.51:  asset names had ".dat" tacked onto the end and held only binary data
 * 1.3.51-63:  assets were stored in XML under their asset name, no extension
 * 1.3.64+:  asset objects are in XML (name, MD5key), but the image is in another
 * file with the asset's image type as an extension (.jpeg, .png)
 *
 * @author frank
 */
public class AssetNameTransform implements ModelVersionTransformation {
	private final String regexOld;
	private final String regexNew;

	public AssetNameTransform(String from, String to) {
		regexOld = from;
		regexNew = to;
	}
	public String transform(String name) {
		return name.replace(regexOld, regexNew);
	}
}
