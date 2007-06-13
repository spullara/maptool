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

		return assetPanel;
	}
	
	@Override
	public String getDisplayName() {
		return "Texture";
	}
}
