package net.rptools.maptool.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;

/**
 * Support "asset://" in Swing components </p>
 * 
 * @author Azhrei
 * 
 */
public class AssetURLStreamHandler extends URLStreamHandler {

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		return new AssetURLConnection(u);
	}

	private static class AssetURLConnection extends URLConnection {
		private byte[] data;

		public AssetURLConnection(URL url) {
			super(url);

			String host = url.getHost();
			String file = url.getFile();
			// TODO Do we need to wait for the asset to load?
			Asset asset = AssetManager.getAsset(new MD5Key(host));
			data = asset.getImage();
		}

		@Override
		public void connect() throws IOException {
			// Nothing to do
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(data);
		}
	}
}
