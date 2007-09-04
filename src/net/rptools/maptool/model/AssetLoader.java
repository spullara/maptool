package net.rptools.maptool.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.MapTool;

public class AssetLoader {

	private ExecutorService retrievalThreadPool = Executors.newFixedThreadPool(3);
	private Set<MD5Key> requestedIdSet = new HashSet<MD5Key>();
	
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
