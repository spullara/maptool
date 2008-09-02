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
package net.rptools.maptool.client;

import java.util.List;

public interface MapToolRegistryService {

	public static final int CODE_UNKNOWN = 0;
	public static final int CODE_OK = 1;
	public static final int CODE_COULD_CONNECT_BACK = 2;
    public static final int CODE_ID_IN_USE = 3;

	public int registerInstance(String id, int port, String version);
	public void unregisterInstance(int port);

    public String findInstance(String id);
    public List<String> findAllInstances();
    
	public boolean testConnection(int port);
	
	public void heartBeat(int port);
	
	public String getAddress();
}
