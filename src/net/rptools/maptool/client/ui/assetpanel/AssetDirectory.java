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
package net.rptools.maptool.client.ui.assetpanel;

import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.util.PersistenceUtil;

public class AssetDirectory extends Directory {

	public static final String PROPERTY_IMAGE_LOADED = "imageLoaded";

	private final Map<File, FutureTask<Image>> imageMap = new HashMap<File, FutureTask<Image>>();

	private static final Image INVALID_IMAGE = new BufferedImage(1, 1, Transparency.OPAQUE);

	private static ExecutorService largeImageLoaderService = Executors.newFixedThreadPool(1);
	private static ExecutorService smallImageLoaderService = Executors.newFixedThreadPool(2);

	private AtomicBoolean continueProcessing = new AtomicBoolean(true);

	public AssetDirectory(File directory, FilenameFilter fileFilter) {
		super(directory, fileFilter);
	}

	@Override
	public String toString() {
		return getPath().getName();
	}

	@Override
	public void refresh() {
		imageMap.clear();

		// Tell any in-progress processing to stop
		AtomicBoolean oldBool = continueProcessing;
		continueProcessing = new AtomicBoolean(true);
		oldBool.set(false);

		super.refresh();
	}

	/**
	 * Returns the asset associated with this file, or null if the file has not yet been loaded as an asset
	 * 
	 * @param imageFile
	 * @return
	 */
	public Image getImageFor(File imageFile) {
		FutureTask<Image> future = imageMap.get(imageFile);
		if (future != null) {
			if (future.isDone()) {
				try {
					return future.get() != INVALID_IMAGE ? future.get() : null;
				} catch (InterruptedException e) {
					// TODO: need to indicate a broken image
					return null;
				} catch (ExecutionException e) {
					// TODO: need to indicate a broken image
					return null;
				}
			}
			// Not done loading yet, don't block
			return null;
		}
		// load the asset in the background
		future = new FutureTask<Image>(new ImageLoader(imageFile)) {
			@Override
			protected void done() {
				firePropertyChangeEvent(new PropertyChangeEvent(AssetDirectory.this, PROPERTY_IMAGE_LOADED, false, true));
			}
		};
		if (imageFile.length() < 30 * 1024) {
			smallImageLoaderService.execute(future);
		} else {
			largeImageLoaderService.execute(future);
		}
		imageMap.put(imageFile, future);
		return null;
	}

	@Override
	protected Directory newDirectory(File directory, FilenameFilter fileFilter) {
		return new AssetDirectory(directory, fileFilter);
	}

	private class ImageLoader implements Callable<Image> {
		private final File imageFile;

		public ImageLoader(File imageFile) {
			this.imageFile = imageFile;
		}

		public Image call() throws Exception {
			// Have we been orphaned ?
			if (!continueProcessing.get()) {
				return null;
			}
			// Load it up
			Image thumbnail = null;
			try {
				if (imageFile.getName().toLowerCase().endsWith(Token.FILE_EXTENSION)) {
					thumbnail = PersistenceUtil.getTokenThumbnail(imageFile);
				} else {
					thumbnail = MapTool.getThumbnailManager().getThumbnail(imageFile);
				}
			} catch (Throwable t) {
				t.printStackTrace();
				thumbnail = INVALID_IMAGE;
			}
			return thumbnail;
		}
	}
}
