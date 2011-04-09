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
package net.rptools.maptool.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class WebDownloader {
	private final URL url;

	public WebDownloader(URL url) {
		if (url == null) {
			throw new IllegalArgumentException("URL cannot be null");
		}
		this.url = url;
	}

	/**
	 * Read the data at the given URL. This method should not be called on the EDT.
	 * 
	 * @return File pointer to the location of the data, file will be deleted at program end
	 */
	public String read() throws IOException {
		URLConnection conn = url.openConnection();

		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);

		// Send the request.
		conn.connect();

		int length = conn.getContentLength();

		InputStream in = null;
		ByteArrayOutputStream out = null;

		try {
			in = conn.getInputStream();
			out = new ByteArrayOutputStream();

			int buflen = 1024 * 30;
			int bytesRead = 0;
			byte[] buf = new byte[buflen];

			for (int nRead = in.read(buf); nRead != -1; nRead = in.read(buf)) {
				bytesRead += nRead;
				out.write(buf, 0, nRead);
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
		return out != null ? new String(out.toByteArray()) : null;
	}

	public static void main(String[] args) throws Exception {
		WebDownloader downloader = new WebDownloader(new URL("http://library.rptools.net/1.3/listArtPacks"));
		String result = downloader.read();
		System.out.println(result);
	}
}
