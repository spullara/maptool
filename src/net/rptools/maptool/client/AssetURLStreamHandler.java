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
package net.rptools.maptool.client;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;

import net.rptools.lib.MD5Key;
import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.util.ImageManager;

import org.apache.log4j.Logger;

/**
 * Support "asset://" in Swing components
 * 
 * @author Azhrei
 */
public class AssetURLStreamHandler extends URLStreamHandler {

	private static final Logger log = Logger.getLogger(AssetURLStreamHandler.class);

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		return new AssetURLConnection(u);
	}

	private static class AssetURLConnection extends URLConnection {

		public AssetURLConnection(URL url) {
			super(url);
		}

		@Override
		public void connect() throws IOException {
			// Nothing to do
		}

		@Override
		public InputStream getInputStream() throws IOException {
			final MD5Key assetId = new MD5Key(url.getHost());
			String query = url.getQuery();
			Map<String, String> var = new HashMap<String, String>();

			while (query != null && query.length() > 1) {
				int delim = query.indexOf('=');
				if (delim > 0) {
					String name = query.substring(0, delim).trim();
					String value;
					int delim2 = query.indexOf('&', delim);
					if (delim2 < 0) {
						value = query.substring(delim+1);
						query = null;
					} else {
						value = query.substring(delim+1, delim2);
						query = query.substring(delim2+1);
					}
					var.put(name, value);
				} else
					break;
			}
			// Default value is 0:  scale the dimension to preserve the aspect ratio
			// Use -1 to indicate that the original dimension from the image should be used
			int scaleW = var.get("width") != null ? Integer.valueOf(var.get("width")) : 0;
			int scaleH = var.get("height") != null ? Integer.valueOf(var.get("height")) : 0;

			// Need to make sure the image is available
			// TODO: Create a AssetManager.getAssetAndWait(id) and put this block in it
//			final CountDownLatch latch = new CountDownLatch(1);
//			AssetManager.getAssetAsynchronously(assetId, new AssetAvailableListener() {
//				public void assetAvailable(MD5Key key) {
//					if (key.equals(assetId)) {
//						latch.countDown();
//					}
//				}
//			});

			byte[] data = null;
//			latch.await();
			BufferedImage img = ImageManager.getImageAndWait(assetId);

			Asset asset = AssetManager.getAsset(assetId);
			if (asset != null && asset.getImage() != null) {
				if (scaleW > 0 || scaleH > 0) {
					switch (scaleW) {
					case -1 : scaleW = img.getWidth(); break;
					case  0: scaleW = img.getWidth() * scaleH / img.getHeight(); break;
					}
					switch (scaleH) {
					case -1 : scaleH = img.getHeight(); break;
					case  0: scaleH = img.getHeight() * scaleW / img.getWidth(); break;
					}
					BufferedImage bimg = new BufferedImage(scaleW, scaleH, BufferedImage.TRANSLUCENT);
					Graphics2D g = bimg.createGraphics();
					g.drawImage(img, 0, 0, scaleW, scaleH, null);
					g.dispose();
					data = ImageUtil.imageToBytes(bimg);
				} else
					data = asset.getImage();
			} else {
				log.error("Could not find asset: " + assetId);
				data = new byte[]{};
			}
			return new ByteArrayInputStream(data);
		}
	}
}
