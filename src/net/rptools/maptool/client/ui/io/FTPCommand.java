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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sun.net.TransferProtocolClient;
import sun.net.ftp.FtpClient;

/**
 * <p>
 * This class extends the Sun-standard {@link FtpClient} class by adding support for
 * additional commands.
 * </p>
 * <p>
 * This class creates its own connection to the specified host and does not try to
 * reuse an existing connection.  This has significant downsides, not the least of
 * which is the need to provide login information (username and password), but
 * also the server seeing multiple incoming connections might think that some kind
 * of DOS attack is being attempted and lock the account!
 * </p>
 * <p>
 * Currently the only added command is <code>MKDIR</code>.  This command may
 * be implemented differently on different servers (<code>MKDIR</code>,
 * <code>MKD</code>, <code>XMKD</code>, etc) so the first time the application
 * tries to create a directory for a given host we loop through the possibilities that we
 * know of until one works.  That command string is then saved for later use.
 * </p>
 * @author crash
 *
 */

public class FTPCommand extends FtpClient {
	private String host;
	private static Map<String, String> mkdirMap = new HashMap<String, String>();

	public FTPCommand(String h) throws IOException {
		super(h);
		host = h;
	}

	public int getReplyCode() {
		int code = Integer.parseInt(getResponseString().substring(0, 3));
		return code;
	}

	public int mkdir(String dir) throws IOException {
		int result = 0;
		if (mkdirMap.get(host) == null) {
			// We need to figure out which MKDIR command works for this host.
			for (String cmd : new String[] { "MKDIR ", "XMKD ", "MKD " } ) {
				try {
					issueCommand(cmd + dir);
				} catch (IOException e) {
					// Failed.  But why?
					// It could be the command worked and the directory already exists...
				}
				result = getReplyCode();
				if (result/100 <= 3 || result == 550) {
					// Since it worked, save the command and return.
					mkdirMap.put(host, cmd);
					return result;
				}
			}
		    throw new IOException("MKDIR: cannot determine server command");
		}
		try {
			issueCommand(mkdirMap.get(host) + dir);
			result = getReplyCode();
		} catch (IOException e) {
			result = getReplyCode();
			if (result != 550)		// "Directory already exists" is not necessarily an error
				e.printStackTrace();
		}
		return result;
	}

	public int remove(String filename) throws IOException {
		int result = 0;
		try {
			issueCommand("DELE " + filename);
			result = getReplyCode();
		} catch (IOException e) {
			result = getReplyCode();
			if (result != 550)		// "File doesn't exist" is not an error
				e.printStackTrace();
		}
		return result;
	}
}
