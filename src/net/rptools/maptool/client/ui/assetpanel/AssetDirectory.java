package net.rptools.maptool.client.ui.assetpanel;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JComponent;

import net.rptools.lib.util.FileUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.util.MD5Key;

public class AssetDirectory extends Directory {

	private Map<File, Future<Asset>> assetMap = new HashMap<File, Future<Asset>>();

	private static final Asset INVALID_ASSET = new Asset((MD5Key)null);
	
	private static ExecutorService assetLoaderService = Executors.newFixedThreadPool(3);
	
	public AssetDirectory(File directory, FilenameFilter fileFilter) {
		super(directory, fileFilter);
	}

	@Override
	public void refresh() {
		assetMap.clear();
		super.refresh();
	}
	
	/**
	 * Returns the asset associated with this file, or null if the
	 * file has not yet been loaded as an asset
	 * @param imageFile
	 * @return
	 */
	public Asset getAssetFor(File imageFile) {

		Future<Asset> future = assetMap.get(imageFile);
		if (future != null) {
			if (future.isDone()) {
				try {
					return future.get() != INVALID_ASSET ? future.get() : null;
				} catch (InterruptedException e) {
					return null;
				} catch (ExecutionException e) {
					return null;
				} 
			}
			
			// Not done loading yet, don't block
			return null; 
		}
		
		// load the asset in the background
		future = assetLoaderService.submit(new AssetLoader(imageFile));
		assetMap.put(imageFile, future);
		return null;
	}
	
	@Override
	protected Directory newDirectory(File directory, FilenameFilter fileFilter) {
		return new AssetDirectory(directory, fileFilter);
	}
	
	private static class AssetLoader implements Callable<Asset> {
		
		private File imageFile;
		
		public AssetLoader (File imageFile) {
			this.imageFile = imageFile;
		}
		
		public Asset call() throws Exception {

			Asset asset = null;
			try {
				asset = new Asset(FileUtil.loadFile(imageFile));
				if (AssetManager.hasAssetInMemory(asset.getId())) {
					asset = AssetManager.getAsset(asset.getId());;
				}
			} catch (Throwable t) {
                t.printStackTrace();
				asset = INVALID_ASSET;
			}
			
			// TODO: Find a better way to notify the app to redraw
			MapTool.getFrame().repaint();
			return asset;
		}
	}
}
