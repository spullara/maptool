package net.rptools.maptool.client.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;

import net.rptools.lib.FileUtil;
import net.rptools.lib.swing.AbstractPaintChooserPanel;
import net.rptools.lib.swing.ColorPicker;
import net.rptools.lib.swing.ImagePanel;
import net.rptools.lib.swing.ImagePanelModel;
import net.rptools.lib.swing.SelectionListener;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.util.ImageManager;

public class TextureChooserPanel extends AbstractPaintChooserPanel {

	private ColorPicker colorPicker;
	private ImagePanel imagePanel;
	
	public TextureChooserPanel(ColorPicker picker) {
		setLayout(new GridLayout());

		colorPicker = picker;

		imagePanel = new ImagePanel();
		imagePanel.setSelectionMode(ImagePanel.SelectionMode.SINGLE);
		imagePanel.setModel(new TextureChooserImagePanelModel());
		imagePanel.addSelectionListener(new SelectionListener(){
			public void selectionPerformed(List<Object> selectedList) {
				
				colorPicker.getPaintChooser().setPaint(new AssetPaint((Asset)selectedList.get(0)));
			}
		});
		imagePanel.setBackground(Color.white);
		imagePanel.setPreferredSize(new Dimension(400, 400));
		add(new JScrollPane(imagePanel));
	}

	public void addTexture(Asset asset) {
		
		((TextureChooserImagePanelModel)imagePanel.getModel()).addAsset(asset);
	}
	
	@Override
	public String getDisplayName() {
		return "Texture";
	}

	private static class TextureChooserImagePanelModel implements ImagePanelModel {

		private List<Asset> textureList = new ArrayList<Asset>();
		public TextureChooserImagePanelModel() {
			try {
				Asset asset = new Asset("Cobblestone", FileUtil.loadResource("net/rptools/lib/resource/image/texture/cobblestone.jpg"));
				textureList.add(asset);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		public void addAsset(Asset asset) {
			if (!textureList.contains(asset)) {
				textureList.add(asset);
			}
		}
		
		public String getCaption(int index) {
			return null;
		}
		public Object getID(int index) {
			return textureList.get(index);
		}
		public Image getImage(int index) {
			return ImageManager.getImageAndWait(textureList.get(index));
		}
		public Image getImage(Object ID) {
			return ImageManager.getImageAndWait((Asset) ID);
		}
		public int getImageCount() {
			return textureList.size();
		}
		public Transferable getTransferable(int index) {
			return null;
		}
	}
}
