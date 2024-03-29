/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.model;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;

import net.rptools.lib.FileUtil;
import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.MapTool;

public class AssetLoader {
	
	public enum RepoState {
		ACTIVE,
		BAD_URL,
		INDX_BAD_FORMAT,
		UNAVAILABLE
	}

	private static final File REPO_CACHE_DIR = AppUtil.getAppHome("repoindx");
	
	private ExecutorService retrievalThreadPool = Executors.newFixedThreadPool(3);
	private Set<MD5Key> requestedIdSet = new HashSet<MD5Key>();
	private Map<String, Map<String, String>> repositoryMap = new HashMap<String, Map<String, String>>();
	private Map<String, RepoState> repositoryStateMap = new HashMap<String, RepoState>();
	
	public synchronized void addRepository(String repository) {
		// Assume active, unless we find otherwise during setup
		repositoryStateMap.put(repository, RepoState.ACTIVE);
		repositoryMap.put(repository, getIndexMap(repository));
	}

	public synchronized void removeRepository(String repository) {
		repositoryStateMap.remove(repository);
		repositoryMap.remove(repository);
	}
	
	public synchronized void removeAllRepositories() {
		repositoryMap.clear();
		repositoryStateMap.clear();
	}
	
	public synchronized boolean isIdRequested(MD5Key id) {
		return requestedIdSet.contains(id);
	}

	/**
	 * <p>
	 * This method returns the mapping from MD5Key to asset name on the server for
	 * the given repository.
	 * </p>
	 * <p>
	 * The return value is an immutable mapping so as
	 * to prevent any chance of the mapping being corrupted by the caller.
	 * </p>
	 * @param repo the name of the repository, probably from the campaign properties
	 * @return an immutable <code>Map&lt;String, String></code>
	 */
	public Map<String, String> getRepositoryMap(String repo) {
		return repositoryMap.get(repo);
	}

	/**
	 * <p>
	 * This method extracts an asset map from the given repository.
	 * </p>
	 * <p>
	 * It starts by checking to see if the repository index is already in the cache.  If not, it
	 * makes a network connection and grabs it, calling
	 * {@link #storeIndexFile(String, byte[])} to store it into the cache.
	 * </p>
	 * <p>
	 * Once the index file has been located, {@link #parseIndex(List)} is called to convert
	 * the text file into a <code>Map&lt;String, Sting></code> for the return value.
	 * </p>
	 * @param repository
	 * @return
	 */
	protected Map<String, String> getIndexMap(String repository) {
		RepoState status = RepoState.ACTIVE;
		Map<String, String> indexMap = new HashMap<String, String>();
		try {
			byte[] index = null;
			if (!hasCurrentIndexFile(repository)) {
				URL url = new URL(repository);
				index = FileUtil.getBytes(url);
				storeIndexFile(repository, index);
			} else {
				index = FileUtils.readFileToByteArray(getRepoIndexFile(repository));
			}
			indexMap = parseIndex(decode(index));
		} catch (MalformedURLException e) {
			System.out.println("Invalid repository URL: " + repository);
			status = RepoState.BAD_URL;
		} catch (IOException e) {
			System.out.println("I/O error retrieving/saving index for '" + repository + "': " + e);
			status = RepoState.UNAVAILABLE;
		} catch (Throwable t) {
			System.out.println("Could not retrieve index for '" + repository + "': " + t);
			t.printStackTrace();
			status = RepoState.UNAVAILABLE;
		}
		repositoryStateMap.put(repository, status);
		return indexMap;
	}
	
	protected List<String> decode(byte[] indexData) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(indexData))));

		List<String> list = new ArrayList<String>();
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			list.add(line);
		}
		return list;
	}

	protected Map<String, String> parseIndex(List<String> index) {
		Map<String, String> idxMap = new HashMap<String, String>();
		
		for (String line : index) {
			if (line == null) {
				continue;
			}
			line = line.trim();
			if (line.length() == 0) {
				continue;
			}
			
			String id = line.substring(0, 32);
			String ref = line.substring(33).trim();
			
			idxMap.put(id, ref);
		}
		return idxMap;
	}

	/**
	 * <p>
	 * Converts the specified repository of assets into an index file that can be uploaded
	 * and used as the <b>index.gz</b> (after being compressed, of course).
	 * </p>
	 * @param repository
	 * @return
	 */
	protected byte[] createIndexFile(String repository) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(bout);
		Map<String, String> assets = repositoryMap.get(repository);
		for (String asset : assets.keySet()) {
			pw.println(asset + " " + assets.get(asset));
		}
		pw.close();
		return bout.toByteArray();
	}
	
	protected void storeIndexFile(String repository, byte[] data) throws IOException {
		File file = getRepoIndexFile(repository);
		FileUtils.writeByteArrayToFile(file, data);
	}
	
	protected boolean hasCurrentIndexFile(String repository) {
		// TODO: make this check timestamps, or update once a day or something like that
		return getRepoIndexFile(repository).exists();
	}
	
	protected File getRepoIndexFile(String repository) {
		return new File(REPO_CACHE_DIR.getAbsolutePath() + "/" + new MD5Key(repository.getBytes()));
	}
	
	public synchronized void requestAsset(MD5Key id) {
		retrievalThreadPool.submit(new ImageRetrievalRequest(id, createRequestQueue(id)));
		requestedIdSet.add(id);
	}
	
	public synchronized void completeRequest(MD5Key id) {
		requestedIdSet.remove(id);
	}
	
	protected List<String> createRequestQueue(MD5Key id) {
		List<String> requestList = new LinkedList<String>();
		for (java.util.Map.Entry<String, Map<String, String>> entry : repositoryMap.entrySet()) {
			
			String repo = entry.getKey();
			if (repositoryStateMap.get(repo) == RepoState.ACTIVE && entry.getValue().containsKey(id.toString())) {
				requestList.add(repo);
			}
		}
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
				
				String repo = repositoryQueue.remove(0);
				Map<String, String> repoMap = repositoryMap.get(repo);
				if (repoMap == null) {
					// Must have been removed while we were asleep
					continue;
				}
				
				String ref = repoMap.get(id.toString());
				if (ref == null) {
					// Must have updated while we were asleep
					continue;
				}
				
				// Create the reference, need to work relative to the repo indx file
				int split = repo.lastIndexOf('/');
				
				try {
					// make the URL http safe
					String path = repo.substring(0, split+1) + ref;
					path = path.replaceAll(" ", "%20");

					// Get the content
					byte[] data = FileUtil.getBytes(new URL(path));
					
					// Verify the content
					MD5Key sum = new MD5Key(data);
					if (!sum.equals(id)) {
						// Bad file
						// TODO: Does this mean it's time to update our cache of the index.gz?
						// (See hasCurrentIndexFile() for the comment there.)
						System.err.println("Downloaded invalid file from: " + path);
						
						// Try a different repo
						continue;
					}
					
					// Done
					split = ref.lastIndexOf('/');
					if (split >= 0) {
						ref = ref.substring(split+1);
					}
//					System.out.println("Got " + id + " from " + repo);
					ref = FileUtil.getNameWithoutExtension(ref);
					AssetManager.putAsset(new Asset(ref, data));

					completeRequest(id);
					return;
				} catch (IOException ioe) {
					// Well, try a different repo
//					ioe.printStackTrace();
					continue;
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}

			// Last resort, ask the MT server
			// We can drop off the end of this runnable because it'll background load the 
			// image from the server
//			System.out.println("Got " + id + " from MT");
	        MapTool.serverCommand().getAsset(id);
		}
	}
	
}
