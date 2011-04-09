/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *  
 *	http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.rptools.maptool.client.ui.io;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author crash
 * 
 */
public class ResolveLocalHostname {
	private static final int RANDOM_PORT = 54321;

	/**
	 * Currently the parameter is unused.
	 * 
	 * @param intendedDestination used to determine which NIC MapTool should bind to
	 * @return
	 * @throws UnknownHostException
	 * @throws SocketException
	 */
	public static InetAddress getLocalHost(InetAddress intendedDestination) throws UnknownHostException, SocketException {
		InetAddress inet = InetAddress.getLocalHost(); // This determines whether IP4 or IP6
		return inet;
	}
}
