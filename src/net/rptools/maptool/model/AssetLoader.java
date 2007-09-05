package net.rptools.maptool.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.rptools.lib.FileUtil;
import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.MapTool;

public class AssetLoader {
	
	public enum RepoState {
		ACTIVE,
		BAD_URL,
		INDX_BAD_FORMAT,
		UNAVAILABLE
	}

	private ExecutorService retrievalThreadPool = Executors.newFixedThreadPool(3);
	private Set<MD5Key> requestedIdSet = new HashSet<MD5Key>();
	private Map<String, Map<String, String>> repositoryMap = new HashMap<String, Map<String, String>>();
	private Map<String, RepoState> repositoryStateMap = new HashMap<String, RepoState>();
	
	public synchronized void addRepository(String repository) {
		
		try {
			URL url = new URL(repository);
			String index = new String(FileUtil.getBytes(url));
			
			repositoryMap.put(repository, createIndexMap(index));
			
		} catch (MalformedURLException e) {
			repositoryStateMap.put(repository, RepoState.BAD_URL);
		} catch (IOException e) {
			repositoryStateMap.put(repository, RepoState.UNAVAILABLE);
		}
	}
	
	protected Map<String, String> createIndexMap(String index) {
		
		Map<String, String> indxMap = new HashMap<String, String>();

		
		
		return indxMap;
	}
	
	public synchronized void requestAsset(MD5Key id) {
		retrievalThreadPool.submit(new ImageRetrievalRequest(id, createRequestQueue()));
		requestedIdSet.add(id);
	}
	
	public synchronized void completeRequest(MD5Key id) {
		requestedIdSet.remove(id);
	}
	
	protected List<String> createRequestQueue() {
		
		List<String> requestList = new LinkedList<String>();
		
		return requestList;
	}

	private class ImageRetrievalRequest implements Runnable {
		MD5Key id;
		List<String> repositoryQueue;

		public ImageRetrievalRequest(MD5Key id, List<String> repositoryQueue) {
			this.id = id;
			this.repositoryQueue = repositoryQueue;
		}
		
		public void run() {
			
			while (repositoryQueue.size() > 0) {
				
				String repoUrl = repositoryQueue.remove(0);
				
				
			}

			// Last resort, ask the MT server
			// We can drop off the end of this runnable because it'll background load the 
			// image from the server
	        MapTool.serverCommand().getAsset(id);
		}
	}
}
