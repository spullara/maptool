package net.rptools.maptool.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.concurrent.CountDownLatch;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetAvailableListener;
import net.rptools.maptool.model.AssetManager;

import org.apache.log4j.Logger;

/**
 * Support "asset://" in Swing components </p>
 * 
 * @author Azhrei
 * 
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
			
			// Need to make sure the image is available
			// TODO: Create a AssetManager.getAssetAndWait(id) and put thise block in it
			final CountDownLatch latch = new CountDownLatch(1);
			AssetManager.getAssetAsynchronously(assetId, new AssetAvailableListener() {
				public void assetAvailable(MD5Key key) {
					if (key.equals(assetId)) {
						latch.countDown();
					}
				}
			});

			byte[] data = null;
			try {
				latch.await();
				
				Asset asset = AssetManager.getAsset(assetId);
				if (asset != null && asset.getImage() != null) {
					data = asset.getImage();
				} else {
					log.error("Could not find asset: " + assetId);
					data = new byte[]{};
				}
			} catch (InterruptedException e) {
				log.error("Could not get asset: " + assetId, e);
				data = new byte[]{};
			}
			
			return new ByteArrayInputStream(data);
		}
	}
}
