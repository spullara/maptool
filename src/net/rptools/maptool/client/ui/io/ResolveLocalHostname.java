/**
 * 
 */
package net.rptools.maptool.client.ui.io;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * @author crash
 * 
 */
public class ResolveLocalHostname {
	private static final int RANDOM_PORT = 54321;

	public static InetAddress getLocalHost(InetAddress intendedDestination) throws SocketException {
		DatagramSocket sock = new DatagramSocket(RANDOM_PORT);
		sock.connect(intendedDestination, RANDOM_PORT);
		InetAddress addr = sock.getLocalAddress();
		sock.close(); // Cleanup as many resources as possible. :)
		return addr;
	}
}
