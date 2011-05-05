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

import java.io.Serializable;

public class AssetChunk implements Serializable {
	private Serializable id;
	private byte[] data;
	
	public AssetChunk(Serializable id, byte[] data) {
		this.id = id;
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public Serializable getId() {
		return id;
	}
}
