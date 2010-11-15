/**
 * 
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
