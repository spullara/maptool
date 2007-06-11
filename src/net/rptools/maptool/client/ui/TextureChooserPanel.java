package net.rptools.maptool.client.ui;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import net.rptools.lib.swing.AbstractPaintChooserPanel;
import net.rptools.lib.swing.ColorPicker;
import net.rptools.lib.swing.ImagePanel;
import net.rptools.lib.swing.SelectionListener;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.assetpanel.AssetPanel;
import net.rptools.maptool.client.ui.assetpanel.AssetPanelModel;

public class TextureChooserPanel extends AbstractPaintChooserPanel {

	private ColorPicker colorPicker;
	private ImagePanel imagePanel;
	
	public TextureChooserPanel(ColorPicker picker, AssetPanelModel model) {
		setLayout(new GridLayout());

		colorPicker = picker;
		
		add(createImageExplorerPanel(model));
	}
	
	private JComponent createImageExplorerPanel(AssetPanelModel model) {

		final AssetPanel assetPanel = new AssetPanel("paintChooserImageExplorer", model, JSplitPane.HORIZONTAL_SPLIT);
		assetPanel.addImageSelectionListener(new SelectionListener() {
			public void selectionPerformed(List<Object> selectedList) {
				// There should be exactly one
				if (selectedList.size() != 1) {
					return;
				}

				Integer imageIndex = (Integer) selectedList.get(0);

				colorPicker.getPaintChooser().setPaint(new AssetPaint(assetPanel.getAsset(imageIndex)));
			}
		});

		return assetPanel;
	}
	
	@Override
	public String getDisplayName() {
		return "Texture";
	}
}
