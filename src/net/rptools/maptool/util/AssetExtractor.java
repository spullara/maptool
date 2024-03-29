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
package net.rptools.maptool.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.rptools.lib.FileUtil;
import net.rptools.lib.io.PackedFile;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Asset;

import org.apache.commons.io.IOUtils;

import com.thoughtworks.xstream.XStream;

/**
 * Appears to be unused within MapTool.  What was its original purpose?  It appears
 * to be some way to extract individual images from a campaign, but it has multiple
 * problems:  always names output images with <b>.jpg</b> extensions, doesn't
 * allow a choice of which images are extracted, doesn't turn on annotation processing
 * for {@link Asset} objects (needed for XStream processing), and doesn't used
 * buffered I/O classes.
 * 
 * @author ??
 */
public class AssetExtractor {
	public static void extract() throws Exception {
		new Thread() {
			@Override
			public void run() {
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(false);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File file = chooser.getSelectedFile();
				File newDir = new File(file.getParentFile(), file.getName().substring(0, file.getName().lastIndexOf('.')) + "_images");

				JLabel label = new JLabel("", JLabel.CENTER);
				JFrame frame = new JFrame();
				frame.setTitle("Campaign Image Extractor");
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setSize(400, 75);
				frame.add(label);
				SwingUtil.centerOnScreen(frame);
				frame.setVisible(true);
				Reader r = null;
				OutputStream out = null;
				PackedFile pakfile = null;
				try {
					newDir.mkdirs();

					label.setText("Loading campaign ...");
					pakfile = new PackedFile(file);

					Set<String> files = pakfile.getPaths();
					XStream xstream = new XStream();
					int count = 0;
					for (String filename : files) {
						count ++;
						if (filename.indexOf("assets") < 0) {
							continue;
						}
						r = pakfile.getFileAsReader(filename);
						Asset asset = (Asset) xstream.fromXML(r);
						IOUtils.closeQuietly(r);

						File newFile = new File(newDir, asset.getName() + ".jpg");
						label.setText("Extracting image " + count + " of " + files.size() + ": " + newFile);
						if (newFile.exists()) {
							newFile.delete();
						}
						newFile.createNewFile();
						out = new FileOutputStream(newFile);
						FileUtil.copyWithClose(new ByteArrayInputStream(asset.getImage()), out);
					}
					label.setText("Done.");
				} catch (Exception ioe) {
					MapTool.showInformation("AssetExtractor failure", ioe);
				} finally {
					if (pakfile != null)
						pakfile.close();
					IOUtils.closeQuietly(r);
					IOUtils.closeQuietly(out);
				}
			}
		}.start();
	}
}
