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
package net.rptools.maptool.client.ui;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import net.rptools.lib.swing.AbstractPaintChooserPanel;
import net.rptools.lib.swing.ImagePanel;
import net.rptools.lib.swing.PaintChooser;
import net.rptools.lib.swing.SelectionListener;
import net.rptools.maptool.client.ui.assetpanel.AssetPanel;
import net.rptools.maptool.client.ui.assetpanel.AssetPanelModel;

public class TextureChooserPanel extends AbstractPaintChooserPanel {

	private PaintChooser paintChooser;
	private ImagePanel imagePanel;
	
	public TextureChooserPanel(PaintChooser paintChooser, AssetPanelModel model) {
		this(paintChooser, model, "textureChooser");
	}
	public TextureChooserPanel(PaintChooser paintChooser, AssetPanelModel model, String controlName) {
		setLayout(new GridLayout());

		this.paintChooser = paintChooser;
		
		add(createImageExplorerPanel(model, controlName));
	}
	
	private JComponent createImageExplorerPanel(AssetPanelModel model, String controlName) {

		final AssetPanel assetPanel = new AssetPanel(controlName, model, JSplitPane.HORIZONTAL_SPLIT);
		assetPanel.addImageSelectionListener(new SelectionListener() {
			public void selectionPerformed(List<Object> selectedList) {
				// There should be exactly one
				if (selectedList.size() != 1) {
					return;
				}

				Integer imageIndex = (Integer) selectedList.get(0);

				paintChooser.setPaint(new AssetPaint(assetPanel.getAsset(imageIndex)));
			}
		});
		assetPanel.setThumbSize(100);

		return assetPanel;
	}
	
	@Override
	public String getDisplayName() {
		return "Texture";
	}
}
