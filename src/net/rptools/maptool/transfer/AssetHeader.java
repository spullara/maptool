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

public class AssetHeader implements Serializable {

	private Serializable id;
	private String name;
	private long size;
	
	public AssetHeader(Serializable id, String name, long size) {
		this.id = id;
		this.size = size;
		this.name = name;
	}

	public Serializable getId() {
		return id;
	}

	public long getSize() {
		return size;
	}
	
	public String getName() {
		return name;
	}
}
