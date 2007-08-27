package net.rptools.maptool.client.swing;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import net.rptools.lib.MD5Key;
import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.util.ImageManager;

public class HTMLPanelImageCache extends Dictionary<URL, Image> {

	private Map<String, Image> imageMap = new HashMap<String, Image>();

	public void flush() {
		imageMap.clear();
	}
	
	@Override
	public Enumeration elements() {
		// Not used
		return null;
	}

	@Override
	public Image get(Object key) {
		URL url = (URL) key;

		// URLs take a huge amount of time in equals(), so simplify by
		// converting to a string
		Image image = imageMap.get(url.toString());
		if (image == null) {

			String protocol = url.getProtocol();
			String path = url.getHost() + url.getPath();

			if ("cp".equals(protocol)) {
				try {
					image = ImageUtil.getImage(path);
				} catch (IOException ioe) {
					// TODO: Show my own broken image
					ioe.printStackTrace();
				}
			} else if ("asset".equals(protocol)) {

				image = ImageManager.getImageAndWait(AssetManager
						.getAsset(new MD5Key(path)));

			} else {

				// Normal method
				image = Toolkit.getDefaultToolkit().createImage(url);
			}

			imageMap.put(url.toString(), image);
		}

		return image;
	}

	@Override
	public boolean isEmpty() {
		// Not used
		return false;
	}

	@Override
	public Enumeration keys() {
		// Not used
		return null;
	}

	@Override
	public Image put(URL key, Image value) {
		// Not used
		return null;
	}

	@Override
	public Image remove(Object key) {
		// Not used
		return null;
	}

	@Override
	public int size() {
		// Not used
		return 0;
	}
}
