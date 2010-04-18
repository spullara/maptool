/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.client;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import net.rptools.lib.FileUtil;
import net.rptools.maptool.model.AssetManager;

import org.apache.log4j.Logger;
import org.jdesktop.swingworker.SwingWorker;

/**
 * Executes only the first time the application is run.
 */
public class AppSetup {

	private static final Logger log = Logger.getLogger(AppSetup.class);

	public static void install() {

		File appDir = AppUtil.getAppHome();

		// Only init once
		if (appDir.listFiles().length > 0) {
			return;
		}

		try {

			installDefaultTokens();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void installDefaultTokens() throws IOException {

		installLibrary("Default", AppSetup.class.getClassLoader().getResource("default_images.zip"));
	}

	public static void installLibrary(String libraryName, URL resourceFile) throws IOException {
		File unzipDir = new File(AppConstants.UNZIP_DIR.getAbsolutePath() + File.separator + libraryName);

		FileUtil.unzip(resourceFile, unzipDir);

		installLibrary(libraryName, unzipDir);
	}

	public static void installLibrary(final String libraryName, final File root) throws IOException {

		// Add as a resource root
		AppPreferences.addAssetRoot(root);
		if (MapTool.getFrame() != null) {
			MapTool.getFrame().addAssetRoot(root);

			// License
			File licenseFile = new File(root.getAbsolutePath() + "/License.txt");
			if (!licenseFile.exists()) {
				licenseFile = new File(root.getAbsolutePath() + "/license.txt");
			}
			if (licenseFile.exists()) {

				final File licenseFileFinal = licenseFile;
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							JTextPane pane = new JTextPane();
							pane.setPage(licenseFileFinal.toURL());
							JOptionPane.showMessageDialog(MapTool.getFrame(), pane, "License for " + libraryName, JOptionPane.INFORMATION_MESSAGE);
						} catch (MalformedURLException e) {
							log.error("Could not load license file: " + licenseFileFinal, e);
						} catch (IOException e) {
							log.error("Could not load license file: " + licenseFileFinal, e);
						}
					}
				});
			}
		}

		new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				AssetManager.searchForImageReferences(root, AppConstants.IMAGE_FILE_FILTER);
				return null;
			}
		}.execute();

	}
}
