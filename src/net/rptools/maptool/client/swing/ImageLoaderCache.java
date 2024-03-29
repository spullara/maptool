/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.client.swing;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.rptools.lib.MD5Key;
import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.util.ImageManager;

import org.apache.log4j.Logger;

public class ImageLoaderCache {
	private static final Logger log = Logger.getLogger(ImageLoaderCache.class);

	private final Map<String, Image> imageMap = new HashMap<String, Image>();

	public void flush() {
		imageMap.clear();
	}

	public Image get(URL url, ImageObserver... observers) {
		// URLs take a huge amount of time in equals(), so simplify by
		// converting to a string
		if (url == null) {
			log.debug("ImageLoaderCache.get(null), using BROKEN_IMAGE");
			return ImageManager.BROKEN_IMAGE;
		}
		Image image = imageMap.get(url.toString());
		if (image == null) {
			String protocol = url.getProtocol();
			String path = url.getHost() + url.getPath();

			if ("cp".equals(protocol)) {
				try {
					image = ImageUtil.getImage(path);
				} catch (IOException ioe) {
					MapTool.showWarning("Can't find 'cp://" + url.toString() + "' in image cache?!", ioe);
				}
			} else if ("asset".equals(protocol)) {
				// Look for size request
				int index = path.indexOf("-");
				int size = -1;
				if (index >= 0) {
					String szStr = path.substring(index + 1);
					path = path.substring(0, index);
					size = Integer.parseInt(szStr);
				}
				image = ImageManager.getImage(new MD5Key(path), observers);
				boolean imageLoaded = image != ImageManager.TRANSFERING_IMAGE;
				if (!imageLoaded) {
					size = 38;
				}
				if (size > 0) {
					Dimension sz = new Dimension(image.getWidth(null), image.getHeight(null));
					SwingUtil.constrainTo(sz, size);

					BufferedImage img = new BufferedImage(sz.width, sz.height, ImageUtil.pickBestTransparency(image));
					Graphics2D g = img.createGraphics();
					g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					g.drawImage(image, 0, 0, sz.width, sz.height, null);
					g.dispose();

					image = img;
				}
				if (imageLoaded) {
					// Don't have to load it again
					imageMap.put(url.toString(), image);
				}
				return image;
			} else {
				// Normal method
				image = Toolkit.getDefaultToolkit().createImage(url);
			}
			imageMap.put(url.toString(), image);
		}
		return image;
	}
}
