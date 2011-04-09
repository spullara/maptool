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
package net.rptools.maptool.client.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

/**
 * An ImageStream created from a Component. 
 * The Component may be very large in terms of XY size: it will
 * be rendered piecemeal by modifying its bounds().
 * @author mcl
 */
/*public class PNGInputStream extends InputStream {

	Component largeComponent;
	Rectangle origBounds;
	Dimension origSize;

	ImageWriter pngWriter = null; 

	*//**
	 * @param largeComponent the Component to be turned into a PNG input stream
	 *//*
	public PNGInputStream(Component c) {
		largeComponent = c;

		origBounds = largeComponent.getBounds();
		origSize   = largeComponent.getSize();
	}

	@Override
	public int read() throws IOException {
		if (pngWriter != null) {
		}
		else {
			pngWriter = (ImageWriter)ImageIO.getImageWritersByFormatName("png").next();
			pngWriter.setOutput(output);
			IIOImage image = new IIOImage(cachedZoneImage, null, null);
			pngWriter.write(null, image, iwp);
		}
		return 0;
	}
}
*/
