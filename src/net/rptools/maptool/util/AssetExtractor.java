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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import net.rptools.lib.FileUtil;
import net.rptools.lib.io.PackedFile;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.model.Asset;

import com.thoughtworks.xstream.XStream;

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
				try {
					newDir.mkdirs();

					label.setText("Loading campaign ...");
					PackedFile pakfile = new PackedFile(file);

					Set<String> files = pakfile.getPaths();
					XStream xstream = new XStream();
					int count = 0;
					for (String filename : files) {
						count ++;

						if (filename.indexOf("assets") < 0) {
							continue;
						}
						
						InputStream in = pakfile.getFile(filename);
						Asset asset = (Asset) xstream.fromXML(in);
						in.close();
						
						File newFile = new File(newDir, asset.getName() + ".jpg");
						label.setText("Extracting image " + count + " of " + files.size() + ": " + newFile);
						if (newFile.exists()) {
							newFile.delete();
						}
						newFile.createNewFile();
						OutputStream out = new FileOutputStream(newFile);
						FileUtil.copy(new ByteArrayInputStream(asset.getImage()), out);
						out.close();

					}
					
					label.setText("Done.");
				} catch (Exception ioe) {
					ioe.printStackTrace();
					JOptionPane.showMessageDialog(null, "Failure: " + ioe);
				}
			}
		}.start();
	}
}
