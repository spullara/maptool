/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.util;

import java.io.IOException;
import java.net.InetAddress;

import net.rptools.maptool.client.MapTool;
import net.sbbi.upnp.impls.InternetGatewayDevice;
import net.sbbi.upnp.messages.ActionResponse;
import net.sbbi.upnp.messages.UPNPResponseException;

/**
 * @author Phil Wright
 */
public class UPnPUtil {
	private static int discoveryTimeout = 5000; // Should be made a preference setting
	private static InternetGatewayDevice[] IGDs;
	
	public static boolean findIGDs() {
		try {
			IGDs = InternetGatewayDevice.getDevices(discoveryTimeout);
		} catch (IOException ex) {
			// some IO Exception occured during communication with device
			ex.printStackTrace();
		}
		
		if (IGDs != null) {
			System.out.println("Found device: "	+ IGDs[0].getIGDRootDevice().getModelName());
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean openPort(int port) {
		boolean mapped = false;
		String localHostIP; 
		InternetGatewayDevice ourIGD;
		
		if (IGDs == null) {
			findIGDs();
		}
		
		if (IGDs != null) {
			// use the first device found
			ourIGD = IGDs[0];
		} else {
			MapTool.showError("No Internet Gateway Devices found.");
			return false;
		}
		
		try {
			// Get our local address
			localHostIP = InetAddress.getLocalHost().getHostAddress();
		
			mapped = ourIGD.addPortMapping(
						"MapTool", null,
						port, port, localHostIP, 0, "TCP");
				
		} catch (UPNPResponseException respEx) {
			// oops the IGD did not like something !!
			respEx.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (mapped) {
			System.out.println("Port " + port + " mapped");
			return true;
		} else {
			MapTool.showError("UPnP Port Mapping Failed");
			return false;
		}
	}
	
	public static boolean closePort(int port) {
		try{
	
			if (IGDs != null) {
				// using the first device found
				InternetGatewayDevice testIGD = IGDs[0];
				
				// See if there is a mapping before we try to get rid of it
				ActionResponse actResp = testIGD.getSpecificPortMappingEntry(null, port, "TCP");
				
				if (actResp != null) {
					// Gonna just assume the mapping belongs to us. as I can't make 
					// One of these days should figure out the  action response to verify it
					boolean unmapped = testIGD.deletePortMapping(null, port, "TCP");
					if (unmapped) {
						System.out.println("Port unmapped");
					} else {
						System.out.println("Failed to unmap port.");
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static int getDiscoveryTimeout() {
		return discoveryTimeout;
	}

	public static void setDiscoveryTimeout(int discoveryTimeout) {
		UPnPUtil.discoveryTimeout = discoveryTimeout;
	}
}
