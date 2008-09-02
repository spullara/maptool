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
package net.rptools.maptool.transfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Creates data chunks for transferring binary data.  Assumes large datasets (otherwise
 * it would be a direct transfer) so expects the data to be streamed from a file
 * 
 * @author trevor
 */
public class AssetProducer {

	private Serializable id;
	private String name;
	private File assetFile;
	private long length;
	private long currentPosition = 0;
	
	public AssetProducer(Serializable id, String name, File assetFile) {
		if (!assetFile.exists() || assetFile.isDirectory()) {
			throw new IllegalArgumentException(assetFile + " is an invalid asset path");
		}
		this.id = id;
		this.name = name;
		this.assetFile = assetFile;
		length = assetFile.length();
	}
	
	/**
	 * Get the header needed to create the corresponding AssetConsumer
	 * @throws IOException
	 */
	public AssetHeader getHeader() throws IOException {
		return new AssetHeader(id, name, assetFile.length());
	}

	/**
	 * Get the next chunk of data
	 * @param size how many bytes to grab, may end up being less if there isn't enough data
	 * @throws IOException
	 */
	public AssetChunk nextChunk(int size) throws IOException {
		
		if (currentPosition + size > length) {
			size = (int)(length - currentPosition);
		}
		
		byte[] data = new byte[size];
		FileInputStream in = new FileInputStream(assetFile);
		
		in.skip(currentPosition);
		in.read(data, 0, size);

		in.close();
		
		currentPosition += size;
		
		return new AssetChunk(id, data);
	}

	/**
	 * Whether all the data has been transferred
	 */
	public boolean isComplete() {
		return currentPosition >= length;
	}
}
